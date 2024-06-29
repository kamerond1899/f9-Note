
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JLabel;
import javax.swing.JPanel;

class CustomTabHeader extends JPanel {
    private JLabel label;
    private Color bgColor;

    public CustomTabHeader(String title) {
        setLayout(new BorderLayout());
        label = new JLabel(title);
        add(label, BorderLayout.CENTER);
        setOpaque(false);
    }

    public void setTitle(String title) {
        label.setText(title);
    }

    public void setBgColor(Color color) {
        this.bgColor = color;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (bgColor != null) {
            g.setColor(bgColor);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}
