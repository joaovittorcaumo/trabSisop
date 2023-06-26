import components.*;
import domain.ProcessStatus;
import executable.Programas;

import java.util.*;

import static utils.MemoryTranslator.getEndFrame;
import static utils.MemoryTranslator.getStartFrame;

public class Shell extends Thread {
    static ProcessManager processManager = ProcessManager.getInstance();
    private Scanner in;

    public Shell() {
        this.in = new Scanner(System.in);
    }

    @Override
    public void run() {
        super.run();
        System.out.printf("%-60s %s\n", "---------------------------------------------------",
                "---------------------------------------------------");
        System.out.printf("%-60s %s\n", "--------------------Program Names------------------",
                "----------------------Instructions-----------------");
        System.out.printf("%-60s %s\n", "1. fatorial", "1. cria <nomeDoPrograma>");
        System.out.printf("%-60s %s\n", "2. progMinimo", "2. listaProcessos");
        System.out.printf("%-60s %s\n", "3. fibonacci10", "3. dump <id>");
        System.out.printf("%-60s %s\n", "4. fatorialTRAP", "4. desaloca <id>");
        System.out.printf("%-60s %s\n", "5. fibonacciTRAP", "5. dumpM <inicio, fim>");
        System.out.printf("%-60s %s\n", "6. PB", "6. traceOn");
        System.out.printf("%-60s %s\n", "7. PC", "7. traceOff");
        System.out.printf("%-60s %s\n", "8. paIn", "8. exit");
        System.out.printf("%-60s\n", "9. pbOutput");
        System.out.printf("%-60s %s\n", "---------------------------------------------------",
                "---------------------------------------------------");

        while (true) {
            String readLine = in.nextLine();
            String command = readLine.split(" ")[0];
            List<String> programNames = Arrays.asList("fatorial", "progMinimo", "fibonacci10", "fatorialTRAP",
                    "fibonacciTRAP", "PB", "PC", "paInput", "pbOutput");
            switch (command) {
                case "cria":
                    String programName = readLine.split(" ")[1];
                    if (!programNames.contains(programName)) {
                        System.out.println("Invalid program name.");
                    } else {
                        int processId = cria(programName);
                        System.out.println("Processo " + processId + " criado");
                    }
                    break;

                case "listaProcessos":
                    listProcessos();
                    break;

                case "dump":
                    int idInt = Integer.parseInt(readLine.split(" ")[1]);
                    dump(idInt);
                    break;

                case "desaloca":
                    int idDesaloca = Integer.parseInt(readLine.split(" ")[1]);
                    desaloca(idDesaloca);
                    break;

                case "dumpM":
                    int start = Integer.parseInt(readLine.split(" ")[1]);
                    int end = Integer.parseInt(readLine.split(" ")[2]);
                    dumpM(start, end);
                    break;

                case "traceOn":
                    traceOn();
                    break;

                case "traceOff":
                    traceOff();
                    break;

                case "exit":
                    System.out.println("Ending system");
                    System.exit(0);
                    in.close();
                    break;

                default:
                    System.out.println("Invalid command");
                    break;
            }
        }
    }

    static int cria(String nomePrograma) {
        Programas programas = new Programas();
        ProcessControlBlock processControlBlock = switch (nomePrograma) {
            case "fatorial" -> processManager.loadProgram(programas.fatorial);
            case "progMinimo" -> processManager.loadProgram(programas.progMinimo);
            case "fibonacci10" -> processManager.loadProgram(programas.fibonacci10);
            case "fatorialTRAP" -> processManager.loadProgram(programas.fatorialTRAP);
            case "fibonacciTRAP" -> processManager.loadProgram(programas.fibonacciTRAP);
            case "PB" -> processManager.loadProgram(programas.PB);
            case "PC" -> processManager.loadProgram(programas.PC);
            case "paInput" -> processManager.loadProgram(programas.paInput);
            case "pbOutput" -> processManager.loadProgram(programas.pbOutput);
            default -> null;
        };

        if (processControlBlock == null) {
            System.out.println("Nome do programa inválido, tente novamente");
            return 0;
        }

        if (Scheduler.schedulerSemaphore.availablePermits() == 0 && !processManager.someProcessIsRunning()) {
            Scheduler.schedulerSemaphore.release();
        }

        return processControlBlock.getProcessId();
    }

    static void listProcessos() {
        System.out.println("\nID \t| Status dos processos");
        processManager.allProcesses
                .forEach(p -> System.out.println(p.getProcessId() + "\t| " + p.getProcessStatus()));
    }

    static void dump(int processId) {
        ProcessControlBlock processControlBlock = processManager.allProcesses.stream()
                .filter(p -> p.getProcessId() == processId).findFirst().orElse(null);
        if (processControlBlock == null) {
            System.out.println("Processo com id" + processId + " não encontrado");
            return;
        }

        System.out.println("\nPCB para processo " + processId);
        System.out.println("pid \t | pc \t | status \t | page table \t | registers");
        System.out.println(processControlBlock);

        System.out.println("\nDump de memória para processo " + processId);

        int pageSize = VM.memory.pageSize;
        int[] pageTable = processControlBlock.getPageTable();

        for (int i = 0; i < pageTable.length; i++) {
            int frame = pageTable[i];
            System.out.println("\n página " + i + " frame " + frame);
            VM.memory.dump(getStartFrame(frame, pageSize), getEndFrame(frame, pageSize) + 1);
        }
    }

    static void desaloca(int processId) {
        ProcessControlBlock processControlBlock = processManager.allProcesses.stream()
                .filter(p -> p.getProcessId() == processId).findFirst().orElse(null);
        if (processControlBlock == null) {
            System.out.println("Processo com id" + processId + " não encontrado");
            return;
        }

        processManager.deallocate(processControlBlock);
    }

    static void dumpM(int inicio, int fim) {
        VM.memory.dump(inicio, fim);
    }

    static void traceOn() {
        CPU.setDebug(true);
    }

    static void traceOff() {
        CPU.setDebug(false);
    }
}