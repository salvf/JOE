/* java.lang.Object - The universal superclass in Java
 Copyright (C) 1998, 1999, 2000, 2001, 2002, 2004
 Free Software Foundation, Inc.

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
 Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 02111-1307 USA.

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


package java.lang;

import baremetal.vm.VMObject;

/**
 * @author Tom Tromey <tromey@cygnus.com>@date September 30, 1998
 */

/*
 * Written using "Java Class Libraries", 2nd edition, ISBN 0-201-31002-3 "The
 * Java Language Specification", ISBN 0-201-63451-1 plus online API docs for
 * JDK 1.2 beta from http://www.javasoft.com. plus gcj compiler sources (to
 * determine object layout) Status: Complete to version 1.1
 */

public class Object {
  // This must come first. See _JvObjectPrefix in Object.h.
  protected void finalize() throws Throwable {
  }

  public final Class getClass() {
     return baremetal.runtime.Class.getClass(this);
  }

  public int hashCode() {
    // fixme
    return VMObject.hashCode(this);
  }

  public final void notify() {
    VMObject.notify(this);
  }

  public final void notifyAll() {
    VMObject.notifyAll(this);
  }

  public final void wait(long timeout, int nanos) throws InterruptedException {
    VMObject.wait(this,timeout,nanos);
  }


  public boolean equals(Object obj) {
    return this == obj;
  }

  public Object() {
  }

  public String toString() {
    return getClass().getName() + '@' + Integer.toHexString(hashCode());
  }

  public final void wait() throws InterruptedException {
    wait(0, 0);
  }

  public final void wait(long timeout) throws InterruptedException {
    wait(timeout, 0);
  }

  protected Object clone() throws CloneNotSupportedException {
    return VMObject.clone((Cloneable)this);
  }

  // This initializes the sync_info member. It is here for
  // completeness (some day we'll be able to auto-generate Object.h).
  private final void sync_init() {
  }

  // Note that we don't mention the sync_info field here. If we do,
  // jc1 will not work correctly.
}
