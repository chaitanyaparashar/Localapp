package com.fourway.localapp.data;

/**
 * Created by 4 way on 23-03-2017.
 */

public class NoticeBoardMessage {
    private String id;
    private String adminId;
    private String msg;
    private String imgUrl;
    private String timestamp;

    public NoticeBoardMessage() {
    }

    public NoticeBoardMessage(String adminId, String msg) {
        this.adminId = adminId;
        this.msg = msg;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
