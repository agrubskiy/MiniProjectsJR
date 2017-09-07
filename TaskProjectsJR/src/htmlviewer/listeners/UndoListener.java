package htmlviewer.listeners;

import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoManager;

public class UndoListener implements UndoableEditListener {
    private UndoManager undoManager;

    public UndoListener(UndoManager manager) {
        this.undoManager = manager;
    }

    @Override
    public void undoableEditHappened(UndoableEditEvent e) {
        undoManager.addEdit(e.getEdit());
    }
}
