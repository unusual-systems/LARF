/*
 * <cvs:source>$Source: /cvsroot/larf/larf/src/org/larf/mime/MimeNames.java,v $</cvs:source>
 * <cvs:author>$Author: hillmc $</cvs:author>
 * <cvs:date>$Date: 2006/01/10 17:41:13 $</cvs:date>
 * <cvs:version>$Revision: 1.1 $</cvs:version>
 *
 */


package org.larf.mime ;

/**
 * Converters between 'human' names and official mime types
 *
 */
import java.util.Hashtable;

public class MimeNames implements MimeTypes  {

   private static Hashtable humanLookup= null;
   private static Hashtable mimeLookup= null;
   

   private static void addLookup(String mime, String human) {
      humanLookup.put(mime.trim().toLowerCase(), human);
      mimeLookup.put(human.trim().toLowerCase(), mime);
   }

   public synchronized static void initialise() {

      if (humanLookup != null) { return; } //must have initialised on another thread
      
      humanLookup = new Hashtable();
      mimeLookup = new Hashtable();
      
      //populate lookup tables
      addLookup(PLAINTEXT, "Text");
      addLookup(VOTABLE,   "VOTable");
      addLookup(HTML,      "HTML");
      addLookup(CSV,       "Comma-Separated");
      addLookup(TSV,       "Tab-Separated");
      addLookup(FITS,      "FITS");
   }
      
   /**
    * Guess the mime type from a given 'human' string (eg 'VOTable')
    * @return The mime type if the filename has a recognised .extension, otherwise null.
    *
    */
   public static String getMimeType(String humanString) {

      if (humanLookup == null) {
         initialise();
      }
      
      if (null == humanString) {
         return null;
      }
      
      String mime = (String) mimeLookup.get(humanString.toLowerCase());
      
      //if mime is null, it might be because the humanstring is actually a mime
      //type, so return that
      if (mime == null) {
         return humanString;
      }
      return mime;
      
   }
   
   /**
    * Get a human-friendly string corresponding to the given mime type
    *
    */
   public static String humanFriendly(String mimeType) {

      if (humanLookup == null) {
         initialise();
      }
      
      if (null == mimeType) {
         return null;
      }
      
      String friendly = (String) humanLookup.get(mimeType.toLowerCase());

      if (friendly == null) {
         //give up finding a nice one and just return the mime type...
         friendly = mimeType;
      }
      
      return friendly;
   }
}
/*
 *   $Log: MimeNames.java,v $
 *   Revision 1.1  2006/01/10 17:41:13  hillmc
 *   Initial commit to keep it backed up.  Currently won't even compile.
 *
 *   Revision 1.2  2005/05/27 16:21:02  clq2
 *   mchv_1
 *
 *   Revision 1.1.2.1  2005/04/21 17:09:03  mch
 *   incorporated homespace etc into URLs
 *
 *   Revision 1.1  2005/02/14 20:47:38  mch
 *   Split into API and webapp
 *
 *   Revision 1.3  2005/01/26 17:31:56  mch
 *   Split slinger out to scapi, swib, etc.
 *
 *   Revision 1.1.2.3  2004/12/08 18:37:11  mch
 *   Introduced SPI and SPL
 *
 *   Revision 1.1.2.2  2004/11/25 18:28:21  mch
 *   More tarting up
 *
 *   Revision 1.1.2.1  2004/11/25 07:18:13  mch
 *   added mime names
 *
 */
