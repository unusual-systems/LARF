/*
 $Id: EscEnterListener.java,v 1.4 2005-03-29 16:26:45 mch Exp $
 */

package org.larf.browser;

import java.awt.Window;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.*;
import javax.swing.JButton;

/**
 * This is an adapter for catching esc & enter keypresses in, eg, a
 * dialog, so that it calls the default button or cancel button.
 * Further catches can be made by subclassing. Also ties windows
 * close button to cancel button.  Should be created and registered
 * after the dialog and its child components have been created and
 * assembled, so that it can add itself ot all the subcomponents.
 * <P>
 * To use, add the following line <B>after</B> the components have been assembled:
 * <pre>
 *    new EscEnterListener(dialog box, default button, cancel button)
 * </pre>
 * It works by registering itself as a keypress listener for all the child
 * components of the dialog box, trapping all [Esc] or [Enter] keypresses.
 * <p>
 * Scope for improvement: If
 * changes are made to the componen tree after constructing the instance as
 * above, keypresses in the new components will not be trapped.  This class
 * could also register itself as a ComponentListener to track such changes if
 * necessary.  At the moment the method RegisterThis(root container) can be
 * used 'manually' to register it with new components.
 *
 * @author M Hill
 */
/* Change history:
    11/2/2002 - MCH - fix for passing in null default button (don't set font)
    15/2/2002 - MCH - added disposeOnCancel which disposes of window if cancel pressed
 */

public class EscEnterListener extends KeyAdapter
{
   JButton defaultBtn = null;
   JButton cancelBtn = null;
   Window window = null;

   /** Standard constructor - close presses the cancel button but the window itself
    * must listen out for the button events
    */
   public EscEnterListener(Window aWindow, JButton aDefault, JButton aCancel)
   {
      this(aWindow, aDefault, aCancel, false);
   }

   /**
    * Constructor providing a disposeOnCancel argument which will close &
    * dispose the display if cancel (or, therefore, the close icon) is
    * pressed
    */
   public EscEnterListener(Window aWindow, JButton aDefault, JButton aCancel, boolean disposeOnCancel)
   {
      window = aWindow;
      setDefaultBtn(aDefault);
      setCancelBtn(aCancel);
      registerWith(window);

      // Add standard window close listener - this presses the cancel button
      // but nothing else (in case cancel fails).
      window.addWindowListener(
            new WindowAdapter()
            {
               public void windowClosing(WindowEvent e)
               {
                  if (cancelBtn == null)
                  {
                     //no cancel button, so do standard
                     window.setVisible(false);
                  }
                  else if (cancelBtn.isEnabled())
                  {
                     cancelBtn.doClick();
                  }
               }
            }
         );

      if ((disposeOnCancel) && (cancelBtn != null))
      {
         //add standard dispose-window to cancel button
         cancelBtn.addActionListener(
            new ActionListener()
            {
               public void actionPerformed(ActionEvent e)
               {
                  window.hide();
               }
            }
         );
      }
   }

   /** Sets the button that will close the dialog box when [Esc] is pressed
    */
   public void setCancelBtn(JButton b)
   {
      cancelBtn = b;
   }

   /** Sets the default button. Also sets the button's font to bold
    * to show which one is default.  This button will be activated
    * by the [Enter] key. */
   public void setDefaultBtn(JButton b)
   {
      defaultBtn = b;
      if (defaultBtn != null)
         defaultBtn.setFont(defaultBtn.getFont().deriveFont(Font.BOLD));
   }

   /**
    * Registers this listener with all the components in the tree given
    * by comp.  Call this with the root dialog frame and all components
    * will be recursively called down the container tree.  It does not
    * (yet :-) handle later changes to component trees - a subclass could
    * register as a container listener too.. */
   public void registerWith(Component comp)
   {
      //first remove so we don/t get any duplications
      comp.removeKeyListener(this);

      //go through all component's children, and add key listener
      comp.addKeyListener(this);
      if (comp instanceof Container)
      {
         Component[] children = ((Container) comp).getComponents();
         for (int i=0;i<children.length;i++)
         {
            registerWith(children[i]);
         }
      }
   }

   /** Check for ESC or ENTER being pressed.  If so, and the relevent
    * button is enabled, click it */
   public void keyPressed(KeyEvent ke)
   {
      if ((ke.getKeyCode() == ke.VK_ENTER) && (defaultBtn != null) && (defaultBtn.isEnabled()))
      {
         defaultBtn.doClick();
      }
      if ((ke.getKeyCode() == ke.VK_ESCAPE) && (cancelBtn != null) && (cancelBtn.isEnabled()))
      {
         cancelBtn.doClick();
      }
      if ((ke.getKeyCode() == ke.VK_ESCAPE) && (cancelBtn == null))
      {
         window.hide();
      }
   }

}

/**
 $Log: EscEnterListener.java,v $
 Revision 1.4  2005-03-29 16:26:45  mch
 Fix in case of null cancel button

 Revision 1.3  2004/04/15 16:34:53  mch
 Tidied up, introduced stuff from datacenter ui

 Revision 1.1  2004/03/03 17:40:58  mch
 Moved ui package

 Revision 1.1  2004/02/17 16:04:06  mch
 New Desktop GUI

 Revision 1.1.1.1  2003/08/25 18:36:32  mch
 Reimported to fit It02 source structure

 Revision 1.3  2003/07/02 19:15:14  mch
 Removed windows/linux double spacing

 */

