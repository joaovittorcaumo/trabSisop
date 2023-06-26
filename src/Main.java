import components.Console;
import components.Scheduler;
import components.VM;

public class Main {
    public static void main(String[] args) {
        VM vm = new VM();
        Shell shell = new Shell();
        Scheduler scheduler = new Scheduler();
        Console console = new Console(vm.cpu);

        vm.run();
        shell.start();
        scheduler.start();
        console.start();
    }
}
