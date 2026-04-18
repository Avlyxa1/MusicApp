package com.example.musicapp.player;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;

import com.example.musicapp.models.Song;
import com.example.musicapp.utils.RecentManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MusicPlayerManager {
    private static MusicPlayerManager instance;
    private MediaPlayer mediaPlayer;
    private List<Song> queue;
    private List<Song> originalQueue;
    private int currentIndex = -1;
    private boolean isShuffled = false;
    private boolean isRepeat = false;
    private Context context;
    private Handler handler;
    private OnPlayerEventListener listener;

    public interface OnPlayerEventListener {
        void onSongChanged(Song song);
        void onPlayStateChanged(boolean isPlaying);
        void onProgressChanged(int progress, int duration);
        void onQueueChanged();
    }

    private MusicPlayerManager(Context context) {
        this.context = context.getApplicationContext();
        this.queue = new ArrayList<>();
        this.originalQueue = new ArrayList<>();
        this.handler = new Handler(Looper.getMainLooper());
    }

    public static synchronized MusicPlayerManager getInstance(Context context) {
        if (instance == null) {
            instance = new MusicPlayerManager(context);
        }
        return instance;
    }

    public void setListener(OnPlayerEventListener listener) {
        this.listener = listener;
    }

    public void playSong(Song song, List<Song> songList) {
        originalQueue.clear();
        originalQueue.addAll(songList);
        queue.clear();
        queue.addAll(songList);

        if (isShuffled) {
            int songIndex = -1;
            for (int i = 0; i < queue.size(); i++) {
                if (queue.get(i).getId() == song.getId()) {
                    songIndex = i;
                    break;
                }
            }
            if (songIndex >= 0) {
                queue.remove(songIndex);
                Collections.shuffle(queue);
                queue.add(0, song);
                currentIndex = 0;
            }
        } else {
            for (int i = 0; i < queue.size(); i++) {
                if (queue.get(i).getId() == song.getId()) {
                    currentIndex = i;
                    break;
                }
            }
        }

        startPlayback(song);
    }

    private void startPlayback(Song song) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        try {
            mediaPlayer = MediaPlayer.create(context, song.getResourceId());
            if (mediaPlayer != null) {
                mediaPlayer.start();
                RecentManager.getInstance(context).addRecentSong(song);
                if (listener != null) {
                    listener.onSongChanged(song);
                    listener.onPlayStateChanged(true);
                }
                startProgressTracking();
                mediaPlayer.setOnCompletionListener(mp -> onSongComplete());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onSongComplete() {
        if (isRepeat) {
            if (currentIndex >= 0 && currentIndex < queue.size()) {
                startPlayback(queue.get(currentIndex));
            }
        } else {
            playNext();
        }
    }

    public void playNext() {
        if (queue.isEmpty()) return;
        currentIndex = (currentIndex + 1) % queue.size();
        startPlayback(queue.get(currentIndex));
    }

    public void playPrevious() {
        if (queue.isEmpty()) return;
        if (mediaPlayer != null && mediaPlayer.getCurrentPosition() > 3000) {
            mediaPlayer.seekTo(0);
            return;
        }
        currentIndex = (currentIndex - 1 + queue.size()) % queue.size();
        startPlayback(queue.get(currentIndex));
    }

    public void togglePlayPause() {
        if (mediaPlayer == null) return;
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        } else {
            mediaPlayer.start();
        }
        if (listener != null) {
            listener.onPlayStateChanged(mediaPlayer.isPlaying());
        }
    }

    public void seekTo(int position) {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(position);
        }
    }

    public void toggleShuffle() {
        isShuffled = !isShuffled;
        if (isShuffled) {
            Song current = getCurrentSong();
            queue.clear();
            queue.addAll(originalQueue);
            if (current != null) {
                queue.remove(current);
                Collections.shuffle(queue);
                queue.add(0, current);
                currentIndex = 0;
            } else {
                Collections.shuffle(queue);
            }
        } else {
            Song current = getCurrentSong();
            queue.clear();
            queue.addAll(originalQueue);
            if (current != null) {
                for (int i = 0; i < queue.size(); i++) {
                    if (queue.get(i).getId() == current.getId()) {
                        currentIndex = i;
                        break;
                    }
                }
            }
        }
        if (listener != null) listener.onQueueChanged();
    }

    public void toggleRepeat() {
        isRepeat = !isRepeat;
    }

    public void addToQueue(Song song) {
        queue.add(song);
        originalQueue.add(song);
        if (listener != null) listener.onQueueChanged();
    }

    public void removeFromQueue(int index) {
        if (index >= 0 && index < queue.size()) {
            if (index == currentIndex) {
                playNext();
                queue.remove(index);
                if (currentIndex > index) currentIndex--;
            } else {
                queue.remove(index);
                if (currentIndex > index) currentIndex--;
            }
            if (listener != null) listener.onQueueChanged();
        }
    }

    public void moveInQueue(int from, int to) {
        Song song = queue.remove(from);
        queue.add(to, song);
        if (currentIndex == from) {
            currentIndex = to;
        } else if (from < currentIndex && to >= currentIndex) {
            currentIndex--;
        } else if (from > currentIndex && to <= currentIndex) {
            currentIndex++;
        }
        if (listener != null) listener.onQueueChanged();
    }

    private void startProgressTracking() {
        handler.removeCallbacksAndMessages(null);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    if (listener != null) {
                        listener.onProgressChanged(mediaPlayer.getCurrentPosition(), mediaPlayer.getDuration());
                    }
                    handler.postDelayed(this, 500);
                }
            }
        }, 500);
    }

    public Song getCurrentSong() {
        if (currentIndex >= 0 && currentIndex < queue.size()) {
            return queue.get(currentIndex);
        }
        return null;
    }

    public List<Song> getQueue() { return queue; }
    public int getCurrentIndex() { return currentIndex; }
    public boolean isPlaying() { return mediaPlayer != null && mediaPlayer.isPlaying(); }
    public boolean isShuffled() { return isShuffled; }
    public boolean isRepeat() { return isRepeat; }

    public int getCurrentPosition() {
        return mediaPlayer != null ? mediaPlayer.getCurrentPosition() : 0;
    }

    public int getDuration() {
        return mediaPlayer != null ? mediaPlayer.getDuration() : 0;
    }

    public void release() {
        handler.removeCallbacksAndMessages(null);
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}