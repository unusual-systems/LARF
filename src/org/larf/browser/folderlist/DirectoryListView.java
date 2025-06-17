/*
 * $Id: DirectoryListView.java,v 1.2 2005-04-04 01:10:15 mch Exp $
 */

package org.larf.browser.folderlist;

import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.TransferHandler;

import org.larf.FileNode;
import org.larf.browser.FileTransferHandler;
import org.larf.browser.TransferableFile;
import org.larf.browser.actions.SelectedFileGetter;
import org.larf.browser.tree.FilePopupMenu;
import org.larf.browser.tree.FolderPopupMenu;

public class DirectoryListView extends JTable implements MouseListener, DropTargetListener, SelectedFileGetter
{

	Logger log = Logger.getLogger(DirectoryListView.class.toString());   

	FolderPopupMenu folderPopupMenu = null;
	FilePopupMenu filePopupMenu = null;

	public DirectoryListView() {

		showHorizontalLines = false;
		showVerticalLines = false;

		//setDragEnabled(true);
		addMouseListener(this);
		new DropTarget(this, this);

		folderPopupMenu = new FolderPopupMenu(this);
		filePopupMenu = new FilePopupMenu(this);
	}

	public FileNode getSelectedFile() {
		int row = getSelectedRow();
		return ((DirectoryListModel) getModel()).getFile(row);
	}

	public FileNode getFile(int row) {
		return ((DirectoryListModel) getModel()).getFile(row);
	}
	/**
	 * Invoked when a mouse button has been pressed on a component.  If
	 * appropriate displays a popupmenu
	 */
	public void mousePressed(MouseEvent e) {
		if (e.getButton()==1) {
			int row = rowAtPoint(e.getPoint());
			if (row != -1) {
				changeSelection(row, -1, false, false);
				FileNode selectedFile = getSelectedFile();
				//StoreTreeView view = (StoreTreeView)e.getSource();
				FileTransferHandler fth = new FileTransferHandler(selectedFile);
				fth.exportAsDrag(this, e, TransferHandler.COPY);
			}
		}
	}

	/**
	 * Invoked when a mouse button has been released on a component- does nothing
	 */
	public void mouseReleased(MouseEvent e) {
	}

	/**
	 * Invoked when the mouse button has been clicked (pressed
	 * and released) on a component- does nothing.
	 */
	public void mouseClicked(MouseEvent e) {
		if (e.isPopupTrigger() || (e.getButton()==3)) { //popup trigger flag doesn't always seem to work...
			int row = rowAtPoint(e.getPoint());
			if (row != -1) {
				changeSelection(row, -1, false, false);
				FileNode file = getSelectedFile();
				if (file.isFolder()) {
					folderPopupMenu.show(this, e.getX(), e.getY());
				}
				else {
					filePopupMenu.show(this, e.getX(), e.getY());
				}
			}
		}
	}

	/**
	 * Invoked when the mouse exits a component- does nothing.
	 */
	public void mouseExited(MouseEvent e) { }

	/**
	 * Invoked when the mouse enters a component - does nothing.
	 */
	public void mouseEntered(MouseEvent e) {
	}

	/**
	 * Called if the user has modified
	 * the current drop gesture.
	 * <P>
	 * @param dtde the <code>DropTargetDragEvent</code>
	 */
	public void dropActionChanged(DropTargetDragEvent dtde) {
	}

	/**
	 * Called while a drag operation is ongoing, when the mouse pointer enters
	 * the operable part of the drop site for the <code>DropTarget</code>
	 * registered with this listener.
	 *
	 * @param dtde the <code>DropTargetDragEvent</code>
	 */
	public void dragEnter(DropTargetDragEvent dtde) {
	}

	/**
	 * Called while a drag operation is ongoing, when the mouse pointer has
	 * exited the operable part of the drop site for the
	 * <code>DropTarget</code> registered with this listener.
	 *
	 * @param dte the <code>DropTargetEvent</code>
	 */
	public void dragExit(DropTargetEvent dte) {
	}

	/**
	 * Called when a drag operation is ongoing, while the mouse pointer is still
	 * over the operable part of the drop site for the <code>DropTarget</code>
	 * registered with this listener.
	 *
	 * @param dtde the <code>DropTargetDragEvent</code>
	 */
	public void dragOver(DropTargetDragEvent dtde) {
		int row = rowAtPoint(dtde.getLocation());
		if (row != -1) {
			changeSelection(row, -1, false, false);
		}
	}

	/**
	 * Called when the drag operation has terminated with a drop on
	 * the operable part of the drop site for the <code>DropTarget</code>
	 * registered with this listener.
	 * <p>
	 * This method is responsible for undertaking
	 * the transfer of the data associated with the
	 * gesture. The <code>DropTargetDropEvent</code>
	 * provides a means to obtain a <code>Transferable</code>
	 * object that represents the data object(s) to
	 * be transfered.<P>
	 * From this method, the <code>DropTargetListener</code>
	 * shall accept or reject the drop via the
	 * acceptDrop(int dropAction) or rejectDrop() methods of the
	 * <code>DropTargetDropEvent</code> parameter.
	 * <P>
	 * Subsequent to acceptDrop(), but not before,
	 * <code>DropTargetDropEvent</code>'s getTransferable()
	 * method may be invoked, and data transfer may be
	 * performed via the returned <code>Transferable</code>'s
	 * getTransferData() method.
	 * <P>
	 * At the completion of a drop, an implementation
	 * of this method is required to signal the success/failure
	 * of the drop by passing an appropriate
	 * <code>boolean</code> to the <code>DropTargetDropEvent</code>'s
	 * dropComplete(boolean success) method.
	 * <P>
	 * Note: The data transfer should be completed before the call  to the
	 * <code>DropTargetDropEvent</code>'s dropComplete(boolean success) method.
	 * After that, a call to the getTransferData() method of the
	 * <code>Transferable</code> returned by
	 * <code>DropTargetDropEvent.getTransferable()</code> is guaranteed to
	 * succeed only if the data transfer is local; that is, only if
	 * <code>DropTargetDropEvent.isLocalTransfer()</code> returns
	 * <code>true</code>. Otherwise, the behavior of the call is
	 * implementation-dependent.
	 * <P>
	 * @param dtde the <code>DropTargetDropEvent</code>
	 */
	public void drop(DropTargetDropEvent dtde) {

		//let's see what we've got
		Transferable t = dtde.getTransferable();
		try {
			FileNode file = (FileNode) t.getTransferData(TransferableFile.FILE_FLAVOUR);
			dtde.acceptDrop(TransferHandler.COPY);
			log.finer("Would insert "+file);
		}
		catch (IOException ioe) {
			log.log(Level.SEVERE, ioe+" dropping "+t,ioe);
			JOptionPane.showMessageDialog(this, ioe+ " dropping "+t, "Drag and drop", JOptionPane.ERROR_MESSAGE);
		}
		catch (UnsupportedFlavorException ufe) {
			log.log(Level.SEVERE, ufe+" dropping "+t,ufe);
			dtde.rejectDrop();
		}
	}

}

