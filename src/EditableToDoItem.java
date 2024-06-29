
import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

// New class for editable to-do items
class EditableToDoItem extends JPanel {
    private JCheckBox checkBox;
    private JTextField textField;

 public EditableToDoItem(String text) {
        setLayout(new BorderLayout());
        checkBox = new JCheckBox();
        textField = new JTextField(text);
        add(checkBox, BorderLayout.WEST);
        add(textField, BorderLayout.CENTER);

        textField.setEditable(false);
    }

    public void handleMouseEvent(MouseEvent e) {
        Rectangle checkBoxBounds = checkBox.getBounds();
        Rectangle textFieldBounds = textField.getBounds();

        if (checkBoxBounds.contains(e.getPoint())) {
            checkBox.setSelected(!checkBox.isSelected());
        } else if (textFieldBounds.contains(e.getPoint())) {
            textField.setEditable(true);
            textField.requestFocus();
        }
    }

    public boolean isChecked() {
        return checkBox.isSelected();
    }

    public void setChecked(boolean checked) {
        checkBox.setSelected(checked);
    }

    public String getText() {
        return textField.getText();
    }

    public void setText(String text) {
        textField.setText(text);
    }
    
    public void setEditable(boolean editable) {
    textField.setEditable(editable);
    if (editable) {
        textField.requestFocus();
    }
}
}