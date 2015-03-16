package ca.dalezak.androidbase.utils;

import android.content.Context;
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

    private File getImageFile(Context context, String filename) {
        File imagesDirectory = getImagesDirectory(context);
        return new File(imagesDirectory, filename);
    }

    public static File getRootDirectory() {
        return isExternalStorageWritable()
                ? Environment.getExternalStorageDirectory()
                : Environment.getDataDirectory();
    }

    public static File getAppDirectory(Context context) {
        File rootDirectory = getRootDirectory();
        File appDirectory = new File(rootDirectory, context.getPackageName());
        if (!appDirectory.exists() && appDirectory.mkdirs()){
            Log.i(Files.class, "Created Directory %s", appDirectory);
        }
        return appDirectory;
    }

    public static File getVideosDirectory(Context context) {
        File appDirectory = getAppDirectory(context);
        File videosDirectory = new File(appDirectory, "videos");
        if (!videosDirectory.exists() && videosDirectory.mkdirs()){
            Log.i(Files.class, "Created Directory %s", videosDirectory);
        }
        return videosDirectory;
    }

    public static File getImagesDirectory(Context context) {
        File appDirectory = getAppDirectory(context);
        File imagesDirectory = new File(appDirectory, "images");
        if (!imagesDirectory.exists() && imagesDirectory.mkdirs()){
            Log.i(Files.class, "Created Directory %s", imagesDirectory);
        }
        return imagesDirectory;
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
