/**
 * $Id: FileChangedListener.java,v 1.1 2005-04-04 01:10:15 mch Exp $
 *
 */

package org.larf.browser.actions;

import java.util.Vector;

public interface FileChangedListener
{
   /** Called when a file has been changed somewhere */
   public void fileChanged(FileChangedEvent event);
   
   
}

