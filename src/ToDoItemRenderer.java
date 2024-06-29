
import java.awt.Component;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class ToDoItemRenderer extends JCheckBox implements ListCellRenderer<ToDoItem> {
    @Override
    public Component getListCellRendererComponent(JList<? extends ToDoItem> list, ToDoItem value, int index, boolean isSelected, boolean cellHasFocus) {
        setText(value.getText());
        setSelected(value.isChecked());
        setFont(list.getFont());
        setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
        setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
        return this;
    }
}
