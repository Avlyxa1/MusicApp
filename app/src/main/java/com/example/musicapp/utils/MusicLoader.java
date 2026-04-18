package com.example.musicapp.utils;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;

import com.example.musicapp.R;
import com.example.musicapp.models.Song;

import java.util.ArrayList;
import java.util.List;

public class MusicLoader {

    private static final int[] RAW_RESOURCES = {
            R.raw.song1, R.raw.song2, R.raw.song3, R.raw.song4, R.raw.song5,
            R.raw.song6, R.raw.song7, R.raw.song8, R.raw.song9, R.raw.song10
    };

    private static final String[] DEFAULT_TITLES = {
            "Song 1", "Song 2", "Song 3", "Song 4", "Song 5",
            "Song 6", "Song 7", "Song 8", "Song 9", "Song 10"
    };

    private static final String[] DEFAULT_ARTISTS = {
            "Unknown Artist", "Unknown Artist", "Unknown Artist", "Unknown Artist", "Unknown Artist",
            "Unknown Artist", "Unknown Artist", "Unknown Artist", "Unknown Artist", "Unknown Artist"
    };

    public static List<Song> loadAllSongs(Context context) {
        List<Song> songs = new ArrayList<>();
        for (int i = 0; i < RAW_RESOURCES.length; i++) {
            Song song = loadSongFromRaw(context, i + 1, RAW_RESOURCES[i], DEFAULT_TITLES[i], DEFAULT_ARTISTS[i]);
            songs.add(song);
        }
        return songs;
    }

    private static Song loadSongFromRaw(Context context, int id, int resourceId, String defaultTitle, String defaultArtist) {
        String title = defaultTitle;
        String artist = defaultArtist;
        String album = "Unknown Album";
        int duration = 0;

        try {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            Uri resourceUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + resourceId);
            retriever.setDataSource(context, resourceUri);

            String metaTitle = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            String metaArtist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            String metaAlbum = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
            String metaDuration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

            if (metaTitle != null && !metaTitle.isEmpty()) title = metaTitle;
            if (metaArtist != null && !metaArtist.isEmpty()) artist = metaArtist;
            if (metaAlbum != null && !metaAlbum.isEmpty()) album = metaAlbum;
            if (metaDuration != null) duration = (int) (Long.parseLong(metaDuration) / 1000);

            retriever.release();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new Song(id, title, artist, album, resourceId, duration);
    }
}