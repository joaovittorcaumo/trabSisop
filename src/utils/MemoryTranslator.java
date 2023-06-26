package utils;

public class MemoryTranslator {

    // Get the start frame for a given frame index and page size
    public static int getStartFrame(int frameIndex, int pageSize) {
        return frameIndex * pageSize;
    }

    // Get the end frame for a given frame index and page size
    public static int getEndFrame(int frameIndex, int pageSize) {
        return (frameIndex + 1) * pageSize - 1;
    }

    // Translate the logical address to a physical address using the page table and
    // page size
    public static int translate(int logicalAddress, int[] pageTable, int pageSize) {
        int pageIndex = logicalAddress / pageSize;
        int offset = logicalAddress % pageSize;
        return getStartFrame(pageTable[pageIndex], pageSize) + offset;
    }
}
