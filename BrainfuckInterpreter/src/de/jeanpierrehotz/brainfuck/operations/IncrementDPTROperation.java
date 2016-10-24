package de.jeanpierrehotz.brainfuck.operations;

import de.jeanpierrehotz.brainfuck.Memory;

public class IncrementDPTROperation extends Operation {

    @Override
    public void operate(Memory m) {
        m.incrementDPTR();
    }

    @Override
    public String toString() {
        return ">\t" + super.toString();
    }
    
}
