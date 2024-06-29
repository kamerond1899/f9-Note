import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

class ResizableTable extends JTable {
    private boolean resizing = false;
    private Point lastPoint;

    public ResizableTable(Object[][] data, Object[] columnNames) {
        super(data, columnNames);
        setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (getCursor().getType() == Cursor.SE_RESIZE_CURSOR) {
                    resizing = true;
                    lastPoint = e.getPoint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                resizing = false;
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!resizing) {
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (e.getX() > getWidth() - 10 && e.getY() > getHeight() - 10) {
                    setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
                } else {
                    if (!resizing) {
                        setCursor(Cursor.getDefaultCursor());
                    }
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (resizing) {
                    int deltaX = e.getX() - lastPoint.x;
                    int deltaY = e.getY() - lastPoint.y;
                    setSize(getWidth() + deltaX, getHeight() + deltaY);
                    lastPoint = e.getPoint();
                }
            }
        });

        // Set the context menu for the table
        this.setComponentPopupMenu(createTableContextMenu());
    }

    private JPopupMenu createTableContextMenu() {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem addRow = new JMenuItem("Add Row");
        JMenuItem removeRow = new JMenuItem("Remove Row");
        JMenuItem addColumn = new JMenuItem("Add Column");
        JMenuItem removeColumn = new JMenuItem("Remove Column");

        addRow.addActionListener(e -> {
            DefaultTableModel model = (DefaultTableModel) this.getModel();
            model.addRow(new Object[]{});
        });

        removeRow.addActionListener(e -> {
            DefaultTableModel model = (DefaultTableModel) this.getModel();
            int selectedRow = this.getSelectedRow();
            if (selectedRow != -1) {
                model.removeRow(selectedRow);
            }
        });

        addColumn.addActionListener(e -> {
            DefaultTableModel model = (DefaultTableModel) this.getModel();
            model.addColumn("New Column");
        });

        removeColumn.addActionListener(e -> {
            int selectedColumn = this.getSelectedColumn();
            if (selectedColumn != -1) {
                this.removeColumn(this.getColumnModel().getColumn(selectedColumn));
            }
        });

        menu.add(addRow);
        menu.add(removeRow);
        menu.add(addColumn);
        menu.add(removeColumn);

        return menu;
    }
}



class ResizableScrollPane extends JScrollPane {
    private boolean resizing = false;
    private Point lastPoint;

    public ResizableScrollPane(Component view) {
        super(view);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (getCursor().getType() == Cursor.SE_RESIZE_CURSOR) {
                    resizing = true;
                    lastPoint = e.getPoint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                resizing = false;
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!resizing) {
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (e.getX() > getWidth() - 10 && e.getY() > getHeight() - 10) {
                    setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
                } else {
                    if (!resizing) {
                        setCursor(Cursor.getDefaultCursor());
                    }
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (resizing) {
                    int deltaX = e.getX() - lastPoint.x;
                    int deltaY = e.getY() - lastPoint.y;
                    setSize(getWidth() + deltaX, getHeight() + deltaY);
                    lastPoint = e.getPoint();
                }
            }
        });
    }


}