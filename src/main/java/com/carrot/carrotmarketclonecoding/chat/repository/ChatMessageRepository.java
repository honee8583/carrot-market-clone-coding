package com.carrot.carrotmarketclonecoding.chat.repository;

import com.carrot.carrotmarketclonecoding.chat.domain.ChatMessage;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
    void deleteAllByRoomNum(String roomNum);

    List<ChatMessage> findAllByRoomNumOrderByCreateDateDesc(String roomNum);
}
