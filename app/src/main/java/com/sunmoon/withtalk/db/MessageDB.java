package com.sunmoon.withtalk.db;

public class MessageDB {//내부디비 저장용
    private int seqNo;
    private String senderId;
    private String contents;
    private String sendTime;
    private int chatRoomNo;
    private String isRead;

    public MessageDB() {

    }

    public MessageDB(int seqNo, String senderId, String contents, String sendTime, int chatRoomNo, String isRead) {
        this.seqNo = seqNo;
        this.senderId = senderId;
        this.contents = contents;
        this.sendTime = sendTime;
        this.chatRoomNo = chatRoomNo;
        this.isRead = isRead;
    }

    public int getSeqNo() {
        return seqNo;
    }

    public void setSeqNo(int seqNo) {
        this.seqNo = seqNo;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public int getChatRoomNo() {
        return chatRoomNo;
    }

    public void setChatRoomNo(int chatRoomNo) {
        this.chatRoomNo = chatRoomNo;
    }

    public String getIsRead() {
        return isRead;
    }

    public void setIsRead(String isRead) {
        this.isRead = isRead;
    }

    @Override
    public String toString() {
        return "MessageDB{" +
                "seqNo=" + seqNo +
                ", senderId='" + senderId + '\'' +
                ", contents='" + contents + '\'' +
                ", sendTime='" + sendTime + '\'' +
                ", chatRoomNo=" + chatRoomNo +
                ", isRead='" + isRead + '\'' +
                '}';
    }
}