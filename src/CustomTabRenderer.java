
import java.awt.Color;
import java.awt.Component;
import java.util.HashMap;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import java.awt.Color;
import java.awt.Component;
import java.util.HashMap;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

class CustomTabRenderer extends JLabel implements ListCellRenderer<Object> {
    private HashMap<Integer, Color> tabColors = new HashMap<>();

    public void setTabColor(int index, Color color) {
        getTabColors().put(index, color);
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        setText(value.toString());
        Color color = getTabColors().get(index);
        if (color != null) {
            setBackground(color);
            setOpaque(true);  // Make sure it's opaque to show the background color

            // Calculate the luminance of the background color to decide on the text color
            double luminance = 0.299 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue();

            // If the background color is dark, set the text to white. Otherwise, set it to black.
            if (luminance < 128) {
                setForeground(Color.WHITE);
            } else {
                setForeground(Color.BLACK);
            }
        } else {
            setOpaque(false);
            setForeground(Color.BLACK);  // Default text color
        }
        return this;
    }

    public HashMap<Integer, Color> getTabColors() {
        return tabColors;
    }

    public void setTabColors(HashMap<Integer, Color> tabColors) {
        this.tabColors = tabColors;
    }
}
