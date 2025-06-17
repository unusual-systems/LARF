/**
 * FileChangedHub.java
 *
 * @author Created by Omnicore CodeGuide
 */

package org.larf.browser.actions;

import java.util.Enumeration;
import java.util.Vector;

/** Hub for filechangedevents - all actions notify this singleton
 * and so components can register with that to be notified rather than all
 * action instances */

public class FileChangedHub
{
   
   private static Vector listeners = new Vector();

   public static void addFileChangedListener(FileChangedListener newListener) {
      listeners.add(newListener);
   }

   public static void removeFileChangedListener(FileChangedListener delListener) {
      listeners.remove(delListener);
   }
   
   public static void fireFileChanged(FileChangedEvent event) {
      Enumeration e = listeners.elements();
      while (e.hasMoreElements()) {
         ((FileChangedListener) e.nextElement()).fileChanged(event);
      }
   }
}

