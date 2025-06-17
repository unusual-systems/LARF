/**
 * MimeIcons.java
 *
 * @author Created by Omnicore CodeGuide
 */

package org.larf.browser;

import javax.swing.Icon;

import org.larf.mime.MimeTypes;

/** Works out which icon to show for the given file type */

public class MimeIcons
{
   public static Icon getIcon(String mimeType) {
      if (mimeType == null) {
         return null;
      }
      if (mimeType.equals(MimeTypes.ADQL)) {
         return IconFactory.getIcon("FileType_XML", IconFactory.SMALL);
      }
      if (mimeType.equals(MimeTypes.WORKFLOW)) {
         return IconFactory.getIcon("FileType_XML", IconFactory.SMALL);
      }
      if (mimeType.equals(MimeTypes.HTML)) {
         return IconFactory.getIcon("FileType_HTML", IconFactory.SMALL);
      }
      if (mimeType.equals(MimeTypes.XML)) {
         return IconFactory.getIcon("FileType_XML", IconFactory.SMALL);
      }
      if (mimeType.equals(MimeTypes.ZIP)) {
         return IconFactory.getIcon("FileType_ZIP", IconFactory.SMALL);
      }
      if (mimeType.startsWith(MimeTypes.IMAGE)) {
         return IconFactory.getIcon("FileType_Image", IconFactory.SMALL);
      }
      
      return null;
   }
   
}

