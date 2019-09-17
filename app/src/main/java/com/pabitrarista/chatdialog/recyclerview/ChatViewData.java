package com.pabitrarista.chatdialog.recyclerview;

public class ChatViewData {
    public final static String MSG_TYPE_SENT = "MSG_TYPE_SENT";
    public final static String MSG_TYPE_RECEIVED = "MSG_TYPE_RECEIVED";
    public final static String MSG_TYPE_IMAGE = "MSG_TYPE_IMAGE";
    public final static String MSG_TYPE_VIDEO = "MSG_TYPE_VIDEO";

    // Message type.
    private String msgType;
    // Message content.
    private String msgContent;
    // Video start time
    private int startSeconds;

    public ChatViewData(String msgType, String msgContent) {
        this.msgType = msgType;
        this.msgContent = msgContent;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public void setMsgContent(String msgContent) {
        this.msgContent = msgContent;
    }

    public void setStartSeconds(int startSeconds) {
        this.startSeconds = startSeconds;
    }

    public String getMsgType() {
        return msgType;
    }

    public String getMsgContent() {
        return msgContent;
    }

    public int getStartSeconds() {
        return startSeconds;
    }
}
