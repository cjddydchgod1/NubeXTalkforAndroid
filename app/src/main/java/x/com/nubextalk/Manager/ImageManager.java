/*
 * Created By JONGHO, LEE, Researcher of Institute of Biomedical Engineering, TechHeim Co.,Ltd.
 * on  2021.
 * This software is not stable health-care system and not certificated any health-care authority(like CE, FDA)
 * Prohibit redistribution, modification, commercial use.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk.Manager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import x.com.nubextalk.Module.CodeResources;

public class ImageManager {
    private Context mContext;

    public ImageManager(Context context) {
        this.mContext = context;
    }


    /**
     * Cache directory + Cache file name
     **/
    public String getCachePath(String name) {
        return mContext.getCacheDir().toString() + "/" + name;
    }

    /**
     * External storage directory +  File name
     **/
    public String getExternalStoragePath(String name) {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + CodeResources.TAG + "/" + name;
    }

    /**
     * URL -> Cache file
     **/
    public String saveUrlToCache(String imageUrl, String name) {
        Bitmap bitmap = getImageFromURL(imageUrl);
        String path = saveBitmapToCache(bitmap, name);
        return path;
    }

    /**
     * URL -> External Storage
     **/
    public String saveUrlToExternalStorage(String imageUrl, String name) {
        Bitmap bitmap = getImageFromURL(imageUrl);
        String path = saveBitmapToExternalStorage(bitmap, name);
        return path;
    }

    /**
     * Cache file -> Bitmap
     **/
    public Bitmap getBitmapFromCache(String name) {
        String path = getCachePath(name);
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        return bitmap;
    }

    /**
     * URL -> Bitmap
     **/
    public Bitmap getImageFromURL(String imageURL) {
        URL imgUrl = null;
        HttpURLConnection connection = null;
        InputStream is = null;
        Bitmap bitmap = null;
        try {
            imgUrl = new URL(imageURL);
            connection = (HttpURLConnection) imgUrl.openConnection();
            connection.setDoInput(true);
            connection.connect();
            is = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            return bitmap;
        }
    }

    /**
     * Bitmap -> Cache file
     **/
    public String saveBitmapToCache(Bitmap bitmap, String name) {
        File storage = mContext.getCacheDir();
        File tempFile = new File(storage, name);
        String path = getCachePath(name);

        try {
            tempFile.createNewFile();
            FileOutputStream out = new FileOutputStream(tempFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 60, out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }

    /**
     * Bitmap -> External Storage
     **/
    public String saveBitmapToExternalStorage(Bitmap bitmap, String name) {
        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + CodeResources.TAG;
        File storage = new File(dirPath);
        File tempFile = new File(storage, name);
        String path = null;
        if (!storage.exists()) storage.mkdir();

        try {
            tempFile.createNewFile();
            FileOutputStream out = new FileOutputStream(tempFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            path = getExternalStoragePath(name);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }

}
