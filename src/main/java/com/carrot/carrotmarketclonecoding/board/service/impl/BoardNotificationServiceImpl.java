package com.carrot.carrotmarketclonecoding.board.service.impl;

import com.carrot.carrotmarketclonecoding.board.domain.Board;
import com.carrot.carrotmarketclonecoding.board.dto.BoardRequestDto.BoardRegisterRequestDto;
import com.carrot.carrotmarketclonecoding.board.dto.BoardResponseDto.BoardNotificationResponseDto;
import com.carrot.carrotmarketclonecoding.board.service.BoardNotificationService;
import com.carrot.carrotmarketclonecoding.keyword.domain.Keyword;
import com.carrot.carrotmarketclonecoding.keyword.repository.KeywordRepository;
import com.carrot.carrotmarketclonecoding.notification.domain.enums.NotificationType;
import com.carrot.carrotmarketclonecoding.notification.service.NotificationService;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BoardNotificationServiceImpl implements BoardNotificationService {

    private final KeywordRepository keywordRepository;
    private final NotificationService notificationService;

    @Override
    public void sendKeywordNotification(BoardRegisterRequestDto registerRequestDto, Board board) {
        Set<String> wordsNotDuplicated = getTitleAndDescriptionNotDuplicated(registerRequestDto);
        Set<Keyword> keywords = keywordRepository.findByNameIn(wordsNotDuplicated);
        BoardNotificationResponseDto notification = new BoardNotificationResponseDto(board);
        sendBoardNotificationResponseDto(keywords, notification);
    }

    private Set<String> getTitleAndDescriptionNotDuplicated(BoardRegisterRequestDto registerRequestDto) {
        String[] words = (registerRequestDto.getTitle() + " " + registerRequestDto.getDescription()).split(" ");
        return new HashSet<>(List.of(words));
    }

    private void sendBoardNotificationResponseDto(Set<Keyword> keywords, BoardNotificationResponseDto notification) {
        for (Keyword keyword : keywords) {
            notificationService.add(keyword.getMember().getAuthId(), NotificationType.NOTICE, notification);
        }
    }
}
