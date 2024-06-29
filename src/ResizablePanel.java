
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;


public class ResizablePanel extends JPanel {
    private Point initialClick;
    private Component parentComponent;

    public ResizablePanel(Component parentComponent) {
        this.parentComponent = parentComponent;
        setLayout(new BorderLayout());
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
                getComponentAt(initialClick);
            }
        });
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int thisWidth = parentComponent.getWidth();
                int thisHeight = parentComponent.getHeight();

                if (parentComponent != null) {
                    int xMoved = e.getX() - initialClick.x;
                    int yMoved = e.getY() - initialClick.y;

                    int newWidth = thisWidth + xMoved;
                    int newHeight = thisHeight + yMoved;
                    parentComponent.setSize(newWidth, newHeight);
                    setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
                }
            }
        });
    }
}