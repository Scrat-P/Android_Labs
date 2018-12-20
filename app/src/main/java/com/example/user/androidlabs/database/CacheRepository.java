package com.example.user.androidlabs.database;

import android.content.Context;
import android.util.Log;

import com.example.user.androidlabs.rss.FeedItem;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class CacheRepository {
    private static CacheRepository instance;

    private CacheRepository() {}

    public static CacheRepository getInstance() {
        if (instance == null) {
            instance = new CacheRepository();
        }
        return instance;
    }

    public void writeRssToCache(Context context, ArrayList<FeedItem> feedItems, String userUid) {
        File cacheFile = getTempFile(context, userUid);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(cacheFile, false);
            JsonWriter jsonWriter = new JsonWriter(new OutputStreamWriter(fileOutputStream, "UTF-8"));

            jsonWriter.beginArray();
            Gson gson = new Gson();
            for (int i = 0; i < 10; i++) {
                if (i > feedItems.size()) {
                    break;
                }
                gson.toJson(feedItems.get(i), FeedItem.class, jsonWriter);
            }
            jsonWriter.endArray();
            jsonWriter.close();
            fileOutputStream.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<FeedItem> readRssFromCache(Context context, String userUid){
        ArrayList<FeedItem> feedItems = new ArrayList<>();
        File cacheFile = getTempFile(context, userUid);
        try {
            FileInputStream fi = new FileInputStream(cacheFile);
            JsonReader reader = new JsonReader(new InputStreamReader(fi, "UTF-8"));
            reader.beginArray();
            Gson gson = new Gson();
            while (reader.hasNext()) {
                FeedItem item = gson.fromJson(reader, FeedItem.class);
                feedItems.add(item);
            }
            reader.endArray();

        } catch (FileNotFoundException exception) {
            Log.d("CacheRepository", exception.getMessage());
        } catch (IOException exception) {
            Log.d("CacheRepository", exception.getMessage());
        }
        return feedItems;
    }

    private File getTempFile(Context context, String fileName) {
        File cacheFile = new File(context.getCacheDir(), fileName);
        if (cacheFile.exists()) {
            return  cacheFile;
        }
        try {
            cacheFile.createNewFile();
        } catch (IOException exception) {
            Log.d("CacheRepository", exception.getMessage());
        }
        return cacheFile;
    }

    private void removeTempFile(Context context, String fileName){
        File cacheFile = new File(context.getCacheDir(), fileName);
        if (cacheFile != null && cacheFile.exists()) {
            cacheFile.delete();
        }
    }

    public void removeCacheForUser(Context context, String userUid) {
        removeTempFile(context, userUid);
    }
}
