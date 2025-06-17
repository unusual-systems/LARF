/*
 * $Id: FileViewNodeRenderer.java,v 1.1 2005-04-04 01:10:15 mch Exp $
 *
 * Copyright 2003 AstroGrid. All rights reserved.
 *
 * This software is published under the terms of the AstroGrid Software License,
 * a copy of which has been included with this distribution in the LICENSE.txt file.
 */

package org.larf.browser.tree;

import java.awt.Color;
import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.larf.browser.IconFactory;
import org.larf.browser.MimeIcons;



/**
 * Special renderer for store nodes in trees.  DefaultTreeCellRenderer subclasses
 * JLabel, which can take both icon and string
 */

public class FileViewNodeRenderer extends DefaultTreeCellRenderer  {

   Icon errorIcon = null;
   Icon storeIcon = null;
   
   /** Initialise icons etc */
   public FileViewNodeRenderer() {

      errorIcon = IconFactory.getIcon("Error", IconFactory.SMALL);
      storeIcon = IconFactory.getIcon("Store", IconFactory.SMALL);
      
   }

   public Icon getLeafIcon(Object node) {
      if (node instanceof FileViewNode) {
         String mimetype = ((FileViewNode) node).getFile().getMimeType();
         Icon icon = MimeIcons.getIcon(mimetype);
         if (icon != null) {
            return icon;
         }
         return getLeafIcon();
      }
      else {
         return getLeafIcon(); //default
      }
   }
   
   public Component getTreeCellRendererComponent(JTree tree, Object storeNode, boolean isSelected, boolean isExpanded, boolean isLeaf, int row, boolean hasFocus) {

      //Sets all the properties (mostly copied from DefaultTreeCellRenderer)
      if (isSelected)   setForeground(getTextSelectionColor());
      else              setForeground(getTextNonSelectionColor());

      // There needs to be a way to specify disabled icons.
      if (!tree.isEnabled()) {
         setEnabled(false);
         if (storeNode instanceof StoreRootNode) {
            setDisabledIcon(storeIcon);
         } else if (isLeaf) {
            setDisabledIcon(getLeafIcon(storeNode));
         } else if (isExpanded) {
            setDisabledIcon(getOpenIcon());
         } else {
            setDisabledIcon(getClosedIcon());
         }
      }
      else {
         setEnabled(true);
         if (storeNode instanceof StoreRootNode) {
            setIcon(storeIcon);
         } else if (isLeaf) {
            setIcon(getLeafIcon(storeNode));
         } else if (isExpanded) {
            setIcon(getOpenIcon());
         } else {
            setIcon(getClosedIcon());
         }
      }

      //so painter knows to colour in background
      selected = isSelected;
      
      if (storeNode instanceof FileViewNode) {
         
         FileViewNode storeFileNode = ((FileViewNode) storeNode);

         String loading = "";
         if (storeFileNode.getCompleteness() == FileViewNode.CONNECTING) {
            loading = " [Connecting] ";
            setForeground(Color.GRAY); //should be loaded from look and feel but it will do for now
         }
         if (storeFileNode.getCompleteness() == FileViewNode.LOADINGCHILDREN) {
            loading = " [Loading] ";
            setForeground(Color.GRAY); //should be loaded from look and feel but it will do for now
         }
   
         Throwable error = storeFileNode.getError(); //read once in case it's removed while we execute this
         if (error == null) {
            setText(storeFileNode.getName()+loading);
         }
         else {
            setForeground(Color.PINK);
            setIcon(errorIcon);
            setText(storeFileNode.getName()+" ["+error.getMessage()+"]");
            setToolTipText(error.getMessage());
            tree.validate();
         }
      }

      
      /*
      try {
         StoreFile file = label.storeFileNode.getFile(); //make sure you can get it OK
         
      }
      catch (IOException e) {
         label.setIcon(errorIcon);
         label.setToolTipText(e.getMessage());
      }
      catch (URISyntaxException e) {
         label.setIcon(errorIcon);
         label.setToolTipText(e.getMessage());
      }
       /**/
      return this;
   }
      
}

