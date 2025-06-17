/**
 * ContentViewer.java
 *
 * @author Created by Omnicore CodeGuide
 */

package org.larf.browser.textview;

/** A content viewer is a component that may display the contents of a file
 * on a seperate thread.  This means that it must have a way of stopping the load,
 * say when a different file is selected.   This could be done with a listener
 * to the component heirarchy, but this would have to be implemented a lot.
 * Easier to add a single method that passes the abortLoad message onto
 * the loading thread
 */

public interface ContentViewer
{
   public void abortLoad();
}

