/*
 $Id: SlingerFtpClient.java,v 1.1.1.1 2005-02-16 15:02:46 mch Exp $

 (c) Copyright...
 */

package org.larf.adaptors.ftp;

import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.security.Principal;
import java.util.logging.Logger;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.parser.ParserInitializationException;
import org.slinger.account.LoginAccount;

/**
 * An FTP client that wraps/extends whatever implementation we settle on using.
 * Separate from FtpFile so that I can mess about with connections/disconnections
 * more easily than using the file tree.
 *
 * This implementation uses the apache commons net clients.  As far as I can tell,
 * this is *not* threadsafe, as there are commands that get partially executed, and
 * need tidying up when complete..
 * 
 * So we should have one client per thread, with some lookup so we can tell which one to use... and some decent fail mechanism
 * if too many connections are established.
 *
 * @author MCH
 */
public class JakartaFtpClient extends FTPClient {
   

   protected  static Logger log = Logger.getLogger(JakartaFtpClient.class.toString());
   
   /** New client connected to given host with given login account */
   public JakartaFtpClient(URL url, Principal ftpUser) throws IOException {
      super();
      if (url.getPort() == -1) {
         connect(url.getHost());
      } else {
         connect(url.getHost(), url.getPort());
      }
      
      if (!(ftpUser instanceof LoginAccount)) {
    	  throw new IllegalArgumentException("Principal/User '"+ftpUser.getClass()+" must extend LoginAccount");
      }
      
      login(ftpUser.getName(), ((LoginAccount) ftpUser).getPassword());
   }
   
   /** Close connection (& log off) */
   public void close() throws IOException {
      super.logout();
      super.disconnect();
   }
   
   /** Returns true if the given URL is an FTP */
   public static boolean isUrlFtp(URL url) {
      return url.getProtocol().equals("ftp");
   }

   /** Returns the URL for this server */
   public String getUrl() {
      return "ftp://"+getRemoteAddress().getHostName()+":"+getRemotePort();
   }
   
   /** Returns the FTPFile apache.net representation of the single file/folder
    * at the given path. Returns null if the file doesn't exist */
   //@todo - this sometimes returns the first file in the directory rather than the directory
   public FTPFile getFile(String path) throws IOException {
      //get rid of final '/' so that listFiles only returns one match for directories, not the list of children
      if (!path.equals("/") && path.endsWith("/")) { path = path.substring(0, path.length()-1);}
      try {
    	  FTPFile[] files = listFiles(path);
          if ((files == null) || (files.length==0)) {
              return null; //throw new FileNotFoundException(path+" not found on "+getUrl());
           }
          if ((files.length>1)) {
              throw new IOException("Too many files match path '"+path+"' on "+getUrl());
           }
           return files[0];
      }
      catch (ParserInitializationException pie) {
    	  //special case, trap this error and find out what the problem is
    	  IOException ioe = new IOException(pie.getRootCause()+" initialising FTP parser - might be a login problem");
    	  ioe.initCause(pie.getRootCause());
    	  pie.getRootCause().printStackTrace();
    	  throw ioe;
      }
   }
   
   public boolean removeFolder(String folderPath) throws IOException {
      return removeDirectory(folderPath);
   }

   /** For backwards compatibility */
   public boolean createFolder(String folderPath) throws IOException {
      return makeDirectory(folderPath);
   }
   
   public String toString() {
      return "Slinger FTP Client to "+getUrl();
   }
   
   /** Override to provide special closing streams */
   public InputStream  retrieveFileStream(String path) throws IOException {
      InputStream in = new SlingerFtpInputStream(super.retrieveFileStream(path));
      if (!FTPReply.isPositiveIntermediate(getReplyCode())) {
         throw new IOException("Failed to open inputstream to "+getUrl()+"#"+path);
      }
      return in;
   }

      /** Override to provide special closing streams */
   public OutputStream  storeFileStream(String path) throws IOException {
      OutputStream out = new SlingerFtpOutputStream(super.storeFileStream(path));
      int status = getReplyCode();
      if (!FTPReply.isPositiveIntermediate(status) && (status != 150)) {
         throw new IOException("Failed to open outputtstream to "+getUrl()+"#"+path+" (Status="+status+")");
      }
      return out;
   }

   /** InputStream that does the right ftp completion stuff when closed */
   public class SlingerFtpInputStream extends FilterInputStream {
      public SlingerFtpInputStream(InputStream in) {
         super(in);
      }
      public void close() throws IOException {
         in.close();
         completePendingCommand();
      }
   }
   public class SlingerFtpOutputStream extends FilterOutputStream {
      public SlingerFtpOutputStream(OutputStream out) {
         super(out);
      }
      public void close() throws IOException {
         out.close();
         completePendingCommand();
      }
   }
   
   
   /** test stuff */
   public static void main(String[] args) throws IOException {
      String roeFtp = "ftp://ftp.roe.ac.uk";
      JakartaFtpClient client = new JakartaFtpClient(new URL("ftp://ftp.roe.ac.uk"), LoginAccount.ANONYMOUS);

      /*
      URLConnection conn = new URL("ftp://ftp.at.debian.org/debian/").openConnection();
      InputStream ftpIn = conn.getInputStream();
      StringWriter sw = new StringWriter();
      Piper.pipe(new InputStreamReader(ftpIn), sw);
      String s = sw.toString();
      System.out.println(s);
       */
   }

}
