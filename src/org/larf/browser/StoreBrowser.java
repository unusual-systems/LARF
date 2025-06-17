/*
 * $Id: StoreBrowser.java,v 1.11 2005-04-04 01:10:15 mch Exp $
 *
 * Copyright 2003 AstroGrid. All rights reserved.
 *
 * This software is published under the terms of the AstroGrid Software License,
 * a copy of which has been included with this distribution in the LICENSE.txt file.
 */

package org.larf.browser;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.Principal;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import org.larf.FileNode;
import org.larf.browser.actions.AddStoreAction;
import org.larf.browser.actions.CopyFromUrlAction;
import org.larf.browser.actions.DeleteAction;
import org.larf.browser.actions.DownloadAction;
import org.larf.browser.actions.NewFolderAction;
import org.larf.browser.actions.RefreshAction;
import org.larf.browser.actions.SelectedFileGetter;
import org.larf.browser.actions.UploadAction;
import org.larf.browser.folderlist.DirectoryListModel;
import org.larf.browser.folderlist.DirectoryListView;
import org.larf.browser.imageview.ImageContentsView;
import org.larf.browser.textview.ContentViewer;
import org.larf.browser.textview.TextContentsView;
import org.larf.browser.tree.DirectoryTreeModel;
import org.larf.browser.tree.FileViewNode;
import org.larf.browser.tree.StoreTreeView;
import org.larf.mime.MimeTypes;
import org.slinger.account.IvoAccount;
import org.slinger.account.LoginAccount;

/**
 * A non-modal window for managing files in stores. Uses storeclient
 * package so should work with anything that that package can handle.
 *
 * @author mch
 */


public class StoreBrowser extends JFrame implements SelectedFileGetter
{
   JHistoryComboBox addressPicker = new JHistoryComboBox();

   JSplitPane splitter = null;
   
   //left hand panel - tree view
   StoreTreeView treeView = null;

   /** Right hand panel shows folder or file contents */
   Component contentViewer;
   JPanel contentHolder; //the panel that holds teh content
   
   /** Special case of contentPanel holding lists of files, so that we can
    examine it for selections, etc */
   DirectoryListView directoryView = null;
   /** We keep one instance of the directory viewer to save recreating it */
   DirectoryListView directoryViewer = new DirectoryListView();
   
   //toolbar buttons
   JButton addStoreBtn = null;
   JButton refreshBtn = null;
   JButton uploadBtn = null;
   JButton uploadUrlBtn = null;
   JButton downloadBtn = null;
   JButton deleteBtn = null;
   JButton copyBtn = null;
   JButton newFolderBtn = null;

   //default size
   public static final int DEF_SIZE_X = 800;
   public static final int DEF_SIZE_Y = 600;
   
   //used to lookup files on disk
   JFileChooser chooser = new JFileChooser();

   static Logger log = Logger.getLogger(StoreBrowser.class.toString());
   
   //component operator
   Principal operator = null;

   private StoreBrowser(Principal user) throws IOException   {
      super();
      this.operator = user;
      initComponents();
   }

   private StoreBrowser(JFrame owner, Principal user) throws IOException   {
      super();
      this.operator = user;
      initComponents();
   }

   /**
    * Builds GUI and initialises components
    */
   private void initComponents() throws IOException
   {
      setTitle("Browsing Stores as "+operator.getName());
      
      //menu bar
      JMenuBar mainMenu = new JMenuBar();
      JMenu fileMenu = new JMenu("File");
      mainMenu.add(fileMenu);
      fileMenu.add(new JMenuItem(new DeleteAction(this)));
      fileMenu.add(new JMenuItem(new RefreshAction(this)));
      fileMenu.add(new JMenuItem(new DownloadAction(chooser, this)));
      fileMenu.add(new JMenuItem(new UploadAction(chooser, this)));
      fileMenu.add(new JMenuItem(new CopyFromUrlAction(this)));

      setJMenuBar(mainMenu);

      //address picker
      addressPicker.addItem(""); //empty one to start with
      JPanel addressPanel = new JPanel(new BorderLayout());
      addressPanel.add(new JLabel("Address"), BorderLayout.WEST);
      addressPanel.add(addressPicker, BorderLayout.CENTER);
      
      //tree view
      treeView = new StoreTreeView(operator);
      JScrollPane treePanel = new JScrollPane();
      treePanel.getViewport().setView(treeView);

      //toolbar
      addStoreBtn = makeToolbarButton(new AddStoreAction(treeView)); //IconButtonHelper.makeIconButton("AddStore", "AddStore", "Add store to the tree");
      refreshBtn = makeToolbarButton(new RefreshAction(this)); //IconButtonHelper.makeIconButton("Refresh", "Refresh", "Reloads file list from server");
      newFolderBtn = makeToolbarButton(new NewFolderAction(this)); //IconButtonHelper.makeIconButton("New", "NewFolder", "Creates a new folder");
      uploadBtn = makeToolbarButton(new UploadAction(chooser, this)); //IconButtonHelper.makeIconButton("Put", "Up", "Upload file from local disk to MySpace");
      uploadUrlBtn = makeToolbarButton(new CopyFromUrlAction(this)); //IconButtonHelper.makeIconButton("PutUrl","Putty", "Copy file from public URL to MySpace");
      downloadBtn = makeToolbarButton(new DownloadAction(chooser, this)); //IconButtonHelper.makeIconButton("Get","Down", "Download file from MySpace to local disk");
      deleteBtn = makeToolbarButton(new DeleteAction(this)); //IconButtonHelper.makeIconButton("Del","Delete", "Deletes selected item");
      //copyBtn = makeToolbarButton(new CopyAction(this, this)); //IconButtonHelper.makeIconButton("Copy", "Copy", "Copies selected item");

      JToolBar toolbar = new JToolBar();
      toolbar.add(addStoreBtn);
      toolbar.add(refreshBtn);
      toolbar.add(newFolderBtn);
      toolbar.add(uploadBtn);
      toolbar.add(uploadUrlBtn);
      toolbar.add(downloadBtn);
      toolbar.add(deleteBtn);
      //toolbar.add(copyBtn);


      //layout the components
      contentHolder = new JPanel(new BorderLayout());
      JPanel RHS = new JPanel(new BorderLayout());
      JPanel LHS = new JPanel(new BorderLayout());
      LHS.add(treePanel, BorderLayout.CENTER);
      RHS.add(contentHolder, BorderLayout.CENTER);
      splitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, LHS, RHS);
      splitter.setDividerLocation(0.35);
      
      //setting the divider location only works once the UI is displayed, so add to queue
      SwingUtilities.invokeLater(new Runnable() {
            public void run() {
               // TODO - load from preferences
               setSize(DEF_SIZE_X,DEF_SIZE_Y);
               splitter.setDividerLocation(0.35);
            }
      });
      /**/
      //add address panel to top
      JPanel panel1 = new JPanel(new BorderLayout());
      panel1.add(addressPanel, BorderLayout.NORTH);
      panel1.add(splitter, BorderLayout.CENTER);

      //add toolbar above that (so it can be moved around)
      JPanel panel2 =  new JPanel(new BorderLayout());
      panel2.add(toolbar, BorderLayout.NORTH);
      panel2.add(panel1, BorderLayout.CENTER);

      //topPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
      getContentPane().setLayout(new BorderLayout());

      getContentPane().add(panel2);

      addressPicker.addActionListener(
         new ActionListener() {
            public void actionPerformed(ActionEvent e) {
//               treeView.setSelectionsetStore(addressPicker.getSelectedItem().toString());
            }
         }
      );
      

      treeView.addTreeSelectionListener(
         new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
               FileNode f = treeView.getSelectedFile();
               if (f!= null) {
                  addressPicker.addItem(f.getUri());
                  addressPicker.setSelectedItem(f.getUri());
               }
               setContentPane();
            }
         }
      );

      /*
      treeView.addMouseListener( new MouseAdapter() {
         public void mouseClicked(MouseEvent e) {
      
            if (e.isPopupTrigger() || (e.getButton()==3)) { //popup trigger flag doesn't always seem to work...
               TreePath mousePath = treeView.getPathForLocation(e.getX(), e.getY());
               if (mousePath != null) {
                  treeView.setSelectionPath(mousePath);
                  FileViewNode selectedNode = (FileViewNode) mousePath.getLastPathComponent();
                  if (selectedNode instanceof StoreRootNode) {
                        new StorePopupMenu(treeView, (StoreRootNode) treeView.getSelectedNode()).show(treeView, e.getX(), e.getY());
                  }
                  else {
                     if (selectedNode.getFile().isFolder()) {
                        new FolderPopupMenu((DirectoryTreeModel) treeView.getModel(), treeView).show(treeView, e.getX(), e.getY());
                     }
                     else {
                        new FilePopupMenu((DirectoryTreeModel) treeView.getModel(), treeView).show(treeView, e.getX(), e.getY());
                     }
                  }
               }
            }
            
         }
      });
       */
      
      directoryViewer.addMouseListener( new MouseAdapter() {
         public void mouseClicked(MouseEvent e) {
            //select file in tree if double clicked in directory list
            if ((e.getButton()==1) && (e.getClickCount()>1)) {
               int row = directoryView.rowAtPoint(e.getPoint());
               if (row != -1) {
                  FileNode file = directoryView.getFile(row);
                  if (file.isFolder()) {
                     //find corresponding node in tree
                     FileViewNode selectedNode = treeView.getSelectedNode();
                     FileViewNode childNode = selectedNode.getViewChild(file);
                     TreePath pathToSelect = treeView.getSelectionPath().pathByAddingChild(childNode);
                     ((DirectoryTreeModel) treeView.getModel()).reload(selectedNode);
                     treeView.expandPath(treeView.getSelectionPath());
                     treeView.setSelectionPath(pathToSelect);
                  }
                  else {
                     //open in special editor?
                  }
               }
            }
            
         }
      });
      
      new EscEnterListener(this, null, null, true);
      
      pack();
      invalidate();
   }

   /** Makes a toolbar button for an action - removes text if icon is available  */
   public JButton makeToolbarButton(Action action) {
      JButton button = new JButton(action);
      if (action.getValue(Action.SMALL_ICON) != null) {
         button.setText("");
      }
      return button;
   }
   

   /** Returns current operator of this component */
   public Principal getOperator() {
      return operator;
   }
  
   /** Sets the content pane depending on the selected file/folder */
   protected void setContentPane() {
      FileNode f = treeView.getSelectedFile();

      //abort load of existing panel
      contentHolder.removeAll(); //clear display
      if (contentViewer != null) {
         if (contentViewer instanceof ContentViewer) {
               ((ContentViewer) contentViewer).abortLoad();
         }
         contentViewer = null;
      }
      
      if (f == null) {
         //do nothing
      }
      else {
         if (f.isFolder()) {
            //show directory view
            try {
               directoryView = directoryViewer;
               directoryView.setModel(new DirectoryListModel(f));
               contentViewer = new JScrollPane(directoryView);
               contentHolder.add(contentViewer);
            }
            catch (IOException ioe) {
               log.log(Level.SEVERE, ioe+" Making model of "+f,ioe);
               JOptionPane.showMessageDialog(null, ioe, "Error making model of "+f, JOptionPane.ERROR);
            }
         }
         else {
            directoryView = null; //there is no directory view so set to null so we know not to look in it for selected files
            
            //show file contents if possible
            if (f.getMimeType() == null) {
               //do nothing - don't know what it is
            }
            else {
               if ((f.getMimeType().startsWith(MimeTypes.TEXT)) || (f.getMimeType().equals(MimeTypes.VOTABLE))) {
                  
                  contentViewer = new TextContentsView(f);
                  contentHolder.add(contentViewer);
               }
               else if (f.getMimeType().startsWith(MimeTypes.IMAGE)) {
                  contentViewer = new ImageContentsView(f);
                  contentHolder.add(contentViewer);
               }
               else {
                  //don't know what to do with this type
               }
               
            }
         }
      }
   }
   /** Returns the currently selected file, which may be selected on the tree view,
    * or may be selected via a folder on teh tree view and a row selection on the folder
    * contents view */
   public FileNode getSelectedFile() {
      FileNode file = treeView.getSelectedFile();
      if ((file != null) && (file.isFolder()) && (directoryView != null) && (directoryView.getSelectedRow()>-1)) {
         try {
            file = file.listFiles()[directoryView.getSelectedRow()];
         }
         catch (IOException ioe) {
            log.log(Level.SEVERE, ioe+" reading children of "+file,ioe);
            JOptionPane.showMessageDialog(null, ioe, "Error reading children of "+file, JOptionPane.ERROR);
         }
      }
      return file;
   }
    
    /**
     * Standalone
     */
   public static void main(String[] args) throws IOException, URISyntaxException, IOException {

      Image logo = ImageFactory.getImage("AstroGrid.gif");
      logo = logo.getScaledInstance(70, 50,Image.SCALE_REPLICATE);
      
      Splash splash = new Splash("LARF", "Light Access to Remote Files                ", "1.0", Color.WHITE, Color.BLUE, logo, null);
      
//      Principal user = new IvoAccount("DSATEST1", "uk.ac.le.star", null);
      Principal user = new LoginAccount("vxxgoeti", "lorraine");//new IvoAccount("martinhill", "uk.ac.le.star", null);
      //Principal user = new LoginAccount("anonymous", "mch@roe.ac.uk");
      if ((args.length>1)) {
         user = new IvoAccount(args[0]);
      }
      
      final StoreBrowser browser = new StoreBrowser(user);
      browser.setIconImage(ImageFactory.getImage("Folder.gif"));
      browser.setLocation(100,100);
      //((StoresList) browser.treeView.getModel().getRoot()).addTestStores();
      splash.hide();
      browser.show();
      browser.setSize(DEF_SIZE_X, DEF_SIZE_Y);
      browser.splitter.setDividerLocation(0.4);
      //setting the divider location only works once the UI is displayed, so add to queue
      SwingUtilities.invokeLater(new Runnable() {
            public void run() {
               // TODO - load from preferences
               browser.setSize(DEF_SIZE_X,DEF_SIZE_Y);
               browser.splitter.setDividerLocation(0.45);
            }
      });
   }
}


