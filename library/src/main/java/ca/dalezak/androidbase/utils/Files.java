package ca.dalezak.androidbase.utils;

import android.os.Environment;
import android.util.Base64;
import android.webkit.MimeTypeMap;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class Files {

    public static File getVideosDirectory() {
        File rootDirectory = isExternalStorageWritable()
                ? Environment.getExternalStorageDirectory()
                : Environment.getDataDirectory();
        return new File(rootDirectory, "videos");
    }

    public static File getImagesDirectory() {
        File rootDirectory = isExternalStorageWritable()
                ? Environment.getExternalStorageDirectory()
                : Environment.getDataDirectory();
        return new File(rootDirectory, "images");
    }

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            type = mime.getMimeTypeFromExtension(extension);
        }
        return type;
    }

    public static String getMimeType(File file) {
        return getMimeType(file.getAbsolutePath());
    }

    public static String getBase64(File file) {
        try {
            String filePath = file.getAbsolutePath();
            InputStream inputStream = new FileInputStream(filePath);
            byte[] bytes;
            byte[] buffer = new byte[8192];
            int bytesRead;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            try {
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            bytes = output.toByteArray();
            return Base64.encodeToString(bytes, Base64.DEFAULT);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

}
