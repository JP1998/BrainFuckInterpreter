package de.jeanpierrehotz.brainfuck;

import de.jeanpierrehotz.brainfuck.compiler.CompilationError;
import de.jeanpierrehotz.brainfuck.operations.PrintValueOperation;
import de.jeanpierrehotz.brainfuck.operations.StoreValueOperation;

public class BrainFuckProgram {
    
    private Memory m;
    private BrainFuckProgramInterface programInterface;
    private int delay;

    public BrainFuckProgram(int cells, String code) throws CompilationError {
        m = new Memory(cells, de.jeanpierrehotz.brainfuck.compiler.Compiler.compile(code));
    }
    
    public BrainFuckProgram(int cells, String code, PrintValueOperation printOp, StoreValueOperation storeOp) throws CompilationError {
        m = new Memory(cells, de.jeanpierrehotz.brainfuck.compiler.Compiler.compile(code));
    }
        
    public void setDelay(int del) {
        delay = del;
    }
    
    public void setProgramInterface(BrainFuckProgramInterface interf) {
        programInterface = interf;
    }
    
    public void terminate() {
        m.end();
    }
    private boolean paused = false;
    public void pause() {
        paused = true;
    }
    public void resume() {
        paused = false;
    }
    
    public void execute(int del) {
        delay = del;
        new Thread(() -> {
            while(!m.hasEnded()) {
                try {
                    m.getNextInstruction().operate(m);
                    m.setNextInstruction();
                }catch(ExecutionError err) {
                    notifyOfError(err);
                    return;
                }
                
                do {
                    try {
                        Thread.sleep(delay);
                    }catch(Exception exc) {}
                }while(paused);
                
                if(programInterface != null)
                    programInterface.updateStorage(m);
            }
            
            if(programInterface != null)
                programInterface.updateEnded(m);
        }).start();
    }
    
    private void notifyOfError(ExecutionError error) {
        m.end();
        
        if(programInterface != null)
            programInterface.updateError(m, error.getMessage());
    }
}
