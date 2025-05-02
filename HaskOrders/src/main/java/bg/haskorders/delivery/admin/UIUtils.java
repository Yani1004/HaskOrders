package bg.haskorders.delivery.admin;

import javax.swing.*;
import java.awt.*;

public class UIUtils {
    public static JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        return button;
    }
}