import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ClickListener extends MouseAdapter {
    private final MouseAdapter adapter;
    private final int clickCount;

    public ClickListener(MouseAdapter adapter, int clickCount, int mask) {
        this.adapter = adapter;
        this.clickCount = clickCount;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == clickCount) {
            adapter.mouseClicked(e);
        }
    }
}
