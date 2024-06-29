import com.formdev.flatlaf.ui.FlatTabbedPaneUI;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import javax.swing.JComponent;

public class CustomFlatLafTabbedPaneUI extends FlatTabbedPaneUI {

    private ArrayList<Color> tabColors;
    private int hoveredTabIndex = -1;
  private MouseAdapter mouseAdapter;
   private MouseMotionAdapter mouseMotionAdapter;


    public CustomFlatLafTabbedPaneUI(ArrayList<Color> tabColors) {
        this.tabColors = tabColors;
    }

    @Override
    public void installUI(JComponent c) {
        super.installUI(c);
        mouseMotionAdapter = new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                hoveredTabIndex = tabPane.indexAtLocation(e.getX(), e.getY());
                tabPane.repaint();
            }
        };
        c.addMouseMotionListener(mouseMotionAdapter);

        mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                hoveredTabIndex = -1;
                tabPane.repaint();
            }
        };
        c.addMouseListener(mouseAdapter);
    }

    @Override
    public void uninstallUI(JComponent c) {
        c.removeMouseMotionListener(mouseMotionAdapter);
        c.removeMouseListener(mouseAdapter);
        super.uninstallUI(c);
    }
    
    
    
@Override
protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
    Color color = tabColors.size() > tabIndex ? tabColors.get(tabIndex) : super.tabPane.getBackground();

    // Calculate the average brightness of the color
    int brightness = (color.getRed() + color.getGreen() + color.getBlue()) / 3;

    if (tabIndex == hoveredTabIndex) {
        if (brightness <= 127 && brightness > 63) {
            // Darken the color when it's a dark color
            int brighterRed = Math.min(color.getRed() + 25, 255);
            int brighterGreen = Math.min(color.getGreen() + 25, 255);
            int brighterBlue = Math.min(color.getBlue() + 25, 255);
            color = new Color(brighterRed, brighterGreen, brighterBlue);
        } else if (brightness <= 63){
             int brighterRed = Math.min(color.getRed() + 45, 255);
            int brighterGreen = Math.min(color.getGreen() + 45, 255);
            int brighterBlue = Math.min(color.getBlue() + 45, 255);
            color = new Color(brighterRed, brighterGreen, brighterBlue);
        }else {
            // Brighten the color when it's a light color
            int darkerRed = (int) (color.getRed() * 0.85);
            int darkerGreen = (int) (color.getGreen() * 0.85);
            int darkerBlue = (int) (color.getBlue() * 0.85);
            color = new Color(darkerRed, darkerGreen, darkerBlue);
        }
    }

    g.setColor(color);
    g.fillRect(x, y, w, h);
}
    @Override
    protected void paintText(Graphics g, int tabPlacement, Font font, FontMetrics metrics, int tabIndex, String title, Rectangle textRect, boolean isSelected) {
        Color background = tabColors.size() > tabIndex ? tabColors.get(tabIndex) : super.tabPane.getBackground();
        double luminance = 0.299 * background.getRed() + 0.587 * background.getGreen() + 0.114 * background.getBlue();
        if (luminance < 128) {
            g.setColor(Color.WHITE);
        } else {
            g.setColor(Color.BLACK);
        }
        super.paintText(g, tabPlacement, font, metrics, tabIndex, title, textRect, isSelected);
    }




}
