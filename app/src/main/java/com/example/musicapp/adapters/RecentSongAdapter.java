package com.example.musicapp.adapters;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.R;
import com.example.musicapp.models.Song;

import java.util.ArrayList;
import java.util.List;

public class RecentSongAdapter extends RecyclerView.Adapter<RecentSongAdapter.ViewHolder> {

    public interface OnSongClickListener {
        void onSongClick(Song song, int position);
    }

    private List<Song> songs;
    private OnSongClickListener listener;
    private List<Song> recentSongs = new ArrayList<>();

    public RecentSongAdapter(List<Song> songs, OnSongClickListener listener) {
        this.songs = songs;
        this.listener = listener;
    }

    int[] songList = {
            R.raw.song1,
            R.raw.song2,
            R.raw.song3,
            R.raw.song4,
            R.raw.song5,
            R.raw.song6,
            R.raw.song7,
            R.raw.song8,
            R.raw.song9,
            R.raw.song10
    };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recent_song, parent, false);
        return new ViewHolder(view);
    }

    private Bitmap getAlbumArtFromRaw(Context context, int rawId) {
        try {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            AssetFileDescriptor afd = context.getResources().openRawResourceFd(rawId);

            mmr.setDataSource(
                    afd.getFileDescriptor(),
                    afd.getStartOffset(),
                    afd.getLength()
            );

            byte[] artBytes = mmr.getEmbeddedPicture();

            if (artBytes != null) {
                return BitmapFactory.decodeByteArray(artBytes, 0, artBytes.length);
            }

            mmr.release();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Song song = songs.get(position);

        holder.tvTitle.setText(String.valueOf(song.getTitle()));
        holder.tvArtist.setText(String.valueOf(song.getArtist()));

        Context context = holder.itemView.getContext();
        int index = song.getId() - 1;
        Bitmap bitmap = getAlbumArtFromRaw(context, songList[index]);

        if (bitmap != null) {
            holder.ivThumbnail.setImageBitmap(bitmap);
        } else {
            holder.ivThumbnail.setImageResource(R.drawable.thumb_song);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onSongClick(song, position);
        });
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public void updateSongs(List<Song> newSongs) {
        this.songs = newSongs;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivThumbnail;
        TextView tvTitle, tvArtist;

        ViewHolder(View itemView) {
            super(itemView);
            ivThumbnail = itemView.findViewById(R.id.ivThumbnail);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvArtist = itemView.findViewById(R.id.tvArtist);
        }
    }
}