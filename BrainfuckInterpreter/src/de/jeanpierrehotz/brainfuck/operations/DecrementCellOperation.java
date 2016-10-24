package de.jeanpierrehotz.brainfuck.operations;

import de.jeanpierrehotz.brainfuck.Memory;

public class DecrementCellOperation extends Operation {

    @Override
    public void operate(Memory m) {
        m.decrementCell();
    }

    @Override
    public String toString() {
        return "-\t" + super.toString();
    }
    
}
