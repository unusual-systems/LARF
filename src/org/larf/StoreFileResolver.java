/*
 * $Id: StoreFileResolver.java,v 1.1.1.1 2005-02-16 15:02:46 mch Exp $
 *
 * (C) Copyright Astrogrid...
 */

package org.larf;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.Principal;

import org.slinger.account.LoginAccount;

/**
 * Resolves the appropriate StoreFile implementation from a *locator*, eg
 * URL, MSRL, given as a URI.
 * /**
 * This is done by looking up org.larf.adaptors.{URI-scheme}.FileAdaptor, where the URI-scheme
 * has had colons replaced by full stops.
 */

public class StoreFileResolver {
   
	   /** Creates the appropriate adaptor instance from the given uri (see class doc). 
	    * Uses reflection to create the right class; this normally throws a whole bunch of exceptions.  They are wrapped up 
	    * into two here; ClassNotFound (ie, the adaptor is not in the
	    * classpath), or InvocationTargetException (ie there was an error running the constructor).
	    */
	public static FileNode resolveFile(URI uri, Principal user) throws ClassNotFoundException, InvocationTargetException {
	   String scheme = uri.getScheme();
	   scheme = scheme.replace('+','.');
	   scheme = scheme.replace('-','.');
	   String implementerClassname = "org.larf.adaptors."+scheme+".FileAdaptor";
	   
	   Class implementerClass = Class.forName(implementerClassname);

	   try {
		   Constructor constr = implementerClass.getConstructor(new Class[] { URI.class, Principal.class });
		   Object implementor = constr.newInstance(new Object[] { uri, user } );
		   assert (implementor instanceof FileNode) : implementerClass.getName()+" must implement "+FileNode.class.getName();
		   return (FileNode) implementor; 

	   } catch (NoSuchMethodException e) {
			throw new InvocationTargetException(e, implementerClass.getName()+" must implement the constructor "+implementerClassname+"(URI, Principal)");
	   } catch (IllegalArgumentException e) {
			throw new InvocationTargetException(e, implementerClass.getName()+" must implement a constructor "+implementerClassname+"(URI, Principal)");
		} catch (InstantiationException e) {
			throw new InvocationTargetException(e, "Could not construct "+implementerClass.getName());
		} catch (IllegalAccessException e) {
			throw new InvocationTargetException(e, "Could not construct "+implementerClass.getName());
		}
	}

	/** Convenience routine - takes URL instead of URI, eg for http, ftp, etc */
	public static FileNode resolveFile(URL url, Principal user) throws ClassNotFoundException, InvocationTargetException {
		try {
			return resolveFile(new URI(url.toString()), user);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e+" from "+url.toString()); //should never happen - all URLs are valid URIs
		}
	}
	
	/** Unit test. can we resolve local files (in Windows) and ftp files (at ROE) */
	public static void main(String[] args) throws Exception {
	   FileNode directory = StoreFileResolver.resolveFile(new URI("file:///C:/workspace"), LoginAccount.ANONYMOUS);
	   System.out.println("File: "+directory);
	   if (directory.exists()) System.out.println("exists"); else System.out.println("doesn't exist"); 
	   
	   for (FileNode child : directory.listFiles()) {
			   System.out.println(child);
	   }
	   
	   FileNode ftpDir = StoreFileResolver.resolveFile(new URI("ftp://ftp.roe.ac.uk/pub/"), LoginAccount.ANONYMOUS);
	   System.out.println("File: "+ftpDir);
	   if (ftpDir.exists()) System.out.println("exists"); else System.out.println("doesn't exist"); 
	   for (FileNode child : ftpDir.listFiles()) {
		   System.out.println(child);
	   }

	}	   
	
}
