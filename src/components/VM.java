package components;

import domain.Word;
import handlers.InterruptHandling;
import handlers.SysCallHandling;

public class VM {
    public int totalMemorySize;
    public static Memory memory;
    public CPU cpu;

    public VM() {
        // VM should be configured with the address for interrupt handling and system
        // calls
        // Create memory
        totalMemorySize = 1024;
        int pageSize = 8;
        System.out.println("Creating memory with " + totalMemorySize / pageSize + " frames");
        memory = new Memory(totalMemorySize, pageSize);
        // Create CPU
        cpu = new CPU(memory, true); // true enables debug mode
    }

    public void run() {
        cpu.start();
    }
}
