package common;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

public class FolderCreator {
	public static String createFolder(String folderName) {
		Path path = Paths.get("output").resolve(folderName);
		File dir = path.toFile();
		dir.mkdirs();

		String[] folders = dir.list();
		Arrays.sort(folders);
		String newDirName = null;
		if (folders.length == 0) {
			newDirName = "000";
		} else {
			String lastFile = folders[folders.length - 1];
			String[] files = path.resolve(lastFile).toFile().list();
			if (files.length == 0) {
				newDirName = lastFile;
			} else {
				newDirName = StringUtils.leftPad(String.valueOf(Integer.parseInt(lastFile) + 1), 3, "0");
			}
		}

		Path newPath = path.resolve(newDirName);
		File newDir = newPath.toFile();
		newDir.mkdirs();

		return newPath.toString();
	}
}
