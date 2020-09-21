package com.example.app.video;

import java.util.ArrayList;
import java.util.List;

public class VideoList {

    private List<Video> list;

    public List<Video> getVideosList() {
        if (list == null) {
            list = setupVideos();
        }
        return list;
    }

    public List<Video> setupVideos() {
        Video video = new Video();
        video.setVideoUrl("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4");
        video.setTitle("Big Buck Bunny");
        video.setDescription("Big Buck Bunny tells the story of a giant rabbit with a heart bigger than himself. When one sunny day three rodents rudely harass him, something snaps... and the rabbit ain't no bunny anymore! In the typical cartoon tradition he prepares the nasty rodents a comical revenge.\\n\\nLicensed under the Creative Commons Attribution license\\nhttp://www.bigbuckbunny.org");
        video.setStudio("Example Studio");
        video.setCardImageUrl("https://banner2.cleanpng.com/20190730/shy/kisspng-photographic-film-movie-camera-cinema-website-and-mobile-application-development-service-5d3fc924ce3b33.8538265315644613488447.jpg");
        video.setBackgroundImageUrl("https://banner2.cleanpng.com/20190730/shy/kisspng-photographic-film-movie-camera-cinema-website-and-mobile-application-development-service-5d3fc924ce3b33.8538265315644613488447.jpg");
        list = new ArrayList<>();
        list.add(video);
        return list;
    }

}
