package de.jeanpierrehotz.brainfuck.operations;

import de.jeanpierrehotz.brainfuck.Memory;

public class DecrementDPTROperation extends Operation {

    @Override
    public void operate(Memory m) {
        m.decrementDPTR();
    }
    
    @Override
    public String toString() {
        return "<\t" + super.toString();
    }
    
}
