package com.example.jean.jcplayer;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by jean on 27/06/16.
 */

public class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.AudioAdapterViewHolder> implements View.OnClickListener{
    private Context context;
    private JCPlayerView activity;
    private List<JCAudio> JCAudioList;

    public AudioAdapter(JCPlayerView activity) {
        this.activity = activity;
        this.context = activity.getContext();
    }


    @Override
    public AudioAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.audio_item, parent, false);
        AudioAdapterViewHolder audiosViewHolder = new AudioAdapterViewHolder(view);
        audiosViewHolder.itemView.setOnClickListener(this);
        return audiosViewHolder;
    }

    @Override
    public void onBindViewHolder(AudioAdapterViewHolder holder, int position) {
        String title = position+1 + " - " + JCAudioList.get(position).getTitle();
        holder.audioTitle.setText(title);
        holder.itemView.setTag(JCAudioList.get(position));
    }

    @Override
    public int getItemCount() {
        return JCAudioList == null ? 0 : JCAudioList.size();
    }

    public void setupItems(List<JCAudio> JCAudioList) {
        this.JCAudioList = JCAudioList;
        notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        JCAudio JCAudio = (JCAudio) view.getTag();
        activity.playAudio(JCAudio);
    }

    static class AudioAdapterViewHolder extends RecyclerView.ViewHolder{
        private TextView audioTitle;
        private ImageView audioImage;

        public AudioAdapterViewHolder(View view){
            super(view);
            this.audioTitle = (TextView) view.findViewById(R.id.audio_title);
            this.audioImage = (ImageView) view.findViewById(R.id.lock);
        }
    }
}
