package com.localapp.models;

import com.localapp.ui.fragments.FeedFragment;

/**
 * Created by Vijay Kumar on 08-08-2017.
 */

public class ReplyMessage {


    public void setTextMessage(String textMessage) {
        this.textMessage = textMessage;
    }

    private String id;
    private String name;
    private String textMessage;
    private FeedFragment.MediaType mediaType;

    public ReplyMessage() {
    }

    public ReplyMessage(String id, String name, String textMessage) {
        this.id = id;
        this.name = name;
        this.textMessage = textMessage;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTextMessage() {
        return textMessage;
    }

    public FeedFragment.MediaType getMediaType() {
        return mediaType;
    }

    public void setMediaType(FeedFragment.MediaType mediaType) {
        this.mediaType = mediaType;
    }

    /**
     * Returns a string representation of the object. In general, the
     * {@code toString} method returns a string that
     * "textually represents" this object. The result should
     * be a concise but informative representation that is easy for a
     * person to read.
     * It is recommended that all subclasses override this method.
     * <p>
     * The {@code toString} method for class {@code Object}
     * returns a string consisting of the name of the class of which the
     * object is an instance, the at-sign character `{@code @}', and
     * the unsigned hexadecimal representation of the hash code of the
     * object. In other words, this method returns a string equal to the
     * value of:
     * <blockquote>
     * <pre>
     * getClass().getName() + '@' + Integer.toHexString(hashCode())
     * </pre></blockquote>
     *
     * @return a string representation of the object.
     */
    /*@Override
    public String toString() {
        StringBuilder s = new StringBuilder("{\"id\":\"" + id + "\",\"name\":\"" + name + "\",\"textMessage\":\"" + textMessage + "\"}");
        return s.toString();
    }*/
}
