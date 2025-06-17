/*
 * $Id: StoreFileInputField.java,v 1.3 2005-03-31 19:25:39 mch Exp $
 *
 * Copyright 2003 AstroGrid. All rights reserved.
 *
 * This software is published under the terms of the AstroGrid Software License,
 * a copy of which has been included with this distribution in the LICENSE.txt file.
 */

package org.larf.browser;


import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.security.Principal;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.larf.FileNode;
import org.slinger.account.LoginAccount;

/**
 * A Panel for selecting files from VoSpace.  Provides buttons for browsing
 * myspace as well as local files, urls, etc.  Also includes a history of
 * previous choices.
 *
 * @author M Hill
 */

public class StoreFileInputField extends JPanel implements KeyListener {

   JHistoryComboBox fileEntryField = new JHistoryComboBox();
   JPasteButton pasteBtn = null;
   JButton myspaceBrowserBtn = null;
   JButton fileBrowserBtn = null;
   JButton validateBtn = null;
   JLabel validLabel = new JLabel("Valid");
   JLabel mainLabel = null;
   JPanel btnPanel = new JPanel();
   
   boolean isValid = false;

   private String browserAction = "Select";
   
   //the person operating the browser is not nec the same as the owner of
   //the files we are using
   private Principal operator = LoginAccount.ANONYMOUS;
   
   JFileChooser fileChooser = new JFileChooser();
   
   /** action is SAVE_ACTION or OPEN_ACTION from MySpaceBrowser */
   public StoreFileInputField(String label, String action, Principal aUser ) {


     this.browserAction = action;
      this.operator = aUser;
      
      fileEntryField.setEditable(true);
      fileEntryField.addKeyListener(this);
      fileEntryField.getEditor().getEditorComponent().addKeyListener(this);

      pasteBtn = new JPasteButton(fileEntryField);
      
      myspaceBrowserBtn = IconButtonHelper.makeIconButton("MySpace", "World", "Browse MySpace");
      fileBrowserBtn = IconButtonHelper.makeIconButton("Files", "Open", "Browse Local Files");
      validateBtn = IconButtonHelper.makeIconButton("Check", "Question", "Validate reference");
      
      btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.X_AXIS));
      btnPanel.add(pasteBtn);
      btnPanel.add(myspaceBrowserBtn);
      btnPanel.add(fileBrowserBtn);
      btnPanel.add(validateBtn);
      btnPanel.add(validLabel);
      
      myspaceBrowserBtn.addActionListener(
         new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               choose();
            }
         }
      );
      
      validateBtn.addActionListener(
         new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               validate();
            }
         }
      );
      
      
      mainLabel = new JLabel(label+" ", JLabel.RIGHT);

      //layout components - grid
      setLayout(new BorderLayout());
      add(mainLabel, BorderLayout.WEST);
      add(fileEntryField, BorderLayout.CENTER);
      add(btnPanel, BorderLayout.EAST);

      validate();
   }

   /** Returns label so that owning components can lay out differently */
   public JComponent getLabel() {   return mainLabel; }
   
   /** Returns entry field so that owning components can lay out differently */
   public JComponent getEntryField() { return fileEntryField;  }
   
   /** Returns control buttons so that owning components can lay out differently */
   public JComponent getControls() {   return btnPanel;  }


   /** Set who the operator is who is using this view */
   public void setOperator(Principal aUser)
   {
      this.operator = aUser;
   }

   /** Returns the text entered in the field - ie the file picked/typed in */
   public String getFileLoc()
   {
      return fileEntryField.getText();
   }

   /** For setting the text */
   public void setFileLoc(String newEntry)
   {
      fileEntryField.setItem(newEntry);
   }

   /** Start Store Browser */
   public void choose()
   {
      try
      {
         
         FileNode chosen = StorePicker.choose(operator, browserAction);
         if (chosen != null)
         {
            fileEntryField.setItem(chosen.getUri());
            validate();
         }
      } catch (IOException ioe)
      {
         JOptionPane.showMessageDialog(this, ioe, "Error starting myspace browser", JOptionPane.ERROR_MESSAGE);
      }
   }

   
   /** Checks (1) that the given reference is valid, and (2) that it exists */
   public void validate()
   {
      String loc = getFileLoc();
      
      if ((loc == null) || (loc.trim().length()==0)) {
         setValid(false);
         return;
      }

      //erm
      
      //does it exist?
      
      //this is not always wanted so let's leave it
   }
   
   
   /**
    * sets the state to invalid
    */
   public void setValid(boolean newValid)
   {
      boolean oldValid = this.isValid; //remember - we want to have this state set before we fire the changes
      this.isValid = newValid;
      firePropertyChange("Valid", this.isValid, newValid);

      if (isValid) { validLabel.setText("Valid"); } else { validLabel.setText("INVALID"); }
   }

   public boolean getValid() { return isValid; }
   
   public boolean isValid() { return isValid; }

   /**
    * Invoked when a key has been typed.
    * See the class description for {@link KeyEvent} for a definition of
    * a key typed event.
    */
   public void keyTyped(KeyEvent ke) {   }
   
   /**
    * Invoked when a key has been pressed.
    * See the class description for {@link KeyEvent} for a definition of
    * a key pressed event.
    */
   public void keyPressed(KeyEvent ke) {
      if (ke.getKeyCode() == ke.VK_ENTER)
      {
         validate();
      }

   }
   
   /**
    * Invoked when a key has been released.
    * See the class description for {@link KeyEvent} for a definition of
    * a key released event.
    */
   public void keyReleased(KeyEvent e) {  }
   
}

/*
$Log: StoreFileInputField.java,v $
Revision 1.3  2005-03-31 19:25:39  mch
semi fixed a few threading things, introduced sort order to tree

Revision 1.2  2005/03/28 02:06:35  mch
Major lump: split picker and browser and added threading to seperate UI interations from server interactions

 */



