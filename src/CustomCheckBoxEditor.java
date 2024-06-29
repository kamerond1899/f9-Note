import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CustomCheckBoxEditor extends AbstractCellEditor implements TableCellEditor {
    private CustomCheckBoxRenderer renderer;
    public Boolean currentVal;

    public CustomCheckBoxEditor(CustomCheckBoxRenderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        if (value instanceof Boolean) {
            currentVal = (Boolean) value;
        } else if (value instanceof String) {
            currentVal = Boolean.valueOf((String) value);
        }
        
        
        renderer.getTableCellRendererComponent(table, value, isSelected, true, row, column);
        
        // Update the icon based on the current value
        if (currentVal) {
            renderer.setIcon(renderer.getIconCheck());
        } else {
            renderer.setIcon(renderer.getIconUncheck());
        }
        
        // Update the background color to match the selection state
        if (isSelected) {
            renderer.setBackground(table.getSelectionBackground());
        } else {
            renderer.setBackground(table.getBackground());
        }

        return renderer;
    }

    @Override
    public Object getCellEditorValue() {
        return currentVal;
    }

    @Override
    public boolean stopCellEditing() {
        fireEditingStopped();
        return true;
    }
}
