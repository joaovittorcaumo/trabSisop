package handlers;

import components.*;
import domain.*;

public class SysCallHandling {

    private final CPU cpu;
    private final ProcessManager processManager;

    public SysCallHandling(CPU cpu) {
        this.cpu = cpu;
        this.processManager = ProcessManager.getInstance();
    }

    // Handle system calls
    public void handle(ProcessControlBlock processBlock) {
        int operationCode = cpu.registers[8];
        int memoryAddress = cpu.registers[9];

        // Invalid operation code
        if (operationCode != 1 && operationCode != 2) {
            cpu.interrupt = Interrupts.intInstrucaoInvalida;
            return;
        }

        // If CPU can access memory
        if (cpu.canAccessMemory(memoryAddress)) {
            // Process requires IO, add to blocked queue
            processBlock.setProcessStatus(ProcessStatus.BLOCKED);
            processManager.blockedProcesses.add(processBlock);

            // Request IO
            IOType requestType = operationCode == 1 ? IOType.READ : IOType.WRITE;
            IORequest request = new IORequest(requestType, memoryAddress);
            Console.ioRequests.add(request);
            Console.consoleSemaphore.release();

            // If there are ready processes and scheduler is not running, release scheduler
            if (Scheduler.schedulerSemaphore.availablePermits() == 0
                    && !processManager.readyProcesses.isEmpty()) {
                Scheduler.schedulerSemaphore.release();
            }
        }
        System.out.println(
                "                                               System Call with op  /  par:  "
                        + cpu.registers[8] + " / " + cpu.registers[9]);
    }
}
