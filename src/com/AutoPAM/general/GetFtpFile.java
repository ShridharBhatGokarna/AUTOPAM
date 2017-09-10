package com.AutoPAM.general;



import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.StringTokenizer;

import sun.net.TelnetInputStream;
import sun.net.ftp.FtpClient;



public class GetFtpFile {
	public static int BUFFER_SIZE = 10240;
	
	public static int getFileSize(FtpClient client, String fileName)
			throws IOException {/*
		TelnetInputStream lst = client.list();
		String str = "";
		fileName = fileName.toLowerCase();
		while (true) {
			int c = lst.read();
			char ch = (char) c;
			if ((c < 0) || (ch == '\n')) {
				str = str.toLowerCase();
				if (str.indexOf(fileName) >= 0) {
					StringTokenizer tk = new StringTokenizer(str);
					int index = 0;
					while (tk.hasMoreTokens()) {
						String token = tk.nextToken();
						if (index == 4) {
							try {
								return Integer.parseInt(token);
							} catch (NumberFormatException ex) {
								return -1;
							}
						}
						index++;
					}
				}
				str = "";
			}
			if (c <= 0) {
				break;
			}
			str += ch;
		}
		return -1;
	*/
	return -1;	
	}



	
	public GetFtpFile(String hostAdd, String userName, String password,
			String dir) {/*
		try {
			// System.out.println("Connecting to FTP Machine To Download File ::
			// " + hostAdd);
			m_client = new FtpClient(hostAdd);		
			m_client.login(userName, password);			
			m_client.cd(dir);
			m_client.binary();
		} catch (Exception ex) {			
			ex.printStackTrace();
		}
	*/}
	public static void main(String[] args){	
		GetFtpFile get=new GetFtpFile("camry","toolinst","in910inst$","/home/toolinst/Node2/source/services/AdministratorConsole");
		get.getFile("C:\\test", "administrator.log");
	}
	
	protected void disconnect() {/*
		if (m_client != null) {
			try {
				m_client.closeServer();
			} catch (IOException ex) {
			}
			m_client = null;
		}
	*/}

	
	public void getFile(String m_sLocalFile, String m_sHostFile) {/*
		FileOutputStream out=null;
		if (m_sLocalFile.length() == 0) {
			m_sLocalFile = m_sHostFile;
		}
		byte[] buffer = new byte[BUFFER_SIZE];
		try {
			int size = getFileSize(m_client, m_sHostFile);
			if (size > 0) {
				System.out.println(size);
			} else {
				System.out.println("File " + m_sHostFile + ": size unknown");
			}
			try{
			File sFileData=new File(m_sLocalFile);
			out = new FileOutputStream(sFileData);
			}catch(Exception e){
				System.out.println("Error in getFile Method : Creation of File "+m_sLocalFile);
			}
			InputStream in = m_client.get(m_sHostFile);
			int counter = 0;
			while (true) {
				int bytes = in.read(buffer);
				if (bytes < 0) {
					break;
				}
				out.write(buffer, 0, bytes);
				counter += bytes;
			}
			out.close();
			in.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	*/}

}
