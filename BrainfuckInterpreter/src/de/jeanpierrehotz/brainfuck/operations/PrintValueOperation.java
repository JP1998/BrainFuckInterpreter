package de.jeanpierrehotz.brainfuck.operations;

import de.jeanpierrehotz.brainfuck.Memory;

public abstract class PrintValueOperation extends Operation {
    
    public static class DefaultVersion extends PrintValueOperation{
        @Override
        public void operate(Memory m) {
            m.printValue();
        }
    }

    @Override
    public String toString() {
        return ".\t" + super.toString();
    }
    
}
