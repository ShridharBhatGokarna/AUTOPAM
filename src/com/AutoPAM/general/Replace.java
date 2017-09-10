package com.AutoPAM.general;

import java.lang.reflect.Array;
import java.util.regex.Pattern;

/**
 * This class is used replace ols string with new string in the base string that
 * is provided. For example if base string is "CustomInstallation", old string
 * is "Custom" and new string is "New", the output would be "NewInstallation"
 * 
 * 
 * 
 */
public class Replace {

	/**
	 * This method returns the parsed data from the specified string
	 * 
	 * @param string -
	 *            String which has to be parsed
	 * @return - returns the parsed string
	 */
	public String getParsedData(String string) {
		String returnStr = "";
		try {
			Pattern pattern = Pattern.compile(":");
			String[] Filedata = pattern.split(string);
			Object returnObj = Array.get(Filedata, 1);
			returnStr = returnObj.toString();
		} catch (Exception e) {
			CILogger.logError("Replace", "getParsedData",
					"Cannot Get The Values From Config File : " + e);
		}
		return returnStr;
	}

	/**
	 * This method replaces the old pattern with new pattern in the provided
	 * string
	 * 
	 * @param oldStr -
	 *            Old pattern which has to be replaced
	 * @param newStr-
	 *            New pattern which should replace the old pattern
	 * @param inString -
	 *            String in which the specified pattern should be replaced
	 * @return - Return true if the pattern is replaced successfully or false if
	 *         is not found
	 */
	public String replaceString(String oldStr, String newStr, String inString) {
		int start = inString.indexOf(oldStr);
		StringBuffer sb = new StringBuffer();
		sb.append(inString.substring(0, start));
		sb.append(newStr);
		sb.append(inString.substring(start + oldStr.length()));
		return sb.toString();
	}

	/**
	 * Validates if the pattern exists in the specified string
	 * 
	 * @param oldStr -
	 *            Pattern which needs to be validated
	 * @param inString -
	 *            String in which the pattern should be present
	 * @return - true if pattern exists or false if pattern doesnt exist
	 */
	public boolean searchPatternExists(String oldStr, String inString) {
		int start = inString.indexOf(oldStr);
		if (start == -1) {
			return false;
		} else {
			return true;
		}

	}
}
