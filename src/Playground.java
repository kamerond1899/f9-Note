/*
private JScrollPane addSpreadsheetTab(DefaultTableModel model, boolean isNewSpreadsheet) {
    
JTabbedPane innerTabbedPane = new JTabbedPane();
    innerTabbedPane.setTabPlacement(JTabbedPane.BOTTOM);
    
    final DefaultTableModel[] localModel = new DefaultTableModel[1];
    if (model == null) {
        localModel[0] = new DefaultTableModel(100, 26); // Default model with 100 rows and 26 columns
    } else {
        localModel[0] = model;
    }
    
    
// Set column names to represent Excel-like columns (A, B, C, ..., Z)
String[] columnNames = getColumnNames(localModel[0], 100); // Generate initial 100 column names
for (int i = 0; i < 26; i++) {
    columnNames[i] = String.valueOf((char) ('A' + i));
}
localModel[0].setColumnIdentifiers(columnNames);

// Create a table with the model
JTable spreadsheetTable = new JTable(localModel[0]) {
    @Override
public Class<?> getColumnClass(int columnIndex) {
    return StyledCell.class;
}

    @Override
    public Object getValueAt(int row, int column) {
        Object value = super.getValueAt(row, column);
        if (!(value instanceof StyledCell)) {
            value = new StyledCell(value == null ? "" : value.toString(), new Font("Serif", Font.PLAIN, 12), Color.WHITE);
            setValueAt(value, row, column);
        }
        System.out.println("JTable: Getting value at (" + row + ", " + column + "): " + value);
        return value;
    }

    @Override
    public void setValueAt(Object aValue, int row, int column) {
        if (aValue instanceof StyledCell) {
            super.setValueAt(aValue, row, column);
        } else {
            super.setValueAt(new StyledCell(aValue.toString(), new Font("Serif", Font.PLAIN, 12), Color.WHITE), row, column);
        }
    }
    
    @Override
    public boolean getScrollableTracksViewportWidth() {
        return getPreferredSize().width < getParent().getWidth();
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        return getPreferredSize().height < getParent().getHeight();
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return new Dimension(getPreferredSize().width, getRowHeight() * getRowCount());
    }
};


    
    // Allow users to select individual cells
    spreadsheetTable.setCellSelectionEnabled(true);
    

    // Set some basic configurations to make the table look like a spreadsheet
    spreadsheetTable.setGridColor(Color.LIGHT_GRAY);
    spreadsheetTable.setShowGrid(true);
    
    // Add a key listener to handle paste operations (Ctrl+V)
    spreadsheetTable.addKeyListener(new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_V) {
                pasteData(spreadsheetTable);
            }
        }
    });
    
    
    
  // Set default column width and make columns resizable
    TableColumnModel columnModel = spreadsheetTable.getColumnModel();
    for (int i = 0; i < columnModel.getColumnCount(); i++) {
        TableColumn column = columnModel.getColumn(i);
        column.setPreferredWidth(100); // Set your preferred default width here
    }
    spreadsheetTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    
    //Grid appear
    spreadsheetTable.setShowGrid(false);
    
    // Make rows resizable
    spreadsheetTable.setRowHeight(30); // Set your preferred row height here

    // Allow users to resize columns by dragging the edges of the column headers
    spreadsheetTable.getTableHeader().setResizingAllowed(true);

    // Allow users to reorder columns by dragging the column headers
    spreadsheetTable.getTableHeader().setReorderingAllowed(true);
    
    

    // Add a component to allow users to resize rows
    spreadsheetTable.setFillsViewportHeight(true);
    
    //CUSTOM RENDERER NIahA AHAH
    spreadsheetTable.setDefaultRenderer(Object.class, new StyledCellRenderer());
    spreadsheetTable.setFont(null);
    spreadsheetTable.setDefaultEditor(StyledCell.class, new StyledCellEditor(spreadsheetTable));

    spreadsheetTable.addMouseListener(new MouseAdapter() {
    @Override
    public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            Point point = e.getPoint();
            int row = spreadsheetTable.rowAtPoint(point);
            int col = spreadsheetTable.columnAtPoint(point);
            if (row != -1 && col != -1) {
                spreadsheetTable.changeSelection(row, col, false, false);
            }
        }
    }
});

    
    //THIS IS WHERE WE ADD INNERTABS
    JScrollPane scrollPane = new JScrollPane(spreadsheetTable) {

};

       // Create a new table model for the row header
    DefaultTableModel newRowHeaderModel = new DefaultTableModel(0, 1) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false; // This will make the cells in the row header uneditable
        }
    };
    
        // Create a new table using the new row header model
    JTable newRowHeader = new JTable(newRowHeaderModel);



// Populate the row header with row numbers
for (int i = 1; i <= localModel[0].getRowCount(); i++) {
    newRowHeaderModel.addRow(new Object[]{i});
}


newRowHeader.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        setBorder(BorderFactory.createMatteBorder(1, 0, 0, 1, Color.LIGHT_GRAY));
        return this;
    }
});

newRowHeader.setPreferredScrollableViewportSize(new Dimension(50, 0));
newRowHeader.setRowHeight(spreadsheetTable.getRowHeight());
newRowHeader.getColumnModel().getColumn(0).setPreferredWidth(50);

//We can put listeneres for newRowHeader here
newRowHeader.addMouseListener(new MouseAdapter() {
    private int resizingRow = -1;
    
    @Override
    public void mousePressed(MouseEvent e) {
        int row = newRowHeader.rowAtPoint(e.getPoint());
        if (row >= 0) {
            resizingRow = row;
        }
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
        resizingRow = -1;
    }
});

newRowHeader.addMouseMotionListener(new MouseAdapter() {
    private Cursor resizeCursor = Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR);
    private Cursor defaultCursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
    private int resizingRow = -1;
    
    @Override
    public void mouseMoved(MouseEvent e) {
        int row = newRowHeader.rowAtPoint(e.getPoint());
        if (row >= 0) {
            Rectangle r = newRowHeader.getCellRect(row, 0, true);
            if (e.getY() >= r.y + r.height - 3 && e.getY() <= r.y + r.height + 3) {
                newRowHeader.setCursor(resizeCursor);
                resizingRow = row;
            } else {
                newRowHeader.setCursor(defaultCursor);
                resizingRow = -1;
            }
        }
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
        if (resizingRow >= 0) {
            int newHeight = e.getY() - newRowHeader.getCellRect(resizingRow, 0, true).y;
            spreadsheetTable.setRowHeight(resizingRow, newHeight);
            newRowHeader.setRowHeight(resizingRow, newHeight); // Update the row header height as well
        }
    }
});



// Set the row header to your scroll pane
scrollPane.setRowHeaderView(newRowHeader);

innerTabbedPane.addTab("Sheet1", scrollPane); // Add a default sheet to the inner tabbed pane

  
 innerTabbedPane.addMouseListener(new MouseAdapter() {
    public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            int tabIndex = innerTabbedPane.indexAtLocation(e.getX(), e.getY());
            if (tabIndex >= 0) {
                JPopupMenu innerTabPopupMenu = new JPopupMenu();
                JMenuItem newSheetItem = new JMenuItem("New Sheet");
                JMenuItem renameTabItem = new JMenuItem("Rename Tab");
                
                newSheetItem.addActionListener(ev -> {
                    JScrollPane newScrollPane = addSpreadsheetTab(null, false); // Create a new spreadsheet tab with a default model
                    innerTabbedPane.addTab("New Sheet", newScrollPane); // Add the new tab to the inner tabbed pane
                });
                
                renameTabItem.addActionListener(ev -> {
                    JTextField editor = new JTextField(innerTabbedPane.getTitleAt(tabIndex));
                    editor.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                    editor.addFocusListener(new FocusAdapter() {
                        @Override
                        public void focusLost(FocusEvent e) {
                            innerTabbedPane.setTitleAt(tabIndex, editor.getText());
                            innerTabbedPane.setTabComponentAt(tabIndex, null);
                        }
                    });
                    editor.addActionListener(actionEvent -> {
                        innerTabbedPane.setTitleAt(tabIndex, editor.getText());
                        innerTabbedPane.setTabComponentAt(tabIndex, null);
                    });
                    innerTabbedPane.setTabComponentAt(tabIndex, editor);
                    editor.selectAll();
                    editor.requestFocusInWindow();
                });
                
                innerTabPopupMenu.add(newSheetItem);
                innerTabPopupMenu.add(renameTabItem);
                innerTabPopupMenu.show(innerTabbedPane, e.getX(), e.getY());
            }
        }
    }
});


    innerTabbedPane.addMouseListener(new TabEditorMouseAdapter(innerTabbedPane));
     
    if (isNewSpreadsheet) {
        tabbedPane.addTab("New Spreadsheet", innerTabbedPane);
        tabbedPane.setSelectedComponent(innerTabbedPane);
    }

localModel[0].addTableModelListener(new TableModelListener() {
    @Override
    public void tableChanged(TableModelEvent e) {
        if (e.getType() == TableModelEvent.UPDATE) {
            int row = e.getFirstRow();
            int column = e.getColumn();
            if (row >= 0 && column >= 0) {
                // Assuming you have a method to resize the cell
                // resizeCell(row, column); 

                // Get the new height of the row in the main table
                int newHeight = spreadsheetTable.getRowHeight(row);

                // Set the height of the corresponding row in the row header
                newRowHeader.setRowHeight(row, newHeight);
            }
        }
    }
});

    // Add functionalities to add/remove rows and columns through a right-click context menu
    JPopupMenu popupMenu = new JPopupMenu();
    JMenuItem addRowItem = new JMenuItem("Add Row");
    JMenuItem removeRowItem = new JMenuItem("Remove Row");
    JMenuItem addColumnItem = new JMenuItem("Add Column");
    JMenuItem removeColumnItem = new JMenuItem("Remove Column");
    
    JMenu verticalAlignmentMenu = new JMenu("Vertical Alignment");
JMenuItem topAlignmentItem = new JMenuItem("Top");
JMenuItem middleAlignmentItem = new JMenuItem("Middle");
JMenuItem bottomAlignmentItem = new JMenuItem("Bottom");

verticalAlignmentMenu.add(topAlignmentItem);
verticalAlignmentMenu.add(middleAlignmentItem);
verticalAlignmentMenu.add(bottomAlignmentItem);

JMenu horizontalAlignmentMenu = new JMenu("Horizontal Alignment");
JMenuItem leftAlignmentItem = new JMenuItem("Left");
JMenuItem centerAlignmentItem = new JMenuItem("Center");
JMenuItem rightAlignmentItem = new JMenuItem("Right");

horizontalAlignmentMenu.add(leftAlignmentItem);
horizontalAlignmentMenu.add(centerAlignmentItem);
horizontalAlignmentMenu.add(rightAlignmentItem);

//Alignment Listeners :)

topAlignmentItem.addActionListener(e -> {
    int[] selectedRows = spreadsheetTable.getSelectedRows();
    int[] selectedCols = spreadsheetTable.getSelectedColumns();
    for (int row : selectedRows) {
        for (int col : selectedCols) {
            StyledCell cell = (StyledCell) localModel[0].getValueAt(row, col);
            if (cell != null) {
                cell.setVerticalAlignment(JLabel.TOP);
            }
        }
    }
    spreadsheetTable.repaint();
});
middleAlignmentItem.addActionListener(e -> {
    int[] selectedRows = spreadsheetTable.getSelectedRows();
    int[] selectedCols = spreadsheetTable.getSelectedColumns();
    for (int row : selectedRows) {
        for (int col : selectedCols) {
            StyledCell cell = (StyledCell) localModel[0].getValueAt(row, col);
            if (cell != null) {
                cell.setVerticalAlignment(JLabel.CENTER);
            }
        }
    }
    spreadsheetTable.repaint();
});
bottomAlignmentItem.addActionListener(e -> {
    int[] selectedRows = spreadsheetTable.getSelectedRows();
    int[] selectedCols = spreadsheetTable.getSelectedColumns();
    for (int row : selectedRows) {
        for (int col : selectedCols) {
            StyledCell cell = (StyledCell) localModel[0].getValueAt(row, col);
            if (cell != null) {
                cell.setVerticalAlignment(JLabel.BOTTOM);
            }
        }
    }
    spreadsheetTable.repaint();
});
leftAlignmentItem.addActionListener(e -> {
    int[] selectedRows = spreadsheetTable.getSelectedRows();
    int[] selectedCols = spreadsheetTable.getSelectedColumns();
    for (int row : selectedRows) {
        for (int col : selectedCols) {
            StyledCell cell = (StyledCell) localModel[0].getValueAt(row, col);
            if (cell != null) {
                cell.setHorizontalAlignment(JLabel.LEFT);
            }
        }
    }
    spreadsheetTable.repaint();
});
centerAlignmentItem.addActionListener(e -> {
    int[] selectedRows = spreadsheetTable.getSelectedRows();
    int[] selectedCols = spreadsheetTable.getSelectedColumns();
    for (int row : selectedRows) {
        for (int col : selectedCols) {
            StyledCell cell = (StyledCell) localModel[0].getValueAt(row, col);
            if (cell != null) {
                cell.setHorizontalAlignment(JLabel.CENTER);
            }
        }
    }
    spreadsheetTable.repaint();
});
rightAlignmentItem.addActionListener(e -> {
    int[] selectedRows = spreadsheetTable.getSelectedRows();
    int[] selectedCols = spreadsheetTable.getSelectedColumns();
    for (int row : selectedRows) {
        for (int col : selectedCols) {
            StyledCell cell = (StyledCell) localModel[0].getValueAt(row, col);
            if (cell != null) {
                cell.setHorizontalAlignment(JLabel.RIGHT);
            }
        }
    }
    spreadsheetTable.repaint();
});

    
addRowItem.addActionListener(e -> {
    localModel[0].addRow(new Object[localModel[0].getColumnCount()]);
    newRowHeaderModel.addRow(new Object[]{localModel[0].getRowCount()});  // Add a new row number to the row header
});

    
    removeRowItem.addActionListener(e -> {
    if (localModel[0].getRowCount() > 1) {
        int selectedRow = spreadsheetTable.getSelectedRow();
        localModel[0].removeRow(selectedRow);
        newRowHeaderModel.removeRow(selectedRow);  // Remove the corresponding row number from the row header
    }
});

    
    addColumnItem.addActionListener(e -> localModel[0].addColumn("New Column"));
    removeColumnItem.addActionListener(e -> {
    if (spreadsheetTable.getSelectedColumns().length > 0) {
        removeColumns(spreadsheetTable.getSelectedColumns(), localModel[0]);
    }
});

    
    popupMenu.add(addRowItem);
    popupMenu.add(removeRowItem);
    popupMenu.add(addColumnItem);
    popupMenu.add(removeColumnItem);
    
    popupMenu.add(verticalAlignmentMenu);
popupMenu.add(horizontalAlignmentMenu);
    
    spreadsheetTable.setComponentPopupMenu(popupMenu);
    
    scrollPane.getVerticalScrollBar().setUnitIncrement(spreadsheetTable.getRowHeight());
    scrollPane.getHorizontalScrollBar().setUnitIncrement(spreadsheetTable.getColumnModel().getColumn(0).getWidth());

    
         return scrollPane;
} */

// Helper method to remove a column from the table model