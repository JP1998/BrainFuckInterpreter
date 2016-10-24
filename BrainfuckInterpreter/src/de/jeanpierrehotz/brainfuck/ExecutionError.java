package de.jeanpierrehotz.brainfuck;

public class ExecutionError extends RuntimeException{
    public ExecutionError(String string) {
        super(string);
    }
}
