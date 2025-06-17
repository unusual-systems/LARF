/*
 * $Id: UploadAction.java,v 1.1 2005-04-04 01:10:15 mch Exp $
 *
 * Copyright 2003 AstroGrid. All rights reserved.
 *
 * This software is published under the terms of the AstroGrid Software License,
 * a copy of which has been included with this distribution in the LICENSE.txt file.
 */

package org.larf.browser.actions;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.logging.Level;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.larf.FileNode;
import org.larf.browser.IconFactory;


/**
 * Action for deleting a file - see package documentation
 */

public class UploadAction extends FileChangingAction {

	public final static String ACTION_COMMAND = "Upload";

	SelectedFileGetter getter = null;
	JFileChooser chooser = null;

	/** Pass in localChooser so that we can have a single chooser being used for
	 * all local operations  */
	public UploadAction(JFileChooser localChooser, SelectedFileGetter selectedGetter) {
		this.getter = selectedGetter;
		this.chooser = localChooser;

		//set icons etc
//		putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
		putValue(AbstractAction.ACTION_COMMAND_KEY, ACTION_COMMAND);
		putValue(AbstractAction.LONG_DESCRIPTION, "Upload file from local hard disk to currently selected folder");
		putValue(AbstractAction.MNEMONIC_KEY, new Integer(KeyEvent.VK_U));
		putValue(AbstractAction.NAME, "Upload");
		putValue(AbstractAction.SHORT_DESCRIPTION, "Uploads to selected folder");
		putValue(AbstractAction.SMALL_ICON, IconFactory.getIcon("Export"));
	}

	/** Upload button pressed - copy file from disk to Myspace */
	public void actionPerformed(ActionEvent e) {

		FileNode target = getter.getSelectedFile();
		if (target == null)   {
			JOptionPane.showMessageDialog((Component) e.getSource(), "Set directory or filename to upload to");
			return;
		}
		if (!target.isFolder()) {
			int response = JOptionPane.showConfirmDialog((Component) e.getSource(), "Are you sure you want to overwrite '"+target.getPath()+"?" );
			if (response == JOptionPane.NO_OPTION) {
				return;
			}
		}

		int response = chooser.showDialog((Component) e.getSource(), "Upload");

		if (response == chooser.APPROVE_OPTION)
		{
			FileNode source = new org.larf.adaptors.file.FileAdaptor(chooser.getSelectedFile()); //local file

			if (target.isFolder()) {
				//use source to specify name of file at target directory
				try {
					target = target.getChild(source.getName());
				} catch (IOException e1) {
			         log.log(Level.SEVERE, e+" creating child '"+source.getName()+"' of '"+target+"'",e);
			         JOptionPane.showMessageDialog( (Component) e.getSource(), e+" creating child '"+source.getName()+"' of '"+target+"'", "Upload Failure", JOptionPane.ERROR_MESSAGE);
				}
			}

			CopyAction.transfer((Component) e.getSource(), source, target);
			//update relevent displays...
			fireFileChanged(new FileChangedEvent(target, false, true));

	}
}

}

