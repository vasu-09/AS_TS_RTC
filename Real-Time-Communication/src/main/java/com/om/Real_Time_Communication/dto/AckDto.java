package com.om.Real_Time_Communication.dto;

import java.time.Instant;

public class AckDto {
    private Long roomId;
    private String messageId;
    private Instant serverTs;

    public AckDto(Long roomId, String messageId, Instant serverTs) {
        this.roomId = roomId; this.messageId = messageId; this.serverTs = serverTs;
    }
    public Long getRoomId() { return roomId; }
    public String getMessageId() { return messageId; }
    public Instant getServerTs() { return serverTs; }
}
