import java.io.Serializable;

public class TabState implements Serializable {
    private static final long serialVersionUID = 1L;
    public String type;  // NOTE, TODO, SPREADSHEET, etc.
    public String content;  // The text content of the tab
    // Add other fields as needed
}
