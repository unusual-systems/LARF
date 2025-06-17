/*
 * $Id: StoreFile.java,v 1.1.1.1 2005-02-16 15:02:46 mch Exp $
 *
 * Copyright 2003 AstroGrid. All rights reserved.
 *
 * This software is published under the terms of the AstroGrid Software License,
 * a copy of which has been included with this distribution in the LICENSE.txt file.
 */

package org.larf;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Principal;
import java.util.Date;
import java.util.Iterator;


/**
 * Used to represent a file in 'storespace'.  It is supposed to look rather
 * like 'java.io.File' rather than the container/component design pattern.
 *
 * Notay very very benay - do not assume that child[0].getParent() == child[1].getParent().
 * Use equals instead.
 * 
 * Permissions are based around the user given to the StoreFileResolver.
 * 
 * Implementation constructors take a URI defining its location and a Principal defining the user (see
 * StoreFileResolver)
 * 
 * @author mch
 */


public interface FileNode  {

   /** Returns the file/folder/table name without path */
   public String getName();
   
   /** Returns parent folder of this file/folder, if permission granted */
   public FileNode getParent() throws IOException;

   /** Refreshes from the server; clears children and reloads properties for
    * this file */
   public void refresh() throws IOException;
   
   /** Returns the owner of the file */
   public String getOwner();

   /** Returns the creation date  (null if unknown) */
   public Date getCreated();
   
   /** Returns the date the file was last modified (null if unknown) */
   public Date getModified();
   
   /** Returns the size of the file in bytes (-1 if unknown) */
   public long getSize();

   /** Returns the mime type (null if unknown) */
   public String getMimeType();
   
   /** Set the mime type on the disk */
   public void setMimeType(String newMimeType) throws IOException;
   
   /** Returns an appropriate RL (eg url, msrl) for this file */
   public String getUri();
   
   /** Returns true if this is a container that can hold other files/folders */
   public boolean isFolder();
   
   /** Returns true if this is a self-contained file.  For example, a database
    * table might be represented as a StoreFile but it is not a file */
   public boolean isFile();
   
   /** Returns true if it exists - eg it may be a reference to a file about to be
    * created */
   public boolean exists();
   
   /** Lists children files if this is a container - returns null otherwise */
   public FileNode[] listFiles() throws IOException;
   
   /** Returns iterator to children - more useful for remote/slow access than listFiles */
//not yet - pain to implement   public Iterator <StoreFile> childs() throws IOException;
   
   /** Returns the path to this file on the server, including the filename */
   public String getPath();
   
   /** Returns true if this represents the same file as the given one, within
    * this server.  This
    * won't check for references from different stores to the same file */
   public boolean equals(FileNode anotherFile);
   
   /** Renames the file to the given filename. Affects only the name, not the
    * path */
   public void renameTo(String newFilename) throws IOException;
   
   /** Deletes this file */
   public void delete() throws IOException;

   /** Returns a child of this folder with the given name. Doesn't have to exist */
   public FileNode getChild(String name) throws IOException; 
   
   /** returns the root(s) of this file system. abandoned for now as it means assuming things about 
    * servers and locations and may not really be relevent
   public StoreFile[] getRoots() throws IOException;
   /**/
   
   /** IF this is a folder, creats a subfolder */
   public FileNode makeFolder(String newFolderName) throws IOException;
   
   /** If this is a folder, creates an output stream to a child file */
   public OutputStream outputChild(String filename, String mimeType) throws IOException;

   /** If this is not a folder, creates a stream that outputs to it */
   public OutputStream openOutputStream(String mimeType, boolean append) throws IOException;

   /** If this is not a folder, creates a stream that reads from it */
   public InputStream openInputStream() throws IOException;
}

/*

 */

