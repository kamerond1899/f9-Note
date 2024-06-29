import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class CustomCheckBoxRenderer extends JLabel implements TableCellRenderer {
    private ImageIcon iconCheck;
    private ImageIcon iconUncheck;
private Map<Point, Color> cellColors = new HashMap<>();
public CustomCheckBoxRenderer(ImageIcon iconPlus, ImageIcon iconGear) {
    this.iconCheck = iconGear;
    this.iconUncheck = iconPlus;
    
    
    setIcon(iconPlus);  // Set an initial icon
    setHorizontalAlignment(JLabel.CENTER);
    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
}

    
    

@Override
public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
 Color cellColor = cellColors.get(new Point(row, column));
    if (cellColor != null) {
        if ((Boolean) value) {
            setIcon(blendColor(iconCheck, cellColor));
        } else {
            setIcon(blendColor(iconUncheck, cellColor));
        }
    } else {
        if ((Boolean) value) {
            setIcon(iconCheck);
        } else {
            setIcon(iconUncheck);
        }
    }

    // Ensure the icon is updated correctly when the cell is selected
    if (isSelected) {
        setBackground(table.getSelectionBackground());
    } else {
        setBackground(table.getBackground());
    }
    

    return this;
}

    public void setCellColor(int row, int column, Color color) {
        cellColors.put(new Point(row, column), color);
    }

    private ImageIcon blendColor(ImageIcon icon, Color color) {
        int w = icon.getIconWidth();
        int h = icon.getIconHeight();
        BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bi.createGraphics();
        icon.paintIcon(null, g, 0, 0);
        g.setComposite(AlphaComposite.SrcAtop);
        g.setColor(color);
        g.fillRect(0, 0, w, h);
        g.dispose();
        return new ImageIcon(bi);
    }
    

  public void setIconCheck(ImageIcon iconCheck) {
        this.iconCheck = iconCheck;
    }

    public void setIconUncheck(ImageIcon iconUncheck) {
        this.iconUncheck = iconUncheck;
    }

    /**
     * @return the iconCheck
     */
    public ImageIcon getIconCheck() {
        return iconCheck;
    }

    /**
     * @return the iconUncheck
     */
    public ImageIcon getIconUncheck() {
        return iconUncheck;
    }


    
    
    

}
