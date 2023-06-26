package components;

import domain.IORequest;
import domain.Interrupts;
import domain.Opcode;
import domain.Word;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

public class Console extends Thread {
    public static Semaphore consoleSemaphore = new Semaphore(0);
    public static Queue<IORequest> ioRequests = new LinkedList<>();

    private CPU cpu;
    private ProcessManager processManager;

    Scanner inputScanner;

    public Console(CPU cpu) {
        this.cpu = cpu;
        this.processManager = ProcessManager.getInstance();
        this.inputScanner = new Scanner(System.in);
    }

    @Override
    public void run() {
        super.run();

        while (true) {
            try {
                consoleSemaphore.acquire();
                IORequest currentRequest = ioRequests.poll();
                switch (Objects.requireNonNull(currentRequest).getType()) {
                    case READ -> this.read(currentRequest);
                    case WRITE -> this.write(currentRequest);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void read(IORequest currentRequest) {
        System.out.println("(waiting for keyboard input)");
        int result = Integer.parseInt(inputScanner.next());
        VM.memory.memoryArray[currentRequest.getMemoryAddress()] = new Word(Opcode.DATA, -1, -1, result);
        signalIOCompletion();
    }

    private void write(IORequest currentRequest) {
        Word result = VM.memory.memoryArray[currentRequest.getMemoryAddress()];
        System.out.println(result.p);
        signalIOCompletion();
    }

    private void signalIOCompletion() {
        this.cpu.interrupt = Interrupts.ioPronto;
        if (!processManager.someProcessIsRunning()) {
            cpu.checkInterruption(null);
        }
    }
}
