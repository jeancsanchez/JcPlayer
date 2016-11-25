package com.example.jean.jcplayersample;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.jean.jcplayer.JcAudio;

import java.util.List;

/**
 * Created by jean on 27/06/16.
 */

public class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.AudioAdapterViewHolder> implements View.OnClickListener{
    private Context context;
    private MainActivity activity;
    private List<JcAudio> jcAudioList;

    public AudioAdapter(MainActivity activity) {
        this.activity = activity;
        this.context = activity;
    }


    @Override
    public AudioAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(com.example.jean.jcplayersample.R.layout.audio_item, parent, false);
        AudioAdapterViewHolder audiosViewHolder = new AudioAdapterViewHolder(view);
        audiosViewHolder.itemView.setOnClickListener(this);
        return audiosViewHolder;
    }

    @Override
    public void onBindViewHolder(AudioAdapterViewHolder holder, int position) {
        String title = position+1 + "    " + jcAudioList.get(position).getTitle();
        holder.audioTitle.setText(title);
        holder.itemView.setTag(jcAudioList.get(position));
    }

    @Override
    public int getItemCount() {
        return jcAudioList == null ? 0 : jcAudioList.size();
    }

    public void setupItems(List<JcAudio> jcAudioList) {
        this.jcAudioList = jcAudioList;
        notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        JcAudio JcAudio = (com.example.jean.jcplayer.JcAudio) view.getTag();
        activity.playAudio(JcAudio);
    }

    static class AudioAdapterViewHolder extends RecyclerView.ViewHolder{
        private TextView audioTitle;

        public AudioAdapterViewHolder(View view){
            super(view);
            this.audioTitle = (TextView) view.findViewById(com.example.jean.jcplayersample.R.id.audio_title);
        }
    }
}
