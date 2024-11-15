package com.carrot.carrotmarketclonecoding.chat.domain;

import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

@Builder
@Getter
@ToString
@Document(collection = "chat_message")
public class ChatMessage {

    @Id
    private String id;
    private Long senderId;
    private Long receiverId;
    private String message;
    private String roomNum;
    private LocalDateTime createDate;
}
