import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.InputEvent;

public class TableRowTransferHandler extends TransferHandler {
    private JTable table;
    private int[] rows;
    private int addIndex = -1;
    private int addCount = 0;

    public TableRowTransferHandler(JTable table) {
        this.table = table;
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        assert (c == table);
        rows = table.getSelectedRows();
        return new StringSelection(exportString(table));
    }

    @Override
    public int getSourceActions(JComponent c) {
        return TransferHandler.COPY_OR_MOVE;
    }

    @Override
    protected void exportDone(JComponent source, Transferable data, int action) {
        cleanup(table, action == TransferHandler.MOVE);
    }

    @Override
    public boolean canImport(TransferHandler.TransferSupport info) {
        // Check for String flavor
        return info.isDataFlavorSupported(DataFlavor.stringFlavor);
    }

    @Override
    public boolean importData(TransferHandler.TransferSupport info) {
        if (!info.isDrop()) {
            return false;
        }

        DefaultTableModel model = (DefaultTableModel) table.getModel();
        JTable.DropLocation dl = (JTable.DropLocation) info.getDropLocation();
        int index = dl.getRow();
        boolean insert = dl.isInsertRow();
        int max = model.getRowCount();

        if (!insert) {
            return false;
        }

        addIndex = index;
         try {
        String line = (String) info.getTransferable().getTransferData(DataFlavor.stringFlavor);
        String[] values = line.split(",");
        Object[] row = new Object[values.length];
        for (int i = 0; i < values.length; i++) {
            // Handle boolean values
            if (values[i].equalsIgnoreCase("true") || values[i].equalsIgnoreCase("false")) {
                row[i] = Boolean.parseBoolean(values[i]);
            } else {
                row[i] = values[i];
            }
        }
        model.insertRow(index, row);
        addCount = 1;
        return true;
    } catch (Exception e) {
        e.printStackTrace();
    }

        return false;
    }

private String exportString(JTable table) {
    DefaultTableModel model = (DefaultTableModel) table.getModel();
    int[] rows = table.getSelectedRows();
    StringBuilder buff = new StringBuilder();

    for (int i = 0; i < rows.length; i++) {
        for (int j = 0; j < table.getColumnCount(); j++) {
            Object val = model.getValueAt(rows[i], j);
            buff.append(val == null ? "" : val.toString());
            if (j != table.getColumnCount() - 1) {
                buff.append(",");
            }
        }
        if (i != rows.length - 1) {
            buff.append("\n");
        }
    }

    return buff.toString();
}
    private void cleanup(JTable table, boolean remove) {
        if (remove && rows != null) {
            DefaultTableModel model = (DefaultTableModel) table.getModel();

            // If we are moving items around in the same table, we
            // need to adjust the rows accordingly, since those
            // after the insertion point have moved.
            if (addCount > 0) {
                for (int i = 0; i < rows.length; i++) {
                    if (rows[i] > addIndex) {
                        rows[i] += addCount;
                    }
                }
            }
            for (int i = rows.length - 1; i >= 0; i--) {
                model.removeRow(rows[i]);
            }
        }

        rows = null;
        addCount = 0;
        addIndex = -1;
    }
}
