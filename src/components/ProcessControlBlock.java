package components;

import domain.ProcessStatus;

import java.util.Arrays;

public class ProcessControlBlock {
    int processId;
    int[] pageTable;

    int programCounter;

    int[] registerValues;

    int clockCycleCount;

    ProcessStatus processStatus;

    // Constructor for the ProcessControlBlock
    public ProcessControlBlock(int processId, int[] pageTable, ProcessStatus processStatus, int programCounter,
            int[] registerValues) {
        this.processId = processId;
        this.pageTable = pageTable;
        this.processStatus = processStatus;
        this.programCounter = programCounter;
        this.registerValues = registerValues;
    }

    // Getter and setter methods for the class variables
    public int getClockCycleCount() {
        return clockCycleCount;
    }

    public void setClockCycleCount(int clockCycleCount) {
        this.clockCycleCount = clockCycleCount;
    }

    public int getProcessId() {
        return processId;
    }

    public int[] getPageTable() {
        return pageTable;
    }

    public int getProgramCounter() {
        return programCounter;
    }

    public ProcessStatus getProcessStatus() {
        return processStatus;
    }

    public void setProcessId(int processId) {
        this.processId = processId;
    }

    public void setPageTable(int[] pageTable) {
        this.pageTable = pageTable;
    }

    public void setProcessStatus(ProcessStatus processStatus) {
        this.processStatus = processStatus;
    }

    public void setProgramCounter(int programCounter) {
        this.programCounter = programCounter;
    }

    public void setRegisterValues(int[] registerValues) {
        this.registerValues = registerValues;
    }

    // Overriding the toString method to provide a custom string representation of
    // the object
    @Override
    public String toString() {
        return processId + "\t\t | " + programCounter + "\t | " + processStatus + "\t | " + Arrays.toString(pageTable)
                + "\t\t\t | " + Arrays.toString(registerValues);
    }
}
