/**
 * $Id: ContentLoader.java,v 1.2 2005-04-04 01:10:15 mch Exp $
 *
 */

package org.larf.browser.textview;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.larf.FileNode;
import org.slinger.piper.StreamPiper;


/**
 * Runnable that loads the contents from the remote file, then calls SwingUtilities
 * with the given ContentSetter Runnable to do what it has to do to update the display */

public class ContentLoader implements Runnable {
   
	static Logger log = Logger.getLogger(ContentLoader.class.toString());
   
   boolean aborted = false;
   ContentAppenderStream setter;
   FileNode source;
   StreamPiper piper;
   
   public ContentLoader(FileNode file, ContentAppenderStream aSetter) {
      this.setter = aSetter;
      this.source = file;
   }
   
   /** Unfortunately we can't just use spawnPipe as the process of opening the
    * inputstream can take some time, and that really needs to be part of the
    * thread so it doesn't interfere with the UI */
   public void run() {
      try {
         log.fine("Reading contents of "+source.getPath()+"...");

         InputStream in = source.openInputStream();
         piper.pipe(new BufferedInputStream(in), setter, null);
         in.close();
      }
      catch (Throwable e) {
         log.log(Level.SEVERE, e+" loading contents of "+this, e);
         setter.setError(e);
      }
   }

   /** Call to abort the update */
   public void abort() {
      aborted = true;
      log.fine("Aborting load for "+source);
      if (piper != null) {
         piper.abort();
      }
   }
   
}

