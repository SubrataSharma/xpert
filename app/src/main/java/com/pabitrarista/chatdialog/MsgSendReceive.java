package com.pabitrarista.chatdialog;

public class MsgSendReceive {
    public final static String MSG_TYPE_SENT = "MSG_TYPE_SENT";

    public final static String MSG_TYPE_RECEIVED = "MSG_TYPE_RECEIVED";

    public final static String MSG_TYPE_IMAGE = "MSG_TYPE_IMAGE";

    public final static String MSG_TYPE_VIDEO = "MSG_TYPE_VIDEO";

    // Video start time
    private int startSeconds;

    // Message content.
    private String msgContent;

    // Message type.
    private String msgType;

    public MsgSendReceive(String msgType, String msgContent) {
        this.msgType = msgType;
        this.msgContent = msgContent;
    }

    public String getMsgContent() {
        return msgContent;
    }

    public void setMsgContent(String msgContent) {
        this.msgContent = msgContent;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public void setStartSeconds(int startSeconds) {
        this.startSeconds = startSeconds;
    }

    public int getStartSeconds() {
        return startSeconds;
    }
}
