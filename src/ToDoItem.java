public class ToDoItem {
    private String text;
    private boolean checked;

    public ToDoItem(String text) {
        this.text = text;
        this.checked = false;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
