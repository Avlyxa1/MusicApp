package com.example.musicapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.R;
import com.example.musicapp.models.Song;

import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {

    public interface OnSongClickListener {
        void onSongClick(Song song, int position);
        void onMoreClick(Song song, int position, View view);
    }

    private List<Song> songs;
    private OnSongClickListener listener;
    private int currentPlayingId = -1;

    public SongAdapter(List<Song> songs, OnSongClickListener listener) {
        this.songs = songs;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        Song song = songs.get(position);
        holder.bind(song, position);
    }

    @Override
    public int getItemCount() { return songs.size(); }

    public void updateSongs(List<Song> newSongs) {
        this.songs = newSongs;
        notifyDataSetChanged();
    }

    public void setCurrentPlayingId(int id) {
        this.currentPlayingId = id;
        notifyDataSetChanged();
    }

    class SongViewHolder extends RecyclerView.ViewHolder {
        ImageView ivThumbnail;
        TextView tvTitle, tvArtist, tvDuration;
        ImageButton btnMore;
        ImageView ivPlaying;

        SongViewHolder(View itemView) {
            super(itemView);
            ivThumbnail = itemView.findViewById(R.id.ivThumbnail);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvArtist = itemView.findViewById(R.id.tvArtist);
            tvDuration = itemView.findViewById(R.id.tvDuration);
            btnMore = itemView.findViewById(R.id.btnMore);
            ivPlaying = itemView.findViewById(R.id.ivPlaying);
        }

        void bind(Song song, int position) {
            tvTitle.setText(song.getTitle());
            tvArtist.setText(song.getArtist());
            tvDuration.setText(song.getFormattedDuration());
            ivThumbnail.setImageResource(R.drawable.thumb_song);

            boolean isPlaying = song.getId() == currentPlayingId;
            ivPlaying.setVisibility(isPlaying ? View.VISIBLE : View.GONE);
            tvTitle.setTextColor(itemView.getContext().getResources().getColor(
                    isPlaying ? R.color.colorAccent : R.color.textPrimary, null));

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onSongClick(song, position);
            });

            btnMore.setOnClickListener(v -> {
                if (listener != null) listener.onMoreClick(song, position, v);
            });
        }
    }
}