package com.example.app;

import androidx.leanback.widget.AbstractDetailsDescriptionPresenter;

import com.example.app.video.Video;

public class DetailsDescriptionPresenter extends AbstractDetailsDescriptionPresenter {

    @Override
    protected void onBindDescription(ViewHolder viewHolder, Object item) {
        Video video = (Video) item;

        if (video != null) {
            viewHolder.getTitle().setText(video.getTitle());
            viewHolder.getSubtitle().setText(video.getStudio());
            viewHolder.getBody().setText(video.getDescription());
        }
    }
}
