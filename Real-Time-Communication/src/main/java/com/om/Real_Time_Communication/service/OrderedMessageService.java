package com.om.Real_Time_Communication.service;

import com.om.Real_Time_Communication.Repository.ChatRoomRepository;
import com.om.Real_Time_Communication.dto.AckDto;
import com.om.Real_Time_Communication.dto.ChatSendDto;
import com.om.Real_Time_Communication.models.ChatMessage;
import com.om.Real_Time_Communication.models.ChatRoom;
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
    private final ChatRoomRepository chatRoomRepository;

    public OrderedMessageService(PerRoomDispatcher dispatcher, MessageService messageService, SimpMessagingTemplate messagingTemplate, ChatRoomRepository chatRoomRepository) {
        this.dispatcher = dispatcher;
        this.messageService = messageService;
        this.messagingTemplate = messagingTemplate;
        this.chatRoomRepository=chatRoomRepository;
    }

    /**
     * Strict FIFO: persist then ACK the sender and broadcast to the room in order.
     * Runs the work on a per-room executor to preserve message ordering.
     */
    public void saveAndBroadcastOrdered(String roomId, Long senderId, ChatSendDto dto) throws Exception {
        ChatRoom room = chatRoomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));
        Long internalId = room.getId();
        dispatcher.executeAndWait(internalId, () -> {
            // Keep the real work transactional

            ChatMessage saved = messageService.saveInbound(internalId, senderId, dto);
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
