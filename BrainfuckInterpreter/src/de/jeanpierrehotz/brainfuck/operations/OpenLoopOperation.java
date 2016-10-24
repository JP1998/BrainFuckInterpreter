package de.jeanpierrehotz.brainfuck.operations;

import de.jeanpierrehotz.brainfuck.Memory;

public class OpenLoopOperation extends Operation {

    private int mLevel;
    
    public int getLevel() {
        return mLevel;
    }
    
    public OpenLoopOperation(int level) {
        mLevel = level;
    }
    
    @Override
    public void operate(Memory m) {
        if(m.currentCellIsZero()) {
            Operation[] prog = m.getProgram();
            int i = m.getNextInstructionIndex();
            
//            while(!(prog[i] instanceof EndLoopOperation) && ((EndLoopOperation) prog[i]).getLevel() == mLevel) i++;
            
            boolean found = false;
            
            while(!found) {
                i++;
                if(prog[i] instanceof EndLoopOperation) {
                    if(((EndLoopOperation) prog[i]).getLevel() == mLevel){
                        found = true;
                    }
                }
            }
            
            m.setNextInstruction(i);
        }
    }

    @Override
    public String toString() {
        return "[\t" + super.toString();
    }
    
}
