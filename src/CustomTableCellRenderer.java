import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class CustomTableCellRenderer extends DefaultTableCellRenderer {
    private JComboBox<String> fontSelector;
    private JComboBox<String> fontSizeSelector;

    public CustomTableCellRenderer(JComboBox<String> fontSelector, JComboBox<String> fontSizeSelector) {
        this.fontSelector = fontSelector;
        this.fontSizeSelector = fontSizeSelector;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        String selectedFont = (String) fontSelector.getSelectedItem();
        String selectedFontSize = (String) fontSizeSelector.getSelectedItem();

        if (selectedFont != null && selectedFontSize != null) {
            c.setFont(new Font(selectedFont, Font.PLAIN, Integer.parseInt(selectedFontSize)));
        }
        
        return c;
    }
}
