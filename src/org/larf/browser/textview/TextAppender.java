/**
 * $Id: TextAppender.java,v 1.1 2005-04-04 01:10:15 mch Exp $
 *
 */

package org.larf.browser.textview;

import java.awt.Color;
import java.io.IOException;

import org.slinger.piper.ByteToCharConverter;


/**
 * An output stream that the contents are loaded into.  As each block arrives,
 * it appends it to the text area.  The append() method of text area is threadsafe
 * so we can do this*/

public class TextAppender extends ContentAppenderStream {
   
   TextContentsView textView = null;
   long totalBytes = 0;
   
   ByteToCharConverter converter = new ByteToCharConverter();
   
   public TextAppender(TextContentsView target) {
      this.textView = target;
   }
   
   
   /**
    * Writes the specified byte to the text area
    */
   public void write(int b) throws IOException {
      textView.getTextArea().append(String.valueOf( (char) b));
      totalBytes++;
      textView.getStatusBar().setText("Loaded "+totalBytes);
   }
   
   /**
    * Writes the specified block to the text area
    */
   public void write(byte[] block) throws IOException {
      textView.getTextArea().append(String.valueOf( converter.convertAll(block)));
      totalBytes = totalBytes+block.length;
      textView.getStatusBar().setText("Loaded "+totalBytes);
   }

   /**
    * Writes the specified block to the text area
    */
   public void write(byte[] block, int offset, int count) throws IOException {
      textView.getTextArea().append(String.valueOf( converter.convertAll(block), offset, count));
      totalBytes = totalBytes+count;
      textView.getStatusBar().setText("Loaded "+totalBytes);
   }

   /**
    * Error - append that too
    */
   public void setError(Throwable th) {
      textView.getStatusBar().setForeground(Color.RED);
      textView.getStatusBar().setText(""+th);
   }
      
      
  }

