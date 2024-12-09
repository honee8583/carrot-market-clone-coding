package com.carrot.carrotmarketclonecoding.chat.repository;

import com.carrot.carrotmarketclonecoding.board.domain.Board;
import com.carrot.carrotmarketclonecoding.chat.domain.ChatRoom;
import com.carrot.carrotmarketclonecoding.member.domain.Member;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    List<ChatRoom> findAllBySender(Member sender);

    List<ChatRoom> findAllByReceiver(Member receiver);

    Optional<ChatRoom> findByRoomNum(String roomNum);

    List<ChatRoom> findByBoard(Board board);
}
