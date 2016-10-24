package de.jeanpierrehotz.brainfuck.operations;

import de.jeanpierrehotz.brainfuck.Memory;

public class EndLoopOperation extends Operation {
    
    private int mLevel;
    
    public int getLevel() {
        return mLevel;
    }
    
    public EndLoopOperation(int level) {
        mLevel = level;
    }

    @Override
    public void operate(Memory m) {
        if(!m.currentCellIsZero()) {
            Operation[] prog = m.getProgram();
            int i = m.getNextInstructionIndex();
            
//            while(!(prog[i] instanceof OpenLoopOperation) && ((OpenLoopOperation) prog[i]).getLevel() == mLevel) i--;
            
            boolean found = false;
            
            while(!found) {
                i--;
                if(prog[i] instanceof OpenLoopOperation) {
                    if(((OpenLoopOperation) prog[i]).getLevel() == mLevel){
                        found = true;
                    }
                }
            }
            
            m.setNextInstruction(i);
        }
    }
    
    @Override
    public String toString() {
        return "]\t" + super.toString();
    }
    
}
