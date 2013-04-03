package com.doc.dystopia;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.apache.commons.net.ftp.FTPClient;

import android.os.AsyncTask;
import android.util.Log;

class UploadFTPTask extends AsyncTask<InputStream, Void, Void> {
	
	
	private String uname;
	private String password;
	
	public UploadFTPTask(String ftpUname, String ftpPassword){
		uname = ftpUname;
		password = ftpPassword;
	}
	
    // Do the long-running work in here. Close input stream at end.
	//TODO: check boolean results from ftp operations and log errors on false
    protected Void doInBackground(InputStream... inputStreams) {
		FTPClient ftpClient = new FTPClient();
		 
		try {
		    ftpClient.connect(InetAddress.getByName("documentingdystopia.org"));
		    ftpClient.login(uname, password); 
		    boolean dirResult = ftpClient.changeWorkingDirectory("Images/AppUpload");
		 
		    if (ftpClient.getReplyString().contains("250")) {
		        ftpClient.setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE);
		        ftpClient.enterLocalPassiveMode();
		        //ProgressInputStream progressInput = new ProgressInputStream(buffIn, progressHandler);
			    
		        boolean result = ftpClient.storeFile("flubber.jpeg", inputStreams[0]);
		        
		        inputStreams[0].close();
		        
		        
		        ftpClient.logout();
		        ftpClient.disconnect();
		    }
		 
		} catch (SocketException e) {
		    Log.e(this.getClass().getName(), e.getStackTrace().toString());
		} catch (UnknownHostException e) {
		    Log.e(this.getClass().getName(), e.getStackTrace().toString());
		} catch (IOException e) {
		    Log.e(this.getClass().getName(), e.getStackTrace().toString());
		}
		
		return null;
    }


    // This is called when doInBackground() is finished
    protected void onPostExecute(Void v) {
	    Log.e(this.getClass().getName(), "boom done");

    }
}