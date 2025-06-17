/**
 * PipeProgressMonitor.java
 *
 * @author Created by Omnicore CodeGuide
 */

package org.larf.browser;

import java.awt.Component;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;

import org.slinger.piper.DefaultPipeListener;

/** Displays progress bar.  Ideally shoudl subclass ProgressMOnitorInputStream
 * but it never seems to produce a progress box... */

public class PipeProgressMonitor extends DefaultPipeListener {
   
   ProgressMonitor monitor;
   Component parent;
   InputStream in;
   String completed;
   
   /** Pass in component to display in front of size of stream, -1 if not known */
   public PipeProgressMonitor(InputStream givenIn, Component givenParent, String aName, String downloadingMessage, String completedMessage, int streamSize) {
      super(aName, streamSize);
      in = givenIn;
      completed = completedMessage;
      parent = givenParent;
      monitor = new ProgressMonitor(parent, downloadingMessage, null, 0,(int) size);
      monitor.setMillisToDecideToPopup(0);
      monitor.setMillisToPopup(0);
   }
   
   /** Called when a block has been piped */
   public void piped(long bytesRead) {
      System.out.print(".");
      monitor.setProgress( (int) bytesRead);
      if (monitor.isCanceled()) {
         try {
            in.close();
         } catch (IOException e) { /* ignore */}
      }
   }
   
   /** Called when pipe is complete */
   public void pipeComplete() {
      monitor.close();
      //unnecessary clicking JOptionPane.showConfirmDialog(parent, completed, "Piping Complete", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
      
      //update displays
   }
   
   /** Called when any other Exception occurs */
   public void thrown(Throwable th) {
	   Logger.getLogger(name).log(Level.SEVERE, "Piping "+name, th);
	   JOptionPane.showConfirmDialog(parent, th, "Piping Error", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
   }
   

   
}

