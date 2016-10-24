

import java.awt.Button;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Label;
import java.awt.Scrollbar;
import java.awt.TextArea;
import java.awt.TextField;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
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

public class BrainFuckVisualizer extends Frame {
    
    /*
     * Programs to test:
     *      Fibonacci-Reihe berechnen (R4):
     *      +.>+.<[[>>+>+<<<-]>[>+<-]>>[<<+>>-]<[<<+>>-]<<.]
     *      
     *      Zähler (RN):
     *      >+[[>]<[[>+<-]>+<<]>+]
     *      
     *      Zwei eingegebene Zahlen addieren (R3):
     *      ,>,<[>>+<<-]>[>+<-]>.
     *      
     *      Zwei eingegebene Zahlen mutliplizieren (R4):
     *      ,>,<[>[>+>+<<-]>>[<<+>>-]<<<-]>>.
     *      
     *      Das Quadrat einer eingegebenen Zahl berechnen (R4):
     *      ,[>+>+<<-]>>[<<+>>-]<<[>[>+>+<<-]>>[<<+>>-]<<<-]>>.
     *      
     *      Eine unterbewusste Nachricht als ID eines HTML-Tags (R5):
     *      ++++++++++[>+++++++>++++++++++>+++>+<<<<-]>++.>+.+++++++..+++.>++.<<+++++++++++++++.>.+++.------.--------.>+.>.
     *      Output is respectively: "Hello World!\n" with the single characters being decoded into numbers
     *      
     */
    
    private static final int    CELLS_MARGIN_TOP            = 40;
    private static final int    CELLS_MARGIN_LEFT           = 40;
    private static final int    CELLS_MARGIN_RIGHT          = 15;
    private static final int    CELLS_INDICATORLENGTH       = 25;
    private static final int    CELLS_FITTING_IN_ROW        = 8;
    private static final int    CELLS_HEIGHT_PER_ROW        = 30;
    private static final int    CELLS_MARGIN_BETWEEN_ROW    = 10;
    private static final int    CELLS_INNERMARGIN           = (CELLS_HEIGHT_PER_ROW - 10) / 2;
    private static final Color  CELLS_MARK_COLOR            = Color.ORANGE;
    private static final int    COMPONENT_MARGINS           = 15;
    private static final int    COMPONENT_NORMAL_HEIGHT     = 25;
    private static final int    COMPONENT_BTN_WIDTH         = 80;
    private static final int    COMPONENT_OUTPUT_HEIGHT     = 200;
    private static final int    COMPONENT_DELAYLABEL_WIDTH  = 80;
        
    private boolean started;
    private boolean paused;
    private boolean userInputFired;
    
    private int doubleBufferingWidth;
    private int doubleBufferingHeight;
    
    private int selected;
    private int[] cells;
    
    private Image dbImage;
    private Graphics dbg;
    
    private TextField userInputTextField;
    private TextField codeInputTextField;
    private TextArea outputTextField;
    private TextField cellsTextField;
    
    private Button terminateStartBtn;
    private Button pauseResumeBtn;
    
    private Scrollbar delayScrollBar;
    
    private Label userInputLabel;
    private Label codeInputLabel;
    private Label outputLabel;
    private Label cellsLabel;
    private Label delayLabel;

    private BrainFuckProgram currentProgram;
        
    private BrainFuckProgramInterface programInterface 	= new BrainFuckProgramInterface() {
        @Override
        public void updateStorage(Memory m) {
            codeInputTextField.requestFocus();
            codeInputTextField.setSelectionStart(0);
            codeInputTextField.setSelectionStart(m.getNextInstructionIndex());
            codeInputTextField.setSelectionEnd(m.getNextInstructionIndex() + 1);
            
            cells = m.getStorageCells();
            selected = m.getDPTR();
            
            repaint();
        }
        @Override
        public void updateError(Memory m, String error) {
            terminateProgram(error);
        }
        @Override
        public void updateEnded(Memory m) {
            terminateProgram("The program has ended");
        }
    };
    private PrintValueOperation printOperation 	        = new PrintValueOperation() {
        @Override
        public void operate(Memory m) {
            outputTextField.append("\n" + m.getStorageCells()[m.getDPTR()]);
        }
    };
    private ActionListener userInputListener            = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if(userInputTextField.isEditable()) {
                userInputFired = true;
                System.out.println("User input fired.");
            }
        }
    };
    private StoreValueOperation storeOperation 	        = new StoreValueOperation() {
        public void operate(Memory m) {
            outputTextField.append("\nPlease put a number.");
            userInputTextField.setEditable(true);
            while(!userInputFired) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            
            int value = 0;
            try {
                value = Integer.parseInt(userInputTextField.getText().trim());
            }catch(Exception exc) {}
            
            m.getStorageCells()[m.getDPTR()] = value;
            userInputFired = false;
            userInputTextField.setEditable(false);
            userInputTextField.setText("");
        }
    };
    private ActionListener startTerminateListener       = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if(started) {
                terminateProgram("You terminated the program.");
            }else {
                startProgram();
            }
        }
    };
    private ActionListener pauseResumeListener          = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if(!started) return;
            
            if(paused) {
                paused = false;
                pauseResumeBtn.setLabel("Pause");
                
                currentProgram.resume();
            }else {
                paused = true;
                pauseResumeBtn.setLabel("Resume");

                currentProgram.pause();
            }
        }
    };
    private AdjustmentListener delayListener            = new AdjustmentListener() {
        @Override
        public void adjustmentValueChanged(AdjustmentEvent e) {
            delayLabel.setText(delayScrollBar.getValue() + " ms");
            
            if(started) {
                currentProgram.setDelay(delayScrollBar.getValue());
            }
        }
    };
    
    
    public BrainFuckVisualizer() {
        super("Brainfuck Visualizer.");
        
        Compiler.setPrintValueOperation(printOperation);
        Compiler.setStoreValueOperation(storeOperation);
        
        cells = new int[0];
        selected = -1;
        
        started = false;
        paused = false;
        
        userInputFired = false;
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if(started) {
                    currentProgram.terminate();
                }
                System.exit(0);
            }
        });
        
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                repaint();
                assignLayout();
            }
        });
        
        setLayout(null);
        
        terminateStartBtn = new Button("Start");
        terminateStartBtn.addActionListener(startTerminateListener);
        add(terminateStartBtn);
        
        pauseResumeBtn = new Button("Pause");
        pauseResumeBtn.addActionListener(pauseResumeListener);
        add(pauseResumeBtn);
        
        delayScrollBar = new Scrollbar(Scrollbar.HORIZONTAL, 100, 1, 1, 1001);
        delayScrollBar.addAdjustmentListener(delayListener);
        add(delayScrollBar);
        
        delayLabel = new Label("100 ms");
        add(delayLabel);
        
        userInputLabel = new Label("Input:");
        add(userInputLabel);
        
        userInputTextField = new TextField();
        userInputTextField.addActionListener(userInputListener);
        userInputTextField.setEditable(false);
        add(userInputTextField);
        
        cellsLabel = new Label("Number of cells:");
        add(cellsLabel);
        
        cellsTextField = new TextField();
        add(cellsTextField);
        
        codeInputLabel = new Label("Code:");
        add(codeInputLabel);
        
        codeInputTextField = new TextField();
        codeInputTextField.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 15));
        add(codeInputTextField);
        
        outputLabel = new Label("Output:");
        add(outputLabel);
        
        outputTextField = new TextArea();
        outputTextField.setEditable(false);
        add(outputTextField);
        
        setSize(1000, 800);
        setVisible(true);
        
        assignLayout();
    }
    
    private void assignLayout() {
        terminateStartBtn.setBounds(
                COMPONENT_MARGINS,
                getHeight() - COMPONENT_MARGINS * 4 - COMPONENT_OUTPUT_HEIGHT - COMPONENT_NORMAL_HEIGHT * 6,
                COMPONENT_BTN_WIDTH,
                COMPONENT_NORMAL_HEIGHT
        );
        
        pauseResumeBtn.setBounds(
                COMPONENT_MARGINS * 2 + COMPONENT_BTN_WIDTH,
                getHeight() - COMPONENT_MARGINS * 4 - COMPONENT_OUTPUT_HEIGHT - COMPONENT_NORMAL_HEIGHT * 6,
                COMPONENT_BTN_WIDTH,
                COMPONENT_NORMAL_HEIGHT
        );
        
        delayScrollBar.setBounds(
                2 * COMPONENT_BTN_WIDTH + 3 * COMPONENT_MARGINS,
                getHeight() - COMPONENT_MARGINS * 4 - COMPONENT_OUTPUT_HEIGHT - COMPONENT_NORMAL_HEIGHT * 6,
                getWidth() - 5 * COMPONENT_MARGINS - 2 * COMPONENT_BTN_WIDTH - COMPONENT_DELAYLABEL_WIDTH,
                COMPONENT_NORMAL_HEIGHT
        );
        delayLabel.setBounds(
                getWidth() - COMPONENT_MARGINS - COMPONENT_DELAYLABEL_WIDTH,
                getHeight() - COMPONENT_MARGINS * 4 - COMPONENT_OUTPUT_HEIGHT - COMPONENT_NORMAL_HEIGHT * 6,
                COMPONENT_DELAYLABEL_WIDTH,
                COMPONENT_NORMAL_HEIGHT
        );
        
        userInputLabel.setBounds(
                COMPONENT_MARGINS,
                getHeight() - COMPONENT_MARGINS * 3 - COMPONENT_OUTPUT_HEIGHT - COMPONENT_NORMAL_HEIGHT * 5,
                (getWidth() - 3 * COMPONENT_MARGINS) / 2,
                COMPONENT_NORMAL_HEIGHT
        );
        userInputTextField.setBounds(
                COMPONENT_MARGINS,
                getHeight() - COMPONENT_MARGINS * 3 - COMPONENT_OUTPUT_HEIGHT - COMPONENT_NORMAL_HEIGHT * 4,
                (getWidth() - 3 * COMPONENT_MARGINS) / 2,
                COMPONENT_NORMAL_HEIGHT
        );
        
        cellsLabel.setBounds(
                COMPONENT_MARGINS * 2 + (getWidth() - 3 * COMPONENT_MARGINS) / 2,
                getHeight() - COMPONENT_MARGINS * 3 - COMPONENT_OUTPUT_HEIGHT - COMPONENT_NORMAL_HEIGHT * 5,
                (getWidth() - 3 * COMPONENT_MARGINS) / 2,
                COMPONENT_NORMAL_HEIGHT
        );
        cellsTextField.setBounds(
                COMPONENT_MARGINS * 2 + (getWidth() - 3 * COMPONENT_MARGINS) / 2,
                getHeight() - COMPONENT_MARGINS * 3 - COMPONENT_OUTPUT_HEIGHT - COMPONENT_NORMAL_HEIGHT * 4,
                (getWidth() - 3 * COMPONENT_MARGINS) / 2,
                COMPONENT_NORMAL_HEIGHT
        );
        
        codeInputLabel.setBounds(
                COMPONENT_MARGINS,
                getHeight() - COMPONENT_MARGINS * 2 - COMPONENT_OUTPUT_HEIGHT - COMPONENT_NORMAL_HEIGHT * 3,
                getWidth() - 2 * COMPONENT_MARGINS,
                COMPONENT_NORMAL_HEIGHT
        );
        codeInputTextField.setBounds(
                COMPONENT_MARGINS,
                getHeight() - COMPONENT_MARGINS * 2 - COMPONENT_OUTPUT_HEIGHT - COMPONENT_NORMAL_HEIGHT * 2,
                getWidth() - 2 * COMPONENT_MARGINS,
                COMPONENT_NORMAL_HEIGHT
        );
        
        outputLabel.setBounds(
                COMPONENT_MARGINS, 
                getHeight() - COMPONENT_MARGINS - COMPONENT_OUTPUT_HEIGHT - COMPONENT_NORMAL_HEIGHT,
                getWidth() - 2 * COMPONENT_MARGINS,
                COMPONENT_NORMAL_HEIGHT
        );
        outputTextField.setBounds(
                COMPONENT_MARGINS, 
                getHeight() - COMPONENT_MARGINS - COMPONENT_OUTPUT_HEIGHT,
                getWidth() - 2 * COMPONENT_MARGINS,
                COMPONENT_OUTPUT_HEIGHT
        );
    }
    
    private void terminateProgram(String msg) {
        if(started) {
            started = false;
            paused = false;
            
            terminateStartBtn.setLabel("Start");
            codeInputTextField.setEditable(true);
            currentProgram.terminate();
            outputTextField.append("\n" + msg);
        }
    }
    
    private void startProgram() {
        try {
            currentProgram = new BrainFuckProgram(Integer.parseInt(cellsTextField.getText()), codeInputTextField.getText());
        } catch (NumberFormatException | CompilationError e1) {
            outputTextField.setText(e1.getMessage());
            return;
        }
        
        currentProgram.setProgramInterface(programInterface);
        currentProgram.execute(delayScrollBar.getValue());

        started = true;
        paused = false;

        outputTextField.setText(".");
        outputTextField.setText("");
        
        terminateStartBtn.setLabel("Terminate");
        codeInputTextField.setEditable(false);
    }
    
    @Override
    public void paint(Graphics g) {
        if(doubleBufferingWidth != getWidth() || doubleBufferingHeight != getHeight()){
            doubleBufferingWidth = getWidth();
            doubleBufferingHeight = getHeight();
            dbImage = null;
        }
        
        int width = (getWidth() - CELLS_MARGIN_LEFT - CELLS_MARGIN_RIGHT) / CELLS_FITTING_IN_ROW;
        
        for(int i = 0; i < cells.length; i++) {
            /*
             * Draw the current cells number at every new line
             */
            if(i % CELLS_FITTING_IN_ROW == 0) {
                g.drawString(
                        "" + (i + 1), 
                        CELLS_MARGIN_LEFT - CELLS_INDICATORLENGTH, 
                        CELLS_MARGIN_TOP + ((i / CELLS_FITTING_IN_ROW) + 1) * CELLS_HEIGHT_PER_ROW + (i / CELLS_FITTING_IN_ROW) * CELLS_MARGIN_BETWEEN_ROW
                );
            }
            
            /*
             * mark the selected cell
             */
            if(i == selected) {
                Color defColor = g.getColor();
                g.setColor(CELLS_MARK_COLOR);
                
                g.fillRect(
                        CELLS_MARGIN_LEFT + (i % CELLS_FITTING_IN_ROW) * width,
                        CELLS_MARGIN_TOP + (i / CELLS_FITTING_IN_ROW) * (CELLS_HEIGHT_PER_ROW + CELLS_MARGIN_BETWEEN_ROW),
                        width,
                        CELLS_HEIGHT_PER_ROW
                );
                
                g.setColor(defColor);
            }
            
            /*
             * draw every cell with its value
             */
            g.drawRect(
                    CELLS_MARGIN_LEFT + (i % CELLS_FITTING_IN_ROW) * width,
                    CELLS_MARGIN_TOP + (i / CELLS_FITTING_IN_ROW) * (CELLS_HEIGHT_PER_ROW + CELLS_MARGIN_BETWEEN_ROW),
                    width,
                    CELLS_HEIGHT_PER_ROW
            );
            g.drawString(
                    "" + cells[i],
                    CELLS_MARGIN_LEFT + (i % CELLS_FITTING_IN_ROW) * width + CELLS_INNERMARGIN,
                    CELLS_MARGIN_TOP + (i / CELLS_FITTING_IN_ROW) * (CELLS_HEIGHT_PER_ROW + CELLS_MARGIN_BETWEEN_ROW) + CELLS_INNERMARGIN + 10
            );
        }
    }

    @Override
    public void update(Graphics g){
        if(dbImage == null){
            dbImage = createImage(getWidth(), getHeight());
            dbg = dbImage.getGraphics();
        }
        dbg.setColor(getBackground());
        dbg.fillRect(0, 0, getWidth(), getHeight());
        
        dbg.setColor(getForeground());
        paint(dbg);
        
        g.drawImage(dbImage, 0, 0, this);
    }

    public static void main(String[] args) {
        new BrainFuckVisualizer();
    }
}
