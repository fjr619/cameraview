package id.co.cicil.myapplication.camera.utils;

/**
 * Created by Franky Wijanarko on 06/05/2020.
 * franky.wijanarko@cicil.co.id
 */

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Environment;
import android.provider.MediaStore.Images.Media;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import androidx.annotation.Nullable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by K.K. Ho on 3/9/2017.
 * Modified by Fatah F
 */

public class FileUtils {

    private static final String JPEG_FILE_PREFIX = "IMG";

    private static final String JPEG_FILE_SUFFIX = ".jpg";

    private static final String DATE_FILE_FORMAT = "yyyyMMdd_HHmmss";

    private static final char DOT = '.';

    private static final char UNDERLINE = '_';

//    @RequiresApi(VERSION_CODES.Q)
    public static void writeQ(Context context, Bitmap bitmap, Bitmap.CompressFormat compressFormat, int quality)
            throws IOException, SecurityException, NullPointerException {
        //https://www.youtube.com/watch?v=f9-smcUFksQ

        OutputStream fileOutputStream = null;
        String filename = FileUtils.createFilename("NEW_WAYS_");
        String targetFilename = filename + JPEG_FILE_SUFFIX;
        ContentResolver resolver = context.getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaColumns.DISPLAY_NAME, targetFilename);
        contentValues.put(MediaColumns.MIME_TYPE, "image/jpg");
        contentValues.put(MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM+"/CICIL/");

        Uri uri = resolver.insert(Media.EXTERNAL_CONTENT_URI, contentValues);
        if (uri != null) {
            fileOutputStream = resolver.openOutputStream(uri);
        } else {
            Log.e("TAG", "uri kosong pake cara old ways");
        }

        if (VERSION.SDK_INT >= VERSION_CODES.Q) {
            try {
                bitmap.compress(compressFormat, quality, fileOutputStream);
                refreshMediaGallery(context, uri);
            } finally {
                if (fileOutputStream != null) {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                }
            }
        } else {
            try {
                bitmap.compress(compressFormat, quality, fileOutputStream);
            } catch (Exception e) {
                write(context, bitmap, compressFormat,quality, createTempImageFile(context));
            } finally {
                if (fileOutputStream != null) {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                }
            }
        }


    }

    private static void write(Context context, Bitmap bitmap, Bitmap.CompressFormat compressFormat, int quality,
            File photoFile) throws IOException, SecurityException, NullPointerException {
        FileOutputStream fileOutputStream = null;

        try {
            fileOutputStream = new FileOutputStream(photoFile.getAbsoluteFile());
            bitmap.compress(compressFormat, quality, fileOutputStream);
            Uri uri = Uri.fromFile(photoFile);
            refreshMediaGallery(context, uri);
        }
        finally {
            if (fileOutputStream != null) {
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        }
    }

    @Nullable
    private static File getDirectory(Context context) throws NullPointerException {
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM+"/CICIL/");

        if (storageDir != null && !storageDir.mkdirs()) {
            if (!storageDir.exists()) {
                return null;
            }
        }

        return storageDir;
    }

    /**
     * Create temporary file which will be written by Bitmap data later
     *
     * @param context   Just context
     *
     * @return a File that contains no data
     *
     * @throws IOException              If unable to create temp file
     * @throws NullPointerException     If fails when get expected directory
     * @throws SecurityException        If security manager does not allow file to be created
     */
    private static File createTempImageFile(Context context)
            throws IOException, NullPointerException, SecurityException {
        File directory = FileUtils.getDirectory(context);
        if (directory == null) throw new NullPointerException("Unable to create cache directory");

        String imageFileName = FileUtils.createFilename("OLD_WAY_");
        Log.e("TAG", "directory ="+directory.getAbsolutePath());
        return File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, directory);
    }

    /**
     * Make filename
     *
     * @return a specified filename
     */
    private static String createFilename(String prefix) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FILE_FORMAT, Locale.getDefault());
        String timestamp = simpleDateFormat.format(new Date());

        return prefix+JPEG_FILE_PREFIX + UNDERLINE + timestamp;
    }

    private static void refreshMediaGallery(Context context, Uri contentUri) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
            mediaScanIntent.setData(contentUri);
            context.sendBroadcast(mediaScanIntent);
        }
    }

}