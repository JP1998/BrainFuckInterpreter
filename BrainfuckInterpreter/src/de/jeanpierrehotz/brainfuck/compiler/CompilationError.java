package de.jeanpierrehotz.brainfuck.compiler;

public class CompilationError extends Exception{

    public CompilationError(String message) {
        super(message);
    }
    
}
