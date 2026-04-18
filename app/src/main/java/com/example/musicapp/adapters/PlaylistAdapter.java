package com.example.musicapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.R;
import com.example.musicapp.models.Playlist;

import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder> {

    public interface OnPlaylistClickListener {
        void onPlaylistClick(Playlist playlist);
        void onPlaylistLongClick(Playlist playlist);
    }

    private List<Playlist> playlists;
    private OnPlaylistClickListener listener;

    public PlaylistAdapter(List<Playlist> playlists, OnPlaylistClickListener listener) {
        this.playlists = playlists;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_playlist, parent, false);
        return new PlaylistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistViewHolder holder, int position) {
        Playlist playlist = playlists.get(position);
        holder.bind(playlist);
    }

    @Override
    public int getItemCount() { return playlists.size(); }

    public void updatePlaylists(List<Playlist> newPlaylists) {
        this.playlists = newPlaylists;
        notifyDataSetChanged();
    }

    class PlaylistViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPlaylistCover;
        TextView tvPlaylistName, tvSongCount;

        PlaylistViewHolder(View itemView) {
            super(itemView);
            ivPlaylistCover = itemView.findViewById(R.id.ivPlaylistCover);
            tvPlaylistName = itemView.findViewById(R.id.tvPlaylistName);
            tvSongCount = itemView.findViewById(R.id.tvSongCount);
        }

        void bind(Playlist playlist) {
            tvPlaylistName.setText(playlist.getName());
            tvSongCount.setText(playlist.getSongCount() + " songs");
            ivPlaylistCover.setImageResource(R.drawable.thumb_song);

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onPlaylistClick(playlist);
            });

            itemView.setOnLongClickListener(v -> {
                if (listener != null) listener.onPlaylistLongClick(playlist);
                return true;
            });
        }
    }
}