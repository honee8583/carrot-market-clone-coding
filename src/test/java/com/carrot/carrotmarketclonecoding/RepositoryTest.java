package com.carrot.carrotmarketclonecoding;

import com.carrot.carrotmarketclonecoding.board.repository.BoardLikeRepository;
import com.carrot.carrotmarketclonecoding.board.repository.BoardPictureRepository;
import com.carrot.carrotmarketclonecoding.board.repository.BoardRepository;
import com.carrot.carrotmarketclonecoding.chat.repository.ChatRoomRepository;
import com.carrot.carrotmarketclonecoding.config.JpaAuditingConfig;
import com.carrot.carrotmarketclonecoding.keyword.repository.KeywordRepository;
import com.carrot.carrotmarketclonecoding.member.repository.MemberRepository;
import com.carrot.carrotmarketclonecoding.notification.repository.NotificationRepository;
import com.carrot.carrotmarketclonecoding.word.repository.WordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@Import(value = {
        JpaAuditingConfig.class
})
@ActiveProfiles("test")
@DataJpaTest
public class RepositoryTest {

    @Autowired
    protected BoardRepository boardRepository;

    @Autowired
    protected BoardLikeRepository boardLikeRepository;

    @Autowired
    protected BoardPictureRepository boardPictureRepository;

    @Autowired
    protected MemberRepository memberRepository;

    @Autowired
    protected ChatRoomRepository chatRoomRepository;

    @Autowired
    protected KeywordRepository keywordRepository;

    @Autowired
    protected NotificationRepository notificationRepository;

    @Autowired
    protected WordRepository wordRepository;

}
