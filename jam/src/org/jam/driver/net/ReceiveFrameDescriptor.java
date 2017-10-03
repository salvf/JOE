/**
 * Created on Aug 30, 2017
 *
 * Copyright (C) Joe Kulig, 2017
 * All rights reserved.
 */
package org.jam.driver.net;

import org.jam.net.ethernet.Ethernet;
import org.jikesrvm.VM;
import org.jikesrvm.runtime.Magic;
import org.vmmagic.unboxed.Address;
import org.vmmagic.unboxed.Offset;

/**
 * @author Joe Kulig
 *
 */
public class ReceiveFrameDescriptor {
  private static final int EL              = (1 << 31);
  private static final int SUSPEND         = (1 << 30);
  private static final int HEADER          = (1 << 20);
  private static final int SIMPLIFIED_MODE = (1 << 19);
  private static final int COMPLETE        = (1 << 15);
  private static final int OK              = (1 << 13);
  private static final int EOF             = (1 << 15);
  private static final int FINISHED        = (1 << 14);
  private static final int RFD_SIZE        = 16;
  
  private static final Offset LINK_ADDR       = Offset.zero().plus(4);
  private static final Offset INFO            = Offset.zero().plus(12);
  private static final Offset SIZE            = Offset.zero().plus(14);

  private static final int    ACTUAL_SIZE_MASK = 0x3FF;
  private static final int    STATUS_MASK      = 0x1FF;
  
  private byte buffer[];
  private Address rfdAddr;
  
  public ReceiveFrameDescriptor(int bufferSize)
  {
    buffer = new byte[bufferSize];
    rfdAddr = Magic.objectAsAddress(buffer);
    size(bufferSize-RFD_SIZE);
  }
  
  public ReceiveFrameDescriptor()
  {
    this(Ethernet.FRAME_LENGTH + Ethernet.FCS_LENGTH + RFD_SIZE);
  }
  
  /**
   * @param receiveFrameDescriptor
   */
  public void link(ReceiveFrameDescriptor receiveFrameDescriptor)
  {
    // set the link address field
    rfdAddr.store(receiveFrameDescriptor.rfdAddr, LINK_ADDR);
  }

  /**
   * Set the EL bit
   */
  public void endLink()
  {
    int value = rfdAddr.loadInt();
    value |= EL;
    rfdAddr.store(value);
  }

  /**
   * Set the size of buffer
   * @param size
   */
  public void size(int size)
  {
    rfdAddr.store((short)size, SIZE);
  }
  
  public Address getAddress()
  {
    return rfdAddr;
  }

  /**
   * @return
   */
  public int actualSize()
  {
    return rfdAddr.loadInt(INFO) & ACTUAL_SIZE_MASK;
  }

  public int getStatus()
  {
    return rfdAddr.loadInt() & STATUS_MASK;
  }
  /**
   * @return
   */
  public boolean notComplete()
  {
    return (rfdAddr.loadInt() & COMPLETE)==0;
  }

  /**
   * 
   */
  public void dump()
  {
    VM.hexDump(buffer, 16, actualSize());
  }
}