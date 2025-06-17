/**
 * PleaseWait.java
 *
 * @author Created by Omnicore CodeGuide
 */

package org.larf.browser;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

/** Simple box for showing 'please wait'.  Use by doing:
 * <code>
 *    PleaseWait waitBox = nwe PleaseWait(this);
 *    ..some timeconsuming thing...
 *   wait.hide();
 * </code>
 * Where things are likely to take a long time, use threads - and
 */
public class PleaseWait extends JDialog
{
   public PleaseWait(Dialog parent, String message) {
      super(parent, "Please Wait...", false); //not modal
      setTitle("Please Wait...");
      init(message);
   }

   public PleaseWait(Frame parent, String message) {
      super(parent, "Please Wait...", false); //not modal
      setTitle("Please Wait...");
      init(message);
   }
   
   public PleaseWait(String message) {
      super();
      setTitle("Please Wait...");
      init(message);
   }

   /** Add components and show */
   public void init(String message) {
      JPanel msgPanel = new JPanel(new BorderLayout());
      msgPanel.add(new JLabel(message), BorderLayout.CENTER);
      this.getContentPane().add(msgPanel);
      pack();
      invalidate();
      show();
   }
   
}

