/*
 * $Id: TextContentsView.java,v 1.2 2005-04-04 01:10:15 mch Exp $
 */

package org.larf.browser.textview;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.larf.FileNode;

public class TextContentsView extends JPanel implements ContentViewer
{
   FileNode file = null;
   ContentLoader loader = null;
   
   JTextArea textArea = new JTextArea();
   JLabel statusBar = new JLabel();
   JScrollPane scroller = new JScrollPane();
   
   public TextContentsView(FileNode givenFile) {

      scroller.getViewport().setView(textArea);
      setLayout(new BorderLayout());
      add(scroller, BorderLayout.CENTER);
      add(statusBar, BorderLayout.SOUTH);
      
      statusBar.setText("Please Wait, connecting..");
      file = givenFile;
      startLoadContents();
   }

  /** Spawns a thread to pipe via a ByteArrayOUtputStream to this view.
    * Unfortunately we can't just use spawnPipe as the process of opening the
    * inputstream can take some time, and that really needs to be part of the
    * thread so it doesn't interfere with the UI
    */
   public synchronized void startLoadContents() {

      loader = new ContentLoader(file, new TextAppender(this));
      Thread loadingThread = new Thread(loader);
      loadingThread.start();
   }

   public void abortLoad() {
      loader.abort();
   }

   public JTextArea getTextArea() {
      return textArea;
   }
   
   public JLabel getStatusBar() {
      return statusBar;
   }
   
}

