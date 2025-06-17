/**
 * FileSorter.java
 *
 * @author Created by Omnicore CodeGuide
 */

package org.larf.browser;

import java.util.Arrays;

import org.larf.FileNode;

public class FileSorter
{
   //sort options
   boolean foldersOnly = false;
   boolean foldersFirst = true;
   
   public FileSorter(boolean showfoldersOnly, boolean sortFoldersFirst) {
      this.foldersOnly = showfoldersOnly;
      this.foldersFirst = sortFoldersFirst;
   }
   
   public FileNode[] sort(FileNode[] files) {
      
      if (files == null) {
         return null;
      }
      
      //create wrapping comparers and check for folder-only views
      FileComparer[] comparers = new FileComparer[files.length];
      int n=-1;
      for (int f = 0; f < files.length; f++) {
         if ((!foldersOnly) || (files[f].isFolder())) {
            n++;
            comparers[n] = new FileComparer(files[f]);
         }
      }
      if (n==-1) {
         return new FileNode[] {}; //nothing found, return empty array
      }
      //sort
      Arrays.sort(comparers,0,comparers.length);

      //create filenodes
      FileNode[] sorted = new FileNode[comparers.length];
      for (int i = 0; i < comparers.length; i++) {
          sorted[i] = comparers[i].file;
      }
      return sorted;
      
   }

   private class FileComparer implements Comparable {

      String sortKey = null;
      FileNode file = null;
      
      public FileComparer(FileNode fileToSort) {
         this.file = fileToSort;
         if (file.isFolder()) {
            sortKey = "\r"+file.getName().toLowerCase();  //folders before files
         }
         else {
            sortKey = file.getName().toLowerCase();
         }
      }
         
      
         /** Returns < 0 if this file should be placed before the given file in
       * directory lists, 0 if they are equal and > 0 if it should be placed after */
      public int compareTo(Object comparer) {
         return sortKey.compareTo( ((FileComparer) comparer).sortKey);
      }
   }
   
}

