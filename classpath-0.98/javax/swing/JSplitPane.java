/* JSplitPane.java -- 
   Copyright (C) 2004, 2006,  Free Software Foundation, Inc.

This file is part of GNU Classpath.

GNU Classpath is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2, or (at your option)
any later version.

GNU Classpath is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
General Public License for more details.

You should have received a copy of the GNU General Public License
along with GNU Classpath; see the file COPYING.  If not, write to the
Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
02110-1301 USA.

Linking this library statically or dynamically with other modules is
making a combined work based on this library.  Thus, the terms and
conditions of the GNU General Public License cover the whole
combination.

As a special exception, the copyright holders of this library give you
permission to link this library with independent modules to produce an
executable, regardless of the license terms of these independent
modules, and to copy and distribute the resulting executable under
terms of your choice, provided that you also meet, for each linked
independent module, the terms and conditions of the license of that
module.  An independent module is a module which is not derived from
or based on this library.  If you modify this library, you may extend
this exception to your version of the library, but you are not
obligated to do so.  If you do not wish to do so, delete this
exception statement from your version. */


package javax.swing;

import gnu.java.lang.CPStringBuilder;

import java.awt.Component;
import java.awt.Graphics;
import java.beans.PropertyChangeEvent;

import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import javax.accessibility.AccessibleValue;
import javax.swing.plaf.SplitPaneUI;

/**
 * This class implements JSplitPane. It is used to divide two components. By
 * dragging the SplitPane's divider, the user can resize the two components.
 * Note that the divider cannot resize a component to smaller than it's
 * minimum size.
 */
public class JSplitPane extends JComponent implements Accessible
{

  /**
   * Provides the accessibility features for the <code>JSplitPane</code>
   * component.
   */
  protected class AccessibleJSplitPane extends JComponent.AccessibleJComponent
    implements AccessibleValue
  {
  private static final long serialVersionUID = -1788116871416305366L;
  
    /**
     * Creates a new <code>AccessibleJSplitPane</code> instance.
     */
    protected AccessibleJSplitPane()
    {
      // Nothing to do here.
    }

    /**
     * Returns a set containing the current state of the {@link JSplitPane} 
     * component.
     *
     * @return The accessible state set.
     */
    public AccessibleStateSet getAccessibleStateSet()
    {
      AccessibleStateSet result = super.getAccessibleStateSet();
      if (getOrientation() == HORIZONTAL_SPLIT)
        {
          result.add(AccessibleState.HORIZONTAL);
        }
      else if (getOrientation() == VERTICAL_SPLIT)
        {
          result.add(AccessibleState.VERTICAL);
        }
      return result;
    }

    /**
     * Returns the accessible role for the <code>JSplitPane</code> component.
     *
     * @return {@link AccessibleRole#SPLIT_PANE}.
     */
    public AccessibleRole getAccessibleRole()
    {
      return AccessibleRole.SPLIT_PANE;
    }

    /**
     * Returns an object that provides access to the current, minimum and 
     * maximum values for the {@link JSplitPane}.  Since this class implements 
     * {@link AccessibleValue}, it returns itself.
     *
     * @return The accessible value.
     */
    public AccessibleValue getAccessibleValue()
    {
      return this;
    }

    /**
     * Returns the current divider location for the {@link JSplitPane} 
     * component, as an {@link Integer}.
     *
     * @return The current divider location.
     */
    public Number getCurrentAccessibleValue()
    {
      return new Integer(getDividerLocation());
    }

    /**
     * Sets the divider location for the {@link JSplitPane} component and sends 
     * a {@link PropertyChangeEvent} (with the property name 
     * {@link AccessibleContext#ACCESSIBLE_VALUE_PROPERTY}) to all registered
     * listeners.  If the supplied value is <code>null</code>, this method 
     * does nothing and returns <code>false</code>.
     *
     * @param value  the new divider location (<code>null</code> permitted).
     *
     * @return <code>true</code> if the divider location value is updated, and 
     *     <code>false</code> otherwise.
     */
    public boolean setCurrentAccessibleValue(Number value)
    {
      if (value == null)
        return false;
      Number oldValue = getCurrentAccessibleValue();
      setDividerLocation(value.intValue());
      firePropertyChange(AccessibleContext.ACCESSIBLE_VALUE_PROPERTY, oldValue, 
                         new Integer(value.intValue()));
      return true;
    }

    /**
     * Returns the minimum divider location for the {@link JSplitPane} 
     * component, as an {@link Integer}.
     *
     * @return The minimum divider location.
     */
    public Number getMinimumAccessibleValue()
    {
      return new Integer(getMinimumDividerLocation());
    }

    /**
     * Returns the maximum divider location for the {@link JSplitPane} 
     * component, as an {@link Integer}.
     *
     * @return The maximum divider location.
     */
    public Number getMaximumAccessibleValue()
    {
      return new Integer(getMaximumDividerLocation());
    }
  }

  private static final long serialVersionUID = -5634142046175988380L;
  
  /** The constraints string used to add components to the bottom. */
  public static final String BOTTOM = "bottom";

  /** The property fired when the continuousLayout property changes. */
  public static final String CONTINUOUS_LAYOUT_PROPERTY = "continuousLayout";

  /** The property fired when the divider property changes. */
  public static final String DIVIDER = "divider";

  /** The property fired when the divider location property changes. */
  public static final String DIVIDER_LOCATION_PROPERTY = "dividerLocation";

  /** The property fired when the divider size property changes. */
  public static final String DIVIDER_SIZE_PROPERTY = "dividerSize";

  /**
   * The value of the orientation when the components are split horizontally.
   */
  public static final int HORIZONTAL_SPLIT = 1;

  /** The property fired when the last divider location property changes. */
  public static final String LAST_DIVIDER_LOCATION_PROPERTY = 
    "lastDividerLocation";

  /** The constraints string used to add components to the left. */
  public static final String LEFT = "left";

  /** The property fired when the one touch expandable property changes. */
  public static final String ONE_TOUCH_EXPANDABLE_PROPERTY = 
    "oneTouchExpandable";

  /** The property fired when the orientation property changes. */
  public static final String ORIENTATION_PROPERTY = "orientation";

  /** The property fired when the resize weight property changes. */
  public static final String RESIZE_WEIGHT_PROPERTY = "resizeWeight";

  /** The constraints string used to add components to the right. */
  public static final String RIGHT = "right";

  /** The constraints string used to add components to the top. */
  public static final String TOP = "top";

  /** The value of the orientation when the components are split vertically. */
  public static final int VERTICAL_SPLIT = 0;

  /** Whether the JSplitPane uses continuous layout. */
  protected boolean continuousLayout;

  /** Whether the JSplitPane uses one touch expandable buttons. */
  protected boolean oneTouchExpandable = false;

  // This is the master dividerSize variable and sets the 
  // BasicSplitPaneDivider one accordingly

  /** The size of the divider. */
  protected int dividerSize = 10;

  /** The last location of the divider given by the UI. */
  protected int lastDividerLocation;

  /** The orientation of the JSplitPane. */
  protected int orientation;

  /** The component on the top or left. */
  protected Component leftComponent;

  /** The component on the right or bottom. */
  protected Component rightComponent;

  /**
   * The divider location.
   */
  private int dividerLocation;

  /** Determines how extra space should be allocated. */
  private transient double resizeWeight;

  /**
   * Indicates if the dividerSize property has been set by a client program or
   * by the UI.
   *
   * @see #setUIProperty(String, Object)
   * @see LookAndFeel#installProperty(JComponent, String, Object)
   */
  private boolean clientDividerSizeSet = false;

  /**
   * Indicates if the oneTouchExpandable property has been set by a client
   * program or by the UI.
   *
   * @see #setUIProperty(String, Object)
   * @see LookAndFeel#installProperty(JComponent, String, Object)
   */
  private boolean clientOneTouchExpandableSet = false;

  /**
   * Creates a new JSplitPane object with the given orientation, layout mode,
   * and left and right components.
   *
   * @param newOrientation The orientation to use.
   * @param newContinuousLayout The layout mode to use.
   * @param newLeftComponent The left component.
   * @param newRightComponent The right component.
   *
   * @throws IllegalArgumentException DOCUMENT ME!
   */
  public JSplitPane(int newOrientation, boolean newContinuousLayout,
                    Component newLeftComponent, Component newRightComponent)
  {
    if (newOrientation != HORIZONTAL_SPLIT && newOrientation != VERTICAL_SPLIT)
      throw new IllegalArgumentException("orientation is invalid.");
    orientation = newOrientation;
    continuousLayout = newContinuousLayout;
    setLeftComponent(newLeftComponent);
    setRightComponent(newRightComponent);
    dividerLocation = -1;
    updateUI();
  }

  /**
   * Creates a new JSplitPane object using nonContinuousLayout mode, the given
   * orientation and left and right components.
   *
   * @param newOrientation The orientation to use.
   * @param newLeftComponent The left component.
   * @param newRightComponent The right component.
   */
  public JSplitPane(int newOrientation, Component newLeftComponent,
                    Component newRightComponent)
  {
    this(newOrientation, false, newLeftComponent, newRightComponent);
  }

  /**
   * Creates a new JSplitPane object with the given layout mode and
   * orientation.
   *
   * @param newOrientation The orientation to use.
   * @param newContinuousLayout The layout mode to use.
   */
  public JSplitPane(int newOrientation, boolean newContinuousLayout)
  {
    this(newOrientation, newContinuousLayout, null, null);
  }

  /**
   * Creates a new JSplitPane object using a nonContinuousLayout mode and the
   * given orientation.
   *
   * @param newOrientation The orientation to use.
   */
  public JSplitPane(int newOrientation)
  {
    this(newOrientation, false, null, null);
  }

  /**
   * Creates a new JSplitPane object using HORIZONTAL_SPLIT and a
   * nonContinuousLayout mode.
   */
  public JSplitPane()
  {
    this(HORIZONTAL_SPLIT, false, new JButton("left button"),
         new JButton("right button"));
  }

  /**
   * This method adds a component to the JSplitPane. The constraints object is
   * a string that identifies where this component should go. If the
   * constraints is not a known one, it will throw an
   * IllegalArgumentException. The valid constraints are LEFT, TOP, RIGHT,
   * BOTTOM and DIVIDER.
   *
   * @param comp The component to add.
   * @param constraints The constraints string to use.
   * @param index Where to place to component in the list of components.
   *
   * @throws IllegalArgumentException When the constraints is not a known 
   * identifier.
   */
  protected void addImpl(Component comp, Object constraints, int index)
  {
    if (constraints == null)
      {
        if (leftComponent == null)
          constraints = LEFT;
        else if (rightComponent == null)
          constraints = RIGHT;
      }

    if (constraints instanceof String)
      {
        String placement = (String) constraints;

        if (placement.equals(BOTTOM) || placement.equals(RIGHT))
          {
            if (rightComponent != null)
              remove(rightComponent);
            rightComponent = comp;
          }
        else if (placement.equals(LEFT) || placement.equals(TOP))
          {
            if (leftComponent != null)
              remove(leftComponent);
            leftComponent = comp;
          }
        else if (placement.equals(DIVIDER))
          constraints = null;
        else
          throw new 
            IllegalArgumentException("Constraints is not a known identifier.");

        // If no dividerLocation has been set, then we need to trigger an
        // initial layout.
        if (getDividerLocation() != -1)
          resetToPreferredSizes();

        super.addImpl(comp, constraints, index);
      }
  }

  /**
   * Returns the object that provides accessibility features for this
   * <code>JSplitPane</code> component.
   *
   * @return The accessible context (an instance of 
   *     {@link AccessibleJSplitPane}).
   */
  public AccessibleContext getAccessibleContext()
  {
    if (accessibleContext == null)
      accessibleContext = new AccessibleJSplitPane();
    
    return accessibleContext;
  }

  /**
   * This method returns the bottom component.
   *
   * @return The bottom component.
   */
  public Component getBottomComponent()
  {
    return rightComponent;
  }

  /**
   * This method returns the location of the divider. This method is passed to
   * the UI.
   *
   * @return The location of the divider.
   */
  public int getDividerLocation()
  {
    return dividerLocation;
  }

  /**
   * This method returns the size of the divider.
   *
   * @return The size of the divider.
   */
  public int getDividerSize()
  {
    return dividerSize;
  }

  /**
   * This method returns the last divider location.
   *
   * @return The last divider location.
   */
  public int getLastDividerLocation()
  {
    return lastDividerLocation;
  }

  /**
   * This method returns the left component.
   *
   * @return The left component.
   */
  public Component getLeftComponent()
  {
    return leftComponent;
  }

  /**
   * This method returns the maximum divider location. This method is passed
   * to  the UI.
   *
   * @return DOCUMENT ME!
   */
  public int getMaximumDividerLocation()
  {
    if (ui != null)
      return ((SplitPaneUI) ui).getMaximumDividerLocation(this);
    else
      return -1;
  }

  /**
   * This method returns the minimum divider location. This method is passed
   * to the UI.
   *
   * @return The minimum divider location.
   */
  public int getMinimumDividerLocation()
  {
    if (ui != null)
      return ((SplitPaneUI) ui).getMinimumDividerLocation(this);
    else
      return -1;
  }

  /**
   * This method returns the orientation that the JSplitPane is using.
   *
   * @return The current orientation.
   */
  public int getOrientation()
  {
    return orientation;
  }

  /**
   * This method returns the current resize weight.
   *
   * @return The current resize weight.
   */
  public double getResizeWeight()
  {
    return resizeWeight;
  }

  /**
   * This method returns the right component.
   *
   * @return The right component.
   */
  public Component getRightComponent()
  {
    return rightComponent;
  }

  /**
   * This method returns the top component.
   *
   * @return The top component.
   */
  public Component getTopComponent()
  {
    return leftComponent;
  }

  /**
   * This method returns the UI.
   *
   * @return The UI.
   */
  public SplitPaneUI getUI()
  {
    return (SplitPaneUI) ui;
  }

  /**
   * This method returns true if the JSplitPane is using a continuousLayout.
   *
   * @return True if using a continuousLayout.
   */
  public boolean isContinuousLayout()
  {
    return continuousLayout;
  }

  /**
   * This method returns true if the divider has one touch expandable buttons.
   *
   * @return True if one touch expandable is used.
   */
  public boolean isOneTouchExpandable()
  {
    return oneTouchExpandable;
  }

  /**
   * This method returns true.
   *
   * @return true.
   */
  public boolean isValidateRoot()
  {
    return true;
  }

  /**
   * This method overrides JComponent's paintChildren so the UI can be
   * messaged when the children have finished painting.
   *
   * @param g The Graphics object to paint with.
   */
  protected void paintChildren(Graphics g)
  {
    super.paintChildren(g);
    if (ui != null)
      ((SplitPaneUI) ui).finishedPaintingChildren(this, g);
  }

  /**
   * Returns an implementation-dependent string describing the attributes of
   * this <code>JSplitPane</code>.
   *
   * @return A string describing the attributes of this <code>JSplitPane</code>
   *         (never <code>null</code>).
   */
  protected String paramString()
  {
    // FIXME: the next line can be restored once PR27208 is fixed
    String superParamStr = ""; //super.paramString();
    CPStringBuilder sb = new CPStringBuilder();
    sb.append(",continuousLayout=").append(isContinuousLayout());
    sb.append(",dividerSize=").append(getDividerSize());
    sb.append(",lastDividerLocation=").append(getLastDividerLocation());
    sb.append(",oneTouchExpandable=").append(isOneTouchExpandable());
    sb.append(",orientation=");
    if (orientation == HORIZONTAL_SPLIT)
      sb.append("HORIZONTAL_SPLIT");
    else
      sb.append("VERTICAL_SPLIT");
    return superParamStr + sb.toString();
  }

  /**
   * This method removes the given component from the JSplitPane.
   *
   * @param component The Component to remove.
   */
  public void remove(Component component)
  {
    if (component == leftComponent)
      leftComponent = null;
    else if (component == rightComponent)
      rightComponent = null;
    super.remove(component);
  }

  /**
   * This method removes the component at the given index.
   *
   * @param index The index of the component to remove.
   */
  public void remove(int index)
  {
    Component component = getComponent(index);
    if (component == leftComponent)
      leftComponent = null;
    else if (component == rightComponent)
      rightComponent = null;
    super.remove(index);
  }

  /**
   * This method removes all components from the JSplitPane.
   */
  public void removeAll()
  {
    leftComponent = null;
    rightComponent = null;
    super.removeAll();
  }

  /**
   * This method resets all children of the JSplitPane to their preferred
   * sizes.
   */
  public void resetToPreferredSizes()
  {
    if (ui != null)
      ((SplitPaneUI) ui).resetToPreferredSizes(this);
  }

  /**
   * This method sets the bottom component.
   *
   * @param comp The Component to be placed at the bottom.
   */
  public void setBottomComponent(Component comp)
  {
    if (comp != null)
      add(comp, BOTTOM);
    else
      add(new JButton("right button"), BOTTOM);
  }

  /**
   * This method sets the layout mode for the JSplitPane.
   *
   * @param newContinuousLayout Whether the JSplitPane is in continuousLayout
   *        mode.
   */
  public void setContinuousLayout(boolean newContinuousLayout)
  {
    if (newContinuousLayout != continuousLayout)
      {
        boolean oldValue = continuousLayout;
        continuousLayout = newContinuousLayout;
        firePropertyChange(CONTINUOUS_LAYOUT_PROPERTY, oldValue,
                           continuousLayout);
      }
  }

  /**
   * This method sets the location of the divider. A value of 0 sets the
   * divider to the farthest left. A value of 1 sets the divider to the
   * farthest right.
   *
   * @param proportionalLocation A double that describes the location of the
   *        divider.
   *
   * @throws IllegalArgumentException if <code>proportionalLocation</code> is
   *     not in the range from 0.0 to 1.0 inclusive.
   */
  public void setDividerLocation(double proportionalLocation)
  {
    if (proportionalLocation > 1 || proportionalLocation < 0)
      throw new IllegalArgumentException
        ("proportion has to be between 0 and 1.");

    int max = ((orientation == HORIZONTAL_SPLIT) ? getWidth() : getHeight())
              - getDividerSize();
    setDividerLocation((int) (proportionalLocation * max));
  }

  /**
   * This method sets the location of the divider.
   * 
   * @param location The location of the divider. The negative value forces to
   *          compute the new location from the preferred sizes of the split
   *          pane components.
   */
  public void setDividerLocation(int location)
  {
    int oldLocation = dividerLocation;
    dividerLocation = location;
    SplitPaneUI ui = getUI();
    if (ui != null)
      ui.setDividerLocation(this, location);
    firePropertyChange(DIVIDER_LOCATION_PROPERTY, oldLocation, 
                       location);
  }

  /**
   * This method sets the size of the divider.
   *
   * @param newSize The size of the divider.
   */
  public void setDividerSize(int newSize)
  {
    clientDividerSizeSet = true;
    if (newSize != dividerSize)
      {
        int oldSize = dividerSize;
        dividerSize = newSize;
        firePropertyChange(DIVIDER_SIZE_PROPERTY, oldSize, dividerSize);
      }
  }

  // This doesn't appear to do anything when set from user side.
  // so it probably is only used from the UI side to change the
  // lastDividerLocation var.

  /**
   * This method sets the last location of the divider.
   *
   * @param newLastLocation The last location of the divider.
   */
  public void setLastDividerLocation(int newLastLocation)
  {
    if (newLastLocation != lastDividerLocation)
      {
        int oldValue = lastDividerLocation;
        lastDividerLocation = newLastLocation;
        firePropertyChange(LAST_DIVIDER_LOCATION_PROPERTY, oldValue,
                           lastDividerLocation);
      }
  }

  /**
   * This method sets the left component.
   *
   * @param comp The left component.
   */
  public void setLeftComponent(Component comp)
  {    
    if (comp != null)
      add(comp, LEFT);
    else
      remove (leftComponent);
  }

  /**
   * This method sets whether the divider has one touch expandable buttons.
   * The one touch expandable buttons can expand the size of either component
   * to the maximum allowed size.
   *
   * @param newValue Whether the divider will have one touch expandable
   *        buttons.
   */
  public void setOneTouchExpandable(boolean newValue)
  {
    clientOneTouchExpandableSet = true;
    if (newValue != oneTouchExpandable)
      {
        boolean oldValue = oneTouchExpandable;
        oneTouchExpandable = newValue;
        firePropertyChange(ONE_TOUCH_EXPANDABLE_PROPERTY, oldValue,
                           oneTouchExpandable);
      }
  }

  /**
   * Sets the orientation for the <code>JSplitPane</code> and sends a 
   * {@link PropertyChangeEvent} (with the property name 
   * {@link #ORIENTATION_PROPERTY}) to all registered listeners.
   *
   * @param orientation  the orientation (either {@link #HORIZONTAL_SPLIT}
   * or {@link #VERTICAL_SPLIT}).
   *
   * @throws IllegalArgumentException if <code>orientation</code> is not one of
   *     the listed values.
   */
  public void setOrientation(int orientation)
  {
    if (orientation != HORIZONTAL_SPLIT && orientation != VERTICAL_SPLIT)
      throw new IllegalArgumentException
        ("orientation must be one of VERTICAL_SPLIT, HORIZONTAL_SPLIT");
    if (orientation != this.orientation)
      {
        int oldOrientation = this.orientation;
        this.orientation = orientation;
        firePropertyChange(ORIENTATION_PROPERTY, oldOrientation,
                           this.orientation);
      }
  }

  /**
   * This method determines how extra space will be distributed among the left
   * and right components. A value of 0 will allocate all extra space to the
   * right component. A value of 1 indicates that all extra space will go to
   * the left component. A value in between 1 and 0 will split the space
   * accordingly.
   *
   * @param value The resize weight.
   */
  public void setResizeWeight(double value)
  {
    if (value < 0.0 || value > 1.0)
      throw new IllegalArgumentException("Value outside permitted range.");
    if (this.resizeWeight != value)
      { 
        double old = resizeWeight;
        resizeWeight = value;
        firePropertyChange(RESIZE_WEIGHT_PROPERTY, old, value);
      }
  }

  /**
   * This method sets the right component.
   *
   * @param comp The right component.
   */
  public void setRightComponent(Component comp)
  {
    if (comp != null)
      add(comp, RIGHT);
    else
      remove (rightComponent);
  }

  /**
   * This method sets the top component.
   *
   * @param comp The top component.
   */
  public void setTopComponent(Component comp)
  {
    if (comp != null)
      add(comp, TOP);
    else
      add(new JButton("left button"), TOP);
  }

  /**
   * This method sets the UI used by the JSplitPane.
   *
   * @param ui The UI to use.
   */
  public void setUI(SplitPaneUI ui)
  {
    super.setUI(ui);
  }

  /**
   * This method resets the UI to the one specified by the current Look and
   * Feel.
   */
  public void updateUI()
  {
    setUI((SplitPaneUI) UIManager.getUI(this));
  }

  /**
   * This method returns a string identifier to determine which UI class it
   * needs.
   *
   * @return A string that identifies it's UI class.
   */
  public String getUIClassID()
  {
    return "SplitPaneUI";
  }

  /**
   * Helper method for
   * {@link LookAndFeel#installProperty(JComponent, String, Object)}.
   * 
   * @param propertyName the name of the property
   * @param value the value of the property
   *
   * @throws IllegalArgumentException if the specified property cannot be set
   *         by this method
   * @throws ClassCastException if the property value does not match the
   *         property type
   * @throws NullPointerException if <code>c</code> or
   *         <code>propertyValue</code> is <code>null</code>
   */
  void setUIProperty(String propertyName, Object value)
  {
    if (propertyName.equals("dividerSize"))
      {
        if (! clientDividerSizeSet)
          {
            setDividerSize(((Integer) value).intValue());
            clientDividerSizeSet = false;
          }
      }
    else if (propertyName.equals("oneTouchExpandable"))
      {
        if (! clientOneTouchExpandableSet)
          {
            setOneTouchExpandable(((Boolean) value).booleanValue());
            clientOneTouchExpandableSet = false;
          }
      }
    else
      {
        super.setUIProperty(propertyName, value);
      }
  }
}
