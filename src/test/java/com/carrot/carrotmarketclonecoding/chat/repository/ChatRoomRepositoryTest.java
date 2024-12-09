package com.carrot.carrotmarketclonecoding.chat.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.carrot.carrotmarketclonecoding.RepositoryTest;
import com.carrot.carrotmarketclonecoding.board.domain.Board;
import com.carrot.carrotmarketclonecoding.chat.domain.ChatRoom;
import com.carrot.carrotmarketclonecoding.member.domain.Member;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class ChatRoomRepositoryTest extends RepositoryTest {

    @Test
    void findAllBySenderTest() {
        // given
        Member sender = Member.builder()
                .authId(1111L)
                .build();
        ChatRoom chatRoom1 = ChatRoom.builder()
                .sender(sender)
                .build();
        ChatRoom chatRoom2 = ChatRoom.builder()
                .sender(sender)
                .build();
        memberRepository.save(sender);
        chatRoomRepository.save(chatRoom1);
        chatRoomRepository.save(chatRoom2);

        // when
        List<ChatRoom> chatRooms = chatRoomRepository.findAllBySender(sender);

        // then
        assertThat(chatRooms.size()).isEqualTo(2);
    }

    @Test
    void findAllByReceiverTest() {
        // given
        Member receiver = Member.builder()
                .authId(1111L)
                .build();
        ChatRoom chatRoom1 = ChatRoom.builder()
                .receiver(receiver)
                .build();
        ChatRoom chatRoom2 = ChatRoom.builder()
                .receiver(receiver)
                .build();
        memberRepository.save(receiver);
        chatRoomRepository.save(chatRoom1);
        chatRoomRepository.save(chatRoom2);

        // when
        List<ChatRoom> chatRooms = chatRoomRepository.findAllByReceiver(receiver);

        // then
        assertThat(chatRooms.size()).isEqualTo(2);
    }

    @Test
    void findByRoomNumTest() {
        // given
        String roomNum = "test room";
        ChatRoom chatRoom = ChatRoom.builder()
                .roomNum(roomNum)
                .build();
        chatRoomRepository.save(chatRoom);

        // when
        Optional<ChatRoom> result = chatRoomRepository.findByRoomNum(roomNum);

        // then
        assertThat(result.isPresent()).isEqualTo(true);
    }

    @Test
    void findByBoardTest() {
        // given
        Board board = Board.builder().build();
        boardRepository.save(board);

        Member receiver = Member.builder().build();
        memberRepository.save(receiver);

        ChatRoom chatRoom = ChatRoom.builder()
                .roomNum("test room")
                .board(board)
                .receiver(receiver)
                .build();
        chatRoomRepository.save(chatRoom);

        // when
        List<ChatRoom> result = chatRoomRepository.findByBoard(board);

        // then
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getRoomNum()).isEqualTo("test room");
    }
}