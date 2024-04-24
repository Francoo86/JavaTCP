package shd_utils;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

            if(fileEntry.getName().equals(target)) {
                return fileEntry;
            }
        }

        return null;
    }

    public static Set<String> getAllFileNames(final File folder) {
        Set<String> allFiles = new HashSet<>();
        saveFileNamesToList(folder, allFiles);
        return allFiles;
    }

    //duplicates moment
    private static void saveFileNamesToList(final File folder, Set<String> cache) {
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                saveFileNamesToList(fileEntry, cache);
                return;
            };

            cache.add(fileEntry.getName());
        }
    }
}
