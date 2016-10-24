package de.jeanpierrehotz.brainfuck.operations;

import de.jeanpierrehotz.brainfuck.Memory;

public class IncrementCellOperation extends Operation {

    @Override
    public void operate(Memory m) {
        m.incrementCell();
    }
    
    @Override
    public String toString() {
        return "+\t" + super.toString();
    }
    
}
