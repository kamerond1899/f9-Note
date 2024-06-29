import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class WordWrapCellRenderer extends JTextArea implements TableCellRenderer {

    public WordWrapCellRenderer() {
        setLineWrap(true);
        setWrapStyleWord(true);
        setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        setText((String) value);
        setFont(table.getFont());
        
        if (isSelected) {
            setBackground(table.getSelectionBackground());
            setForeground(table.getSelectionForeground());
        } else {
            setBackground(table.getBackground());
            setForeground(table.getForeground());
        }

        // Adjust row height to accommodate potentially multi-line text
        int columnWidth = table.getColumnModel().getColumn(column).getWidth();
        setSize(columnWidth, Short.MAX_VALUE);
        int prefHeight = getPreferredSize().height;
        if (table.getRowHeight(row) != prefHeight) {
            table.setRowHeight(row, prefHeight);
        }

        return this;
    }
}
