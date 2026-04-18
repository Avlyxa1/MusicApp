package com.example.musicapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.musicapp.models.Playlist;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {
    private static final String PREF_NAME = "MusicAppPrefs";
    private static final String KEY_PLAYLISTS = "playlists";
    private static final String KEY_FAVORITES = "favorites";

    private static Gson gson = new Gson();

    public static void savePlaylists(Context context, List<Playlist> playlists) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = gson.toJson(playlists);
        prefs.edit().putString(KEY_PLAYLISTS, json).apply();
    }

    public static List<Playlist> loadPlaylists(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_PLAYLISTS, null);
        if (json == null) return new ArrayList<>();
        Type type = new TypeToken<List<Playlist>>(){}.getType();
        List<Playlist> playlists = gson.fromJson(json, type);
        return playlists != null ? playlists : new ArrayList<>();
    }

    public static void saveFavoriteIds(Context context, List<Integer> favoriteIds) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = gson.toJson(favoriteIds);
        prefs.edit().putString(KEY_FAVORITES, json).apply();
    }

    public static List<Integer> loadFavoriteIds(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_FAVORITES, null);
        if (json == null) return new ArrayList<>();
        Type type = new TypeToken<List<Integer>>(){}.getType();
        List<Integer> ids = gson.fromJson(json, type);
        return ids != null ? ids : new ArrayList<>();
    }
}