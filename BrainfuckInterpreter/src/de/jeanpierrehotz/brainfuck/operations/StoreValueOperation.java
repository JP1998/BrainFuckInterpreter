package de.jeanpierrehotz.brainfuck.operations;

import de.jeanpierrehotz.brainfuck.Memory;

public abstract class StoreValueOperation extends Operation {

    public static class DefaultVersion extends StoreValueOperation{
        @Override
        public void operate(Memory m) {
            m.storeInput();
        }
    }

    @Override
    public String toString() {
        return ",\t" + super.toString();
    }
    
}
