package com.localapp.models;

/**
 * Created by 4 way on 21-02-2017.
 */

public class BroadcastRequestData {

    private String text;
    private String mediaURL;
    private String emoji;



    public BroadcastRequestData(String text, String mediaURL, String emoji) {
        this.text = text;
        this.mediaURL = mediaURL;
        this.emoji = emoji;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getMediaURL() {
        return mediaURL;
    }

    public void setMediaURL(String mediaURL) {
        this.mediaURL = mediaURL;
    }

    public String getEmoji() {
        return emoji;
    }

    public void setEmoji(String emoji) {
        this.emoji = emoji;
    }
}
