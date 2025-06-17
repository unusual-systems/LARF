/**
 * FileChangingAction.java
 *
 * @author Created by Omnicore CodeGuide
 */

package org.larf.browser.actions;

import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.AbstractAction;

/** superclass for the actions that will change files. Components that are
 * interested in such actions can register with it so they can be notified of
 * such changes
 */

public abstract class FileChangingAction extends AbstractAction
{
   protected static Logger log = Logger.getLogger(AbstractAction.class.toString());
   
   private Vector listeners = new Vector();
   
   public void addFileChangedListener(FileChangedListener newListener) {
      listeners.add(newListener);
   }

   public void removeFileChangedListener(FileChangedListener delListener) {
      listeners.remove(delListener);
   }
   
   public void fireFileChanged(FileChangedEvent event) {

      //always tell the hub
      FileChangedHub.fireFileChanged(event);
      
      //tell other listeners
      Enumeration e = listeners.elements();
      while (e.hasMoreElements()) {
         ((FileChangedListener) e).fileChanged(event);
      }
   }

}

