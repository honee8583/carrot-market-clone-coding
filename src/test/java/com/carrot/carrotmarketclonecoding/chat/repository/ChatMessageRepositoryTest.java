package com.carrot.carrotmarketclonecoding.chat.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.carrot.carrotmarketclonecoding.MongoTestContainerTest;
import com.carrot.carrotmarketclonecoding.chat.domain.ChatMessage;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ChatMessageRepositoryTest extends MongoTestContainerTest {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Test
    void deleteAllByRoomNumTest() {
        // given
        String roomNum = "test room";
        ChatMessage chatMessage = ChatMessage.builder()
                .senderId(1111L)
                .receiverId(2222L)
                .roomNum(roomNum)
                .message("test message")
                .createDate(LocalDateTime.now())
                .build();
        chatMessageRepository.save(chatMessage);

        // when
        chatMessageRepository.deleteAllByRoomNum(roomNum);

        // then
        List<ChatMessage> result = chatMessageRepository.findAll();
        assertThat(result.size()).isEqualTo(0);
    }

    @Test
    void findAllByRoomNumTest() {
        // given
        String roomNum = "test room";
        ChatMessage chatMessage1 = ChatMessage.builder()
                .senderId(1111L)
                .receiverId(2222L)
                .roomNum(roomNum)
                .message("test message")
                .createDate(LocalDateTime.now().minusDays(1))
                .build();
        ChatMessage chatMessage2 = ChatMessage.builder()
                .senderId(1111L)
                .receiverId(2222L)
                .roomNum(roomNum)
                .message("test message2")
                .createDate(LocalDateTime.now())
                .build();
        chatMessageRepository.save(chatMessage1);
        chatMessageRepository.save(chatMessage2);

        // when
        List<ChatMessage> result = chatMessageRepository.findAllByRoomNumOrderByCreateDateDesc(roomNum);

        // then
        assertThat(result.get(0).getMessage()).isEqualTo("test message2");
        assertThat(result.get(1).getMessage()).isEqualTo("test message");
        assertThat(result.size()).isEqualTo(2);
    }
}