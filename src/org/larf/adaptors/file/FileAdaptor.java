/*
 * $Id: LocalFile.java,v 1.1.1.1 2005-02-16 15:02:46 mch Exp $
 *
 * (C) Copyright Astrogrid...
 */

package org.larf.adaptors.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.security.Principal;
import java.util.Date;

import org.larf.FileNode;
import org.larf.mime.MimeFileExts;


/**
 * Represents a local file (ie a File class) as a StoreFile
 * <p>
 * Note that because this uses the File class to navigate around,
 * it is not necessarily true that child[0].getParent() == child[1].getParent()
 *
 * @author M Hill
 */


public class FileAdaptor implements FileNode {
   
   File file = null;
   
   public FileAdaptor(File aFile) {
      assert aFile != null : "Must set file";
      
      this.file = aFile;
   }
   
   /** Creates a file from a file:// url */
   public FileAdaptor(URI uri, Principal user) throws MalformedURLException {
	   URL url = new URL(uri.toString());
      this.file = new File(url.getHost()+"/"+url.getPath());
   }
      
      /** Lists children files if this is a container - returns null otherwise */
   public FileNode[] listFiles() {
      if (!file.isDirectory()) {
         return null;
      }

      File[] localFiles = file.listFiles();
      if (localFiles == null) {
         //empty directory
         return new FileNode[] {};
      }
      FileNode[] storeFiles = new FileNode[localFiles.length];
      for (int i=0;i<localFiles.length;i++) {
         storeFiles[i] = new FileAdaptor(localFiles[i]);
      }
      return storeFiles;
   }
   
   /** Returns parent folder of this file/folder */
   public FileNode getParent() {
      //check to see if we're at the root
      String serverPath = getPath();
      if (serverPath == null) return null;
      if (file.getParentFile() == null) return null;
      
      return new FileAdaptor(file.getParentFile());//but specify full File
   }
   
   /** Returns true if this is a self-contained file.  For example, a database
    * table might be represented as a StoreFile but it is not a file */
   public boolean isFile() {
      return file.isFile();
   }
   
   /** Returns the filename/foldername/tablename/etc */
   public String getName() {
      return file.getName();
   }
   
   /** Returns the path to this file on the server */
   public String getPath() {
      return file.getAbsolutePath();
   }

   /** Returns the owner of the file */
   public String getOwner() {
      return null;
   }
   
   /** Returns the mime type (null if unknown) */
   public String getMimeType() {
      return MimeFileExts.guessMimeType(file.getName());
   }
   
   /** Sets the mime type - this should rename the file with the suitable extension */
   public void setMimeType(String mimeType) throws IOException {
      renameTo(file.getName()+"."+MimeFileExts.guessExt(mimeType));
   }
   
   /** Changes the filename to the given one - give just the file name (no path),
    * does not move */
   public void renameTo(String newFilename) throws IOException {
      boolean success = file.renameTo(new File(newFilename));
      if (!success) {
         throw new IOException("Operating system refused to rename '"+file.getName()+"' to '"+newFilename+"' (don't know why)");
      }
   }
   
   /** Returns the date the file was last modified (null if unknown) */
   public Date getModified() {
      return new Date(file.lastModified());
   }
   
   /** Returns the creation date  (null if unknown) */
   public Date getCreated() {
      return null;
   }
   
   /** Returns the size of the file in bytes (-1 if unknown) */
   public long getSize() {
      return file.length();
   }
   
   
   /** Returns true if this is a container that can hold other files/folders */
   public boolean isFolder() {
      return file.isDirectory();
   }
   
   /** Returns true if it exists - eg it may be a reference to a file about to be
    * created */
   public boolean exists() {
      return file.exists();
   }
   

   /** Returns true if this represents the same file as the given one */
   public boolean equals(FileNode anotherFile) {
      if (anotherFile instanceof FileAdaptor) {
         return file.equals( ((FileAdaptor) anotherFile).file);
      }
      return false;
   }
   
   /** Returns user representation - server path */
   public String toString() {
      return "LocalFile "+getPath()+" (size="+getSize()+", owner="+getOwner()+", changed="+getModified()+")";
   }
   
   /** Returns a file:// url for this file */
   public String getUri() {
      return file.toURI().toString();
   }
   
   /** All targets must be able to resolve to a stream.  The user is required
    * for permissioning. */
   public OutputStream openOutputStream(String mimeType, boolean append) throws IOException {
      return new FileOutputStream(file, append);
   }
   
   /** All targets must be able to resolve to a stream.  The user is required
    * for permissioning. */
   public InputStream openInputStream() throws IOException {
      return new FileInputStream(file);
   }
   
   /** Returns true if the resolved stream/reader should be closed when the
    * indicator's user has finished with it.
    */
   public boolean closeIt() {
      return true;
   }
   
   /** All targets must be able to resolve to a reader. The user is required
    * for permissions */
   public Reader resolveReader() throws IOException {
      return new FileReader(file);
   }
   
   /** All targets must be able to resolve to a writer. The user is required
    * for permissions */
   public Writer resolveWriter() throws IOException {
      return new FileWriter(file);
   }
   
   /** Deletes this file */
   public void delete() throws IOException {
      boolean success = file.delete();
      if (!success) {
         throw new IOException("O/S failed to delete file "+file+" (don't know why)");
      }
   }
   
   /** Refreshes from the server; generally just clears any caches so forcing a
    * reload next time the properties are accessed */
   public void refresh() {
      // Does nothing as local files work off the disk anyway
   }
   
   /** If this is a folder, creates an output stream to a child file */
   public OutputStream outputChild(String childFilename, String mimeType) throws IOException {
      return new FileOutputStream(new File(file, childFilename));
   }
   
   /** IF this is a folder, creats a subfolder */
   public FileNode makeFolder(String newFolderName) throws IOException {
      if (isFolder()) {
         File newFolder = new File(file, newFolderName);
         newFolder.mkdir();
         return new FileAdaptor(newFolder);
      }
      throw new IllegalArgumentException(this+" is not a folder; cannot make subfolder "+newFolderName);
   }

	public FileNode getChild(String name) throws IOException {
		return new FileAdaptor(new File(file, name));
	}
   
   /** not actually useful - gets in the way of thinking about abstract folders and stores

	public StoreFile[] getRoots() throws IOException {
		File[] roots = File.listRoots();
		StoreFile[] storeRoots = new StoreFile[roots.length];
		for (int i = 0; i<roots.length; i++) {
			storeRoots[i] = new FileAdaptor(roots[i]);
		}
		return storeRoots;
	}
    */   
}


