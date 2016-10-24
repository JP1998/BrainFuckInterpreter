import java.awt.Button;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import de.jeanpierrehotz.brainfuck.BrainFuckProgram;
import de.jeanpierrehotz.brainfuck.BrainFuckProgramInterface;
import de.jeanpierrehotz.brainfuck.Memory;
import de.jeanpierrehotz.brainfuck.compiler.CompilationError;
import de.jeanpierrehotz.brainfuck.compiler.Compiler;
import de.jeanpierrehotz.brainfuck.operations.PrintValueOperation;
import de.jeanpierrehotz.brainfuck.operations.StoreValueOperation;

public class BrainFuckMessageGenerator extends Frame {

    private static final int    COMPONENT_MARGIN_TOP        = 40;
    private static final int    COMPONENT_MARGINS           = 15;
    private static final int    COMPONENT_NORMAL_HEIGHT     = 25;
    private static final int    COMPONENT_BTN_WIDTH         = 120;
    private static final int    CHECKBOX_WIDTH              = 125;
    
    
    private int[] mCells;
    private int mPointer;
    
    private BrainFuckProgram mMessageProgram;
    private BrainFuckProgramInterface mMessageProgramInterface = new BrainFuckProgramInterface() {
        @Override
        public void updateStorage(Memory m) {}
        @Override
        public void updateError(Memory m, String error) {
            // Show the error
            mMessageLabel.setText(error);
            setComponentsEnablement(true);
        }
        @Override
        public void updateEnded(Memory m) {
            // Show the end
            mMessageLabel.setText("Generation of the message succeeded.");
            setComponentsEnablement(true);
        }
    };
    private PrintValueOperation mPrintValueOperation = new PrintValueOperation() {
        @Override
        public void operate(Memory m) {
            // Convert number to output to char and print to textfield
            mMessageTextField.setText(mMessageTextField.getText() + "" + ((char) m.getStorageCells()[m.getDPTR()]));
        }
    };
    private StoreValueOperation mStoreValueOperation = new StoreValueOperation() {
        @Override
        public void operate(Memory m) {
            // End program and print error that input is not possible for message-programs
            mMessageProgram.terminate();
            mMessageLabel.setText("You cannot have any input-operations in a message-program!");
        }
    };
    
    private CheckboxGroup mGenerationCheckBoxGroup;
    
    private Checkbox mGenerateCodeCheckBox;
    private Checkbox mGenerateMessageCheckBox;
    
    private Button mStartGenerationButton;
    
    private TextField mCodeTextField;
    private TextField mMessageTextField;
    
    private Label mCodeTextFieldLabel;
    private Label mMessageTextFieldLabel;
    
    private Label mMessageLabel;
    
    private ActionListener mStartGenerationActionListener = (e) -> {
        setComponentsEnablement(false);
        
        if(mGenerateCodeCheckBox.getState()) {
            mCodeTextField.setText(generateCode(mMessageTextField.getText()));
            mMessageLabel.setText("Generation of the code succeeded.");
            
            setComponentsEnablement(true);
        }else {
            mMessageTextField.setText("");
            try {
                mMessageProgram = new BrainFuckProgram(7, mCodeTextField.getText());
                mMessageProgram.setProgramInterface(mMessageProgramInterface);
                mMessageProgram.execute(0);
            } catch (CompilationError e1) {
                // e1.printStackTrace();
                mMessageLabel.setText("Compilation error: " + e1.getMessage());
                setComponentsEnablement(true);
            }
        }
    };
    
    public BrainFuckMessageGenerator() {
        super("Decode and encode messages in Brainfuck-code");
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if(mMessageProgram != null) mMessageProgram.terminate();
                BrainFuckMessageGenerator.this.dispose();
            }
        });
        
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                assignLayout();
            }
        });
        
        Compiler.setPrintValueOperation(mPrintValueOperation);
        Compiler.setStoreValueOperation(mStoreValueOperation);

        setLayout(null);
        
        mGenerationCheckBoxGroup = new CheckboxGroup();
        
        mGenerateCodeCheckBox = new Checkbox("Generate code", true, mGenerationCheckBoxGroup);
        add(mGenerateCodeCheckBox);
        
        mGenerateMessageCheckBox = new Checkbox("Generate message", false, mGenerationCheckBoxGroup);
        add(mGenerateMessageCheckBox);
        
        mStartGenerationButton = new Button("Start generation");
        mStartGenerationButton.addActionListener(mStartGenerationActionListener);
        add(mStartGenerationButton);
        
        mCodeTextFieldLabel = new Label("Code:");
        add(mCodeTextFieldLabel);
        
        mCodeTextField = new TextField();
        add(mCodeTextField);
        
        mMessageTextFieldLabel = new Label("Message:");
        add(mMessageTextFieldLabel);
        
        mMessageTextField = new TextField();
        add(mMessageTextField);

        mMessageLabel = new Label("Ready");
        add(mMessageLabel);
        
        
        setMinimumSize(new Dimension(4 * COMPONENT_MARGINS + 2 * CHECKBOX_WIDTH + COMPONENT_BTN_WIDTH, COMPONENT_MARGIN_TOP + 6 * COMPONENT_NORMAL_HEIGHT + 3 * COMPONENT_MARGINS));
        // setSize(new Dimension(4 * COMPONENT_MARGINS + 2 * CHECKBOX_WIDTH + COMPONENT_BTN_WIDTH, COMPONENT_MARGIN_TOP + 6 * COMPONENT_NORMAL_HEIGHT + 3 * COMPONENT_MARGINS));
        setVisible(true);
        
        assignLayout();
    }
    
    private void assignLayout() {
        mGenerateCodeCheckBox.setBounds(COMPONENT_MARGINS, COMPONENT_MARGIN_TOP, CHECKBOX_WIDTH, COMPONENT_NORMAL_HEIGHT);
        mGenerateMessageCheckBox.setBounds(COMPONENT_MARGINS * 2 + CHECKBOX_WIDTH, COMPONENT_MARGIN_TOP, CHECKBOX_WIDTH, COMPONENT_NORMAL_HEIGHT);
        
        mStartGenerationButton.setBounds(getWidth() - COMPONENT_MARGINS - COMPONENT_BTN_WIDTH, COMPONENT_MARGIN_TOP, COMPONENT_BTN_WIDTH, COMPONENT_NORMAL_HEIGHT);
        
        
        mCodeTextFieldLabel.setBounds(COMPONENT_MARGINS, COMPONENT_MARGIN_TOP + COMPONENT_NORMAL_HEIGHT + COMPONENT_MARGINS, getWidth() - 2 * COMPONENT_MARGINS, COMPONENT_NORMAL_HEIGHT);
        mCodeTextField.setBounds(COMPONENT_MARGINS, COMPONENT_MARGIN_TOP + 2 * COMPONENT_NORMAL_HEIGHT + COMPONENT_MARGINS, getWidth() - 2 * COMPONENT_MARGINS, COMPONENT_NORMAL_HEIGHT);
        
        mMessageTextFieldLabel.setBounds(COMPONENT_MARGINS, COMPONENT_MARGIN_TOP + 3 * COMPONENT_NORMAL_HEIGHT + 2 * COMPONENT_MARGINS, getWidth() - 2 * COMPONENT_MARGINS, COMPONENT_NORMAL_HEIGHT);
        mMessageTextField.setBounds(COMPONENT_MARGINS, COMPONENT_MARGIN_TOP + 4 * COMPONENT_NORMAL_HEIGHT + 2 * COMPONENT_MARGINS, getWidth() - 2 * COMPONENT_MARGINS, COMPONENT_NORMAL_HEIGHT);
        
        mMessageLabel.setBounds(COMPONENT_MARGINS, getHeight() - COMPONENT_NORMAL_HEIGHT - COMPONENT_MARGINS, getWidth() - 2 * COMPONENT_MARGINS, COMPONENT_NORMAL_HEIGHT);
    }
    
    private void setComponentsEnablement(boolean state) {
        mGenerateCodeCheckBox.setEnabled(state);
        mGenerateMessageCheckBox.setEnabled(state);
        
        mStartGenerationButton.setEnabled(state);
        
        mCodeTextField.setEditable(state);
        mMessageTextField.setEditable(state);
    }
    
    /**
     * This method generated a R7-Brainfuck program decoding the given message
     * @param msg The message to decode
     * @return the program that equals the decoded message
     */
    private String generateCode(String msg) {
        mCells = new int[7];
        mPointer = 0;
        
        // Since we only want to 
        for(int i = 0; i < mCells.length; i++) {
            mCells[i] = i * 20;
        }
        
        String code = "++++++++++++++++++++[>+>++>+++>++++>+++++>++++++<<<<<<-]";
        
        for(int i = 0; i < msg.length(); i++) {
            char toDecode = msg.charAt(i);
            
            int desiredCell = ((int) toDecode) / 20;
            
            if(desiredCell > 6)
                desiredCell = 6;
            
            while(mPointer != desiredCell) {
                if(mPointer < desiredCell) {
                    mPointer++;
                    code += ">";
                }else {
                    mPointer--;
                    code += "<";
                }
            }
            
            while(mCells[mPointer] != ((int) toDecode)) {
                if(mCells[mPointer] < ((int) toDecode)) {
                    mCells[mPointer]++;
                    code += "+";
                }else {
                    mCells[mPointer]--;
                    code += "-";
                }
            }
            
            code += ".";
        }
        
        return code;
    }
    
    public static void main(String[] args) {
        new BrainFuckMessageGenerator();
    }

}
