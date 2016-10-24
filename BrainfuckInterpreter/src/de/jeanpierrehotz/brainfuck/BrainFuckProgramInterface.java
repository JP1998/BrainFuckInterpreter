package de.jeanpierrehotz.brainfuck;

public interface BrainFuckProgramInterface {
    void updateStorage(Memory m);
    void updateEnded(Memory m);
    void updateError(Memory m, String error);
}
