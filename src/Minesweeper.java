import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.ArrayList;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;

public class Minesweeper {
    private JFrame frame;
    private static JButton[][] buttons;
    final Font font = new Font("Helvetica", Font.BOLD, 24);
    final Color lightGreen = new Color(200, 240, 100);
    final Color darkGreen = new Color(190, 230, 90);
    final Color lightGreenHover = new Color(200, 240, 150);
    final Color darkGreenHover = new Color(200, 230, 135);
    final Color lightBeige = new Color(230, 200, 160);
    final Color darkBeige = new Color(215, 185, 155);
    final Color bannerGreen = new Color(74, 117, 44);
    private static Clip clip;
    int mineCount = 10;
    JLabel minesLeftLbl;
    static ArrayList<String> mines = new ArrayList<>();
    static ArrayList<String> emptyTiles = new ArrayList<>();
    final ImageIcon flagIcon = new ImageIcon("res//flag.png");
    final ImageIcon mineIcon = new ImageIcon("res//mine.png");
    static Map<String, Integer> tileVals = new HashMap<>();

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Minesweeper window = new Minesweeper();
                    window.frame.setVisible(true);
                    window.frame.setTitle("Minesweeper");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public Minesweeper() {
        initialize();
    }

    private void initialize() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {e.printStackTrace();}

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        // Calculate the center coordinates
        int centerX = (screenSize.width - 604) / 2;
        int centerY = (screenSize.height - 558) / 2;

        frame = new JFrame();
        frame.setBounds(centerX, centerY, 604, 558);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());

        // Initialize the buttons array
        buttons = new JButton[8][10];

        JPanel gridPanel = new JPanel(new GridLayout(8, 10)); // Add 1-pixel gap between buttons
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 10; j++) {
                buttons[i][j] = new JButton();
                buttons[i][j].setOpaque(true);
                buttons[i][j].setFocusable(false);
                buttons[i][j].setBorderPainted(false);
                buttons[i][j].setFocusable(false);
                buttons[i][j].setFont(font);
                setAlternatingColours(i, j);
                gridPanel.add(buttons[i][j]);
            }
        }
        frame.getContentPane().add(gridPanel, BorderLayout.CENTER);
        
        JPanel panel = new JPanel();
        panel.setBackground(bannerGreen);
        frame.getContentPane().add(panel, BorderLayout.NORTH);
        
        minesLeftLbl = new JLabel(Integer.toString(mineCount));
        minesLeftLbl.setFont(new Font("Tahoma", Font.PLAIN, 40));
        minesLeftLbl.setHorizontalAlignment(SwingConstants.CENTER);
        minesLeftLbl.setBackground(bannerGreen);
        panel.add(minesLeftLbl, BorderLayout.LINE_END);
        
        JMenuBar menuBar = new JMenuBar();
        JMenu optionMenu = new JMenu("Options");
        JMenuItem resetItem = new JMenuItem("Reset");
        JMenuItem exitItem = new JMenuItem("Exit");
        
        resetItem.addActionListener(e -> reset());
        exitItem.addActionListener(e -> System.exit(0));
        
        optionMenu.add(resetItem);
        optionMenu.addSeparator();
        optionMenu.add(exitItem);
        menuBar.add(optionMenu);
        
        frame.setJMenuBar(menuBar);
        /*frame.getContentPane().add(minesLeftLbl, BorderLayout.NORTH);*/

        int[][] board = generateMinesweeperBoard(8, 10, 10);
        printBoard(board);
    }
    
    private void reset(){
    	clip.stop();
    	mineCount = 10;
    	minesLeftLbl.setText(Integer.toString(mineCount));

        mines.clear();
        tileVals.clear();
        int[][] board = generateMinesweeperBoard(8, 10, 10);
        printBoard(board);
        
    	for (JButton[] items : buttons) {
    	    for (JButton button : items) {
    	    	button.setEnabled(true);
    	    	button.setText("");
    	    	button.setIcon(null);
    	    }
    	}
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 10; col++) {
                setAlternatingColours(row, col);
            }
        }
    }
    
    private void setAlternatingColours(int row, int col) {
        final int i = row;
        final int j = col;
        buttons[row][col].addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    buttonClick(i, j, 'R');
                } else if (SwingUtilities.isLeftMouseButton(e)) {
                	buttonClick(i, j, 'L');
                }
            }
        });
        if ((row + col) % 2 == 0) {
            buttons[i][j].setBackground(lightGreen);
            buttons[i][j].addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent evt) {
                    buttons[row][col].setBackground((buttons[row][col].getText().isEmpty()) ? lightGreenHover : lightBeige);
                }

                public void mouseExited(MouseEvent evt) {
                    buttons[row][col].setBackground((buttons[row][col].getText().isEmpty()) ? lightGreen : lightBeige);

                }
            });
        } else {
            buttons[i][j].setBackground(darkGreen);
            buttons[i][j].addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent evt) {
                    buttons[row][col].setBackground((buttons[row][col].getText().isEmpty()) ? darkGreenHover : darkBeige);
                }

                public void mouseExited(MouseEvent evt) {
                    buttons[row][col].setBackground((buttons[row][col].getText().isEmpty()) ? darkGreen : darkBeige);

                }
            });
        }
    }
    
    // method generated by chatGPT
    private void checkIfWon() {
        for (int i = 0; i < buttons.length; i++) {
            for (int j = 0; j < buttons[i].length; j++) {
            	// check if cell is mine
                if (tileVals.get(Integer.toString(i) + Integer.toString(j)) != -1) {
                    // Check if the cell is not revealed
                    if (buttons[i][j].getIcon() == null && buttons[i][j].getText().isEmpty()) {
                        return;
                    }
                }
            }
        }
        // If all safe cells are revealed, let player receive win
        /*playSound("sfx//win-theme.wav");
        minesLeftLbl.setText("Victory");*/
        finished('W');
        JOptionPane.showMessageDialog(null, "You've cleared the Ho Chi Minh Trail, enabling villagers, farmers, and soldiers to utilize it effectively. This helped in pushing out American forces from our land. Thank you for your efforts - Ho Chi Minh", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void finished(char outcome) {
    	playSound((outcome == 'W') ? "sfx//win-theme.wav" : "sfx//explosion.wav");
    	minesLeftLbl.setText((outcome == 'W') ? "Victory" : "Defeat");
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 10; c++) {
            	MouseListener[] mouseListeners = buttons[r][c].getMouseListeners();
            	for(MouseListener listener : mouseListeners) {
            	    buttons[r][c].removeMouseListener(listener);
            	}
            }
        }

        if(outcome == 'L') {
            for(String place : mines) {
            	int r = Character.getNumericValue(place.charAt(0));
            	int c = Character.getNumericValue(place.charAt(1));
            	for(int count = 0; count < mines.size(); count++) {
            		buttons[r][c].setText(null);
            		buttons[r][c].setIcon((buttons[r][c].getIcon() == flagIcon) ? flagIcon : mineIcon);
            	}
            }
	        try {
				Thread.sleep(7000);
				playSound("sfx//lose-theme.wav");
			} catch (InterruptedException e) {e.printStackTrace();}
        }
    }
    
    private void buttonClick(int row, int column, char type) {
    	if(type == 'L') {
    		checkIfWon();
    		int val = tileVals.get(Integer.toString(row) + Integer.toString(column));
			buttons[row][column].setText(Integer.toString(val));
			buttons[row][column].setBackground(((row+column) % 2 == 0) ? lightBeige : darkBeige);
			
    		switch(val) {
    		
    		case -1: finished('L'); JOptionPane.showMessageDialog(null, "You have failed to clear out the Ho Chi Minh trail. As a result, a landmine exploded you 7 metres into the air. \n"
    				+ "South Vietnam (USA) has conquered and unified the entirety of Vietnam. Vietnam (now called Indochina) is now \n"
    				+ "under the ruling of an American puppet. Your family's saftey is unknown.", "Failed", JOptionPane.ERROR_MESSAGE); break;
    		case 0:
    			revealSweetSpot(row, column);
    			playSound("sfx//sweet spot.wav");
    			break;
    			
    		case 1: playSound("sfx//1 tile unhide.wav"); break;
    		case 2: playSound("sfx//2 tile unhide.wav"); break;
    		case 3: playSound("sfx//3 tile unhide.wav"); break;
    		case 4: playSound("sfx//4 tile unhide.wav"); break;
    		case 5: playSound("sfx//5 tile unhide.wav"); break;
    		case 6: playSound("sfx//6 tile unhide.wav"); break;
    		case 7: playSound("sfx//7 tile unhide.wav"); break;
    		case 8: playSound("sfx//8 tile unhide.wav"); break;
    		}
    	}
    	else if(type == 'R') {
    		if((buttons[row][column].getIcon() == null) && (buttons[row][column].getText() == "")) {
                buttons[row][column].setIcon(flagIcon);
        		minesLeftLbl.setText(Integer.toString(mineCount-=1));
                playSound("sfx//put flag.wav");
                buttons[row][column].removeActionListener(null);
            }
            else if((buttons[row][column].getIcon() == flagIcon)) {
            	buttons[row][column].setIcon(null);
        		minesLeftLbl.setText(Integer.toString(mineCount+=1));
            	playSound("sfx//pull flag.wav");
                buttons[row][column].setEnabled(true);
            }
    	}
    	checkIfWon();
    }
    
    // method generated by chatGPT
    private void revealSweetSpot(int row, int column) {
        // Check and reveal the neighboring cells within a 2x2 grid
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                int newRow = row + dr;
                int newCol = column + dc;
                if (isValidCell(newRow, newCol)) {
                    // Skip revealing the number if a flag is present
                    if (buttons[newRow][newCol].getIcon() != flagIcon) {
                        int val = tileVals.get(Integer.toString(newRow) + Integer.toString(newCol));
                        if (val != -1) { // Check if it's not a mine
                            if (val == 0 && !buttons[newRow][newCol].getText().equals("0")) {
                                buttons[newRow][newCol].setText("0");
                                buttons[newRow][newCol].setIcon(null);
                                buttons[newRow][newCol].setBackground(((newRow+newCol) % 2 == 0) ? lightBeige : darkBeige);
                                revealSweetSpot(newRow, newCol); // recursion 0_0
                                
                            } else if (val > 0 && !buttons[newRow][newCol].getText().equals(Integer.toString(val))) {
                                buttons[newRow][newCol].setText(Integer.toString(val));
                                buttons[newRow][newCol].setIcon(null);
                                buttons[newRow][newCol].setBackground(((newRow+newCol) % 2 == 0) ? lightBeige : darkBeige);
                            }
                            buttons[newRow][newCol].setIcon(null);
                        }
                    }
                }
            }
        }
    }
    // method generated by chatGPT
    private boolean isValidCell(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 10;
    }

    // method generated by chatGPT
    public static int[][] generateMinesweeperBoard(int rows, int cols, int numMines) {
        int[][] board = new int[rows][cols];

        // Initialize the board with 0s
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                board[r][c] = 0;
            }
        }

        // Randomly place mines
        Random rand = new Random();
        int minesPlaced = 0;
        while (minesPlaced < numMines) {
            int r = rand.nextInt(rows);
            int c = rand.nextInt(cols);
            if (board[r][c] != -1) {
                board[r][c] = -1;
                minesPlaced++;
            }
        }

        // Calculate numbers for non-mine cells
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (board[r][c] != -1) {
                    for (int dr = -1; dr <= 1; dr++) {
                        for (int dc = -1; dc <= 1; dc++) {
                            if (r + dr >= 0 && r + dr < rows && c + dc >= 0 && c + dc < cols && board[r + dr][c + dc] == -1) {
                                board[r][c]++;
                            }
                        }
                    }
                }
            }
        }
        return board;
    }
    
    // method generated by chatGPT
    public static void printBoard(int[][] board) {
        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board[r].length; c++) {
            	if(board[r][c] == -1) {
            		mines.add(Integer.toString(r) + Integer.toString(c));
            	}
            		tileVals.put(Integer.toString(r) + Integer.toString(c), board[r][c]);
            }
        }
    }
    public static void playSound(String soundFilePath) {
	    try {
	        File soundFile = new File(soundFilePath);
	        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile);
	        AudioFormat format = audioInputStream.getFormat();
	        DataLine.Info info = new DataLine.Info(Clip.class, format);
	        clip = (Clip) AudioSystem.getLine(info);
	        clip.open(audioInputStream);
	        clip.start();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
    }
}