import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;

public class clickEvent extends MouseAdapter {
    private JButton[][] buttons;
    private int row, col;
    private Color lightGreen = new Color(200, 240, 100);
    private Color darkGreen = new Color(190, 230, 90);
    private final Color lightGreenHover = new Color(200, 240, 150);
    private final Color darkGreenHover = new Color(200, 230, 135);

    public clickEvent(JButton[][] buttons, int row, int col) {
        this.buttons = buttons;
        this.row = row;
        this.col = col;
    }

    @Override
    public void mouseEntered(MouseEvent evt) {
        if (buttons[row][col].getBackground().equals(lightGreen)) {
            buttons[row][col].setBackground(lightGreenHover);
        } else if (buttons[row][col].getBackground().equals(darkGreen)) {
            buttons[row][col].setBackground(darkGreenHover);
        }
    }

    @Override
    public void mouseExited(MouseEvent evt) {
        if (buttons[row][col].getBackground().equals(lightGreen)) {
            buttons[row][col].setBackground(lightGreen);
        } else if (buttons[row][col].getBackground().equals(darkGreen)) {
            buttons[row][col].setBackground(darkGreen);
        }
    }
}
