package com.l2jserver.datapack.autobots.models;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class BotChat {
    private final ChatType chatType;
    private final String senderName;
    private final String message;
    private final long createdDate;

    public BotChat(ChatType chatType, String senderName, String message) {
        this(chatType,senderName,message, System.currentTimeMillis());
    }
    public BotChat(ChatType chatType, String senderName, String message, long createdDate) {
        this.chatType = chatType;
        this.senderName = senderName;
        this.message = message;
        this.createdDate = createdDate;
    }

    @NotNull
    public final BotChat copy(@NotNull ChatType chatType, @NotNull String senderName, @NotNull String message, long createdDate) {
        return new BotChat(chatType, senderName, message, createdDate);
    }

    public final ChatType getChatType() {
        return this.chatType;
    }

    public final String getSenderName() {
        return this.senderName;
    }

    public final String getMessage() {
        return this.message;
    }

    public final long getCreatedDate() {
        return this.createdDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BotChat botChat = (BotChat) o;
        return createdDate == botChat.createdDate && chatType == botChat.chatType && Objects.equals(senderName, botChat.senderName) && Objects.equals(message, botChat.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chatType, senderName, message, createdDate);
    }

    @Override
    public String toString() {
        return "BotChat{" +
                "chatType=" + chatType +
                ", senderName='" + senderName + '\'' +
                ", message='" + message + '\'' +
                ", createdDate=" + createdDate +
                '}';
    }
}
