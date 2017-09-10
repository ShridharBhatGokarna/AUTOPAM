package com.AutoPAM.general;

import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * The CILogger is responsible for logging the execution details during
 * installation of setups Log and Error information can be logged using CILogger
 */

public class CILogger {

	private static Logger logger;

	private static String installPath = "C:\\INFA_Automation\\INFA_Installer_Automation";
	static {
		try {
			System.out.println("Install path" + installPath);
			String pattern = installPath + "\\log\\InfaInstallLog%g.txt";
			int limit = 1000000;// 1 Mb
			int numLogFiles = 3;
			FileHandler handler = new FileHandler(pattern, limit, numLogFiles,
					true);
			handler.setFormatter(new Formatter() {
				@Override
				public String format(LogRecord rec) {
					StringBuffer buf = new StringBuffer(1000);
					buf.append(new java.util.Date());
					buf.append(' ');
					buf.append(rec.getSourceClassName());
					buf.append(':');
					buf.append(rec.getSourceMethodName());
					buf.append(".....");
					buf.append(rec.getLevel());
					buf.append("...");
					buf.append(formatMessage(rec));
					buf.append('\n');
					return buf.toString();
				}

			});
			logger = Logger.getLogger("CustomInstallLog");
			logger.addHandler(handler);
		} catch (Exception exp) {
			System.out.println("STEMLoger:Initialization:Exception: \n" + exp);
		}
	}

	/**
	 * Logs the message details. This method will not capture the class, method details
	 * @param message - Message that needs to be logged
	 */
	public static void log(String message) {
		logger.log(Level.INFO, message);
	}

	/**
	 * Logs the basic informaiton with class, method and message details
	 * 
	 * @param classname -
	 *            The name of the class to be printed in the log
	 * @param method -
	 *            Name of the method to be printed in the log
	 * @param message -
	 *            Message to be printed in the log
	 */
	public static void log(String classname, String methodname, String message) {
		logger.logp(Level.INFO, classname, methodname, message);
	}

	/**
	 * Logs the error informaiton with class, method and message details
	 * 
	 * @param classname -
	 *            The name of the class to be printed in the log
	 * @param method -
	 *            Name of the method to be printed in the log
	 * @param message -
	 *            Message to be printed in the log
	 */
	public static void logError(String classname, String method, String message) {
		logger.logp(Level.SEVERE, classname, method, message);
	}
}// end class
