package com.carrot.carrotmarketclonecoding.board.service;

import com.carrot.carrotmarketclonecoding.board.domain.Board;
import com.carrot.carrotmarketclonecoding.board.dto.BoardRequestDto.BoardRegisterRequestDto;

public interface BoardNotificationService {
    void sendKeywordNotification(BoardRegisterRequestDto registerRequestDto, Board board);
}
