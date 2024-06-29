import javax.swing.*;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

public class WordUndoManager extends UndoManager implements UndoableEditListener {
    private Timer timer;
    private CompoundEdit compoundEdit;
    private List<Long> editTimestamps = new ArrayList<>();

public WordUndoManager() {
    timer = new Timer(2000, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            endCompoundEdit();
            startCompoundEdit();
        }
    });
}


@Override
public void undoableEditHappened(UndoableEditEvent e) {
    if (compoundEdit == null) {
        startCompoundEdit(e.getEdit());
    } else {
        compoundEdit.addEdit(e.getEdit());
    }

    timer.restart();
}





private void startCompoundEdit(UndoableEdit anEdit) {
    compoundEdit = new CompoundEdit();
    compoundEdit.addEdit(anEdit);
    addEdit(compoundEdit);
}

    
    private void endCompoundEdit() {
    System.out.println("Ending compound edit");
    compoundEdit.end();
    addEdit(compoundEdit);
    compoundEdit = new CompoundEdit();
}


@Override
public void undo() throws CannotUndoException {
    if (canUndo()) {
        endCompoundEdit();
        super.undo();
        startCompoundEdit();
    }
}

@Override
public void redo() throws CannotRedoException {
    if (canRedo()) {
        endCompoundEdit();
        super.redo();
        startCompoundEdit();
    }
}

private void startCompoundEdit() {
    compoundEdit = new CompoundEdit();
}



    private void startNewCompoundEdit() {
        compoundEdit = new CompoundEdit();
    }

    private void undoLastTwoSeconds() {
        long threshold = System.currentTimeMillis() - 2000;
        while (!editTimestamps.isEmpty() && editTimestamps.get(editTimestamps.size() - 1) >= threshold) {
            super.undo();
            editTimestamps.remove(editTimestamps.size() - 1);
        }
    }

    private void redoLastTwoSeconds() {
        long threshold = System.currentTimeMillis() - 2000;
        while (!editTimestamps.isEmpty() && editTimestamps.get(0) >= threshold) {
            super.redo();
            editTimestamps.remove(0);
        }
    }



@Override
public boolean canUndo() {
    return super.canUndo();
}

@Override
public boolean canRedo() {
    return super.canRedo();
}


}



