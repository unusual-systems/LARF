package org.larf;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;

import org.slinger.account.LoginAccount;
import org.slinger.piper.ConsolePipeListener;
import org.slinger.piper.Piper;

import net.mchill.util.TimeStamp;

/** a set of convenience methods for copying, downloading, uploading etc */

public class LarfHelper {

	public static void upload(File localFile, URL remoteDir, String filename, LoginAccount user) throws IOException {
		FileNode fromFile = new org.larf.adaptors.file.FileAdaptor(localFile);
		FileNode toDir = null;
		try {
			toDir = StoreFileResolver.resolveFile(remoteDir, user);
		} catch (ClassNotFoundException e) {
			throw new IOException("No adaptors found for "+remoteDir.getProtocol());
		} catch (InvocationTargetException e) {
			RuntimeException re = new RuntimeException(e.getCause()+" constructing adaptor for "+remoteDir.getProtocol());
			re.setStackTrace(e.getCause().getStackTrace());
			throw re;
		}
		
		assert toDir.exists();
		
		copy(fromFile.openInputStream(), toDir.outputChild(filename, fromFile.getMimeType()), fromFile.getName(), fromFile.getSize());
	}
	
	public static void copy(InputStream fromStream, OutputStream toStream, String name, long size) throws IOException {
		
		System.out.println("Copying...");
	    Piper.pipe(fromStream, new BufferedOutputStream(toStream), new ConsolePipeListener("CopyPipe["+name+"]", size) {
	    	public void piped(long bytesRead) {
	    		super.piped(bytesRead);
	    		System.out.println(totalPiped+" of "+size+"("+getCompletionPercent()+"%), @"+getOverallTransferRate()/1000+"kb/s, "+TimeStamp.getHoursEtc(getSecondsToComplete())+" remaining.");
	    	}
	    });
		System.out.println("...done");
	}
	
	public static void upload(File localFile, URL remoteFile, LoginAccount user) throws IOException {
		FileNode fromFile = new org.larf.adaptors.file.FileAdaptor(localFile);
		FileNode toFile = null;
		try {
			toFile = StoreFileResolver.resolveFile(remoteFile, user);
		} catch (ClassNotFoundException e) {
			throw new IOException("No adaptors found for "+remoteFile.getProtocol());
		} catch (InvocationTargetException e) {
			RuntimeException re = new RuntimeException(e.getCause()+" constructing adaptor for "+remoteFile.getProtocol());
			re.setStackTrace(e.getCause().getStackTrace());
			throw re;
		}
		
		copy(fromFile.openInputStream(), toFile.openOutputStream(fromFile.getMimeType(), false), fromFile.getName(), fromFile.getSize());
	}
	
	/** Tests, coded actions, etc */
	public static void main(String[] args) throws IOException {
		File from = new File("C:\\Documents and Settings\\mhill\\My Documents\\My Pictures\\Birthday BBQ\\DSCN0185-DivX-WhatItShouldBeLike.avi");
		assert from.exists();
//		upload(from, new URL("ftp://ftp.roe.ac.uk/pub/mch/BBQ-1.avi"), new LoginAccount("mch", "lorraine"));
		upload(from, new URL("ftp://ftp.martinhill.me.uk/httpdocs/tmp/"+from.getName()), new LoginAccount("vxxgoeti", "lorraine"));
		//upload(from, new URL("ftp://ftp.roe.ac.uk/pub/mch/"), "BBQ-1.avi", new LoginAccount("anonymous", "MartinHill"));
	}
}
