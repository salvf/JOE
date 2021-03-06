package org.jam.mm;

import org.jikesrvm.VM;
import org.jikesrvm.runtime.BootRecord;
import org.jikesrvm.runtime.Memory;
import org.vmmagic.pragma.Uninterruptible;
import org.vmmagic.unboxed.Address;
import org.vmmagic.unboxed.Extent;

@Uninterruptible
public class MemoryManager {
	static Address freeMemStart;
	static Address freeMemEnd;
	static Address cursor;
	
	public static void boot(BootRecord bootRecord) {
		freeMemStart = bootRecord.bootImageRMapStart;
		freeMemEnd = freeMemStart.plus(bootRecord.maximumHeapSize);
		cursor = freeMemStart;
		VM.sysWrite("MemoryManager: start=", freeMemStart);
		VM.sysWrite(" end=", freeMemEnd);
		VM.sysWrite(" maxHeapSize=", bootRecord.maximumHeapSize.toInt());
		VM.sysWriteln();
	}
	
	public static Address alloc(Address address, Extent size) {
		Address cursor = address;
		if(cursor.plus(size).GT(freeMemEnd)) {
			VM.sysWriteln("PANIC: ", cursor.toInt(), " ", size.toInt());
			VM.sysFail("Out of Memory");
		}
		Memory.zero(false, address,  size);
		return cursor;
	}
}
