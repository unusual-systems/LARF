/*
 * $Id: FileViewNode.java,v 1.1 2005-04-04 01:10:15 mch Exp $
 *
 * Copyright 2003 AstroGrid. All rights reserved.
 *
 * This software is published under the terms of the AstroGrid Software License,
 * a copy of which has been included with this distribution in the LICENSE.txt file.
 */

package org.larf.browser.tree;

import java.io.IOException;
import java.security.Principal;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

import org.larf.FileNode;
import org.larf.browser.ChildrenLoader;
import org.larf.browser.FileSorter;

/**
 * StoreFile adaptor for JTree views (or similar).
 *
 * This implements a threaded child loader, so that the UI doesn't freeze while
 * we wait for remote services to respond.  This requires some rather careful
 * threadsafing; I haven't done this formally, but we have to be very careful
 * with refreshing.  For the moment I've assumed that any reload has to wait
 * for the previous one to finish, otherwise it can get very missing ensuring the
 * loading thread we have a handle to is the same as the one we had a moment ago
 */

public class FileViewNode extends DefaultMutableTreeNode implements TreeNode {

	Logger log = Logger.getLogger(FileViewNode.class.toString());

	FileNode file = null;
	Principal user = null;

	Throwable lastError = null;

//	ChildrenLoader loading = null;

	DefaultTreeModel model = null; //used to update when children are loaded

	FileSorter sorter = new FileSorter(false, true);

	/* -1 = connecting (loading properties) , 0 = loading children, 1 = fully loaded.  By
   using just the one flag and having it changed in one place only we can stop
   threading problems.  Note that the loading thread changes it, but the loading
    thread cna only be created in one place */
	private int completeness = CONNECTING;
	public final static int CONNECTING = -2;
	public final static int CONNECTED = -1;
	public final static int LOADINGCHILDREN = 0;
	public final static int COMPLETE = 1;

	public FileViewNode(DefaultTreeModel givenModel, MutableTreeNode givenParent, FileNode aFile, Principal aUser) throws IOException {
		this.parent = givenParent;
		this.file = aFile;
		this.user = aUser;
		this.model = givenModel;
		children = new Vector();
		if (file != null) {
			completeness = CONNECTED;
		}
	}

	public FileNode getFile()  {
		return file;
	}

	public String getName() {
		return file.getName();
	}

	public Principal getUser() {
		return user;
	}

	/** Used only by loading threads */
	public void setCompleteness(int newCompleteness) {
		this.completeness = newCompleteness;
	}

	/** Used by displays */
	public int getCompleteness() {
		return completeness;
	}


	/** Returns the *node* path, which may be longer than the file path as it may
    include servers, etc beyond the root of the file */
	public String getFilePath() {
		if (getParent() == null) {
			return "/";
		}
		else {
			return ((FileViewNode) getParent()).getPath()+"/"+getName();
		}
	}

	/**
	 * Returns the number of children <code>TreeNode</code>s the receiver
	 * contains.
	 */
	public synchronized int getChildCount() {
		if (completeness < LOADINGCHILDREN) {
			startLoadChildren();
		}
		return children.size();
	}

	/** Refresh = clears the children and reloads them */
	public void refresh() {
		startLoadChildren();
	}

	/** Returns teh child viewnode that wraps the given filenode */
	public FileViewNode getViewChild(FileNode findFile) {
		if (completeness < COMPLETE) {
			//force load children, and do it now - too painful to forward this to a thread
			try {
				FileNode[] fileChilds = sorter.sort(file.listFiles());
				setCompleteness(COMPLETE); //so the consequences of the adds in setChildren don't kick off a loadChildren
				setChildren(fileChilds);
			}
			catch (IOException ioe) {
				log.log(Level.SEVERE, ioe+" loading children for "+file,ioe);
			}
		}
		Enumeration e = children.elements();
		while (e.hasMoreElements()) {
			FileViewNode child = (FileViewNode) e.nextElement();
			if (child.file.equals(findFile)) {
				return child;
			}
		}
		return null;
	}

	/** Override so we can order it properly
   public void add(MutableTreeNode newChild) {

      if (children == null) {
         insert(newChild,0);
      }
      else {
         //find insert position - don't like this, doubt it's threadsafe
         Enumeration e = children.elements();
         int i = 0;
         while (e.hasMoreElements() &&  ((FileViewNode) e.nextElement()).compareTo( (FileViewNode) newChild) < 0 ) {
            i++;
         }
         insert(newChild, i);
      }
   }

   /** Returns < 0 if this file should be placed before the given file in
	 * directory lists, 0 if they are equal and > 0 if it should be placed after
   public int compareTo(Object node) {
      return sortKey.compareTo( ((FileViewNode) node).sortKey);
      /*
      if (file.isFolder() && (!((FileViewNode) node).file.isFolder())) {
         return -1;
      }
      if (!file.isFolder() && (((FileViewNode) node).file.isFolder())) {
         return 1;
      }
      //otherwise by name comparison
//    return (file.getName().compareTo(node.file.getName()));
      //otherwise by caseless name comparison
      return (file.getName().toLowerCase().compareTo(((FileViewNode) node).file.getName().toLowerCase()));

   }
	 */
	/** Refresh - clears the children and file's children. Synchronised as this
	 * should be the only public method that modifies the completeness flag  */
	public synchronized void startLoadChildren() {

		if ( completeness != LOADINGCHILDREN)  { //only if not already loading
			completeness = LOADINGCHILDREN;
			clearError();
			model.nodeChanged(this); //so the display updates

			if ((file != null) && (file.isFolder())) {
				ChildrenLoader loading = new ChildrenLoader(file, new ChildNodeSetter(this, model, user), sorter);
				Thread loadingThread = new Thread(loading);
				loadingThread.start();
			}
		}
	}

	/** Given a list of FileNode children loaded, sorts them into order and
	 * adds them into the tree */
	public void setChildren(FileNode[] childFiles) {

		this.removeAllChildren();

		//done in loading thread now FileNode[] childFiles = sorter.sort(childFiles);
		for (int i = 0; i < childFiles.length; i++) {
			try {
				//it's important that we use the proper 'add' here (rather than adding directly to children) as otherwise the displays screws up
				add(new FileViewNode(model, null, childFiles[i], user));
			}
			catch (IOException e) {
				log.log(Level.SEVERE, "Creating child node for "+childFiles[i],e);
			}
		}
		/*
      //create wrapping treeview nodes and check for folder-only views
      log.trace("Creating wrapper nodes");
      FileViewNode[] childTreeNodes = new FileViewNode[childFiles.length];
      int n=-1;
      for (int f = 0; f < childFiles.length; f++) {
         try {
            if ((!DirectoryListModel.foldersOnly) || (childFiles[f].isFolder())) {
               n++;
               childTreeNodes[n] = new FileViewNode(model, null, childFiles[f], user);
            }
         }
         catch (IOException e) {
            LogFactory.getLog(ChildNodeSetter.class).error("Creating child node for "+childFiles[f],e);
         }
      }
      if (n==-1) {
         return; //nothing found
      }
      //sort
      log.trace("Sorting");
      Arrays.sort(childTreeNodes,0,n);

      //add
      log.trace("Adding");
      for (int i = 0; i < n+1; i++) {
          add(childTreeNodes[i]);
      }
      log.trace("done");
		 */
	}




	/** When we close down this object, we also want to close down any
	 * updating that might be going on.  We could make this more explicit...
	 *
   public void finalize() {
      try {
         loading.abort();
      }
      catch (NullPointerException npe) {
         //not loading - never mind eh? We could test for loading == null but it might be made null between the check and the call
      }
   }

   /**
	 * Returns the child <code>TreeNode</code> at index
	 * <code>childIndex</code>.
	 */
	public TreeNode getChildAt(int childIndex) {
		return (TreeNode) children.get(childIndex);
	}

	/**
	 * Returns the parent <code>TreeNode</code> of the receiver.
	 */
	public TreeNode getParent() {
		return parent;
	}

	/**
	 * Returns true if the receiver is a leaf.
	 */
	public boolean isLeaf() {
		return !file.isFolder();
	}

	/**
	 * Returns the children of the receiver as an <code>Enumeration</code>.
	 */
	public Enumeration children() {
		return children.elements();
	}

	/**
	 * Returns the index of <code>node</code> in the receivers children.
	 * If the receiver does not contain <code>node</code>, -1 will be
	 * returned.
	 */
	public int getIndex(TreeNode node) {
		return children.indexOf(node);
	}

	/**
	 * Returns true if the receiver allows children.
	 */
	public boolean getAllowsChildren() {
		return true;
	}

	/** Stores last error so we can display it;  Recording errors rather than
	 * throwing exceptions allows the display to keep functioning even if
    one of the servers goes down (or the interface changes and everything fails horribly) */
	public void setError(String message, Throwable th) {
		log.log(Level.SEVERE, message,th);
		lastError = th;
	}

	protected void clearError() {
		lastError = null;
	}

	public Throwable getError() {
		return lastError;
	}


}
