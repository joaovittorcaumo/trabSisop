package components;

import domain.Opcode;

import static utils.MemoryTranslator.getEndFrame;
import static utils.MemoryTranslator.getStartFrame;

public class MemoryManager {

    private Memory memory;
    private boolean[] availableFrames;

    MemoryManager(Memory memory) {
        this.memory = memory;
        this.availableFrames = new boolean[memory.numberOfFrames];

        // Initialize all frames as available
        for (int i = 0; i < memory.numberOfFrames; i++) {
            availableFrames[i] = true;
        }
    }

    // Allocate memory for a certain number of words
    public boolean allocate(int wordCount, int[] pageTable) {
        int requiredFrames = (int) Math.ceil((double) wordCount / this.memory.pageSize);
        if (requiredFrames > countAvailableFrames()) {
            return false;
        }

        int[] freeFrames = getAvailableFramesIndexes();
        for (int i = 0; i < requiredFrames; i++) {
            int frameIndex = freeFrames[i];
            pageTable[i] = frameIndex;
            availableFrames[frameIndex] = false;
        }
        return true;
    }

    // Deallocate memory
    public void deallocate(int[] pageTable) {
        for (int frame : pageTable) {
            for (int j = getStartFrame(frame, memory.pageSize); j <= getEndFrame(frame, memory.pageSize); j++) {
                deallocatePosition(j);
            }
            availableFrames[frame] = true;
        }
    }

    // Count the number of available frames
    private int countAvailableFrames() {
        int count = 0;
        for (boolean frame : availableFrames) {
            if (frame)
                count++;
        }
        return count;
    }

    // Get the indexes of available frames
    private int[] getAvailableFramesIndexes() {
        int[] available = new int[countAvailableFrames()];
        int j = 0;
        for (int i = 0; i < availableFrames.length; i++) {
            if (availableFrames[i]) {
                available[j] = i;
                j++;
            }
        }
        return available;
    }

    // Reset the memory position
    private void deallocatePosition(int position) {
        memory.memoryArray[position].opCode = Opcode.___;
        memory.memoryArray[position].r1 = -1;
        memory.memoryArray[position].r2 = -1;
        memory.memoryArray[position].p = -1;
    }

}
