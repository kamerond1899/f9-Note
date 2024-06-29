import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class CustomFontRenderer extends DefaultTableCellRenderer {
    private Font customFont;

    public CustomFontRenderer(Font font) {
        this.customFont = font;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        c.setFont(customFont);
        return c;
    }
}
