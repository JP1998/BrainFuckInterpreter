package de.jeanpierrehotz.brainfuck;

import java.util.Scanner;

import de.jeanpierrehotz.brainfuck.operations.Operation;

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
public class Memory {
    private int[]       mMemoryCells;
    private int         mDPTR;
    
    public int[] getStorageCells() {
        return mMemoryCells;
    }
    
    public int getDPTR() {
        return mDPTR;
    }
    
    private Operation[] mCurrentProgram;
    private int         mNextInstruction;
    
    private boolean hasEnded;
    public boolean hasEnded() {
        return hasEnded;
    }
    
    public Memory(int cells, Operation[] program) {
        mMemoryCells = new int[cells];
        mDPTR = 0;
        mCurrentProgram = program;
        mNextInstruction = 0;
        hasEnded = false;
    }
    
    public void end() {
        hasEnded = true;
    }
    
    public void incrementDPTR() {
        mDPTR++;
        if(mDPTR >= mMemoryCells.length)
            throw new ExecutionError("Datapointer is out of range!");
    }
    
    public void decrementDPTR() {
        mDPTR--;
        if(mDPTR < 0)
            throw new ExecutionError("Datapointer is out of range!");
    }
    
    public void incrementCell() {
        mMemoryCells[mDPTR]++;
    }
    
    public void decrementCell() {
        mMemoryCells[mDPTR]--;
    }
    
    public void printValue() {
        System.out.println(mMemoryCells[mDPTR]);
    }
    
    Scanner in = new Scanner(System.in);
    
    public void storeInput() {
        mMemoryCells[mDPTR] = in.nextInt();
    }
    
    public boolean currentCellIsZero() {
        return mMemoryCells[mDPTR] == 0;
    }
    
    public void setNextInstruction(int nextInstr) {
        mNextInstruction = nextInstr;
        if(nextInstr >= mCurrentProgram.length || nextInstr < 0)
            throw new ExecutionError("PSW has non-sensical content: " + nextInstr + " Max.: " + (mCurrentProgram.length - 1));
    }
    
    public Operation[] getProgram() {
        return mCurrentProgram;
    }
    
    public Operation getNextInstruction() {
        return mCurrentProgram[mNextInstruction];
    }
    
    public int getNextInstructionIndex() {
        return mNextInstruction;
    }
    
    public void setNextInstruction() {
        mNextInstruction++;
        if(mCurrentProgram.length <= mNextInstruction)
            hasEnded = true;
    }
    
    public void printStorage() {
        String storage = (mDPTR == 0)? "|_": "| ";
        for(int i = 0; i < mMemoryCells.length; i++) {
            if(mDPTR == i)
                storage += mMemoryCells[i] + "_| ";
            else if(mDPTR == i + 1)
                storage += mMemoryCells[i] + " |_";
            else
                storage += mMemoryCells[i] + " | ";
        }
        System.out.println(storage);
    }
}
