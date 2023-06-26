package components;

import domain.Opcode;
import domain.Word;

public class Memory {
    public int memorySize;
    public int pageSize;

    public int numberOfFrames;
    public Word[] memoryArray;

    public Memory(int size, int pageSize) {
        this.memorySize = size;
        this.pageSize = pageSize;
        this.numberOfFrames = size / pageSize;
        this.memoryArray = new Word[memorySize];
        for (int i = 0; i < memorySize; i++) {
            memoryArray[i] = new Word(Opcode.___, -1, -1, -1);
        }
    }

    public void dump(Word word) {
        System.out.print("[ ");
        System.out.print(word.opCode);
        System.out.print(", ");
        System.out.print(word.r1);
        System.out.print(", ");
        System.out.print(word.r2);
        System.out.print(", ");
        System.out.print(word.p);
        System.out.println("  ] ");
    }

    public void dump(int start, int end) {
        for (int i = start; i < end; i++) {
            System.out.print(i);
            System.out.print(":  ");
            dump(memoryArray[i]);
        }
    }
}
