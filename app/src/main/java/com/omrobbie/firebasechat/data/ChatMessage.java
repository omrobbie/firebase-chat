package com.omrobbie.firebasechat.data;

import java.util.Date;

/**
 * Created by omrobbie on 23/12/2017.
 */

public class ChatMessage {

    private String messageUser;
    private String messageText;
    private long messageTime;

    public ChatMessage() {
    }

    public ChatMessage(String messageUser, String messageText) {
        this.messageUser = messageUser;
        this.messageText = messageText;
        this.messageTime = new Date().getTime();
    }

    public String getMessageUser() {
        return messageUser;
    }

    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }
}
