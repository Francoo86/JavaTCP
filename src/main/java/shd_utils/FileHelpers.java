package shd_utils;

import org.apache.commons.io.FilenameUtils;

import java.io.File;

public class FileHelpers {
    public static boolean createDirIfNotExists(String dir) {
        File dirObj = new File(dir);
        if(!isValid(dirObj)){
            return dirObj.mkdir();
        }

        return true;
    }

    public static boolean isValid(File fileObj){
        return fileObj.exists();
    }

    public static File searchFile(String dir, String file, String ext) {
        final File dirObj = new File(dir);

        if(!isValid(dirObj)){
            System.out.println("The dir " + dir + " directory doesn't exist.");
            return null;
        }

        return searchFileRecursive(dirObj, file.trim(), ext);
    }

    public static File searchFileRecursive(final File folder, String target, String ext) {
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                return searchFileRecursive(fileEntry, target, ext);
            }

            String fileNameNoExt = FilenameUtils.removeExtension(fileEntry.getName());
            String currentExt = FilenameUtils.getExtension(fileEntry.getName());

            if(fileNameNoExt.equals(target) && currentExt.equals(ext)) {
                return fileEntry;
            }
        }

        return null;
    }
}
