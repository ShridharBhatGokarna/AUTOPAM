package com.AutoPAM.general;

import java.io.File;

/**
 *
 * 

 * 
 */
public class CleanupFiles {

	public String file_path;

	public String pattern_to_delete;

	/**
	 *  */
	public void deleteFiles(String pattern_to_delete, String file_path) {

		try {
			File file_location = new File(file_path);

			if ((!file_location.exists()) || (!file_location.isDirectory())) {
				System.out.println(" Invalid file location " + file_path);
				return;
			}

			File[] file_lists = file_location.listFiles();

			if ((file_lists == null) || (file_lists.length == 0)) {
				System.out
						.println(" CleanupFiles :: deleteFiles :: Files do not exist in the location "
								+ file_path);
				// CILogger.logError("CleanupFiles, "deleteFiles", message)
				return;
			}

			for (File file : file_lists) {

				if (file.getName().startsWith(pattern_to_delete)) {
					file.delete();
				}
			}
		} catch (Exception exp) {
			System.out.println("CleanupFiles :: deleteFiles :: Exception "
					+ exp);
		}

	}

}// end
