package com.example.musicapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.R;
import com.example.musicapp.models.Song;

import java.util.List;

public class QueueAdapter extends RecyclerView.Adapter<QueueAdapter.QueueViewHolder> {

    public interface OnQueueItemListener {
        void onRemoveClick(int position);
        void onItemClick(int position);
    }

    private List<Song> queue;
    private int currentIndex;
    private OnQueueItemListener listener;

    public QueueAdapter(List<Song> queue, int currentIndex, OnQueueItemListener listener) {
        this.queue = queue;
        this.currentIndex = currentIndex;
        this.listener = listener;
    }

    @NonNull
    @Override
    public QueueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_queue, parent, false);
        return new QueueViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QueueViewHolder holder, int position) {
        Song song = queue.get(position);
        holder.bind(song, position);
    }

    @Override
    public int getItemCount() { return queue.size(); }

    public void updateQueue(List<Song> newQueue, int newCurrentIndex) {
        this.queue = newQueue;
        this.currentIndex = newCurrentIndex;
        notifyDataSetChanged();
    }

    class QueueViewHolder extends RecyclerView.ViewHolder {
        ImageView ivThumbnail;
        TextView tvTitle, tvArtist, tvPosition;
        ImageView ivRemove, ivNowPlaying;

        QueueViewHolder(View itemView) {
            super(itemView);
            ivThumbnail = itemView.findViewById(R.id.ivThumbnail);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvArtist = itemView.findViewById(R.id.tvArtist);
            tvPosition = itemView.findViewById(R.id.tvPosition);
            ivRemove = itemView.findViewById(R.id.ivRemove);
            ivNowPlaying = itemView.findViewById(R.id.ivNowPlaying);
        }

        void bind(Song song, int position) {
            tvTitle.setText(song.getTitle());
            tvArtist.setText(song.getArtist());
            tvPosition.setText(String.valueOf(position + 1));
            ivThumbnail.setImageResource(R.drawable.thumb_song);

            boolean isCurrent = position == currentIndex;
            ivNowPlaying.setVisibility(isCurrent ? View.VISIBLE : View.GONE);
            tvTitle.setTextColor(itemView.getContext().getResources().getColor(
                    isCurrent ? R.color.colorAccent : R.color.textPrimary, null));

            ivRemove.setOnClickListener(v -> {
                if (listener != null) listener.onRemoveClick(position);
            });

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onItemClick(position);
            });
        }
    }
}