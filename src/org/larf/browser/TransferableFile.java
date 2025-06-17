/**
 * TransferableFile.java
 *
 * @author Created by Omnicore CodeGuide
 */

package org.larf.browser;


import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.net.URI;

import org.larf.FileNode;

/**
 * Used for transferring files between Swing components for cut/paste and drag/drop etc
 */

public class TransferableFile implements Transferable
{

   public static final DataFlavor URI_FLAVOUR =    new DataFlavor(URI.class, "URI");
   //public static final DataFlavor VIEW_FLAVOUR =   new DataFlavor(FileViewNode.class, "ViewNode");
   public static final DataFlavor FILE_FLAVOUR =   new DataFlavor(FileNode.class, "FileNode");

   FileNode file = null;
   
   public TransferableFile(FileNode transferFile) {
      this.file = transferFile;
   }
   
   /**
    * For debug
    */
   public String toString() {
      return "TransferableFile: "+file;
   }
   
   /**
    * Returns an array of DataFlavor objects indicating the flavors the data
    * can be provided in.  The array should be ordered according to preference
    * for providing the data (from most richly descriptive to least descriptive).
    * @return an array of data flavors in which this data can be transferred
    */
   public DataFlavor[] getTransferDataFlavors() {
      return new DataFlavor[] {
         URI_FLAVOUR,
         //VIEW_FLAVOUR,
         FILE_FLAVOUR
      };
   }
   
   /**
    * Returns whether or not the specified data flavor is supported for
    * this object.
    * @param flavor the requested flavor for the data
    * @return boolean indicating whether or not the data flavor is supported
    */
   public boolean isDataFlavorSupported(DataFlavor flavor) {
      return (flavor.equals(URI_FLAVOUR) /*|| flavor.equals(VIEW_FLAVOUR) */|| flavor.equals(FILE_FLAVOUR));
   }
   
   /**
    * Returns an object which represents the data to be transferred.  The class
    * of the object returned is defined by the representation class of the flavor.
    *
    * @param flavor the requested flavor for the data
    * @see DataFlavor#getRepresentationClass
    * @exception IOException                if the data is no longer available
    *              in the requested flavor.
    * @exception UnsupportedFlavorException if the requested data flavor is
    *              not supported.
    */
   public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
      if (flavor.equals(URI_FLAVOUR)) {
         return file.getUri();
      }
      /*
      else if (flavor.equals(VIEW_FLAVOUR)) {
         return node;
      }
       */
      else if (flavor.equals(FILE_FLAVOUR)) {
         return file;
      }
      else {
         throw new UnsupportedFlavorException(flavor);
      }
   }
}

