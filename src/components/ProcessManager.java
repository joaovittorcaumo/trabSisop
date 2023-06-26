package components;

import domain.ProcessStatus;
import domain.Word;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static utils.MemoryTranslator.translate;

public class ProcessManager {

    private static ProcessManager instance = null;
    private static int processCounter = 0;

    public MemoryManager memoryManager;
    public List<ProcessControlBlock> allProcesses;
    public Queue<ProcessControlBlock> readyProcesses;
    public Queue<ProcessControlBlock> blockedProcesses;

    // Singleton pattern to ensure only one instance of ProcessManager
    public static synchronized ProcessManager getInstance() {
        if (instance == null)
            instance = new ProcessManager();

        return instance;
    }

    private ProcessManager() {
        this.memoryManager = new MemoryManager(VM.memory);
        this.allProcesses = new LinkedList<>();
        this.readyProcesses = new LinkedList<>();
        this.blockedProcesses = new LinkedList<>();
    }

    // Check if any process is running
    public boolean someProcessIsRunning() {
        return this.allProcesses.stream()
                .anyMatch(process -> process.getProcessStatus() == ProcessStatus.RUNNING);
    }

    // Load a program into memory
    public ProcessControlBlock loadProgram(Word[] program) {
        int requiredFrames = (int) Math.ceil((double) program.length / VM.memory.pageSize);
        int[] pageTable = new int[requiredFrames];
        boolean canLoad = this.memoryManager.allocate(program.length, pageTable);

        if (!canLoad) {
            System.out.println("Insufficient memory to load the program");
            return null;
        }

        for (int i = 0; i < program.length; i++) {
            int physicalAddress = translate(i, pageTable, VM.memory.pageSize);
            VM.memory.memoryArray[physicalAddress].opCode = program[i].opCode;
            VM.memory.memoryArray[physicalAddress].r1 = program[i].r1;
            VM.memory.memoryArray[physicalAddress].r2 = program[i].r2;
            VM.memory.memoryArray[physicalAddress].p = program[i].p;
        }

        ProcessControlBlock processControlBlock = new ProcessControlBlock(nextProcessId(), pageTable,
                ProcessStatus.READY, 0, new int[10]);
        this.allProcesses.add(processControlBlock);
        this.readyProcesses.add(processControlBlock);
        return processControlBlock;
    }

    // Deallocate memory for a process
    public void deallocate(ProcessControlBlock processControlBlock) {
        memoryManager.deallocate(processControlBlock.getPageTable());
        allProcesses.remove(processControlBlock);
        readyProcesses.remove(processControlBlock);
    }

    // Generate a new process ID
    private int nextProcessId() {
        return processCounter++;
    }
}
