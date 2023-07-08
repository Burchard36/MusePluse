package com.burchard36.libs.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * This utility compresses a list of files to standard ZIP format file.
 * It is able to compress all sub files and sub directories, recursively.
 * @author www.codejava.net
 *
 */
public class ZipUtility {
    /**
     * A constants for buffer size used to read/write data
     */
    private static final int BUFFER_SIZE = 4096;
    /**
     * Compresses a list of files to a destination zip file
     * @param listFiles A collection of files and directories
     * @param destZipFile The path of the destination zip file
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void zip(List<File> listFiles, String destZipFile) throws FileNotFoundException,
            IOException {
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(destZipFile))) {
            for (File file : listFiles) {
                if (file.isDirectory()) {
                    zipDirectory(file, file.getName(), zos);
                } else {
                    zipFile(file, zos);
                }
            }
        }
    }
    /**
     * Adds a directory to the current zip output stream
     * @param folder the directory to be  added
     * @param parentFolder the path of parent directory
     * @param zos the current zip output stream
     * @throws FileNotFoundException
     * @throws IOException
     */
    private void zipDirectory(File folder, String parentFolder, ZipOutputStream zos) throws FileNotFoundException, IOException {
        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                zipDirectory(file, parentFolder + "/" + file.getName(), zos);
                continue;
            }
            zos.putNextEntry(new ZipEntry(parentFolder + "/" + file.getName()));
            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
                long bytesRead = 0;
                byte[] bytesIn = new byte[BUFFER_SIZE];
                int read = 0;
                while ((read = bis.read(bytesIn)) != -1) {
                    zos.write(bytesIn, 0, read);
                    bytesRead += read;
                }
            }
            zos.closeEntry();
        }
    }
    /**
     * Adds a file to the current zip output stream
     * @param file the file to be added
     * @param zos the current zip output stream
     * @throws FileNotFoundException
     * @throws IOException
     */
    private void zipFile(File file, ZipOutputStream zos)
            throws FileNotFoundException, IOException {
        zos.putNextEntry(new ZipEntry(file.getName()));
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
            long bytesRead = 0;
            byte[] bytesIn = new byte[BUFFER_SIZE];
            int read = 0;
            while ((read = bis.read(bytesIn)) != -1) {
                zos.write(bytesIn, 0, read);
                bytesRead += read;
            }
        }
        zos.closeEntry();
    }
}
