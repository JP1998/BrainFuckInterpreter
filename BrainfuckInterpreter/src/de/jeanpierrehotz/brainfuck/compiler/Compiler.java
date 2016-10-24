package de.jeanpierrehotz.brainfuck.compiler;

import java.util.ArrayList;

import de.jeanpierrehotz.brainfuck.operations.*;

/* 
 * Implementation of the programming language "Brainfuck":
 *  There is a datapointer pointing at a data cell, storing each a number
 *  Instruction set:
 *      
 *      >       increment data pointer so it points at the next cell
 *      <       decrement data pointer so it points at the prev cell
 *      +       increment cell at the data pointer
 *      -       decrement cell at the data pointer
 *      .       print the value of the cell at the data pointer
 *      ,       store an input value into the cell at the data pointer
 *      [       begin of an loop goes in if the cell at the data pointer is not zero go in
 *              otherwise jumps to its end
 *      ]       end of an loop jumps to its beginning if the cell at the data pointer is not zero
 *              otherwise goes out of the loop
 * 
 */

public class Compiler {
    
    private Compiler() {}
    
    private static PrintValueOperation mPrintValueOperation;
    public static void setPrintValueOperation(PrintValueOperation op) {
        mPrintValueOperation = op;
    }
    public static void deletePrintValueOperation() {
        mPrintValueOperation = null;
    }
    
    private static StoreValueOperation mStoreValueOperation;
    public static void setStoreValueOperation(StoreValueOperation op) {
        mStoreValueOperation = op;
    }
    public static void deleteStoreValueOperation() {
        mStoreValueOperation = null;
    }
    
    public static Operation[] compile(String code) throws CompilationError {
        ArrayList<Operation> compiledProgram = new ArrayList<>();
        int level = 0;
        
        for(int i = 0; i < code.length(); i++) {
            switch(code.charAt(i)) {
                case '>':
                    compiledProgram.add(new IncrementDPTROperation());
                    break;
                case '<':
                    compiledProgram.add(new DecrementDPTROperation());
                    break;
                case '+':
                    compiledProgram.add(new IncrementCellOperation());
                    break;
                case '-':
                    compiledProgram.add(new DecrementCellOperation());
                    break;
                case '.':
                    if(mPrintValueOperation == null) {
                        compiledProgram.add(new PrintValueOperation.DefaultVersion());
                    }else {
                        compiledProgram.add(mPrintValueOperation);
                    }
                    break;
                case ',':
                    if(mStoreValueOperation == null) {
                        compiledProgram.add(new StoreValueOperation.DefaultVersion());
                    }else {
                        compiledProgram.add(mStoreValueOperation);
                    }
                    break;
                case '[':
                    compiledProgram.add(new OpenLoopOperation(level++));
                    break;
                case ']':
                    compiledProgram.add(new EndLoopOperation(--level));
                    if(level < 0)
                        throw new CompilationError("Compilation Error at sign: '" + code.charAt(i) + "' (Index: " + (i + 1) + "). Too many closing brackets.");
                    break;
                default:
                    if(!Character.isWhitespace(code.charAt(i)))
                        throw new CompilationError("Compilation Error at sign: '" + code.charAt(i) + "' (Index: " + (i + 1) + "). No resolved symbol.");
            }
        }
        
        if(level > 0)
            throw new CompilationError("Compilation Error. Too few closing brackets detected");
        
        Operation[] returnValue = new Operation[compiledProgram.size()];
        for(int i = 0; i < compiledProgram.size(); i++) {
            returnValue[i] = compiledProgram.get(i);
        }
        
        return returnValue;
    }
}
