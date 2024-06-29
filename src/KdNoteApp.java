import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.rtf.RTFEditorKit;
import org.apache.poi.xwpf.usermodel.*;
import org.json.JSONObject;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.util.ArrayList;
import mdlaf.MaterialLookAndFeel;
import com.formdev.flatlaf.FlatLightLaf;
import java.awt.image.RescaleOp;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import javax.swing.plaf.basic.BasicTableHeaderUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.undo.UndoManager;
import javax.swing.text.Element;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;
import org.apache.poi.util.Units;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.util.logging.Logger;
import java.util.logging.Handler;
import javax.swing.border.MatteBorder;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellEditor;
import javax.swing.text.SimpleAttributeSet;
import java.util.prefs.Preferences;
import javax.swing.event.CellEditorListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.filechooser.FileFilter;
import org.apache.commons.compress.archivers.dump.InvalidFormatException;
import java.util.logging.LogManager;
import javax.swing.event.MouseInputAdapter;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;


public class KdNoteApp {
    
    // Swing components
    private JDialog frame;                       // Base Application Window
    private JTextPane textPane;                  // Default Text Area for Notes, and first Text Tab
    private JComboBox<String> fontSelector;      // Font selection combo box
    private JComboBox<Integer> fontSizeSelector; // Font size selection combo box
    private JTabbedPane tabbedPane;              // Tabbed pane for multiple notes
    private CardLayout cardLayout;               // Layout manager for switching menus
    private JPanel cardPanel;                    // Panel holding different menus
    private JPopupMenu popupMenu;                // Popup menu for the hamburger menu
    private JComboBox<String> noteTypeSelector;  // Combo box for selecting note type

    private ArrayList<Color> tabColors = new ArrayList<>();
    private Color frameBackgroundColor = Color.WHITE;  // Default frame background color
    private int resizingColumnIndex = -1;
private int initialMouseX = -1;
    private Map<Integer, Integer> maxRowHeights = new HashMap<>();
    private Component activeComponent = null;
    private JPanel customTitleBar;  // Moved to class-level
    private JPanel bottomPanel;     // Moved to class-level
    private JPanel hamburgerMenu;
    private JPanel northPanel;
    private JPanel fontPanel;
    private JButton hamburgerButton;
    
    private JMenuItem tabStyleButton;
    private ArrayList<Color> bevelColors = new ArrayList<>();
    // New: Custom UI for the tabbedPane
    private CustomTabbedPaneUI customUI;
    private CustomFlatLafTabbedPaneUI customUI2;
    
    private UndoManager undoManager = new UndoManager();
    CompoundEdit currentEdit = new CompoundEdit();
    
    
    //Boolean Options
    private boolean bgColorSyncOption = true;
    private int tabStyle = 0;
    
    private String appTheme = "Custom";
    private String tabStyleName = "Modern";
 
    //Unsaved changes
    boolean hasUnsavedChanges = false;
        
    private Map<JTextPane, UndoManager> undoManagers = new HashMap<>();
    private Timer timer;

    private Component lastFocusedComponent = null;

    UndoManager tableUndoManager = new UndoManager();
    
 JTable lastFocusedTable = null;  // Declare it globally

HashMap<JTable, Stack<StyledCell[][]>> tableUndoStates = new HashMap<>();
HashMap<JTable, Stack<StyledCell[][]>> tableRedoStates = new HashMap<>();

 private ActionListener tableInsertionListener;
    
    Stack<Object[][]> tableStates = new Stack<>();

    //Checkbox Icons
    private ImageIcon iconCheck_b = new ImageIcon("src/iconCheck_b.png");
    private ImageIcon iconUncheck_b = new ImageIcon("src/iconUncheck_b.png");
    private ImageIcon iconCheck_w = new ImageIcon("src/iconCheck_w.png");
    private ImageIcon iconUncheck_w = new ImageIcon("src/iconUncheck_w.png");

    private int horizontalAlignment = JLabel.LEFT;
    private int verticalAlignment = JLabel.TOP;

    private JButton boldButton = new JButton();
    private JButton italicButton = new JButton();
    private JButton underlineButton = new JButton();
    private JButton strikeThroughButton = new JButton();
    private JButton undoButton = new JButton();
    private JButton redoButton = new JButton();
    
    private JButton fontColorButton = new JButton();
    
    private JButton menuButton = new JButton();
    public Color defaultColor = new Color(240, 240, 240); 
    
    private boolean isProgrammaticUpdate = false;
    private Component activeStylingComponent;

    
        // Create a table model for the row header
    private DefaultTableModel rowHeaderModel = new DefaultTableModel(0, 1) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false; // This will make the cells in the row header uneditable
        }
    };
        // Create a table using the row header model
    private JTable rowHeader = new JTable(rowHeaderModel);
    
    private JScrollPane sP;
 
    //Unsaved Changes
    ArrayList<Boolean> unsavedChanges = new ArrayList<>();

    
    public static void main(String[] args) {
       // Clear previous logging configurations.
LogManager.getLogManager().reset();

// Get the logger for "org.jnativehook" and set the level to off.
Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
logger.setLevel(Level.OFF);

        
try {
    UIManager.setLookAndFeel(new FlatLightLaf());
} catch( Exception ex ) {
    System.err.println("Failed to initialize LaF");
}
        SwingUtilities.invokeLater(() -> {
            KdNoteApp app = new KdNoteApp();
            app.frame.setVisible(false);
            app.addAppToTray();
        });
    }

    public KdNoteApp() {
        
        frame = new JDialog();
        frame.setUndecorated(true);
        frame.setSize(360, 300);
        frame.setLayout(new BorderLayout());
        
        // Bottom Panel with Bullet, Number, and Color buttons
        bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        // Bullet and Number buttons
        JButton bulletButton = new JButton("Bullet");
        JButton numberButton = new JButton("Number");
         UIManager.put("ComboBox.buttonBackground", frameBackgroundColor.brighter());
        //Italic, bold, Underline buttons
        
        frameBackgroundColor = defaultColor;
        
        bulletButton.addActionListener(e -> insertBullet());
        numberButton.addActionListener(e -> insertNumber());
        undoButton.setIcon(new ImageIcon("src/iconUndo.png"));
            undoButton.setBorder(null);
            undoButton.setBackground(frameBackgroundColor);
        redoButton.setIcon(new ImageIcon("src/iconRedo.png"));
            redoButton.setBorder(null);
            redoButton.setBackground(frameBackgroundColor);
        //This is where we add UNDO
        undoButton.addActionListener(e -> {
            System.out.println("Undo button clicked");
            if (lastFocusedTable != null) {
                System.out.println("Last focused table is not null");

                // If a cell is currently being edited, stop editing
                if (lastFocusedTable.isEditing()) {
                    lastFocusedTable.getCellEditor().stopCellEditing();

                    // Capturing the current state after stopping editing
                    StyledCell[][] currentState = captureTableState(lastFocusedTable);
                    Stack<StyledCell[][]> undoStates = tableUndoStates.getOrDefault(lastFocusedTable, new Stack<>());
                    undoStates.push(currentState);
                    tableUndoStates.put(lastFocusedTable, undoStates);
                }

                // Now proceed with the undo operation
                Stack<StyledCell[][]> redoStates = tableRedoStates.getOrDefault(lastFocusedTable, new Stack<>());
                redoStates.push(captureTableState(lastFocusedTable)); // Store the current state for redo
                tableRedoStates.put(lastFocusedTable, redoStates);

                Stack<StyledCell[][]> undoStates = tableUndoStates.getOrDefault(lastFocusedTable, new Stack<>());
                System.out.println("Number of stored states: " + undoStates.size());
                if (!undoStates.isEmpty()) {
                    undoStates.pop();  // Remove the current state
                    if (!undoStates.isEmpty()) {
                        System.out.println("Restoring to last state");
                        StyledCell[][] lastState = undoStates.peek();
                        restoreTableState(lastFocusedTable, lastState);
                    }
                }
            } else {
                System.out.println("Last focused table is null");
                // Existing undo code for JTextPane
            }
        });



redoButton.addActionListener(e -> {
    System.out.println("Redo button clicked");
    if (lastFocusedTable != null) {
        System.out.println("Last focused table is not null");
        Stack<StyledCell[][]> redoStates = tableRedoStates.getOrDefault(lastFocusedTable, new Stack<>());
        if (!redoStates.isEmpty()) {
            System.out.println("Restoring to next state");
            StyledCell[][] nextState = redoStates.pop();
            restoreTableState(lastFocusedTable, nextState);
            
            // Push the restored state onto the undo stack
            Stack<StyledCell[][]> undoStates = tableUndoStates.getOrDefault(lastFocusedTable, new Stack<>());
            undoStates.push(nextState);
            tableUndoStates.put(lastFocusedTable, undoStates);
        }
    } else {
        System.out.println("Last focused table is null");
        // Existing redo code for JTextPane
    }
});



        // Custom title bar
        customTitleBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeButton = new JButton("X");
        closeButton.addActionListener(e -> frame.setVisible(false));
        
        frame.add(customTitleBar, BorderLayout.NORTH);
        //customTitleBar.add(bulletButton);
        //customTitleBar.add(numberButton);

        // New: Add an "Open" button to open files
        JButton openButton = new JButton("Open");
        openButton.addActionListener(e -> openFile());
        customTitleBar.add(openButton);
        
        
        // New: Initialize the note type selector
        String[] noteTypes = {"NOTE", "TODO", "SPREADSHEET"};
        noteTypeSelector = new JComboBox<>(noteTypes);
        customTitleBar.add(noteTypeSelector);  // Add it to the custom title bar
        
        // Font selector
        String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        fontSelector = new JComboBox<>(fonts);
        fontSelector.setRenderer(new FontCellRenderer());

        fontSelector.setSelectedItem("Segoe UI");
        fontSelector.addActionListener(e -> {
    if (!isProgrammaticUpdate) {
        Component selectedComponent = tabbedPane.getSelectedComponent();
        System.out.println("Selected Component: " + selectedComponent.getClass().getName());
        
        if (selectedComponent instanceof JScrollPane) {
            JScrollPane scrollPane = (JScrollPane) selectedComponent;
            Component viewComponent = scrollPane.getViewport().getView();
            System.out.println("View Component: " + viewComponent.getClass().getName());

            if (viewComponent instanceof JTable) {
                JTable table = (JTable) viewComponent;
                System.out.println("Table Name: " + table.getName());

                if ("TodoTable".equals(table.getName())) {
                    System.out.println("Applying Todo List Styling...");
                    applyTodoListStyling();
                } else {
                    System.out.println("Applying Spreadsheet Styling...");
                    applySpreadsheetStyling();
                }
            } else if (viewComponent instanceof JTextPane) {
                System.out.println("Applying JTextPane Styling...");
                applyStyling();
                ((JTextPane) viewComponent).requestFocus();
            } else {
                System.out.println("Unhandled Component Type: " + viewComponent.getClass().getName());
            }
        } else {
            System.out.println("Unhandled Component Type: " + selectedComponent.getClass().getName());
        }
    }
});



        
        
        customTitleBar.add(fontSelector);

        // Font size selector
        Integer[] sizes = {8, 10, 12, 14, 16, 18, 20, 24, 28, 32, 36, 40, 48, 56, 64, 72};
        fontSizeSelector = new JComboBox<>(sizes);
        fontSizeSelector.setSelectedItem(12);  // Set default size to 12
        fontSizeSelector.addActionListener(e -> {if (!isProgrammaticUpdate) {
        Component selectedComponent = tabbedPane.getSelectedComponent();
        System.out.println("Selected Component: " + selectedComponent.getClass().getName());
        
        
        
        if (selectedComponent instanceof JScrollPane) {
            JScrollPane scrollPane = (JScrollPane) selectedComponent;
            Component viewComponent = scrollPane.getViewport().getView();
            System.out.println("View Component: " + viewComponent.getClass().getName());

            if (viewComponent instanceof JTable) {
                JTable table = (JTable) viewComponent;
                System.out.println("Table Name: " + table.getName());

                if ("TodoTable".equals(table.getName())) {
                    System.out.println("Applying Todo List Styling...");
                    applyTodoListStyling();
                } else {
                    System.out.println("Applying Spreadsheet Styling...");
                    applySpreadsheetStyling();
                }
            } else if (viewComponent instanceof JTextPane) {
                System.out.println("Applying JTextPane Styling...");
                applyStyling();
                ((JTextPane) viewComponent).requestFocus();
            } else {
                System.out.println("Unhandled Component Type: " + viewComponent.getClass().getName());
            }
        } else {
            System.out.println("Unhandled Component Type: " + selectedComponent.getClass().getName());
        }
    }
        });
        
        
        customTitleBar.add(fontSizeSelector);

        // Text pane
        textPane = new JTextPane();
        frame.add(new JScrollPane(textPane), BorderLayout.CENTER);
        
        // New: Initialize CardLayout and JPanel
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // New: Create a hamburger menu button
        hamburgerButton = new JButton(new ImageIcon("src/iconBurger.png"));
        updateHamburgerIcon();
        hamburgerButton.addActionListener(e -> showPopupMenu(hamburgerButton));

        // New: Remove the bevel and outline
        hamburgerButton.setBorder(null);
        hamburgerButton.setFocusPainted(false);
        hamburgerButton.setContentAreaFilled(false);
        // New: Create a panel for the hamburger menu
        hamburgerMenu = new JPanel(new FlowLayout(FlowLayout.LEFT));  // Changed to LEFT
        hamburgerMenu.add(hamburgerButton);
        

        // New: Create a panel for the font selector and font size selector
        fontPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        fontPanel.add(fontSelector);
        fontPanel.add(fontSizeSelector);
        

        // New: Create a popup menu for the hamburger menu
        popupMenu = new JPopupMenu();
        

        // Create a submenu for adding new tabs
        JMenu newTabMenu = new JMenu("New Tab");

        // Add options to the submenu
        addButtonToPopupMenu("Note", e -> addNewTab("NOTE"), newTabMenu);
        addButtonToPopupMenu("To-do List", e -> addNewTab("TODO"), newTabMenu);
        addButtonToPopupMenu("Spreadsheet", e -> addNewTab("SPREADSHEET"), newTabMenu);

        // Add the submenu to the main popup menu
        popupMenu.add(newTabMenu);

        // ... other menu items ...
        addButtonToPopupMenu("Open", e -> openFile(), popupMenu);
        addButtonToPopupMenu("Save", e -> {
            try {
                saveFile();
            } catch (org.apache.poi.openxml4j.exceptions.InvalidFormatException ex) {
                Logger.getLogger(KdNoteApp.class.getName()).log(Level.SEVERE, null, ex);
            }
        }, popupMenu);
        
        tabStyleButton = new JMenuItem("Tab Style: " + tabStyleName);
        tabStyleButton.addActionListener(e -> changeTabStyle(tabbedPane));
        addButtonToPopupMenu("Change App Color", e -> changeAppColor(), popupMenu);
        popupMenu.add(tabStyleButton);
        //addButtonToPopupMenu("Tab Style: " + tabStyleName, e -> , popupMenu);
        popupMenu.add(new JSeparator());
 
        addCheckBoxToPopupMenu("Always on top", e -> {
    boolean isSelected = ((JCheckBoxMenuItem) e.getSource()).isSelected();
    frame.setAlwaysOnTop(isSelected);
}, popupMenu);
        addButtonToPopupMenu("Hide", e -> frame.setVisible(false), popupMenu);
        addButtonToPopupMenu("Exit", e -> System.exit(0), popupMenu);
        
        
        // Default color buttons
        JButton whiteButton = createColorButton(Color.WHITE);
        JButton lightYellowButton = createColorButton(Color.getHSBColor(60f / 360f, 0.39f, 1f));
        JButton blackButton = createColorButton(Color.BLACK);
        whiteButton.addActionListener(e -> {
            Component selectedComponent = tabbedPane.getSelectedComponent();
            if (selectedComponent instanceof JTabbedPane) {
                applySpreadsheetBackgroundColor(Color.WHITE);
            } else {
                updateBackgroundColor(Color.WHITE);
            }
        });
        blackButton.addActionListener(e -> {
            Component selectedComponent = tabbedPane.getSelectedComponent();
            if (selectedComponent instanceof JTabbedPane) {
                applySpreadsheetBackgroundColor(Color.BLACK);
            } else {
                updateBackgroundColor(Color.BLACK);
            }
        });
// ... Do the same for other color buttons
        lightYellowButton.addActionListener(e -> {
            Component selectedComponent = tabbedPane.getSelectedComponent();
            if (selectedComponent instanceof JTabbedPane) {
                applySpreadsheetBackgroundColor(Color.getHSBColor(60f / 360f, 0.39f, 1f));
            } else {
                updateBackgroundColor(Color.getHSBColor(60f / 360f, 0.39f, 1f));
            }
        });

  
        //bottomPanel.add(whiteButton);
        //bottomPanel.add(lightYellowButton);
        //bottomPanel.add(blackButton);
        bottomPanel.add(undoButton);
        bottomPanel.add(redoButton);
        
        textPane.putClientProperty("textPaneRef", textPane);
        
        //Embedded table
      JMenu insertItem = new JMenu("Insert");
        JMenuItem insertTableItem = new JMenuItem("Table");
          
    JButton insertTableButton = new JButton("[]");
tableInsertionListener = new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            int rows = Integer.parseInt(JOptionPane.showInputDialog("Enter number of rows:"));
            int columns = Integer.parseInt(JOptionPane.showInputDialog("Enter number of columns:"));
            //int rows = 3;
            //int columns = 3;
           EmbeddedTable table = new EmbeddedTable(rows, columns);
           
          DefaultCellEditor singleClickEditor = new DefaultCellEditor(new JTextField());
singleClickEditor.setClickCountToStart(1);
for (int i = 0; i < table.getColumnCount(); i++) {
    table.getColumnModel().getColumn(i).setCellEditor(singleClickEditor);
}
table.putClientProperty("undoManager", tableUndoManager);

table.addMouseListener(new MouseAdapter() {
    @Override
    public void mousePressed(MouseEvent e) {
        // Check if the right mouse button was clicked
        if (SwingUtilities.isRightMouseButton(e)) {
            int row = table.rowAtPoint(e.getPoint());
            int col = table.columnAtPoint(e.getPoint());
            if (!table.isCellSelected(row, col)) {
                table.changeSelection(row, col, false, false);
            }
        }
    }
});
table.addFocusListener(new FocusAdapter() {
    @Override
    public void focusGained(FocusEvent e) {
        System.out.println("Table gained focus");
        lastFocusedComponent = (JTable) e.getSource();
        lastFocusedTable = (JTable) e.getSource();
        syncFontUI(); // Force a call to syncFontUI
    }
});

table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);


             table.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.isAltDown() && e.getKeyCode() == KeyEvent.VK_ENTER) {
                    // Handle Alt+Enter for line break
                    int row = table.getSelectedRow();
                    int col = table.getSelectedColumn();
                    if (row != -1 && col != -1) {
                        String currentValue = (String) table.getValueAt(row, col);
                        table.setValueAt(currentValue + "\n", row, col);
                        table.setRowHeight(row, table.getRowHeight(row) + 16); // Adjust the row height
                    }
                }
            }
        });

table.getDefaultEditor(String.class).addCellEditorListener(new CellEditorListener() {

@Override
public void editingStopped(ChangeEvent e) {
    System.out.println("Editing stopped");
    JTable source = table;
    StyledCell[][] currentState = captureTableState(source);
    Stack<StyledCell[][]> undoStates = tableUndoStates.getOrDefault(source, new Stack<>());
    undoStates.push(currentState);
    tableUndoStates.put(source, undoStates);
    System.out.println("Added new state, stack size: " + undoStates.size());
}


    @Override
    public void editingCanceled(ChangeEvent e) {
        // No action needed
    }
});

table.addMouseListener(new MouseAdapter() {
    @Override
    public void mousePressed(MouseEvent e) {
        int row = table.rowAtPoint(e.getPoint());
        int column = table.columnAtPoint(e.getPoint());
        if (table.isCellEditable(row, column)) {
            table.editCellAt(row, column);
            Component editorComponent = table.getEditorComponent();
            if (editorComponent instanceof JTextComponent) {
                JTextComponent textComponent = (JTextComponent) editorComponent;
                textComponent.getDocument().addDocumentListener(
                        new ExpandingDocumentListener(table, row, column));
            }
        }
    }
});

             
table.getTableHeader().setReorderingAllowed(false); // Disallow column reordering
table.getTableHeader().setResizingAllowed(true);    // Allow column resizing


table.addMouseListener(new MouseAdapter() {
    private int resizingColumn = -1;
    private int startX;

    @Override
    public void mousePressed(MouseEvent e) {
        int col = table.columnAtPoint(e.getPoint());
        Rectangle rect = table.getCellRect(0, col, true);
        if (e.getX() > rect.x + rect.width - 5 && e.getX() < rect.x + rect.width + 5) {
            resizingColumn = col;
            startX = e.getX();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        resizingColumn = -1;
    }
});

             

                                JPopupMenu colorMenu = new JPopupMenu();
               JMenuItem colorItem = new JMenuItem("Set Cell Color");
               JMenuItem addRowItem = new JMenuItem("Add Row");

                JMenuItem removeRowItem = new JMenuItem("Remove Row");
                JMenuItem removeColumnItem = new JMenuItem("Remove Column");
                JMenuItem addColumnItem = new JMenuItem("Add Column");

                colorMenu.add(removeRowItem);
                colorMenu.add(removeColumnItem);
                colorMenu.add(addColumnItem);
                
                JMenu verticalAlignSubmenu = new JMenu("Vertical Align");
                JMenuItem topAlign = new JMenuItem(new ImageIcon("test0.png"));
                JMenuItem middleAlign = new JMenuItem(new ImageIcon("test1.png"));
                JMenuItem bottomAlign = new JMenuItem(new ImageIcon("test2.png"));

                JMenu horizontalAlignSubmenu = new JMenu("Horizontal Align");
                JMenuItem leftAlign = new JMenuItem(new ImageIcon("test3.png"));
                JMenuItem centerAlign = new JMenuItem(new ImageIcon("test4.png"));
                JMenuItem rightAlign = new JMenuItem(new ImageIcon("test5.png"));

                verticalAlignSubmenu.add(topAlign);
                verticalAlignSubmenu.add(middleAlign);
                verticalAlignSubmenu.add(bottomAlign);

                horizontalAlignSubmenu.add(leftAlign);
                horizontalAlignSubmenu.add(centerAlign);
                horizontalAlignSubmenu.add(rightAlign);

                colorMenu.add(verticalAlignSubmenu);
                colorMenu.add(horizontalAlignSubmenu);
System.out.println("Listener added");
table.getModel().addTableModelListener(new TableModelListener() {
@Override
public void tableChanged(TableModelEvent e) {
    System.out.println("Table changed");
    JTable source = (JTable) e.getSource();
    StyledCell[][] currentState = captureTableState(source);
    Stack<StyledCell[][]> undoStates = tableUndoStates.getOrDefault(source, new Stack<>());
    undoStates.push(currentState);
    tableUndoStates.put(source, undoStates);
    System.out.println("Added new state, stack size: " + undoStates.size());
}


});



colorItem.addActionListener(e2 -> {
    Color newColor = JColorChooser.showDialog(table, "Choose Cell Color", Color.WHITE);
    if (newColor != null) {
        int[] selectedRows = table.getSelectedRows();
        int[] selectedCols = table.getSelectedColumns();
        
        for (int row : selectedRows) {
            for (int col : selectedCols) {
                StyledCell cell = (StyledCell) table.getValueAt(row, col);
                cell.setBackgroundColor(newColor);
                
                // Update the editor's background color if the cell is being edited
                if (table.isEditing() && table.getEditingRow() == row && table.getEditingColumn() == col) {
                    JTextArea editor = ((EmbeddedTableStyledCellEditor) table.getCellEditor()).getEditorComponent();
                    editor.setBackground(newColor);
                }
            }
        }
        table.repaint();  // Repaint the table to reflect the color change
    }
});

//Vertical align              
topAlign.addActionListener(e2 -> {
    // Set vertical alignment to TOP
    
        int[] selectedRows = table.getSelectedRows();
    int[] selectedCols = table.getSelectedColumns();
    for (int row : selectedRows) {
        for (int col : selectedCols) {
            StyledCell cell = (StyledCell) table.getValueAt(row, col);
            if (cell != null) {
                cell.setVerticalAlignment(JLabel.TOP);
            }
        }
    }
    table.repaint();
});
middleAlign.addActionListener(e2 -> {
    int[] selectedRows = table.getSelectedRows();
    int[] selectedCols = table.getSelectedColumns();
    for (int row : selectedRows) {
        for (int col : selectedCols) {
            StyledCell cell = (StyledCell) table.getValueAt(row, col);
            if (cell != null) {
                cell.setVerticalAlignment(JLabel.CENTER);
            }
        }
    }
    table.repaint();
});
bottomAlign.addActionListener(e2 -> {
     int[] selectedRows = table.getSelectedRows();
    int[] selectedCols = table.getSelectedColumns();
    for (int row : selectedRows) {
        for (int col : selectedCols) {
            StyledCell cell = (StyledCell) table.getValueAt(row, col);
            if (cell != null) {
                cell.setVerticalAlignment(JLabel.BOTTOM);
            }
        }
    }
    table.repaint();
});

//Horizontal align
leftAlign.addActionListener(e2 -> {
     int[] selectedRows = table.getSelectedRows();
    int[] selectedCols = table.getSelectedColumns();
    for (int row : selectedRows) {
        for (int col : selectedCols) {
            StyledCell cell = (StyledCell) table.getValueAt(row, col);
            if (cell != null) {
                cell.setHorizontalAlignment(JLabel.LEFT);
            }
        }
    }
    table.repaint();
});
centerAlign.addActionListener(e2 -> {
     int[] selectedRows = table.getSelectedRows();
    int[] selectedCols = table.getSelectedColumns();
    for (int row : selectedRows) {
        for (int col : selectedCols) {
            StyledCell cell = (StyledCell) table.getValueAt(row, col);
            if (cell != null) {
                cell.setHorizontalAlignment(JLabel.CENTER);
            }
        }
    }
    table.repaint();
});
rightAlign.addActionListener(e2 -> {
    int[] selectedRows = table.getSelectedRows();
    int[] selectedCols = table.getSelectedColumns();
    for (int row : selectedRows) {
        for (int col : selectedCols) {
            StyledCell cell = (StyledCell) table.getValueAt(row, col);
            if (cell != null) {
                cell.setHorizontalAlignment(JLabel.RIGHT);
            }
        }
    }
    table.repaint();
});


//Remove Row
removeRowItem.addActionListener(e2 -> {
    int selectedRow = table.getSelectedRow();
    if (selectedRow != -1) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.removeRow(selectedRow);
    }
    sP.revalidate();
    sP.repaint();
});

//Remove Column
removeColumnItem.addActionListener(e2 -> {
    int selectedColumn = table.getSelectedColumn();
    if (selectedColumn != -1) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        TableColumn column = table.getColumnModel().getColumn(selectedColumn);
        table.removeColumn(column);
    }
});
//Add column
addColumnItem.addActionListener(e2 -> {
    DefaultTableModel model = (DefaultTableModel) table.getModel();
    int rowCount = model.getRowCount();
    StyledCell[] newColumn = new StyledCell[rowCount];
    for (int i = 0; i < rowCount; i++) {
        newColumn[i] = new StyledCell("", new Font("Segoe UI", Font.PLAIN, 12), Color.WHITE);
    }
    model.addColumn("", newColumn);
});






colorMenu.add(addRowItem);


table.addMouseListener(new MouseAdapter() {
    @Override
    public void mouseClicked(MouseEvent e) {
        table.requestFocus();
    }
});



               colorMenu.add(colorItem);
               colorMenu.add(addRowItem);
               table.setCellSelectionEnabled(true);
                table.setRowSelectionAllowed(false);
                table.setColumnSelectionAllowed(false);

               table.setComponentPopupMenu(colorMenu);
               
               table.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.isAltDown() && e.getKeyCode() == KeyEvent.VK_ENTER) {
                    // Handle Alt+Enter for line break
                    int row = table.getSelectedRow();
                    int col = table.getSelectedColumn();
                    if (row != -1 && col != -1) {
                        String currentValue = (String) table.getValueAt(row, col);
                        table.setValueAt(currentValue + "\n", row, col);
                        table.setRowHeight(row, table.getRowHeight(row) + 16); // Adjust the row height
                    }
                }
            }
        });
               
               
               
               //Custom Renderer
               table.setDefaultRenderer(Object.class, new StyledCellRenderer());
                table.setDefaultEditor(Object.class, new EmbeddedTableStyledCellEditor(table)); // Assuming you don't need the localRowHeader for this table
                EmbeddedTableStyledCellEditor cellEditor = new EmbeddedTableStyledCellEditor(table);
        table.setDefaultEditor(Object.class, cellEditor);

         // Create a new JScrollPane for this table
            JScrollPane localScrollPane = new JScrollPane(table);
            localScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
            localScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

            
addRowItem.addActionListener(e2 -> {
    DefaultTableModel model = (DefaultTableModel) table.getModel();
    StyledCell[] newRow = new StyledCell[table.getColumnCount()];
    for (int i = 0; i < newRow.length; i++) {
        newRow[i] = new StyledCell("", new Font("Segoe UI", Font.PLAIN, 12), Color.WHITE);
    }
    model.addRow(newRow);
    
    // Calculate the total height of the table including the header height
    int headerHeight = table.getTableHeader().getPreferredSize().height;
    int totalHeight = table.getPreferredSize().height + headerHeight;
    
    // Set the preferred size of the viewport to the total height
    localScrollPane.getViewport().setPreferredSize(new Dimension(table.getPreferredSize().width, totalHeight));
    
    // Revalidate and repaint both the table and the scroll pane
    table.revalidate();
    table.repaint();
    localScrollPane.revalidate();
    localScrollPane.repaint();
});
        //sP = new JScrollPane(table);
                //sP.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

                //sP.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        
 JTextArea editorComponent = cellEditor.getEditorComponent();
 editorComponent.getDocument().addDocumentListener(new CellEditorDocumentListener(table, localScrollPane, editorComponent));

        editorComponent.addFocusListener(new FocusAdapter() {
    @Override
    public void focusLost(FocusEvent e) {
        System.out.println("Editor lost focus FUCKING STUID ASS RRRRRRRRRRRRRRRRRRREEEEEEEEEEER!");
    }
});

        editorComponent.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    colorMenu.show(editorComponent, e.getX(), e.getY());
                }
            }
        });

                //More custom renderer
                StyledCell defaultCell = new StyledCell("", new Font("Segoe UI", Font.PLAIN, 12), Color.WHITE);
                StyledCell[][] data = new StyledCell[rows][columns];
                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < columns; j++) {
                        data[i][j] = new StyledCell("", new Font("Segoe UI", Font.PLAIN, 12), Color.WHITE);
                    }
                }

               String[] columnHeaders = new String[columns];
                Arrays.fill(columnHeaders, "");  // Fill the array with empty strings
                table.setModel(new DefaultTableModel(data, columnHeaders));

                DefaultTableModel model = new DefaultTableModel(data, columnHeaders) {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return true;
                    }
                };
                table.setModel(model);
                table.setSurrendersFocusOnKeystroke(true);


                

                StyledDocument doc = getCurrentTextPane().getStyledDocument();

                Style style = doc.addStyle("tableStyle", null);
                StyleConstants.setComponent(style, localScrollPane);

                doc.insertString(getCurrentTextPane().getCaretPosition(), " ", style);
            } catch (BadLocationException ex) {
                ex.printStackTrace();
            }
        
        //textPane.requestFocus();

    }};//End of table thing
        //bottomPanel.add(insertTableButton);
        // Color Picker button
        JButton colorPickerButton = new JButton();
        colorPickerButton.setPreferredSize(new Dimension(20, 20));
        colorPickerButton.setBackground(Color.WHITE);
        
        
        colorPickerButton.addActionListener(e -> {
            Component selectedComponent = tabbedPane.getSelectedComponent();
            if (selectedComponent instanceof JTabbedPane) {
                Color newColor = JColorChooser.showDialog(frame, "Choose Background Color", Color.WHITE);
                    if (newColor != null) {
                        applySpreadsheetBackgroundColor(newColor);
                        colorPickerButton.setBackground(newColor);
                    }
            } else {
            Color newColor = JColorChooser.showDialog(frame, "Choose Background Color", Color.WHITE);
            if (newColor != null) {
                updateBackgroundColor(newColor);
                colorPickerButton.setBackground(newColor);
            }
        }
        });
        
        
        bottomPanel.add(colorPickerButton);

        frame.add(bottomPanel, BorderLayout.SOUTH);
        
        // New: Add the custom title bar and hamburger menu to the card panel
        cardPanel.add(customTitleBar, "fullMenu");
        cardPanel.add(hamburgerMenu, "hamburgerMenu");

        // New: Add the card panel and font panel to the frame
        northPanel = new JPanel(new BorderLayout());
        northPanel.add(cardPanel, BorderLayout.CENTER);
        northPanel.add(fontPanel, BorderLayout.EAST);
        frame.add(northPanel, BorderLayout.NORTH);

        // New: Listen for window resize events
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int width = frame.getWidth();
                if (width < 500) {
                    cardLayout.show(cardPanel, "hamburgerMenu");
                } else {
                    cardLayout.show(cardPanel, "hamburgerMenu");
                }
            }
        });
        
        ImageIcon icon = new ImageIcon("path/to/image.jpg");
textPane.insertIcon(icon);

tabbedPane = new JTabbedPane();
        addNewTab("NOTE");  // Add the first tab
        frame.add(tabbedPane, BorderLayout.CENTER);
        
        //Style buttons
        
        // Create buttons for each style
        boldButton.addActionListener(e -> {
            toggleStyle("bold");
            getCurrentTextPane().requestFocusInWindow();
        });

        italicButton.addActionListener(e -> {
                    toggleStyle("italic");
                    getCurrentTextPane().requestFocusInWindow();
                });

        underlineButton.addActionListener(e -> {
            toggleStyle("underline");
                    getCurrentTextPane().requestFocusInWindow();;
        });

        strikeThroughButton.addActionListener(e -> {
            toggleStyle("strikeThrough");
                    getCurrentTextPane().requestFocusInWindow();
        });

        
JButton newTabButton = new JButton("+");
        newTabButton.addActionListener(e -> addNewTab("NOTE"));
    

        




                // New: Listen for tab changes to update the current textPane
      tabbedPane.addChangeListener(new ChangeListener() {
         
            public void stateChanged(ChangeEvent e) {
    Component selectedComponent = tabbedPane.getSelectedComponent();
    if (selectedComponent instanceof JTextPane) {
        JTextPane textPane = (JTextPane) selectedComponent;
        textPane = (JTextPane) ((JScrollPane) tabbedPane.getSelectedComponent()).getViewport().getView();
        
           if (textPane != null) {
            textPane.addCaretListener(ea -> updateButtonStates());
        }

    } else if (selectedComponent instanceof JList) {
        JList<?> todoList = (JList<?>) selectedComponent;
        // Code to handle the TODO list
    }
}

        });
      

     
     
      bottomPanel.add(bulletButton);
        bottomPanel.add(numberButton);
        
        //Undo button and related activities
    JButton undoButton = new JButton("U");



    JButton redoButton = new JButton("R");
    
    
    
    
    //bottomPanel.add(redoButton);



   // bottomPanel.add(undoButton);
    
    bottomPanel.add(boldButton);
    bottomPanel.add(italicButton);
    bottomPanel.add(underlineButton);
    bottomPanel.add(strikeThroughButton);
    menuButton = new JButton(new ImageIcon("src/iconToolbar.png"));
            menuButton.setBorder(null);
            menuButton.setBackground(frameBackgroundColor);
    JPopupMenu toolBarMenu = new JPopupMenu();
            JToolBar toolBar = new JToolBar(":asd"); 
               toolBar.add(boldButton);
        toolBar.add(italicButton);
        toolBar.add(underlineButton);
        toolBar.add(strikeThroughButton);
        toolBar.addSeparator();
        
        
        toolBarMenu.add(toolBar);
         // Add an action listener to the button to show the popup menu
        menuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Show the popup menu below the button
                toolBarMenu.show(menuButton, 0, menuButton.getHeight());
            }
        });
    fontColorButton = new JButton();
           fontPanel.add(menuButton);
     boldButton.setIcon(new ImageIcon("src/iconBold.png"));
        //boldButton.setBorder(null);
        //boldButton.setFocusPainted(false);
        //boldButton.setContentAreaFilled(false);
     italicButton.setIcon(new ImageIcon("src/iconItalic_b.png"));
        //italicButton.setBorder(null);
        //italicButton.setFocusPainted(false);
        //italicButton.setContentAreaFilled(false);
     underlineButton.setIcon(new ImageIcon("src/iconUnderline_b.png"));
        //underlineButton.setBorder(null);
        //underlineButton.setFocusPainted(false);
        //underlineButton.setContentAreaFilled(false);
     strikeThroughButton.setIcon(new ImageIcon("src/iconStrikethru_b.png"));
        //strikeThroughButton.setBorder(null);
        //strikeThroughButton.setFocusPainted(false);
        //strikeThroughButton.setContentAreaFilled(false);
    fontColorButton.setIcon( new ImageIcon("src/fontColorIcon.gif"));
    //fontColorButton.setBorder(null);
        //fontColorButton.setFocusPainted(false);
        //fontColorButton.setContentAreaFilled(false);
fontColorButton.addActionListener(e -> {
    Color newColor = openCustomColorChooser(frame);

    if (newColor != null) {
        Component selectedComponent = tabbedPane.getSelectedComponent();

        if (lastFocusedComponent instanceof JTextPane) {
            // Handle JTextPane (Notes)
            applyNoteForegroundColor(newColor);
        } 
        else if (lastFocusedComponent instanceof JTable) {
            // Handle JTable (Could be either Embedded Table or ToDo List)
            JTable focusedTable = (JTable) lastFocusedComponent;
            int[] selectedRows = focusedTable.getSelectedRows();
            int[] selectedCols = focusedTable.getSelectedColumns();

            if (selectedComponent instanceof JTabbedPane) {
                // This is a Spreadsheet
                applySpreadsheetForegroundColor(newColor);
            } else {
                // This is either an embedded table or a ToDo List
                for (int row : selectedRows) {
                    for (int col : selectedCols) {
                        Object value = focusedTable.getValueAt(row, col);
                        if (value instanceof StyledCell) {
                            StyledCell styledCell = (StyledCell) value;
                            styledCell.setForegroundColor(newColor); // Assuming you have a setForegroundColor() method
                            focusedTable.setValueAt(styledCell, row, col);
                        } else {
                            // This should be a ToDo List, apply color accordingly
                            applyNoteForegroundColor(newColor);
                        }
                    }
                }
                focusedTable.repaint();
                focusedTable.revalidate();
            }
        } 
        else {
            // If it's neither JTextPane nor JTable, apply the note foreground color by default
            applyNoteForegroundColor(newColor);
        }
    }
});

//bottomPanel.add(fontColorButton);
toolBar.add(fontColorButton);
        
     /* // Add a key binding for the Enter key
        InputMap inputMap = textPane.getInputMap();
        ActionMap actionMap = textPane.getActionMap();

        inputMap.put(KeyStroke.getKeyStroke("ENTER"), "insertBullet");
        actionMap.put("insertBullet", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                insertBulletOnNewLine();
            }
        });*/
      //JTabbedPane tabbedPane = new JTabbedPane();
      //RIGHT CLICK TAB MENU
tabbedPane.addMouseListener(new MouseAdapter() {
    public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            int tabIndex = tabbedPane.indexAtLocation(e.getX(), e.getY());
            if (tabIndex >= 0) {
                JPopupMenu popupMenu = new JPopupMenu();
                
                // Create a submenu for adding new tabs
                JMenu newTabItem = new JMenu("New Tab");
                
                // Add options to the submenu
                addButtonToPopupMenu("Note", e2 -> addNewTab("NOTE"), newTabItem);
                addButtonToPopupMenu("To-do List", e2 -> addNewTab("TODO"), newTabItem);
                addButtonToPopupMenu("Spreadsheet", e2 -> addNewTab("SPREADSHEET"), newTabItem);
                JMenuItem pinItem = new JMenuItem("Pin");
                JMenuItem closeItem = new JMenuItem("Close Tab");
                //JMenuItem newTabItem = new JMenuItem("New Tab");
                JMenuItem changeColorItem = new JMenuItem("Change Tab Color");
                JMenuItem renameTabItem = new JMenuItem("Rename Tab");


                //closeItem.addActionListener(ev -> tabbedPane.remove(tabIndex));
                closeItem.addActionListener(ev -> 
                        
                        removeTab(tabIndex));
                //newTabItem.addActionListener(ev -> addNewTab("NOTE"));
                changeColorItem.addActionListener(ev -> changeTabColor(tabIndex));
                renameTabItem.addActionListener(ev -> renameTab(tabIndex));
                
                pinItem.addActionListener(ev -> pinTab(tabIndex));
                
                popupMenu.add(newTabItem);
                popupMenu.add(pinItem);
                popupMenu.add(closeItem);
                popupMenu.add(changeColorItem);
                popupMenu.add(renameTabItem);
                popupMenu.show(tabbedPane, e.getX(), e.getY());
            }
        }
    }
});
tabbedPane.addChangeListener(new ChangeListener() {
    public void stateChanged(ChangeEvent e) {
        Component selectedComponent = tabbedPane.getSelectedComponent();
        if (selectedComponent instanceof JScrollPane) {
            JScrollPane scrollPane = (JScrollPane) selectedComponent;
            Component viewComponent = scrollPane.getViewport().getView();
            lastFocusedComponent = viewComponent; // set to either JTextPane or JTable of the current tab
        }
    }
});

final Point initialClickPoint = new Point();
final int[] draggedTabIndex = {-1};

tabbedPane.addMouseListener(new MouseAdapter() {
    @Override
    public void mousePressed(MouseEvent e) {
        initialClickPoint.setLocation(e.getPoint());
        draggedTabIndex[0] = tabbedPane.indexAtLocation(e.getX(), e.getY());
        tabbedPane.setCursor(new Cursor(Cursor.HAND_CURSOR));  // Set cursor to hand on press
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        draggedTabIndex[0] = -1;
        tabbedPane.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));  // Set cursor back to default on release
    }
});

tabbedPane.addMouseMotionListener(new MouseMotionAdapter() {
    @Override
    public void mouseDragged(MouseEvent e) {
        int dx = Math.abs(e.getX() - initialClickPoint.x);
        int dy = Math.abs(e.getY() - initialClickPoint.y);

        // Only start dragging if moved a certain distance (e.g., 10 pixels)
        if (dx < 10 && dy < 10) {
            return;
        }

        int currentTabIndex = tabbedPane.indexAtLocation(e.getX(), e.getY());
        if (currentTabIndex != draggedTabIndex[0] && currentTabIndex != -1 && draggedTabIndex[0] != -1) {
            Component component = tabbedPane.getComponentAt(draggedTabIndex[0]);
            String title = tabbedPane.getTitleAt(draggedTabIndex[0]);
            Color color = tabColors.get(draggedTabIndex[0]);

            tabbedPane.remove(draggedTabIndex[0]);
            tabColors.remove(draggedTabIndex[0]);

            tabbedPane.insertTab(title, null, component, null, currentTabIndex);
            tabColors.add(currentTabIndex, color);

            tabbedPane.setSelectedIndex(currentTabIndex);  // Set the selected index to the new index of the dragged tab

            draggedTabIndex[0] = currentTabIndex;
        }
    }
});

/* try {
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
} catch (Exception e) {
    e.printStackTrace();
} */


        // Right-click context menu
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem cutItem = new JMenuItem("Cut");
        JMenuItem copyItem = new JMenuItem("Copy");
        JMenuItem pasteItem = new JMenuItem("Paste");
        JMenuItem selectAllItem = new JMenuItem("Select All");
        JMenuItem resetSizeItem = new JMenuItem("Reset Size");
        
       

        cutItem.addActionListener(e -> textPane.cut());
        copyItem.addActionListener(e -> textPane.copy());
        pasteItem.addActionListener(e -> textPane.paste());
        selectAllItem.addActionListener(e -> textPane.selectAll());
        resetSizeItem.addActionListener(e -> {
                 frame.setSize(360, 300);
                frame.setLocation(Toolkit.getDefaultToolkit().getScreenSize().width - frame.getWidth(), Toolkit.getDefaultToolkit().getScreenSize().height - frame.getHeight() - 40);
               
        }
);
        
        insertTableItem.addActionListener(tableInsertionListener);
        insertItem.add(insertTableItem);
        popupMenu.add(insertItem);
        popupMenu.add(new JSeparator ());
        popupMenu.add(cutItem);
        popupMenu.add(copyItem);
        popupMenu.add(pasteItem);
        popupMenu.add(selectAllItem);
        popupMenu.add(resetSizeItem);

        textPane.setComponentPopupMenu(popupMenu);
        textPane.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                lastFocusedComponent = textPane;
            }
        });
   
        
        //undoGroupingTimer.setRepeats(false); // The timer should only trigger once after an edit
        // For each JTextPane:
        textPane.getDocument().addUndoableEditListener(undoManager);

        // Color buttons
        /*JPanel colorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton whiteButton = createColorButton(Color.WHITE);
        JButton tanButton = createColorButton(new Color(210, 180, 140));
        JButton blackButton = createColorButton(Color.BLACK);
        whiteButton.addActionListener(e -> updateBackgroundColor(Color.WHITE));
        tanButton.addActionListener(e -> updateBackgroundColor(new Color(210, 180, 140)));
        blackButton.addActionListener(e -> updateBackgroundColor(Color.BLACK));
        colorPanel.add(whiteButton);
        colorPanel.add(tanButton);
        colorPanel.add(blackButton);
        frame.add(colorPanel, BorderLayout.SOUTH);*/
         // Initialize with default colors
        //tabColors.add(Color.WHITE);

           // Create and set the custom UI
        customUI = new CustomTabbedPaneUI(tabColors);
        customUI2 = new CustomFlatLafTabbedPaneUI(tabColors);
        
               if (tabStyle ==0 )
        {
            tabbedPane.setUI(customUI2);
        }else if(tabStyle ==1){
            tabbedPane.setUI(customUI);
        }
        
        // Add resize functionality
        frame.addMouseMotionListener(new MouseMotionAdapter() {
            private Point lastPoint = null;

            @Override
            public void mouseDragged(MouseEvent e) {
                Point point = e.getLocationOnScreen();
                if (lastPoint != null) {
                    int offsetX = point.x - lastPoint.x;
                    int offsetY = point.y - lastPoint.y;
                    int newWidth = frame.getWidth() - offsetX;
                    int newHeight = frame.getHeight() - offsetY;

                    // Enforce size limits
                    newWidth = Math.min(Math.max(newWidth, 320), Toolkit.getDefaultToolkit().getScreenSize().width);
                    newHeight = Math.min(Math.max(newHeight, 200), Toolkit.getDefaultToolkit().getScreenSize().height);

                    frame.setSize(newWidth, newHeight);
                    frame.setLocation(Toolkit.getDefaultToolkit().getScreenSize().width - frame.getWidth(), Toolkit.getDefaultToolkit().getScreenSize().height - frame.getHeight() - 40);
                }
                lastPoint = point;
            }
        });

        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException e) {
            e.printStackTrace();
        }

        GlobalScreen.addNativeKeyListener(new GlobalKeyListener());
        restoreState();
    }

private JButton createColorButton(Color color) {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(20, 20));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);

        try {
            BufferedImage img = ImageIO.read(new File("src/iconCircle.png"));
            BufferedImage tintedImage = tintImage(img, color);
            button.setIcon(new ImageIcon(tintedImage));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return button;
    }

private BufferedImage tintImage(BufferedImage image, Color color) {
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                int rgba = image.getRGB(x, y);
                Color col = new Color(rgba, true);
                col = new Color((int) ((col.getRed() * color.getRed() / 255.0)),
                                (int) ((col.getGreen() * color.getGreen() / 255.0)),
                                (int) ((col.getBlue() * color.getBlue() / 255.0)),
                                col.getAlpha());
                image.setRGB(x, y, col.getRGB());
            }
        }
        return image;
    }
    
// New: Method to show the popup menu when the hamburger menu is clicked
private void showPopupMenu(JButton button) {
        popupMenu.show(button, 0, button.getHeight());
    }

// In the UndoableEditListener:
public void undoableEditHappened(UndoableEditEvent e) {
    undoManager.addEdit(e.getEdit());
    //undoGroupingTimer.restart();
}

// New: Utility method to add a button to the popup menu
private void addButtonToPopupMenu(String title, ActionListener actionListener, JComponent parentMenu) {
    JMenuItem menuItem = new JMenuItem(title);
    menuItem.addActionListener(actionListener);
    parentMenu.add(menuItem);
}

private void addCheckBoxToPopupMenu(String title, ActionListener actionListener, JComponent parentMenu) {
    JCheckBoxMenuItem menuItem = new JCheckBoxMenuItem(title);
    menuItem.addActionListener(actionListener);
    parentMenu.add(menuItem);
}

    
// New: Method to add a new tab
private void addNewTab(String selectedType) {
        //String selectedType = (String) noteTypeSelector.getSelectedItem();
        if ("NOTE".equals(selectedType)) {
            addNoteTab();
        } else if ("TODO".equals(selectedType)) {
            addTodoTab();
        } else {
            addSpreadsheetTab();
        }
JTextPane newTextPane = new JTextPane();
        // Calculate the luminance of the new color
        double luminance = 0.299 * frameBackgroundColor.getRed() + 0.587 * frameBackgroundColor.getGreen() + 0.114 * frameBackgroundColor.getBlue();
        // Choose the font color based on the luminance
        Color fontColor = (luminance < 128) ? Color.WHITE : Color.BLACK;

        Color brighterColor = frameBackgroundColor.brighter();
        
        if (frameBackgroundColor.equals(Color.BLACK)){
            brighterColor = Color.DARK_GRAY;
        }else{
            frameBackgroundColor.brighter();
        }

        // Add a default color for the new tab
        tabColors.add(brighterColor);  // Or any other default color
        tabbedPane.setForegroundAt(tabColors.size()-1, fontColor);
        tabbedPane.repaint();  // Repaint to apply the new color
    }
    
        // New: Method to add a Note tab
private void addNoteTab() {
JTextPane newTextPane = new JTextPane();
   UndoManager newUndoManager = new UndoManager();
newTextPane.getDocument().addUndoableEditListener(newUndoManager);

newTextPane.getDocument().addDocumentListener(new DocumentListener() {
    @Override
    public void insertUpdate(DocumentEvent e) {
        markAsUnsaved();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        markAsUnsaved();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        markAsUnsaved();
    }

    private void markAsUnsaved() {
        int selectedIndex = tabbedPane.getSelectedIndex();
        if (selectedIndex != -1) {
            unsavedChanges.set(selectedIndex, true);
        }
    }
});

// Store the association in the map
undoManagers.put(newTextPane, newUndoManager);

        // Right-click context menu
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem cutItem = new JMenuItem("Cut");
        JMenuItem copyItem = new JMenuItem("Copy");
        JMenuItem pasteItem = new JMenuItem("Paste");
        JMenuItem selectAllItem = new JMenuItem("Select All");
        JMenuItem resetSizeItem = new JMenuItem("Reset Size");
        JMenuItem insertTableItem = new JMenuItem("Table");
              JMenu insertItem = new JMenu("Insert");
              
        insertTableItem.addActionListener(tableInsertionListener);
        insertItem.add(insertTableItem);
        
        cutItem.addActionListener(e -> newTextPane.cut());
        copyItem.addActionListener(e -> newTextPane.copy());
        pasteItem.addActionListener(e -> newTextPane.paste());
        selectAllItem.addActionListener(e -> newTextPane.selectAll());
        resetSizeItem.addActionListener(e -> frame.setSize(300, 200));
        
            // Attach KeyAdapter for handling paste
  newTextPane.addKeyListener(new KeyAdapter() {
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_V) {
            handlePaste(newTextPane);  // Pass the current JTextPane
        }
    }
});

        
    
    

  
        popupMenu.add(insertItem);
        popupMenu.add(new JSeparator());
        popupMenu.add(cutItem);
        popupMenu.add(copyItem);
        popupMenu.add(pasteItem);
        popupMenu.add(selectAllItem);
        popupMenu.add(resetSizeItem);

        newTextPane.setComponentPopupMenu(popupMenu);
        
        // Add the new tab
        JScrollPane scrollPane = new JScrollPane(newTextPane);

        tabbedPane.addTab("New Tab", scrollPane);
        unsavedChanges.add(false);
        tabbedPane.setSelectedComponent(scrollPane);
        textPane = newTextPane;  // Update the current textPane
        // Make the window always on top
        attachListenersToTextPane(newTextPane);

        if(bgColorSyncOption)
        {
            if (frameBackgroundColor != null && frameBackgroundColor != defaultColor){
                updateBackgroundColor(frameBackgroundColor);
        }
            else if(frameBackgroundColor == defaultColor){
                updateBackgroundColor(Color.WHITE);
             }
        }
        else
        {
            updateBackgroundColor(Color.WHITE);
        }
        


    }
    private JTextPane getCurrentTextPane() {
    Component selectedComponent = tabbedPane.getSelectedComponent();
    if (selectedComponent instanceof JScrollPane) {
        Component viewComponent = ((JScrollPane) selectedComponent).getViewport().getView();
        if (viewComponent instanceof JTextPane) {
            return (JTextPane) viewComponent;
        }
    }
    return null;
}

    private JTextPane getActiveTextPane() {
    Component selectedComponent = tabbedPane.getSelectedComponent();
    if (selectedComponent instanceof JScrollPane) {
        JScrollPane scrollPane = (JScrollPane) selectedComponent;
        Component viewComponent = scrollPane.getViewport().getView();
        if (viewComponent instanceof JTextPane) {
            return (JTextPane) viewComponent;
        }
    }
    return null;
}

    
    private void attachListenersToTextPane(JTextPane textPane) {
    textPane.getStyledDocument().addDocumentListener(new DocumentListener() {
        public void insertUpdate(DocumentEvent e) {
            isProgrammaticUpdate = true;
            updateButtonStates();
            syncFontUI();
            isProgrammaticUpdate = false;
        }
        public void removeUpdate(DocumentEvent e) {
            isProgrammaticUpdate = true;
            updateButtonStates();
            syncFontUI();
            isProgrammaticUpdate = false;
        }
        public void changedUpdate(DocumentEvent e) {
            isProgrammaticUpdate = true;
            updateButtonStates();
            syncFontUI();
            isProgrammaticUpdate = false;
        }
    });

    textPane.addCaretListener(new CaretListener() {
        @Override
        public void caretUpdate(CaretEvent e) {
            isProgrammaticUpdate = true;
            updateButtonStates();
            syncFontUI();
            
            isProgrammaticUpdate = false;
        }
    });
    
    textPane.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            isProgrammaticUpdate = true;
            updateButtonStates();
            syncFontUI();
            isProgrammaticUpdate = false;
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            isProgrammaticUpdate = true;
            updateButtonStates();
            syncFontUI();
            isProgrammaticUpdate = false;
        }
    });
}

     // New: Method to remove a tab
private void removeTab(int tabIndex) {
    if (tabIndex >= 0 && tabIndex < tabbedPane.getTabCount()) {
        // Check if there are unsaved changes
        if (hasUnsavedChanges) {
            int dialogResult = JOptionPane.showConfirmDialog(
                    null, 
                    "You have unsaved changes. Do you want to save them before closing?", 
                    "Warning", 
                    JOptionPane.YES_NO_CANCEL_OPTION
            );
            switch (dialogResult) {
                case JOptionPane.YES_OPTION:
                    // Save changes here
                    hasUnsavedChanges = false;
                    break;
                case JOptionPane.NO_OPTION:
                    // Continue to close the tab without saving
                    break;
                case JOptionPane.CANCEL_OPTION:
                default:
                    return; // Cancel the tab close operation
            }
        }

        tabbedPane.remove(tabIndex);
        if (tabIndex < tabColors.size()) {
            tabColors.remove(tabIndex);
        }
        tabbedPane.repaint();  // Repaint to apply the changes
    }
}

private void pinTab(int tabIndex) {
    if (tabIndex > 0) {
        Component component = tabbedPane.getComponentAt(tabIndex);
        String title = tabbedPane.getTitleAt(tabIndex);
        Color color = tabColors.get(tabIndex);

        tabbedPane.remove(tabIndex);
        tabColors.remove(tabIndex);

        tabbedPane.insertTab(title, null, component, null, 0);
        tabColors.add(0, color);

        tabbedPane.setSelectedIndex(0);
    }
}

private void changeTabStyle(JTabbedPane currentPane){
        if (tabStyle ==0 )
        {
            
            tabbedPane.setUI(customUI);
            tabStyle =1;
            tabbedPane.repaint();
        }else if(tabStyle ==1){
            tabbedPane.setUI(customUI2);
            tabbedPane.repaint();
            tabStyle=0;
            
        }
               
        if (tabStyle ==0)
        {
            tabStyleName = "Modern";
            
            tabStyleButton.setText("Tab Style: " + tabStyleName);
            tabStyleButton.repaint();
            popupMenu.repaint();
        }else{
            tabStyleName = "Classic";
            tabStyleButton.setText("Tab Style: " + tabStyleName);
            tabStyleButton.repaint();
            popupMenu.repaint();
        }   
}


private boolean isEditingAllowed = true;
    
private int taskCounter = 1; // Counter for task numbering

private void addTodoTab() {
    ImageIcon iconCheck = new ImageIcon("src/iconCheck_b.png");
    ImageIcon iconUncheck = new ImageIcon("src/iconunCheck_b.png");

 DefaultTableModel model = new DefaultTableModel(new Object[]{"Status", "Task"}, 0);
 JTable todoTable = new JTable(model) {
        @Override
        public Class<?> getColumnClass(int column) {
            if (column == 0) {
                return Boolean.class;  // Explicitly set the first column to Boolean
            }
            return super.getColumnClass(column);
        }
    @Override
    public boolean isCellEditable(int row, int column) {
        if (!isEditingAllowed) {
            return false;
        }
        if (column == 0) { // Assuming the checkbox is in the first column
            return true;
        }
        return super.isCellEditable(row, column);
    }

@Override
        public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
            Component c = super.prepareRenderer(renderer, row, column);
            // Removed border setting code
            return c;
        }
        
@Override
    public void setRowHeight(int rowHeight) {
        int iconHeight = Math.max(iconCheck.getIconHeight(), iconUncheck.getIconHeight());
        super.setRowHeight(Math.max(rowHeight, iconHeight + 10)); // Adding 10 for padding
    }
    };
    
    
       
int iconHeight = Math.max(iconCheck.getIconHeight(), iconUncheck.getIconHeight());
    todoTable.setRowHeight(Math.max(todoTable.getRowHeight(), iconHeight + 10)); // Adding 10 for padding

    todoTable.addPropertyChangeListener("font", new PropertyChangeListener() {
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        int iconHeight = Math.max(iconCheck.getIconHeight(), iconUncheck.getIconHeight());
        todoTable.setRowHeight(Math.max(todoTable.getRowHeight(), iconHeight + 10)); // Adding 10 for padding
    }
        });
    
    todoTable.addMouseMotionListener(new MouseAdapter() {
    @Override
    public void mouseMoved(MouseEvent e) {
        int col = todoTable.columnAtPoint(e.getPoint());
        if (col == 0) { // Assuming the checkbox is in the first column
            todoTable.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        } else {
            todoTable.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }
});
    
    todoTable.setDragEnabled(true);
    todoTable.setDropMode(DropMode.INSERT_ROWS);
    todoTable.setTransferHandler(new TableRowTransferHandler(todoTable));


    
    todoTable.addMouseListener(new MouseAdapter() {
    @Override
    public void mouseReleased(MouseEvent e) {
        int row = todoTable.rowAtPoint(e.getPoint());
        int col = todoTable.columnAtPoint(e.getPoint());
        
        if (row >= 0 && col >= 0) { // Check that the row and column are valid
            if (col == 0) { // Assuming the checkbox is in the first column
                Object cellValue = todoTable.getValueAt(row, col);
                boolean value;
                if (cellValue instanceof Boolean) {
                    value = (Boolean) cellValue;
                } else if (cellValue instanceof String) {
                    value = Boolean.valueOf((String) cellValue);
                } else {
                    return;  // or handle this case as appropriate
                }

                CustomCheckBoxEditor editor = (CustomCheckBoxEditor) todoTable.getCellEditor();
                    if (editor != null) {
                        editor.currentVal = !value;
                        editor.stopCellEditing();
                    }

            }
        }
    }
      @Override
    public void mousePressed(MouseEvent e) {
        Point point = e.getPoint();
        int row = todoTable.rowAtPoint(point);
        int col = todoTable.columnAtPoint(point);
        
        if (!todoTable.isRowSelected(row)) {
            todoTable.setRowSelectionInterval(row, row);
        }
        if (!todoTable.isColumnSelected(col)) {
            todoTable.setColumnSelectionInterval(col, col);
        }
    }
    
});
    
JPopupMenu popupMenu = new JPopupMenu();
JMenuItem deleteItem = new JMenuItem("Delete");
deleteItem.addActionListener(e -> {
    int[] selectedRows = todoTable.getSelectedRows();
    for (int i = selectedRows.length - 1; i >= 0; i--) {
        model.removeRow(selectedRows[i]);
    }
});
popupMenu.add(deleteItem);

// Add a new menu item for changing the checkbox color
JMenuItem changeColorItem = new JMenuItem("Change Checkbox Color");
changeColorItem.addActionListener(e -> {
    Color newColor = JColorChooser.showDialog(null, "Choose Checkbox Color", Color.WHITE);
    if (newColor != null) {
        int[] selectedRows = todoTable.getSelectedRows();
        CustomCheckBoxRenderer renderer = (CustomCheckBoxRenderer) todoTable.getColumnModel().getColumn(0).getCellRenderer();
        for (int row : selectedRows) {
            renderer.setCellColor(row, 0, newColor);  // Assuming the checkbox is in the first column
        }
        todoTable.repaint();  // This is important to refresh the table and reflect the changes
    }
});
popupMenu.add(changeColorItem);


todoTable.setComponentPopupMenu(popupMenu);

    
    CustomCheckBoxRenderer renderer = new CustomCheckBoxRenderer(iconUncheck, iconCheck);
    todoTable.getColumnModel().getColumn(0).setCellRenderer(renderer);
    todoTable.getColumnModel().getColumn(0).setCellEditor(new CustomCheckBoxEditor(renderer));

    

todoTable.getColumnModel().getColumn(0).setCellRenderer(new CustomCheckBoxRenderer(iconUncheck, iconCheck));
todoTable.getColumnModel().getColumn(0).setCellEditor(new CustomCheckBoxEditor(new CustomCheckBoxRenderer(iconUncheck, iconCheck)));



    // Add a default row with placeholder text
    model.addRow(new Object[]{false, "Task goes here"});

    // Add a key listener to handle the Enter key
    todoTable.addKeyListener(new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                model.addRow(new Object[]{false, "Task goes here"});
            }
        }
    });

    // Remove cell borders and header
    todoTable.setShowGrid(false);
    todoTable.setIntercellSpacing(new Dimension(0, 0));
    todoTable.setTableHeader(null);

    // Adjust column widths
    todoTable.getColumnModel().getColumn(0).setPreferredWidth(50);
    todoTable.getColumnModel().getColumn(1).setPreferredWidth(200);
    todoTable.setName("TodoTable");
    
    JScrollPane scrollPane = new JScrollPane(todoTable);


    tabbedPane.addTab("New Todo", scrollPane);
    tabbedPane.setSelectedComponent(scrollPane);
scrollPane.putClientProperty("tabType", "todo");
    // Request focus when the tab is selected
    tabbedPane.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
            if (tabbedPane.getSelectedComponent() == scrollPane) {
                todoTable.requestFocus();
            }
        }
    });
    
    todoTable.setSelectionBackground(Color.LIGHT_GRAY);
/* textPane.addKeyListener(new KeyAdapter() {
    @Override
    public void keyPressed(KeyEvent e) {
        System.out.println("Key pressed: " + e.getKeyCode());  // Debug print
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            insertBulletOnNewLine();
        }
    } 
});*/


    // Update table colors when document background changes
    todoTable.setBackground(textPane.getBackground());
    todoTable.setForeground(textPane.getForeground());
}

private ImageIcon blendColor(ImageIcon icon, Color newColor) {
    int w = icon.getIconWidth();
    int h = icon.getIconHeight();
    BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
    Graphics g = bi.createGraphics();
    icon.paintIcon(null, g, 0, 0);
    g.dispose();

    for (int i = 0; i < w; i++) {
        for (int j = 0; j < h; j++) {
            int pixel = bi.getRGB(i, j);
            if ((pixel >> 24) != 0x00) { // Check if pixel is not transparent
                Color originalColor = new Color(pixel, true);
                Color blendedColor = new Color(
                    (originalColor.getRed() + newColor.getRed()),
                    (originalColor.getGreen() + newColor.getGreen()),
                    (originalColor.getBlue() + newColor.getBlue()) ,
                    originalColor.getAlpha()
                );
                bi.setRGB(i, j, blendedColor.getRGB());
            }
        }
    }

    return new ImageIcon(bi);
}

private void applyTodoListStyling() {
     System.out.println("Inside applyTodoListStyling");
    String selectedFont = (String) fontSelector.getSelectedItem();
    int selectedSize = (int) fontSizeSelector.getSelectedItem();
    int style = Font.PLAIN;
    if (boldButton.isSelected()) style |= Font.BOLD;
    if (italicButton.isSelected()) style |= Font.ITALIC;
        System.out.println("Selected Font: " + selectedFont);
    System.out.println("Selected Size: " + selectedSize);
    System.out.println("Style: " + style);
    Component selectedComponent = tabbedPane.getSelectedComponent();
    if (selectedComponent instanceof JScrollPane) {
        JScrollPane scrollPane = (JScrollPane) selectedComponent;
        Component viewComponent = scrollPane.getViewport().getView();
        if (viewComponent instanceof JTable) {
            JTable todoTable = (JTable) viewComponent;
            todoTable.setFont(new Font(selectedFont, style, selectedSize));
            todoTable.setRowHeight(selectedSize + 10);  // Adjust row height
        }
    }
}

private boolean isTodoTab() {
    Component selectedComponent = tabbedPane.getSelectedComponent();
    if (selectedComponent instanceof JScrollPane) {
        JScrollPane scrollPane = (JScrollPane) selectedComponent;
        Component viewComponent = scrollPane.getViewport().getView();
        if (viewComponent instanceof JTable) {
            JTable table = (JTable) viewComponent;
            return "TodoTable".equals(table.getName());
        }
    }
    return false;
}




private Font defaultFont = new Font("Segoe UI", Font.PLAIN, 12);

public void setDefaultFont(Font font) {
    this.defaultFont = font;
}

/*////////////////////////////////

    Style Button methods/Bullets/Numbering

*////////////////////////////////


private void toggleStyle(String style) {
    SwingUtilities.invokeLater(() -> {
        if (lastFocusedComponent instanceof JTextPane) {
            JTextPane textPane = (JTextPane) lastFocusedComponent;
            AttributeSet attrs = textPane.getInputAttributes();
            SimpleAttributeSet sas = new SimpleAttributeSet(attrs);

            switch (style) {
                case "bold":
                    StyleConstants.setBold(sas, !StyleConstants.isBold(attrs));
                    break;
                case "italic":
                    StyleConstants.setItalic(sas, !StyleConstants.isItalic(attrs));
                    break;
                case "underline":
                    StyleConstants.setUnderline(sas, !StyleConstants.isUnderline(attrs));
                    break;
                case "strikeThrough":
                    StyleConstants.setStrikeThrough(sas, !StyleConstants.isStrikeThrough(attrs));
                    break;
            }

            int start = textPane.getSelectionStart();
            int end = textPane.getSelectionEnd();
            if (start != end) {
                textPane.getStyledDocument().setCharacterAttributes(start, end - start, sas, false);
            }
            textPane.setCharacterAttributes(sas, false);
        }  else if (lastFocusedComponent instanceof JTable) {
            JTable focusedTable = (JTable) lastFocusedComponent;
            int[] selectedRows = focusedTable.getSelectedRows();
            int[] selectedCols = focusedTable.getSelectedColumns();

            for (int row : selectedRows) {
                for (int col : selectedCols) {
                    Object value = focusedTable.getValueAt(row, col);
                    if (value instanceof StyledCell) {
                        StyledCell styledCell = (StyledCell) value;
                        Font currentFont = styledCell.getFont();
                        int currentStyle = currentFont.getStyle();

                        switch (style) {
                            case "bold" -> currentStyle ^= Font.BOLD; // Toggle bold
                            case "italic" -> currentStyle ^= Font.ITALIC; // Toggle italic
                            // Underline and strikethrough would go here, but they are more complex
                        }

                        Font newFont = currentFont.deriveFont(currentStyle);
                        styledCell.setFont(newFont);
                        focusedTable.setValueAt(styledCell, row, col);
                    }
                }
            }
            focusedTable.repaint();
            focusedTable.revalidate();
            focusedTable.requestFocusInWindow();  // Explicitly request focus
        }
        // Additional logic to handle spreadsheets and to-do lists can be inserted here.
        updateButtonStates();
    });
}

private void updateButtonStates() {
    JTextPane textPane = getCurrentTextPane();
    if (textPane != null) {
        AttributeSet attrs;
        int start = textPane.getSelectionStart();
        int end = textPane.getSelectionEnd();
        if (start != end) {
            attrs = textPane.getStyledDocument().getCharacterElement(start).getAttributes();
        } else {
            attrs = textPane.getInputAttributes();
        }

        boldButton.setSelected(StyleConstants.isBold(attrs));
        italicButton.setSelected(StyleConstants.isItalic(attrs));
        underlineButton.setSelected(StyleConstants.isUnderline(attrs));
        strikeThroughButton.setSelected(StyleConstants.isStrikeThrough(attrs));
    }
}



private Object getCurrentComponent() {
    Component selectedComponent = tabbedPane.getSelectedComponent();
    if (selectedComponent instanceof JScrollPane) {
        Component viewComponent = ((JScrollPane) selectedComponent).getViewport().getView();
        if (viewComponent instanceof JTextPane) {
            return (JTextPane) viewComponent;
        } else if (viewComponent instanceof JTable) {
            return (JTable) viewComponent;
        }
    }
    return null;
}


private void insertBulletOnNewLine() {
    try {
        int caretPosition = textPane.getCaretPosition();
        StyledDocument doc = textPane.getStyledDocument();

        // Find the start of the line
        Element root = doc.getDefaultRootElement();
        int line = root.getElementIndex(caretPosition);
        int startOfLine = root.getElement(line).getStartOffset();

        // Debug print
        System.out.println("Caret position: " + caretPosition);
        System.out.println("Line: " + line);
        System.out.println("Start of line: " + startOfLine);

        // Check if the line starts with a bullet point
        String lineText = doc.getText(startOfLine, caretPosition - startOfLine);
        System.out.println("Line text: '" + lineText + "'");  // Debug print

        if (lineText.startsWith("\u2022\t")) {
            // Insert a new bullet point on the next line
            doc.insertString(caretPosition, "\n\u2022\t", null);
        } else {
            // Insert a regular new line
            doc.insertString(caretPosition, "\n", null);
        }
    } catch (BadLocationException e) {
        //e.printStackTrace();
    }
}

private void insertBullet() {
    try {
        int start = textPane.getCaretPosition();
        StyledDocument doc = textPane.getStyledDocument();
        doc.insertString(start, "\u2022\t", null);
    } catch (BadLocationException e) {
        e.printStackTrace();
    }
}


/*///////////////////////////////

Tab 3: Spreadsheet

*////////////////////////////////
private void addSpreadsheetTab() {
DefaultTableModel model = new DefaultTableModel(100, 26) {
@Override
public Object getValueAt(int row, int column) {
    Object value = super.getValueAt(row, column);
    if (value == null) {
        value = new StyledCell("", defaultFont, Color.WHITE);
        setValueAt(value, row, column);
    }
    return value;
}
};
    addSpreadsheetTab(model, true);
}

private JScrollPane addSpreadsheetTab(DefaultTableModel model, boolean isNewSpreadsheet) {
    
           // Create a new table model for the row header
    DefaultTableModel newRowHeaderModel = new DefaultTableModel(0, 1) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false; // This will make the cells in the row header uneditable
        }
    };
           // Create a new table using the new row header model
    JTable newRowHeader = new JTable(newRowHeaderModel);
    


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
        //System.out.println("JTable: Getting value at (" + row + ", " + column + "): " + value);
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
    spreadsheetTable.setDefaultEditor(StyledCell.class, new StyledCellEditor(spreadsheetTable, newRowHeader));


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
}

// Helper method to remove a column from the table model
private void removeColumns(int[] columnIndices, DefaultTableModel model) {
    int nRows = model.getRowCount();
    int nCols = model.getColumnCount();
    
    // Sort the column indices in descending order so that we remove columns from the end first
    Arrays.sort(columnIndices);
    for (int i = columnIndices.length - 1; i >= 0; i--) {
        int columnIndex = columnIndices[i];
        if(columnIndex < 0 || columnIndex >= nCols) {
            continue; // Invalid column index
        }

        Object[][] data = new Object[nRows][nCols - 1];
        String[] columnNames = new String[nCols - 1];

        // Copy the column names and data, skipping the column to be removed
        for (int col = 0; col < nCols; col++) {
            if (col != columnIndex) {
                int newColIndex = col < columnIndex ? col : col - 1;
                columnNames[newColIndex] = model.getColumnName(col);
                for (int row = 0; row < nRows; row++) {
                    data[row][newColIndex] = model.getValueAt(row, col);
                }
            }
        }

        // Update the model with the new data and column names
        model.setDataVector(data, columnNames);
        nCols--; // Decrement the number of columns for the next iteration
    }
}

public void restoreState() {
    ArrayList<TabState> tabStates;
    try (FileInputStream fis = new FileInputStream("tabStates.ser");
         ObjectInputStream ois = new ObjectInputStream(fis)) {
        tabStates = (ArrayList<TabState>) ois.readObject();
    } catch (Exception e) {
        e.printStackTrace();
        return;
    }

    for (TabState state : tabStates) {
        if ("NOTE".equals(state.type)) {
            JTextPane textPane = new JTextPane();
            textPane.setText(state.content);
            tabbedPane.addTab("Note", textPane);
        }
        // Handle other types of tabs (TODO, SPREADSHEET, etc.)
    }
}
// Method to generate column names (A, B, ..., Z, AA, AB, ...)
private String[] getColumnNames(DefaultTableModel model, int count) {
    String[] columnNames = new String[count];
    for (int i = 0; i < count; i++) {
        int dividend = i + 1;
        StringBuilder columnName = new StringBuilder();
        while (dividend > 0) {
            int modulo = (dividend - 1) % 26;
            columnName.insert(0, (char) (65 + modulo));
            dividend = (dividend - modulo) / 26;
        }
        columnNames[i] = columnName.toString();
    }
    return columnNames;
}


private void addAppToTray() {
        try {
            SystemTray tray = SystemTray.getSystemTray();
            Image image = Toolkit.getDefaultToolkit().createImage("src/noteIcon3.gif");
            TrayIcon trayIcon = new TrayIcon(image, "KdNoteApp");
            PopupMenu popup = new PopupMenu();

            MenuItem openItem = new MenuItem("Show Notepad");
            MenuItem aboutItem = new MenuItem("About");
            MenuItem exitItem = new MenuItem("Exit");
            MenuItem saveItem = new MenuItem("Save");

            
            openItem.addActionListener(e -> frame.setLocation(Toolkit.getDefaultToolkit().getScreenSize().width - frame.getWidth(), Toolkit.getDefaultToolkit().getScreenSize().height - frame.getHeight() - 40));
            openItem.addActionListener(e -> frame.setVisible(true));

            
            aboutItem.addActionListener(e -> JOptionPane.showMessageDialog(frame, "fNote version 0.01 Beta"));
            exitItem.addActionListener(e -> System.exit(0));
            saveItem.addActionListener(e -> {
                try {
                    saveFile();
                } catch (org.apache.poi.openxml4j.exceptions.InvalidFormatException ex) {
                    Logger.getLogger(KdNoteApp.class.getName()).log(Level.SEVERE, null, ex);
                }
            });

            popup.add(openItem);
            popup.add(aboutItem);
            popup.add(saveItem);
            popup.add(exitItem);
            trayIcon.setPopupMenu(popup);

            // Left-click to open
            trayIcon.addActionListener(e -> frame.setVisible(!frame.isVisible()));

            tray.add(trayIcon);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//Old method
private void showHamburgerMenu() {
        cardLayout.show(cardPanel, "fullMenu");
    }

private void openFile() {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setFileFilter(new FileNameExtensionFilter("Document Files", "docx", "rtf", "txt", "csv", "xlsx", "tdf"));
    int returnValue = fileChooser.showOpenDialog(frame);
    if (returnValue == JFileChooser.APPROVE_OPTION) {
        File selectedFile = fileChooser.getSelectedFile();
        String ext = getFileExtension(selectedFile);
        if ("docx".equals(ext)) {
            addNoteTab(); // Create a new note tab
            openDocx(selectedFile);
        } else if ("rtf".equals(ext)) {
            addNoteTab(); // Create a new note tab
            openRtf(selectedFile);
        } else if ("txt".equals(ext)) {
            addNoteTab(); // Create a new note tab
            openTxt(selectedFile);
        } else if ("csv".equals(ext)) {
        if (isTodoListCSV(selectedFile)) {
            addTodoTab(); // Create a new todo tab
        } else {
            addSpreadsheetTab(); // Create a new spreadsheet tab
        }
        openCSV(selectedFile);
    } else if ("xlsx".equals(ext)) {
            
            openXLSX(selectedFile);
        } else if ("tdf".equals(ext)) {
            addTodoTab(); // Create a new todo tab
            openTDF(selectedFile);
        }
        // Update the tab name
        tabbedPane.setTitleAt(tabbedPane.getSelectedIndex(), selectedFile.getName());
    }
}


//Check if CSV is todolist
private boolean isTodoListCSV(File file) {
    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
        String line;
        int count = 0;
        while ((line = br.readLine()) != null && count < 3) {
            String[] values = line.split(",");
            if (values.length > 0) {
                String firstValue = values[0].trim().toLowerCase();
                if (firstValue.equals("true") || firstValue.equals("false") || firstValue.equals("☑") || firstValue.equals("☐")) {
                    return true;
                }
            }
            count++;
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
    return false;
}

// New: Method to change the app color
private void changeAppColor() {
    Color newColor = JColorChooser.showDialog(frame, "Choose App Color", frameBackgroundColor);
    if (newColor != null) {
        frameBackgroundColor = newColor;
        frame.getContentPane().setBackground(frameBackgroundColor);
        
        // Update the background color of the top and bottom panes
        customTitleBar.setBackground(frameBackgroundColor);
        bottomPanel.setBackground(frameBackgroundColor);
        cardPanel.setBackground(frameBackgroundColor);
        popupMenu.setBackground(frameBackgroundColor);
        northPanel.setBackground(frameBackgroundColor);
        hamburgerMenu.setBackground(frameBackgroundColor);
       menuButton.setBackground(frameBackgroundColor);
       undoButton.setBackground(frameBackgroundColor);
       redoButton.setBackground(frameBackgroundColor);
       UIManager.put("ComboBox.buttonBackground", Color.RED);
       fontSelector.repaint();
        //tabbedPane.setBackground(frameBackgroundColor);
        fontPanel.setBackground(frameBackgroundColor);
        //fontSelector.setBackground(frameBackgroundColor.brighter());
        Color brighterColor;
        if (frameBackgroundColor.equals(Color.BLACK)) {
            brighterColor = Color.DARK_GRAY; // Use a light gray color as a fallback for black
                    fontSelector.setBackground(brighterColor);
        fontSizeSelector.setBackground(brighterColor);
        
            
        } else {
            brighterColor = frameBackgroundColor.brighter();
                    fontSelector.setBackground(brighterColor);
        fontSizeSelector.setBackground(brighterColor);
        
        }

     
        double luminance = 0.299 * frameBackgroundColor.getRed() + 0.587 * frameBackgroundColor.getGreen() + 0.114 * frameBackgroundColor.getBlue();
        
        
        if (luminance < 85) {
        fontSelector.setForeground(Color.WHITE);
        fontSizeSelector.setForeground(Color.WHITE); // If dark colors
              Component[] components = popupMenu.getComponents();
        for (Component component : components) {
            if (component instanceof JMenuItem) {
                JMenuItem menuItem = (JMenuItem) component;
                menuItem.setForeground(Color.WHITE);
            }
        }
    } else{
        fontSelector.setForeground(Color.BLACK);
        fontSizeSelector.setForeground(Color.BLACK); // Grey color
        Component[] components = popupMenu.getComponents();
        for (Component component : components) {
            if (component instanceof JMenuItem) {
                JMenuItem menuItem = (JMenuItem) component;
                menuItem.setForeground(Color.BLACK);
            }
        }
    }
        updateHamburgerIcon();
    }
}
public void updateHamburgerIcon() {
    Color background = customTitleBar.getBackground();
    double luminance = 0.299 * background.getRed() + 0.587 * background.getGreen() + 0.114 * background.getBlue();
  
    if (luminance < 85) {
        hamburgerButton.setIcon(new ImageIcon("src/iconBurger_w.png")); // Dark color
    } else if (luminance < 170) {
        hamburgerButton.setIcon(new ImageIcon("src/iconBurger_g.png")); // Grey color
    } else if (luminance > 220) {
        hamburgerButton.setIcon(new ImageIcon("src/iconBurger_gw.png")); // Light color
    } else {
        hamburgerButton.setIcon(new ImageIcon("src/iconBurger.png")); // Default color
    }
}

public double calculateLuminance(Color color) {
    double r = color.getRed() / 255.0;
    double g = color.getGreen() / 255.0;
    double b = color.getBlue() / 255.0;

    return (0.299 * r) + (0.587 * g) + (0.114 * b);
}


private void handlePaste(JTextPane currentPane) {
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    Transferable content = clipboard.getContents(null);
    if (content != null && content.isDataFlavorSupported(DataFlavor.imageFlavor)) {
        try {
            Image image = (Image) content.getTransferData(DataFlavor.imageFlavor);
            currentPane.insertIcon(new ImageIcon(image));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

// Method to handle pasting data from the clipboard
private void pasteData(JTable table) {
    try {
        String data = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
        String[] rows = data.split("\n");
        int rowCount = rows.length;
        int columnCount = rows[0].split("\t").length;

        int startRow = table.getSelectedRow();
        int startCol = table.getSelectedColumn();

        for (int i = 0; i < rowCount; i++) {
            String[] values = rows[i].split("\t");
            for (int j = 0; j < columnCount; j++) {
                if (startRow + i < table.getRowCount() && startCol + j < table.getColumnCount()) {
                    table.setValueAt(values[j], startRow + i, startCol + j);
                } else {
                    // Handle cases where the paste operation goes beyond the existing rows/columns
                    // (e.g., by adding new rows/columns as needed)
                }
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
}



private String getFileExtension(File file) {
    String fileName = file.getName();
    int dotIndex = fileName.lastIndexOf('.');
    return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
}
    
/*///////////////////////////////

Opening and Importing

*////////////////////////////////
private void openTxt(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            textPane.read(reader, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

private void openRtf(File file) {
    try {
        FileInputStream fis = new FileInputStream(file);
        textPane.setDocument(new DefaultStyledDocument());
        new RTFEditorKit().read(fis, textPane.getDocument(), 0);
        fis.close();
    } catch (Exception e) {
        e.printStackTrace();
    }
}

private void openCSV(File file) {
    DefaultTableModel model = null;
    Component component = tabbedPane.getSelectedComponent();
    
    if (component instanceof JTabbedPane) {
        JTabbedPane innerTabbedPane = (JTabbedPane) component;
        Component innerComponent = innerTabbedPane.getSelectedComponent();
        if (innerComponent instanceof JScrollPane) {
            JScrollPane scrollPane = (JScrollPane) innerComponent;
            model = (DefaultTableModel) ((JTable) scrollPane.getViewport().getView()).getModel();
        }
    } else if (component instanceof JScrollPane) {
        JScrollPane scrollPane = (JScrollPane) component;
        model = (DefaultTableModel) ((JTable) scrollPane.getViewport().getView()).getModel();
    }

    if (model != null) {
        model.setRowCount(0);
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                Object[] newRow = new Object[values.length];
                
                // Convert the first column to Boolean if it's a todo list
                if (values[0].trim().equalsIgnoreCase("true")) {
                    newRow[0] = Boolean.TRUE;
                } else if (values[0].trim().equalsIgnoreCase("false")) {
                    newRow[0] = Boolean.FALSE;
                } else if (values[0].trim().equals("☑")) {
                    newRow[0] = Boolean.TRUE;
                } else if (values[0].trim().equals("☐")) {
                    newRow[0] = Boolean.FALSE;
                } else {
                    newRow[0] = values[0];
                }
                
                // Copy the rest of the values
                for (int i = 1; i < values.length; i++) {
                    newRow[i] = values[i];
                }
                
                model.addRow(newRow);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    } else {
        JOptionPane.showMessageDialog(frame, "Cannot open file!");
    }
}

private void openXLSX(File file) {
    try (FileInputStream fis = new FileInputStream(file)) {
        XSSFWorkbook workbook = new XSSFWorkbook(fis);
        JTabbedPane innerTabbedPane = new JTabbedPane();
        innerTabbedPane.setTabPlacement(JTabbedPane.BOTTOM);

        for (int sheetNum = 0; sheetNum < workbook.getNumberOfSheets(); sheetNum++) {
            XSSFSheet sheet = workbook.getSheetAt(sheetNum);
            DefaultTableModel model = new DefaultTableModel();

            XSSFRow firstRow = sheet.getRow(0);
            int numOfColumns = firstRow != null ? firstRow.getPhysicalNumberOfCells() : 0;

            // Set column names
            String[] columnNames = new String[numOfColumns];
            for (int j = 0; j < numOfColumns; j++) {
                if (firstRow != null) {
                    XSSFCell cell = firstRow.getCell(j, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    columnNames[j] = cell.toString();
                } else {
                    columnNames[j] = "Column " + (j + 1);
                }
            }
            model.setColumnIdentifiers(columnNames);

            // Read and populate data for all rows including the first
            for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) { 
                XSSFRow row = sheet.getRow(i);
                if (row != null) {
                    Object[] rowData = new Object[numOfColumns];
                    for (int j = 0; j < numOfColumns; j++) {
                        XSSFCell cell = row.getCell(j, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                        switch (cell.getCellType()) {
                            case STRING:
                                rowData[j] = cell.getStringCellValue();
                                break;
                            case NUMERIC:
                                rowData[j] = cell.getNumericCellValue();
                                break;
                            case BOOLEAN:
                                rowData[j] = cell.getBooleanCellValue();
                                break;
                            default:
                                rowData[j] = "";
                        }
                    }
                    model.addRow(rowData);
                }
            }

            // Create a new spreadsheet tab with the populated model
            JScrollPane scrollPane = addSpreadsheetTab(model, false);

            // Add the scroll pane to the inner tabbed pane
            innerTabbedPane.addTab(sheet.getSheetName(), scrollPane);
        }

        // Add the inner tabbed pane to the main tabbed pane
        tabbedPane.addTab(file.getName(), innerTabbedPane);
        tabbedPane.setSelectedComponent(innerTabbedPane);

    } catch (IOException e) {
        e.printStackTrace();
    }
}


private void openTDF(File file) {
    // Here we will implement the logic to read data from a TDF file and populate a new todo tab
}

private void openDocx(File file) {
    XWPFDocument document;
    try {
        FileInputStream fis = new FileInputStream(file);
        document = new XWPFDocument(fis);
        
        // Create a new StyledDocument for the JTextPane
        StyledDocument styledDoc = textPane.getStyledDocument();
        styledDoc.remove(0, styledDoc.getLength()); // Clear existing content
        
        // Iterate through body elements (paragraphs and tables)
        for (IBodyElement element : document.getBodyElements()) {
            if (element.getElementType() == BodyElementType.PARAGRAPH) {
                XWPFParagraph paragraph = (XWPFParagraph) element;
                for (XWPFRun run : paragraph.getRuns()) {
                    String text = run.getText(0);
                    if (text != null && !text.isEmpty()) {
                        // Create a new AttributeSet for the run's styling
                    SimpleAttributeSet attributes = new SimpleAttributeSet();
                    if (run.isBold()) {
                        StyleConstants.setBold(attributes, true);
                    }
                    if (run.isItalic()) {
                        StyleConstants.setItalic(attributes, true);
                    }
                    StyleConstants.setFontSize(attributes, run.getFontSize());
                    
                    // Handle color
                    String colorStr = run.getColor();
                    if (colorStr != null && !colorStr.isEmpty()) {
                        try {
                            Color color = Color.decode("#" + colorStr);
                            StyleConstants.setForeground(attributes, color);
                        } catch (NumberFormatException e) {
                            // Handle color parsing error
                            System.err.println("Error parsing color: " + colorStr);
                        }
                    } else {
                        StyleConstants.setForeground(attributes, Color.BLACK);
                    }
                    
                    StyleConstants.setFontFamily(attributes, run.getFontFamily());
                    
                    // Insert the styled text into the JTextPane
                    styledDoc.insertString(styledDoc.getLength(), text, attributes);
                    }
                    
                    // Handle images
                    for (XWPFPicture pic : run.getEmbeddedPictures()) {
                        // Convert the image into an ImageIcon and insert into JTextPane
                        byte[] bytes = pic.getPictureData().getData();
                        ImageIcon imageIcon = new ImageIcon(bytes);
                        textPane.insertIcon(imageIcon);
                    }
                }
                styledDoc.insertString(styledDoc.getLength(), "\n", null);
            }
            // Handle other body elements like tables if needed
        }
        
        fis.close();
    } catch (Exception e) {
        e.printStackTrace();
    }
}



/*///////////////////////////////

Saving and Exporting

*////////////////////////////////
private void saveFile() throws org.apache.poi.openxml4j.exceptions.InvalidFormatException {
     JFileChooser fileChooser = new JFileChooser();
    Component component = tabbedPane.getSelectedComponent();

    // Filters for note tabs
    FileNameExtensionFilter docxFilter = new FileNameExtensionFilter("Word Document (.docx)", "docx");
    FileNameExtensionFilter rtfFilter = new FileNameExtensionFilter("Rich Text Format (.rtf)", "rtf");
    FileNameExtensionFilter txtFilter = new FileNameExtensionFilter("Text File (.txt)", "txt");

    // Filters for other tabs
    FileNameExtensionFilter csvFilter = new FileNameExtensionFilter("CSV File (.csv)", "csv");
    FileNameExtensionFilter xlsxFilter = new FileNameExtensionFilter("Excel Spreadsheet (.xlsx)", "xlsx");
    FileNameExtensionFilter tdfFilter = new FileNameExtensionFilter("Todo File (.tdf)", "tdf");

    // Add filters based on the type of tab
    if (component instanceof JScrollPane) {
        fileChooser.addChoosableFileFilter(docxFilter);
        fileChooser.addChoosableFileFilter(rtfFilter);
        fileChooser.addChoosableFileFilter(txtFilter);
        fileChooser.setFileFilter(docxFilter); // Set default filter for note tabs
    } else {
        fileChooser.addChoosableFileFilter(csvFilter);
        fileChooser.addChoosableFileFilter(xlsxFilter);
        fileChooser.addChoosableFileFilter(tdfFilter);
        fileChooser.setFileFilter(csvFilter); // Set default filter for other tabs
    }

    
 int returnValue = fileChooser.showSaveDialog(frame);
    if (returnValue == JFileChooser.APPROVE_OPTION) {
        File selectedFile = fileChooser.getSelectedFile();
        FileFilter selectedFilter = fileChooser.getFileFilter();

        String ext = getFileExtension(selectedFile);

        // Warning for overwriting files
        if (selectedFile.exists()) {
            int response = JOptionPane.showConfirmDialog(null, "Do you want to replace the existing file?", "Confirm Overwrite",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (response != JOptionPane.YES_OPTION) {
                return; // Exit the function
            }
        }

        // Automatically append the selected extension if not provided
        if (ext == null || ext.isEmpty()) {
            String description = selectedFilter.getDescription();
            ext = description.substring(description.lastIndexOf("(") + 2, description.lastIndexOf(")"));
            selectedFile = new File(selectedFile.toString() + '.' + ext);
        }

        
        // Handle spreadsheets
        if (component instanceof JTabbedPane) {
            JTabbedPane innerTabbedPane = (JTabbedPane) component;
            Component innerComponent = innerTabbedPane.getSelectedComponent();
            if (innerComponent instanceof JScrollPane) {
                JScrollPane scrollPane = (JScrollPane) innerComponent;
                Component viewportView = scrollPane.getViewport().getView();
                if (viewportView instanceof JTable) {
                    JTable table = (JTable) viewportView;
                    if ("csv".equals(ext)) {
                        exportToCSV(table, selectedFile); // false because it's a spreadsheet
                    } else if ("xlsx".equals(ext)) {
                        exportToXLSX(innerTabbedPane, selectedFile);
                    } else if ("tdf".equals(ext)) {
                        exportToTDF(table, selectedFile);
                    }
                }
            }
        } 
        // Handle todo lists and other types
        else if (component instanceof JScrollPane) {
            JScrollPane scrollPane = (JScrollPane) component;
            Component viewportView = scrollPane.getViewport().getView();
            if (viewportView instanceof JTable) {
                JTable table = (JTable) viewportView;
                if ("csv".equals(ext)) {
                    exportToCSV(table, selectedFile); // true because it's a todo list
                } else if ("xlsx".equals(ext)) {
                    exportTodoToXLSX(table, selectedFile);
                } else if ("tdf".equals(ext)) {
                    exportToTDF(table, selectedFile);
                }
            } else {
                if ("docx".equals(ext)) {
                    saveAsDocx(selectedFile);
                } else if ("rtf".equals(ext)) {
                    saveAsRtf(selectedFile);
                } else {
                    saveAsTxt(selectedFile);
                }
            }
        }
        
        tabbedPane.setTitleAt(tabbedPane.getSelectedIndex(), selectedFile.getName());
    }
}

// Exports todo lists to XLSX (using Apache POI)
private void exportTodoToXLSX(JTable table, File file) {
    XSSFWorkbook workbook = new XSSFWorkbook();
    XSSFSheet sheet = workbook.createSheet("Todo List");
    for (int i = 0; i < table.getRowCount(); i++) {
        XSSFRow row = sheet.createRow(i);
        for (int j = 0; j < table.getColumnCount(); j++) {
            XSSFCell cell = row.createCell(j);
            Object value = table.getValueAt(i, j);
            if (value != null) {
                cell.setCellValue(value.toString());
            }
        }
    }
    try (FileOutputStream out = new FileOutputStream(file)) {
        workbook.write(out);
    } catch (IOException e) {
        e.printStackTrace();
    }
}


private void saveAsTxt(File file) {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(textPane.getText());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
private void saveAsRtf(File file) {
    try {
        FileOutputStream fos = new FileOutputStream(file);
        new RTFEditorKit().write(fos, textPane.getDocument(), 0, textPane.getDocument().getLength());
        fos.close();
    } catch (Exception e) {
        e.printStackTrace();
    }
}
    
// Export to CSV
private void exportToCSV(JTable table, File file) {
    try (FileWriter writer = new FileWriter(file)) {
        // Write column headers
        for (int i = 0; i < table.getColumnCount(); i++) {
            writer.write(table.getColumnName(i) + ",");
        }
        writer.write("\n");

        // Write table data
        for (int i = 0; i < table.getRowCount(); i++) {
            for (int j = 0; j < table.getColumnCount(); j++) {
                Object value = table.getValueAt(i, j);
                if (j == 0 && value instanceof Boolean) {
                    writer.write(((Boolean) value ? "☑" : "☐") + ",");
                } else {
                    writer.write(value.toString() + ",");
                }
            }
            writer.write("\n");
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
}

// Exports to XLSX (using Apache POI)
private void exportToXLSX(JTabbedPane innerTabbedPane, File file) {
    XSSFWorkbook workbook = new XSSFWorkbook();
    
    int tabCount = innerTabbedPane.getTabCount();
    for (int tabIndex = 0; tabIndex < tabCount; tabIndex++) {
        Component component = innerTabbedPane.getComponentAt(tabIndex);
        if (component instanceof JScrollPane) {
            JScrollPane scrollPane = (JScrollPane) component;
            JTable table = (JTable) scrollPane.getViewport().getView();
            
            String sheetName = innerTabbedPane.getTitleAt(tabIndex);
            XSSFSheet sheet = workbook.createSheet(sheetName);
            
            for (int i = 0; i < table.getRowCount(); i++) {
                XSSFRow row = sheet.createRow(i);
                for (int j = 0; j < table.getColumnCount(); j++) {
                    XSSFCell cell = row.createCell(j);
                    Object value = table.getValueAt(i, j);
                    if (value != null) {
                        cell.setCellValue(value.toString());
                    }
                }
            }
        }
    }
    
    try (FileOutputStream out = new FileOutputStream(file)) {
        workbook.write(out);
    } catch (IOException e) {
        e.printStackTrace();
    }
}

private void saveAsDocx(File file) throws org.apache.poi.openxml4j.exceptions.InvalidFormatException {
    System.out.println("Attempting to save as DOCX...");
    XWPFDocument doc = new XWPFDocument();

    StyledDocument styledDoc = (StyledDocument) textPane.getDocument();
    try {
        XWPFParagraph p = doc.createParagraph();
        for (Element elem : getLeafElements(styledDoc)) {
            int start = elem.getStartOffset();
            int end = elem.getEndOffset();
            String text = styledDoc.getText(start, end - start);
            System.out.println("Text to write: " + text);  // Debug print
            
            AttributeSet attrs = elem.getAttributes();
            XWPFRun run = p.createRun();

            // Check if the element contains an image
            Icon icon = (Icon) attrs.getAttribute(StyleConstants.IconAttribute);
            if (icon instanceof ImageIcon) {
                ImageIcon imageIcon = (ImageIcon) icon;
                BufferedImage bufferedImage = new BufferedImage(imageIcon.getIconWidth(), imageIcon.getIconHeight(), BufferedImage.TYPE_INT_RGB);
                Graphics g = bufferedImage.createGraphics();
                icon.paintIcon(null, g, 0, 0);
                g.dispose();

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(bufferedImage, "jpg", baos);
                byte[] bytes = baos.toByteArray();
                try {
                    run.addPicture(new ByteArrayInputStream(bytes), XWPFDocument.PICTURE_TYPE_JPEG, "image.jpg", Units.toEMU(imageIcon.getIconWidth()), Units.toEMU(imageIcon.getIconHeight()));
                } catch (InvalidFormatException e) {
                    e.printStackTrace();
                }
            } else {
                run.setText(text.replace("\n", "").replace("\r", ""));
            }

            // Apply font style
            if (StyleConstants.isBold(attrs)) {
                run.setBold(true);
            }
            if (StyleConstants.isItalic(attrs)) {
                run.setItalic(true);
            }
            // Set font size 
            run.setFontSize((int) (StyleConstants.getFontSize(attrs)));
            
            // Set font color
            Color color = StyleConstants.getForeground(attrs);
            if (color != null) {
                run.setColor(String.format("%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue()));
            }
            
            // Set font family
            String fontFamily = StyleConstants.getFontFamily(attrs);
            if (fontFamily != null) {
                run.setFontFamily(fontFamily);
            }

            // If the text ends with a line break, create a new paragraph for the next run
            if (text.endsWith("\n")) {
                p = doc.createParagraph();
            }
        }
    } catch (BadLocationException | IOException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "An error occurred while processing the document content.");
    }

    try (FileOutputStream out = new FileOutputStream(file)) {
        doc.write(out);
        System.out.println("DOCX saved successfully.");  // Debug print
    } catch (IOException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "An error occurred while saving the document.");
    }
}


private java.util.List<javax.swing.text.Element> getLeafElements(StyledDocument doc) {
    java.util.List<javax.swing.text.Element> elements = new ArrayList<>();
    javax.swing.text.Element root = doc.getDefaultRootElement();
    for (int i = 0; i < root.getElementCount(); i++) {
        javax.swing.text.Element paragraph = root.getElement(i);
        for (int j = 0; j < paragraph.getElementCount(); j++) {
            elements.add(paragraph.getElement(j));
        }
    }
    return elements;
}

private void exportToTDF(JTable table, File file) {
    // Here we will implement the logic to save the data in the TDF format
    try (FileWriter writer = new FileWriter(file)) {
        for (int i = 0; i < table.getRowCount(); i++) {
            // For each row, we will create a JSON object to store the data
            JSONObject task = new JSONObject();
            task.put("status", table.getValueAt(i, 0));
            task.put("description", table.getValueAt(i, 1));
            
            // Write the JSON object to the file
            writer.write(task.toString());
            writer.write(System.lineSeparator());  // Add a newline character to separate each task
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
}

// Method to adjust icon brightness
private ImageIcon adjustIconBrightness(ImageIcon icon, float scaleFactor) {
    BufferedImage img = new BufferedImage(
            icon.getIconWidth(),
            icon.getIconHeight(),
            BufferedImage.TYPE_INT_ARGB);
    Graphics g = img.createGraphics();
    icon.paintIcon(null, g, 0, 0);
    g.dispose();

    RescaleOp rescaleOp = new RescaleOp(scaleFactor, 0, null);
    rescaleOp.filter(img, img);

    return new ImageIcon(img);
}

public boolean isDark(Color color) {
    // Convert the color's RGB values to be between 0 and 1.
    double red = color.getRed() / 255.0;
    double green = color.getGreen() / 255.0;
    double blue = color.getBlue() / 255.0;

    // Calculate the luminance of the color.
    double luminance = 0.299*red + 0.587*green + 0.114*blue;

    // Define a threshold for whether the color is "dark" or not.
    // This is a somewhat arbitrary threshold and can be adjusted based on your needs.
    double threshold = 0.5;

    // If the color's luminance is below the threshold, it is "dark".
    return luminance < threshold;
}

private void updateBackgroundColor(Color color) {
    Component selectedComponent = tabbedPane.getSelectedComponent();
    if (selectedComponent instanceof JScrollPane) {
        Component viewComponent = ((JScrollPane) selectedComponent).getViewport().getView();
        if (viewComponent instanceof JTextPane) {
            JTextPane currentTextPane = (JTextPane) viewComponent;
            currentTextPane.setBackground(color);
            
            
            
            if (color.equals(Color.BLACK)) {
                currentTextPane.setForeground(Color.WHITE);
            } else {
                currentTextPane.setForeground(Color.BLACK);
            }
        } else if (viewComponent instanceof JTable) {
            JTable currentTable = (JTable) viewComponent;
            currentTable.setBackground(color);
            
            CustomCheckBoxRenderer renderer = (CustomCheckBoxRenderer) currentTable.getColumnModel().getColumn(0).getCellRenderer();
            if (isDark(color)) {
                currentTable.setForeground(Color.WHITE);
                renderer.setIconUncheck(iconUncheck_w);
                renderer.setIconCheck(iconCheck_w);
            } else {
                currentTable.setForeground(Color.BLACK);
                renderer.setIconUncheck(iconUncheck_b);
                renderer.setIconCheck(iconCheck_b);
            }
             currentTable.repaint();
        }
    }
}
// Function to change a specific tab color
private void changeTabColor(int tabIndex) {
    Color newColor = JColorChooser.showDialog(frame, "Choose Tab Color", Color.WHITE);
    if (newColor != null) {
        // Calculate the luminance of the new color
        double luminance = 0.299 * newColor.getRed() + 0.587 * newColor.getGreen() + 0.114 * newColor.getBlue();
        
        // Choose the font color based on the luminance
        Color fontColor = (luminance < 128) ? Color.WHITE : Color.BLACK;

        // Set the background and font color of the tab
        tabbedPane.setBackgroundAt(tabIndex, newColor);
        tabbedPane.setForegroundAt(tabIndex, fontColor);

        if (tabIndex < tabColors.size()) {
            tabColors.set(tabIndex, newColor);
        } else {
            // Handle the case where the tabIndex is out of bounds
            // (shouldn't happen if your code is correct, but just to be safe)
            while (tabColors.size() <= tabIndex) {
                tabColors.add(Color.GRAY);  // Or any other default color
            }
            tabColors.set(tabIndex, newColor);
        }
        tabbedPane.repaint();  // Repaint to apply the new color
    }
}


private void renameTab(int tabIndex) {
    String newName = JOptionPane.showInputDialog(frame, "Enter new tab name:", tabbedPane.getTitleAt(tabIndex));
    if (newName != null && !newName.trim().isEmpty()) {
        tabbedPane.setTitleAt(tabIndex, newName);
    }
}
// Captures the current state of a JTable
public StyledCell[][] captureTableState(JTable table) {
    int rows = table.getRowCount();
    int cols = table.getColumnCount();
    StyledCell[][] state = new StyledCell[rows][cols];
    for (int i = 0; i < rows; i++) {
        for (int j = 0; j < cols; j++) {
            StyledCell cell = (StyledCell) table.getValueAt(i, j);
            if (cell != null) {
                String text = cell.getValue();  // Assuming StyledCell has getText()
                Font font = cell.getFont();     // Assuming StyledCell has getFont()
                Color color = cell.getBackgroundColor();  // Assuming StyledCell has getColor()
                state[i][j] = new StyledCell(text, font, color);
            }
        }
    }
    return state;
}

public void restoreTableState(JTable table, StyledCell[][] state) {
    if (state == null) {
        return;
    }
    for (int i = 0; i < state.length; i++) {
        for (int j = 0; j < state[i].length; j++) {
            StyledCell cell = state[i][j];
            if (cell != null) {
                String text = cell.getValue();
                Font font = cell.getFont();
                Color color = cell.getBackgroundColor();
                table.setValueAt(new StyledCell(text, font, color), i, j);
            }
        }
    }
}

private void applySpreadsheetForegroundColor(Color color) {
    Component selectedComponent = tabbedPane.getSelectedComponent();
    if (selectedComponent instanceof JTabbedPane) {
        JTabbedPane innerTabbedPane = (JTabbedPane) selectedComponent;
        Component innerSelectedComponent = innerTabbedPane.getSelectedComponent();
        if (innerSelectedComponent instanceof JScrollPane) {
            Component viewComponent = ((JScrollPane) innerSelectedComponent).getViewport().getView();
            if (viewComponent instanceof JTable) {
                JTable currentTable = (JTable) viewComponent;
                int[] selectedRows = currentTable.getSelectedRows();
                int[] selectedCols = currentTable.getSelectedColumns();
                for (int row : selectedRows) {
                    for (int col : selectedCols) {
                        Object value = currentTable.getValueAt(row, col);
                        if (value instanceof StyledCell) {
                            StyledCell styledCell = (StyledCell) value;
                            styledCell.setForegroundColor(color); // Set the foreground color
                            currentTable.setValueAt(styledCell, row, col);
                        }
                    }
                }
                currentTable.repaint(); // Repaint the table to apply the new settings
                
                // Force the table to stop and restart editing to reflect the new color
                if (currentTable.isEditing()) {
                    int editingRow = currentTable.getEditingRow();
                    int editingCol = currentTable.getEditingColumn();
                    currentTable.getCellEditor().stopCellEditing();
                    currentTable.editCellAt(editingRow, editingCol);
                }
            }
        }
    }
}

private void applyNoteForegroundColor(Color color) {
    Component selectedComponent = tabbedPane.getSelectedComponent();
    if (selectedComponent instanceof JScrollPane && ((JScrollPane) selectedComponent).getViewport().getView() instanceof JTextPane) {
        JTextPane textPane = (JTextPane) ((JScrollPane) selectedComponent).getViewport().getView();
        int start = textPane.getSelectionStart();
        int end = textPane.getSelectionEnd();
        StyledDocument doc = textPane.getStyledDocument();
        SimpleAttributeSet set = new SimpleAttributeSet();
        StyleConstants.setForeground(set, color);

        if (start != end) {
            doc.setCharacterAttributes(start, end - start, set, false);
        } else {
            textPane.setCaretPosition(start);
            textPane.setCharacterAttributes(set, false);
        }
    } else if (selectedComponent instanceof JScrollPane && ((JScrollPane) selectedComponent).getViewport().getView() instanceof JTable) {
        JTable todoTable = (JTable) ((JScrollPane) selectedComponent).getViewport().getView();
        int selectedRow = todoTable.getSelectedRow();
        int selectedCol = todoTable.getSelectedColumn();
        if (selectedRow != -1 && selectedCol != -1) {
            todoTable.prepareRenderer(todoTable.getCellRenderer(selectedRow, selectedCol), selectedRow, selectedCol).setForeground(color);
            todoTable.repaint();
        }
    }
}

private void insertNumber() {
    try {
        int start = textPane.getSelectionStart();
        StyledDocument doc = textPane.getStyledDocument();
        doc.insertString(start, "1.\t", null);
    } catch (BadLocationException e) {
        e.printStackTrace();
    }
}

// Recursive method to search for EmbeddedTable within a component
// Create a Set to keep track of tables that already have listeners
private Set<JTable> tablesWithListeners = new HashSet<>();

private JTable findEmbeddedTable(Component comp) {
    if (comp instanceof JTable) {
        JTable table = (JTable) comp;
        
        // Only add listeners if this table hasn't been processed before
        if (!tablesWithListeners.contains(table)) {
            // Add ListSelectionListener to the JTable's row and column selection models
            table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    if (!e.getValueIsAdjusting()) {
                        syncFontUI();
                    }
                }
            });
            table.getColumnModel().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    if (!e.getValueIsAdjusting()) {
                        syncFontUI();
                    }
                }
            });
            
            // Add this table to the set
            tablesWithListeners.add(table);
        }
        
        return table;
    } else if (comp instanceof Container) {
        for (Component child : ((Container) comp).getComponents()) {
            JTable table = findEmbeddedTable(child);
            if (table != null) {
                return table;
            }
        }
    }
    return null;
}

private void applyStyling() {
    String selectedFont = (String) fontSelector.getSelectedItem();
    int selectedSize = (int) fontSizeSelector.getSelectedItem();
    boolean isBold = boldButton.isSelected();
    boolean isItalic = italicButton.isSelected();
    boolean isUnderline = underlineButton.isSelected();
    boolean isStrikeThrough = strikeThroughButton.isSelected();

    int style = Font.PLAIN;
    if (isBold) style |= Font.BOLD;
    if (isItalic) style |= Font.ITALIC;

if (lastFocusedComponent instanceof JTextPane) {
    JTextPane focusedTextPane = (JTextPane) lastFocusedComponent;
    SimpleAttributeSet attributeSet = new SimpleAttributeSet();
    StyleConstants.setFontFamily(attributeSet, selectedFont);
    StyleConstants.setFontSize(attributeSet, selectedSize);
    StyleConstants.setBold(attributeSet, isBold);
    StyleConstants.setItalic(attributeSet, isItalic);
    StyleConstants.setUnderline(attributeSet, isUnderline);
    StyleConstants.setStrikeThrough(attributeSet, isStrikeThrough);

    int start = focusedTextPane.getSelectionStart();
    int end = focusedTextPane.getSelectionEnd();

    if (start != end) {
        // If some text is selected, apply the styling to that text
        focusedTextPane.getStyledDocument().setCharacterAttributes(start, end - start, attributeSet, false);
    } else {
        // If no text is selected, set the input attributes for the current caret position
        focusedTextPane.getInputAttributes().addAttributes(attributeSet);
    }
}
 else if (lastFocusedComponent instanceof JTable) {
        JTable focusedTable = (JTable) lastFocusedComponent;
        int[] selectedRows = focusedTable.getSelectedRows();
        int[] selectedCols = focusedTable.getSelectedColumns();
        for (int row : selectedRows) {
            for (int col : selectedCols) {
                Object value = focusedTable.getValueAt(row, col);
                if (value instanceof StyledCell) {
                    StyledCell styledCell = (StyledCell) value;
                    Font newFont = new Font(selectedFont, style, selectedSize);
                    styledCell.setFont(newFont);
                    focusedTable.setValueAt(styledCell, row, col);
                }
            }
        }
        focusedTable.repaint();
        focusedTable.revalidate();
    }
}

private void syncFontUI() {
    System.out.println("Active Component in [MethodName]: " + (lastFocusedComponent != null ? lastFocusedComponent.getClass().getName() : "None"));

    isProgrammaticUpdate = true;

    // [1] Disconnect action listeners
    ActionListener[] fontSelectorListeners = fontSelector.getActionListeners();
    for (ActionListener listener : fontSelectorListeners) {
        fontSelector.removeActionListener(listener);
    }

    ActionListener[] fontSizeSelectorListeners = fontSizeSelector.getActionListeners();
    for (ActionListener listener : fontSizeSelectorListeners) {
        fontSizeSelector.removeActionListener(listener);
    }

    if (lastFocusedComponent instanceof JTextPane) {
        JTextPane focusedTextPane = (JTextPane) lastFocusedComponent;

        AttributeSet attrs;
        int start = focusedTextPane.getSelectionStart();
        int end = focusedTextPane.getSelectionEnd();

        if (start != end) {
            attrs = focusedTextPane.getStyledDocument().getCharacterElement(start).getAttributes();
        } else {
            attrs = focusedTextPane.getInputAttributes();
        }

        String fontName = StyleConstants.getFontFamily(attrs);
        int fontSize = StyleConstants.getFontSize(attrs);

        fontSelector.setSelectedItem(fontName);
        fontSizeSelector.setSelectedItem(fontSize);
    } else if (lastFocusedComponent instanceof JTable) {
        JTable focusedTable = (JTable) lastFocusedComponent;
        int selectedRow = focusedTable.getSelectedRow();
        int selectedCol = focusedTable.getSelectedColumn();

        if (selectedRow != -1 && selectedCol != -1) {
            Object value = focusedTable.getValueAt(selectedRow, selectedCol);
            if (value instanceof StyledCell) {
                StyledCell cell = (StyledCell) value;
                Font font = cell.getFont();

                fontSelector.setSelectedItem(font.getFamily());
                fontSizeSelector.setSelectedItem(font.getSize());
            }
        }
    }

    isProgrammaticUpdate = false;

    // [2] Reconnect action listeners
    for (ActionListener listener : fontSelectorListeners) {
        fontSelector.addActionListener(listener);
    }

    for (ActionListener listener : fontSizeSelectorListeners) {
        fontSizeSelector.addActionListener(listener);
    }
}

/*

Spreadsheet Styling Methods/ Custom classes

*/

private void applySpreadsheetStyling() {
    String selectedFont = (String) fontSelector.getSelectedItem();
    int selectedSize = (int) fontSizeSelector.getSelectedItem();

    Component selectedComponent = tabbedPane.getSelectedComponent();
    if (selectedComponent instanceof JTabbedPane) {
        JTabbedPane innerTabbedPane = (JTabbedPane) selectedComponent;
        Component innerSelectedComponent = innerTabbedPane.getSelectedComponent();
        if (innerSelectedComponent instanceof JScrollPane) {
            Component viewComponent = ((JScrollPane) innerSelectedComponent).getViewport().getView();
            if (viewComponent instanceof JTable) {
                JTable currentTable = (JTable) viewComponent;
                int[] selectedRows = currentTable.getSelectedRows();
                int[] selectedCols = currentTable.getSelectedColumns();
                SimpleAttributeSet currentStyles = getCurrentStyles();
                for (int row : selectedRows) {
                    for (int col : selectedCols) {
                        Object value = currentTable.getValueAt(row, col);
                        if (value instanceof StyledCell) {
                            StyledCell styledCell = (StyledCell) value;
                            styledCell.setFont(new Font(selectedFont, Font.PLAIN, selectedSize));
                            //Font Styling
                            Font currentFont = styledCell.getFont();
                            Font newFont = currentFont.deriveFont(getFontStyle(currentStyles), selectedSize);
                            styledCell.setFont(newFont);
                            currentTable.setValueAt(styledCell, row, col);
                            if (currentTable.isEditing()) {
                                TableCellEditor editor = currentTable.getCellEditor();
                                if (editor instanceof StyledCellEditor) {
                                    ((StyledCellEditor) editor).updateFont(new Font(selectedFont, Font.PLAIN, selectedSize));
                                }
                            }
                            TableColumn tableColumn = currentTable.getColumnModel().getColumn(col);
                            tableColumn.sizeWidthToFit();  // Adjust column width to fit the content
                        }
                    }
                    currentTable.setRowHeight(row, selectedSize + 10);  // Adjust row height for each row that contains a selected cell
                    
                }
                currentTable.repaint();  // Repaint the table to apply the new settings
            }
        }
    }
}

public Color openCustomColorChooser(JDialog frame) {  // Pass your main JFrame as an argument
    final Color[] selectedColor = new Color[1];
    final JDialog colorDialog = new JDialog(frame);  // Set the parent frame
    colorDialog.setTitle("Custom Color Picker");
    colorDialog.setModal(true);
    colorDialog.setSize(400, 200);
    colorDialog.setLayout(new BorderLayout());
    colorDialog.setLocationRelativeTo(frame);  // Center relative to the frame

    JPanel sliderPanel = new JPanel();
    sliderPanel.setLayout(new GridLayout(3, 3));

    JLabel redLabel = new JLabel("Red");
    JSlider redSlider = new JSlider(0, 255);
    JSpinner redSpinner = new JSpinner(new SpinnerNumberModel(redSlider.getValue(), 0, 255, 1));
    redSlider.addChangeListener(e -> redSpinner.setValue(redSlider.getValue()));
    redSpinner.addChangeListener(e -> redSlider.setValue((Integer) redSpinner.getValue()));

    JLabel greenLabel = new JLabel("Green");
    JSlider greenSlider = new JSlider(0, 255);
    JSpinner greenSpinner = new JSpinner(new SpinnerNumberModel(greenSlider.getValue(), 0, 255, 1));
    greenSlider.addChangeListener(e -> greenSpinner.setValue(greenSlider.getValue()));
    greenSpinner.addChangeListener(e -> greenSlider.setValue((Integer) greenSpinner.getValue()));

    JLabel blueLabel = new JLabel("Blue");
    JSlider blueSlider = new JSlider(0, 255);
    JSpinner blueSpinner = new JSpinner(new SpinnerNumberModel(blueSlider.getValue(), 0, 255, 1));
    blueSlider.addChangeListener(e -> blueSpinner.setValue(blueSlider.getValue()));
    blueSpinner.addChangeListener(e -> blueSlider.setValue((Integer) blueSpinner.getValue()));

    sliderPanel.add(redLabel);
    sliderPanel.add(redSlider);
    sliderPanel.add(redSpinner);
    sliderPanel.add(greenLabel);
    sliderPanel.add(greenSlider);
    sliderPanel.add(greenSpinner);
    sliderPanel.add(blueLabel);
    sliderPanel.add(blueSlider);
    sliderPanel.add(blueSpinner);

    colorDialog.add(sliderPanel, BorderLayout.CENTER);

    JPanel colorPreview = new JPanel();
    colorPreview.setPreferredSize(new Dimension(50, 50));
    colorPreview.setBackground(new Color(redSlider.getValue(), greenSlider.getValue(), blueSlider.getValue()));

    colorDialog.add(colorPreview, BorderLayout.EAST);

    JButton okButton = new JButton("OK");
    okButton.addActionListener(e -> {
        selectedColor[0] = new Color(redSlider.getValue(), greenSlider.getValue(), blueSlider.getValue());
        colorDialog.dispose();
    });
    
    ChangeListener colorChangeListener = e -> {
    Color newColor = new Color(redSlider.getValue(), greenSlider.getValue(), blueSlider.getValue());
    colorPreview.setBackground(newColor);
};

redSlider.addChangeListener(colorChangeListener);
greenSlider.addChangeListener(colorChangeListener);
blueSlider.addChangeListener(colorChangeListener);

    colorDialog.add(okButton, BorderLayout.SOUTH);

    colorDialog.setVisible(true);
    return selectedColor[0];
}


private void applySpreadsheetBackgroundColor(Color color) {
    Component selectedComponent = tabbedPane.getSelectedComponent();
    if (selectedComponent instanceof JTabbedPane) {
        JTabbedPane innerTabbedPane = (JTabbedPane) selectedComponent;
        Component innerSelectedComponent = innerTabbedPane.getSelectedComponent();
        if (innerSelectedComponent instanceof JScrollPane) {
            Component viewComponent = ((JScrollPane) innerSelectedComponent).getViewport().getView();
            if (viewComponent instanceof JTable) {
                JTable currentTable = (JTable) viewComponent;
                int[] selectedRows = currentTable.getSelectedRows();
                int[] selectedCols = currentTable.getSelectedColumns();
                for (int row : selectedRows) {
                    for (int col : selectedCols) {
                        Object value = currentTable.getValueAt(row, col);
                        if (value instanceof StyledCell) {
                            StyledCell styledCell = (StyledCell) value;
                            styledCell.setBackgroundColor(color); // Set the background color
                            currentTable.setValueAt(styledCell, row, col);
                        }
                    }
                }
                currentTable.repaint(); // Repaint the table to apply the new settings
            }
        }
    }
}

class FontCellRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (value instanceof String) {
            String fontName = (String) value;
            setFont(new Font(fontName, Font.PLAIN, 14));
        }
        if (isSelected) {
            setBackground(Color.BLUE);   // Color when an item is selected
            setForeground(Color.WHITE); // Text color when an item is selected
        } else {
            setBackground(Color.LIGHT_GRAY); // Default background color
            setForeground(Color.BLACK);     // Default text color
        }
        return this;
    }
}

class CellEditorDocumentListener implements DocumentListener {
    private JTable table;
    private JScrollPane scrollPane;
    private JTextComponent editorComponent;
    private int previousMaxRowHeight = -1;
    private Map<Integer, Integer> heightDeterminingCellColumn = new HashMap<>();

    private Map<Integer, Integer> initialRowHeights = new HashMap<>();
     private Map<Integer, Integer> rowMaxHeights = new HashMap<>(); // To store the maximum height for each row
    public CellEditorDocumentListener(JTable table, JScrollPane scrollPane, JTextComponent editorComponent) {
        this.table = table;
        this.scrollPane = scrollPane;
        this.editorComponent = editorComponent;
    }
    
    
    private int getTallestCellHeightInRow(int row) {
    int maxCellHeight = 0;
    for (int column = 0; column < table.getColumnCount(); column++) {
        TableCellRenderer renderer = table.getCellRenderer(row, column);
        Component comp = table.prepareRenderer(renderer, row, column);
        maxCellHeight = Math.max(comp.getPreferredSize().height, maxCellHeight);
    }
    return maxCellHeight;
}

private void adjustSize(JScrollPane localScrollPane, JTable localTable) {
    int row = localTable.getEditingRow();
    int col = localTable.getEditingColumn();
    if (row == -1 || col == -1) return;
    
    int editingCellHeight = editorComponent.getPreferredSize().height + 5;
    int currentRowHeight = localTable.getRowHeight(row);
    
    // If this row hasn't had a height determining cell yet, or if the current editing cell's height
    // is greater than the current row height, then set the current cell as the height determining cell.
    if (!heightDeterminingCellColumn.containsKey(row) || editingCellHeight > currentRowHeight) {
        table.setRowHeight(row, editingCellHeight);
        heightDeterminingCellColumn.put(row, col);
    }
    // If the current editing cell is the height determining cell for this row, and its new height
    // is less than the row's current height, then adjust the row height.
    else if (heightDeterminingCellColumn.get(row) == col && editingCellHeight < currentRowHeight) {
        table.setRowHeight(row, editingCellHeight);
    }

    // Calculate the total height and width of the table
    int totalHeight = table.getPreferredSize().height;
    int totalWidth = table.getPreferredSize().width;

    // Adjust the JScrollPane's preferred size based on the table's size
    localScrollPane.getViewport().setPreferredSize(new Dimension(totalWidth, totalHeight));
    localScrollPane.revalidate();
    
    localScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
    localScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

    // Revalidate and repaint the JScrollPane to reflect the changes
    localScrollPane.revalidate();
    localScrollPane.repaint();
}

private void adjustOnEditingStopped(int row) {
    int recordedHeight = initialRowHeights.getOrDefault(row, 0);

    // Find the current maximum cell height in the row
    int maxCellHeightInRow = 0;
    for (int column = 0; column < table.getColumnCount(); column++) {
        TableCellRenderer renderer = table.getCellRenderer(row, column);
        Component comp = table.prepareRenderer(renderer, row, column);
        maxCellHeightInRow = Math.max(comp.getPreferredSize().height, maxCellHeightInRow);
    }

    if (maxCellHeightInRow != recordedHeight) {
        table.setRowHeight(row, maxCellHeightInRow);
    }

    // Clean up the recorded height
    initialRowHeights.remove(row);
}

@Override
public void insertUpdate(DocumentEvent e) {
    SwingUtilities.invokeLater(() -> {
        adjustSize(this.scrollPane, this.table);
    });
}

@Override
public void removeUpdate(DocumentEvent e) {
    SwingUtilities.invokeLater(() -> {
        adjustSize(this.scrollPane, this.table);
    });
}

@Override
public void changedUpdate(DocumentEvent e) {
    SwingUtilities.invokeLater(() -> {
        adjustSize(this.scrollPane, this.table);
    });
}


    
}

public class StyledCell {
    private String value;
    private Font font;
    private Color backgroundColor;
    private int horizontalAlignment = JLabel.LEFT;
    private int verticalAlignment = JLabel.TOP;
    private Color foregroundColor;

public Color getForegroundColor() {
    return foregroundColor;
}

public void setForegroundColor(Color foregroundColor) {
    this.foregroundColor = foregroundColor;
}


public StyledCell(String value, Font font, Color backgroundColor) {
        this.value = value;
        this.font = font;
        this.backgroundColor = backgroundColor;  // Use the parameter value
        
        //System.out.println("StyledCell constructor called with value: " + value);
    }
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Font getFont() {
        return font;
    }

    public void setFont(Font font) {
    //System.out.println("StyledCell: Setting font to THIS FUCKIN FONT: " + font.getFontName() + " " + font.getStyle() + " " + font.getSize());
    this.font = font;
}


public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    @Override
    public String toString() {
        return value;
    }
    
   // Add new getters and setters for the alignment fields
    public int getHorizontalAlignment() {
        return horizontalAlignment;
    }

    public void setHorizontalAlignment(int horizontalAlignment) {
        this.horizontalAlignment = horizontalAlignment;
    }

    public int getVerticalAlignment() {
        return verticalAlignment;
    }

    public void setVerticalAlignment(int verticalAlignment) {
        this.verticalAlignment = verticalAlignment;
    } 
    
}

public class StyledCellRenderer extends JTextArea implements TableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (value instanceof StyledCell) {
            StyledCell styledCell = (StyledCell) value;
            setText(styledCell.getValue());
            setFont(styledCell.getFont());

            setWrapStyleWord(true);
            setLineWrap(true);
            
                         if (isSelected) {
            setBackground(table.getSelectionBackground());
            setForeground(table.getSelectionForeground());
        } else {
                        setBackground(styledCell.getBackgroundColor());
            setForeground(styledCell.getForegroundColor() != null ? styledCell.getForegroundColor() : table.getForeground());
        }
        
        
        }
        
   
        // Set the border here
        setBorder(new MatteBorder(1, 0, 0, 1, Color.GRAY));
        
        return this;
    }
}


public class StyledCellEditor extends DefaultCellEditor {
    public JTextArea editor;
    private StyledCell styledCell;
    public JTable spreadsheetTable;
    private JTable localRowHeader;


 public StyledCellEditor(JTable spreadsheetTable, JTable localRowHeader) {
        super(new JTextField());  // We initialize with JTextField but we will replace it with JTextArea wrapped in a JScrollPane
        this.spreadsheetTable = spreadsheetTable;
        this.localRowHeader = localRowHeader;
        editor = new JTextArea();
        editor.setWrapStyleWord(true);
        editor.setLineWrap(true);
        editor.setBorder(new MatteBorder(1, 0, 0, 1, Color.GRAY));
        
         editor.addPropertyChangeListener("font", new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            int row = spreadsheetTable.getSelectedRow();
            int col = spreadsheetTable.getSelectedColumn();
            if (row != -1 && col != -1) {
                resizeAndSyncRowHeader(row, col);
                System.out.println("crongbone");
            }
        }
    });
        
        // Replace the editor component with a JScrollPane that contains the JTextArea
        JScrollPane scrollPane = new JScrollPane(editor);
        scrollPane.setBorder(null);
        editorComponent = scrollPane;

        // Add a key listener to handle "Alt+Enter" for line breaks
        editor.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.isAltDown() && e.getKeyCode() == KeyEvent.VK_ENTER) {
                    editor.insert("\n", editor.getCaretPosition());
                    e.consume();  // Consume the event to prevent default behavior
                }
            }
        });
        
        
        
        editor.addKeyListener(new KeyAdapter() {
    public void keyPressed(KeyEvent e) {
        if (e.isAltDown() && e.getKeyCode() == KeyEvent.VK_ENTER) {
            editor.append("\n");
            e.consume();  // Consume the event to prevent default behavior
        } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            int row = spreadsheetTable.getSelectedRow();
            int col = spreadsheetTable.getSelectedColumn();
            if (row < spreadsheetTable.getRowCount() - 1) {
                spreadsheetTable.changeSelection(row + 1, col, false, false);
                stopCellEditing();
            }
            e.consume();  // Consume the event to prevent default behavior
        }
    }
});

    }

@Override
public boolean stopCellEditing() {
    if (editor.getText().endsWith("\n")) {
        editor.setText(editor.getText().substring(0, editor.getText().length() - 1));
    }
    try {
        return super.stopCellEditing();
    } catch (ArrayIndexOutOfBoundsException e) {
        // Handle the exception gracefully, e.g., log it or ignore it
        return false;
    }
}


@Override
public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
    if (value instanceof StyledCell) {
        styledCell = (StyledCell) value;
        editor.setText(styledCell.getValue());
        editor.setFont(styledCell.getFont());
        editor.setBackground(styledCell.getBackgroundColor());
        editor.setForeground(styledCell.getForegroundColor()); 
        editor.repaint();
    }
    

/*editor.getDocument().addDocumentListener(new DocumentListener() {
    public void insertUpdate(DocumentEvent e) {
        SwingUtilities.invokeLater(() -> resizeAndSyncRowHeader(row, column));
    }
    public void removeUpdate(DocumentEvent e) {
        SwingUtilities.invokeLater(() -> resizeAndSyncRowHeader(row, column));
    }
    public void changedUpdate(DocumentEvent e) {
        // Often not used for plain text components, but included for completeness
        SwingUtilities.invokeLater(() -> resizeAndSyncRowHeader(row, column));
    }
});*/

    return super.getTableCellEditorComponent(table, value, isSelected, row, column);
}
  int i = 1;
  private long lastResizeTime = 0;
private void resizeAndSyncRowHeader(int row, int column) {
    
     System.out.print("Crongbone");
         long now = System.currentTimeMillis();
    if (now - lastResizeTime < 200) {  // 200 ms debounce time
        return;
    }
    lastResizeTime = now; 

    System.out.println("OPE YOURE BRUH" + i);
    System.out.println("\n");
    i++;
    Object cellValue = spreadsheetTable.getValueAt(row, column);
    if (cellValue == null || cellValue.toString().isEmpty()) {
        return; // Exit early if the cell is empty
    }

    Dimension preferredSize = editor.getPreferredSize();
    //System.out.println("Preferred height: " + preferredSize.height);
    //System.out.println("Current row height: " + spreadsheetTable.getRowHeight(row));
    
    // Update the row height
    if (preferredSize.height > spreadsheetTable.getRowHeight(row)) {
        spreadsheetTable.setRowHeight(row, preferredSize.height);
    }
    
    // Update the column width
    if (preferredSize.width > spreadsheetTable.getColumnModel().getColumn(column).getPreferredWidth()) {
        spreadsheetTable.getColumnModel().getColumn(column).setPreferredWidth(preferredSize.width);
    }

    // Assuming rowHeader is accessible here
    if (localRowHeader != null) {
        localRowHeader.setRowHeight(row, spreadsheetTable.getRowHeight(row));
        System.out.println("allah");
    }
    
  
    // Remove the condition that checks if the new height is greater
    // Just set the new height directly
    spreadsheetTable.setRowHeight(row, preferredSize.height);
    
    // Assuming rowHeader is accessible here
    if (localRowHeader != null) {
        localRowHeader.setRowHeight(row, spreadsheetTable.getRowHeight(row));
    }


}



public JTextArea getEditorComponent() {
    return editor;
}


private void resizeCell(int row, int column) {
    Dimension preferredSize = editor.getPreferredSize();
   
    
    // Update the row height only if the new preferred height is greater than the current row height
    if (preferredSize.height > spreadsheetTable.getRowHeight(row)) {
        spreadsheetTable.setRowHeight(row, preferredSize.height);
        System.out.println("allah");
        
        // Assuming rowHeader is accessible here, update its row height as well
        if (rowHeader != null) {
            rowHeader.setRowHeight(row, preferredSize.height);
            System.out.println("allah2");
        }
    }
    
    // Update the column width only if the new preferred width is greater than the current column width
    if (preferredSize.width > spreadsheetTable.getColumnModel().getColumn(column).getPreferredWidth()) {
        spreadsheetTable.getColumnModel().getColumn(column).setPreferredWidth(preferredSize.width);
    }
}


    @Override
    public Object getCellEditorValue() {
        styledCell.setValue(editor.getText());
        return styledCell;
    }

    public void updateFont(Font font) {
        editor.setFont(font);
    }
}

public class EmbeddedTableStyledCellEditor extends StyledCellEditor {
 Timer debounceTimer; 
    Object originalValue; // To store the original value of the cell when editing starts
    boolean cellContentChanged = false; // New flag to indicate typing

    public EmbeddedTableStyledCellEditor(JTable table) {
        super(table, null);
        setClickCountToStart(1);

        debounceTimer = new Timer(600, e -> handleTableChanged(table));
        debounceTimer.setRepeats(false); 

        // ... [Same code as before for other listeners]

   
        // Add a KeyListener to mark the cell as changed upon typing
        editor.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                cellContentChanged = true;
            }
        });

        // Add a MouseListener to capture the original value of the cell and reset the flag
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (row != -1 && col != -1) {
                    originalValue = table.getValueAt(row, col); // Store the original value
                    cellContentChanged = false; // Reset the flag
                }
            }
        });

        // Add a cell editor listener to handle resizing after editing
        addCellEditorListener(new CellEditorListener() {

@Override
public void editingStopped(ChangeEvent e) {
    int row = table.getSelectedRow();
    int col = table.getSelectedColumn();
    if (row != -1 && col != -1) {
        Object currentValue = table.getValueAt(row, col);
        if (!Objects.equals(originalValue, currentValue)) { // Check if the value has changed
            debounceTimer.restart();
        } else {
            // Forcefully capture the state if it's the first edit.
            // This assumes that `originalValue` is null for the first edit.
            if (originalValue == null && currentValue != null) {
                handleTableChanged(table);
            }
        }
        resizeEmbeddedTable(row, col);
    }
}


            @Override
            public void editingCanceled(ChangeEvent e) {
                // No need to reset the flag here as it is reset in mousePressed
            }
        });
    
    }
private void resizeEmbeddedTable(int row, int column) {
    
    System.out.println("aetata");
    // Remember the editing state
    boolean wasEditing = spreadsheetTable.isEditing();
    int editingRow = spreadsheetTable.getEditingRow();
    int editingColumn = spreadsheetTable.getEditingColumn();
    int caretPosition = -1;
    if (wasEditing && editor != null) {
        caretPosition = editor.getCaretPosition();
    }

    SwingUtilities.invokeLater(() -> {
        if (spreadsheetTable.isEditing()) {
            return; // Skip resizing if the table is still in editing mode
        }

        Object cellValue = spreadsheetTable.getValueAt(row, column);
        if (cellValue == null || cellValue.toString().isEmpty()) {
            return; // Exit early if the cell is empty
        }

        Dimension preferredSize = editor.getPreferredSize();
        // Update the row height only if the new preferred height is significantly different
        if (Math.abs(preferredSize.height - spreadsheetTable.getRowHeight(row)) > 5) {
            spreadsheetTable.setRowHeight(row, preferredSize.height);
            System.out.println("ok dga");
        }
        
        // Update the column width only if the new preferred width is significantly different
        if (Math.abs(preferredSize.width - spreadsheetTable.getColumnModel().getColumn(column).getPreferredWidth()) > 5) {
            spreadsheetTable.getColumnModel().getColumn(column).setPreferredWidth(preferredSize.width);
        }
    });

    // Restore the editing state outside of the SwingUtilities.invokeLater block
    if (wasEditing && editingRow != -1 && editingColumn != -1) {
        spreadsheetTable.editCellAt(editingRow, editingColumn);
        if (editor != null && caretPosition != -1) {
            editor.setCaretPosition(caretPosition);
        }
    }
}

}
public void handleTableChanged(JTable source) {
    StyledCell[][] currentState = captureTableState(source);
    Stack<StyledCell[][]> undoStates = tableUndoStates.getOrDefault(source, new Stack<>());

    // Don't add to the stack if the state hasn't actually changed
    if (!undoStates.isEmpty()) {
        StyledCell[][] lastState = undoStates.peek();
        if (Arrays.deepEquals(lastState, currentState)) {
            return;
        }
    }

    undoStates.push(currentState);
    tableUndoStates.put(source, undoStates);
    tableRedoStates.remove(source);
}

class TabEditorMouseAdapter extends MouseAdapter {
    private JTabbedPane tabbedPane;

    public TabEditorMouseAdapter(JTabbedPane tabbedPane) {
        this.tabbedPane = tabbedPane;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
            int tabIndex = tabbedPane.indexAtLocation(e.getX(), e.getY());
            if (tabIndex >= 0) {
                JTextField editor = new JTextField(tabbedPane.getTitleAt(tabIndex));
                editor.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                editor.addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusLost(FocusEvent e) {
                        tabbedPane.setTitleAt(tabIndex, editor.getText());
                        tabbedPane.setTabComponentAt(tabIndex, null);
                    }
                });
                editor.addActionListener(actionEvent -> {
                    tabbedPane.setTitleAt(tabIndex, editor.getText());
                    tabbedPane.setTabComponentAt(tabIndex, null);
                });
                tabbedPane.setTabComponentAt(tabIndex, editor);
                editor.selectAll();
                editor.requestFocusInWindow();
            }
        }
    }
}

private int getFontStyle(SimpleAttributeSet attrs) {
    int style = Font.PLAIN;
    
    if (StyleConstants.isBold(attrs)) {
        style = style | Font.BOLD;
    }
    if (StyleConstants.isItalic(attrs)) {
        style = style | Font.ITALIC;
    }
    
    return style;
}

private SimpleAttributeSet getCurrentStyles() {
    SimpleAttributeSet attrs = new SimpleAttributeSet();
    
    // Assuming boldButton, italicButton, underlineButton, and strikeThroughButton are the names of your JToggleButtons
    StyleConstants.setBold(attrs, boldButton.isSelected());
    StyleConstants.setItalic(attrs, italicButton.isSelected());
    StyleConstants.setUnderline(attrs, underlineButton.isSelected());
    StyleConstants.setStrikeThrough(attrs, strikeThroughButton.isSelected());
    
    return attrs;
}

public class EmbeddedTable extends JTable {
    public EmbeddedTable(int rows, int columns) {
        
        
        super(rows, columns);
            getTableHeader().setPreferredSize(new Dimension(3, 5));
        // Set cell borders
        for (int i = 0; i < getColumnCount(); i++) {
            getColumnModel().getColumn(i).setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    setBorder(new MatteBorder(1, 0, 0, 1, Color.GRAY));  // Adjusted thickness
                    return c;
                }
            });
        }
        
        // Adjust table size
        int tableHeight = 0;
        for (int i = 0; i < getRowCount(); i++) {
            tableHeight += getRowHeight(i);
        }
        setPreferredScrollableViewportSize(new Dimension(150, tableHeight));
        setCellSelectionEnabled(true);
        setRowSelectionAllowed(false);
        setColumnSelectionAllowed(false);

    
    
    }
    
    public int getMaxRowHeight(int row) {
    int maxRowHeight = 0;
    for (int col = 0; col < getColumnCount(); col++) {
        TableCellRenderer renderer = getCellRenderer(row, col);
        Component comp = prepareRenderer(renderer, row, col);
        maxRowHeight = Math.max(comp.getPreferredSize().height, maxRowHeight);
    }
    return maxRowHeight;
}

    @Override
public void changeSelection(int rowIndex, int columnIndex, boolean toggle, boolean extend) {
    super.changeSelection(rowIndex, columnIndex, toggle, extend);
    if (editCellAt(rowIndex, columnIndex)) {
        Component editor = getEditorComponent();
        editor.requestFocusInWindow();
    }
}
@Override
public void setRowHeight(int row, int rowHeight) {
    int maxRowHeight = getMaxRowHeight(row);
    super.setRowHeight(row, Math.max(rowHeight, maxRowHeight));
}

    
}

class ExpandingDocumentListener implements DocumentListener {
    
    private final JTable table;
    private final int row;
    private final int column;

    public ExpandingDocumentListener(JTable table, int row, int column) {
        
        this.table = table;
        this.row = row;
        this.column = column;
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        updateRowHeight();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        updateRowHeight();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        updateRowHeight();
    }

    private void updateRowHeight() {
        Component editorComponent = table.getEditorComponent();
        if (editorComponent instanceof JTextComponent) {
            JTextComponent textComponent = (JTextComponent) editorComponent;
            int textHeight = textComponent.getPreferredSize().height;
            table.setRowHeight(row, Math.max(table.getRowHeight(row), textHeight));
            System.out.println("BEINGE");
        }
    }
}

    private class GlobalKeyListener implements NativeKeyListener {
        private boolean controlPressed = false;
        public void nativeKeyPressed(NativeKeyEvent e) {
            
            if(e.getKeyCode() == NativeKeyEvent.VC_CONTROL)
            {
                controlPressed = true;
            }
            
            if (controlPressed && e.getKeyCode() == NativeKeyEvent.VC_O)
            {
                SwingUtilities.invokeLater(() -> {
                    openFile();
                });
            }
            if (e.getKeyCode() == NativeKeyEvent.VC_F9) {
                SwingUtilities.invokeLater(() -> {
                    frame.setLocation(Toolkit.getDefaultToolkit().getScreenSize().width - frame.getWidth(), Toolkit.getDefaultToolkit().getScreenSize().height - frame.getHeight() - 40);
                    frame.setVisible(!frame.isVisible());
                     frame.requestFocus();
                    //frame.toFront();
                });
            }
        }

        public void nativeKeyReleased(NativeKeyEvent e) {
            if (e.getKeyCode() == NativeKeyEvent.VC_CONTROL){
                controlPressed = false;
            }
            
        }

        public void nativeKeyTyped(NativeKeyEvent e) {
        }
    }
}


