package com.om.Real_Time_Communication.service;

import com.om.Real_Time_Communication.dto.AckDto;
import com.om.Real_Time_Communication.dto.ChatSendDto;
import com.om.Real_Time_Communication.models.ChatMessage;
import com.om.Real_Time_Communication.presence.PerRoomDispatcher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderedMessageService {
    private final PerRoomDispatcher dispatcher;
    private final MessageService messageService; // existing service doing DB + fanout
    private final SimpMessagingTemplate messagingTemplate;

    public OrderedMessageService(PerRoomDispatcher dispatcher, MessageService messageService, SimpMessagingTemplate messagingTemplate) {
        this.dispatcher = dispatcher;
        this.messageService = messageService;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Strict FIFO: persist then ACK the sender and broadcast to the room in order.
     * Runs the work on a per-room executor to preserve message ordering.
     */
    public void saveAndBroadcastOrdered(Long roomId, Long senderId, ChatSendDto dto) throws Exception {
        dispatcher.executeAndWait(roomId, () -> {
            // Keep the real work transactional

            ChatMessage saved = messageService.saveInbound(roomId, senderId, dto);
            messagingTemplate.convertAndSendToUser(
                    String.valueOf(senderId),
                    "/queue/ack",
                    new AckDto(roomId, saved.getMessageId(), saved.getServerTs())
            );

            // Broadcast the message event to the room topic for other subscribers
            Map<String, Object> event = messageService.toRoomEvent(saved);
            messagingTemplate.convertAndSend("/topic/room." + roomId, event);

            return null; // required by Callable
        });
    }
}
