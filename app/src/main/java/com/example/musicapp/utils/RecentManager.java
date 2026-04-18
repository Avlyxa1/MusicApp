package com.example.musicapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.musicapp.models.Song;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class RecentManager {
    private static RecentManager instance;
    private static final String PREF_NAME = "RecentSongs";
    private static final String KEY_RECENT_IDS = "recent_ids";
    private static final int MAX_RECENT = 10;

    private Context context;
    private List<Integer> recentIds;

    private RecentManager(Context context) {
        this.context = context.getApplicationContext();
        loadRecentIds();
    }

    public static synchronized RecentManager getInstance(Context context) {
        if (instance == null) {
            instance = new RecentManager(context);
        }
        return instance;
    }

    private void loadRecentIds() {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_RECENT_IDS, null);
        if (json == null) {
            recentIds = new ArrayList<>();
        } else {
            Type type = new TypeToken<List<Integer>>(){}.getType();
            recentIds = new Gson().fromJson(json, type);
            if (recentIds == null) recentIds = new ArrayList<>();
        }
    }

    private void saveRecentIds() {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_RECENT_IDS, new Gson().toJson(recentIds)).apply();
    }

    public void addRecentSong(Song song) {
        recentIds.remove(Integer.valueOf(song.getId()));
        recentIds.add(0, song.getId());
        if (recentIds.size() > MAX_RECENT) {
            recentIds = recentIds.subList(0, MAX_RECENT);
        }
        saveRecentIds();
    }

    public List<Song> getRecentSongs(List<Song> allSongs) {
        List<Song> recentSongs = new ArrayList<>();
        for (int id : recentIds) {
            for (Song song : allSongs) {
                if (song.getId() == id) {
                    recentSongs.add(song);
                    break;
                }
            }
        }
        return recentSongs;
    }
}