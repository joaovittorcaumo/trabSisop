package handlers;

import components.CPU;
import components.ProcessControlBlock;
import components.ProcessManager;
import components.Scheduler;
import domain.Interrupts;
import domain.ProcessStatus;

public class InterruptHandling {

    private final ProcessManager processManager;
    private final CPU cpu;

    public InterruptHandling(CPU cpu) {
        this.processManager = ProcessManager.getInstance();
        this.cpu = cpu;
    }

    // Handle different types of interrupts
    public void handle(Interrupts interrupt, int programCounter, ProcessControlBlock processBlock) {
        System.out.println(
                "                                               Interrupt " + interrupt + "   pc: " + programCounter);
        switch (interrupt) {
            case clockInterrupt -> handleClock(programCounter, processBlock);
            case intSTOP -> handleStop(programCounter, processBlock);
            case ioPronto -> handleIoReady();
        }
    }

    // Handle IO ready interrupt
    public void handleIoReady() {
        ProcessControlBlock processBlock = processManager.blockedProcesses.poll();
        assert processBlock != null;
        processBlock.setProcessStatus(ProcessStatus.READY);
        processManager.readyProcesses.add(processBlock);

        // If the only ready process is the one that returned from IO and nothing is
        // running
        if (Scheduler.schedulerSemaphore.availablePermits() == 0 && !processManager.someProcessIsRunning()) {
            Scheduler.schedulerSemaphore.release();
        }
    }

    // Handle clock interrupt
    public void handleClock(int programCounter, ProcessControlBlock processBlock) {
        cpu.updatePCB(processBlock, 0);
        processBlock.setProcessStatus(ProcessStatus.READY);
        processManager.readyProcesses.add(processBlock);
        cpu.resetClockCycles();
        if (Scheduler.schedulerSemaphore.availablePermits() == 0 && !processManager.readyProcesses.isEmpty()) {
            Scheduler.schedulerSemaphore.release();
        }
    }

    // Handle stop interrupt
    public void handleStop(int programCounter, ProcessControlBlock processBlock) {
        processBlock.setProcessStatus(ProcessStatus.FINISHED);
        this.processManager.deallocate(processBlock);
        if (Scheduler.schedulerSemaphore.availablePermits() == 0 && !processManager.readyProcesses.isEmpty()) {
            Scheduler.schedulerSemaphore.release();
        }
    }
}
