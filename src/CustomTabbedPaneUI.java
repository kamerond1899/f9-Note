import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.*;
import java.util.ArrayList;

public class CustomTabbedPaneUI extends BasicTabbedPaneUI {

    private ArrayList<Color> tabColors;

    public CustomTabbedPaneUI(ArrayList<Color> tabColors) {
        this.tabColors = tabColors;
        
    }

    @Override
    protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
        Color color = tabColors.size() > tabIndex ? tabColors.get(tabIndex) : super.tabPane.getBackground();
        g.setColor(color);
        g.fillRect(x, y, w, h);
    }

    @Override
    protected void paintText(Graphics g, int tabPlacement, Font font, FontMetrics metrics, int tabIndex, String title, Rectangle textRect, boolean isSelected) {
        System.out.println("paintText is called");  // Debug print statement
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
