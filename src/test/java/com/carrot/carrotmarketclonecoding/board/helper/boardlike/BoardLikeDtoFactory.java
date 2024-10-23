package com.carrot.carrotmarketclonecoding.board.helper.boardlike;

import com.carrot.carrotmarketclonecoding.board.dto.BoardResponseDto.BoardSearchResponseDto;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.boot.test.context.TestComponent;

@TestComponent
public class BoardLikeDtoFactory {

    public List<BoardSearchResponseDto> createBoardSearchResponseDtos() {
        return new ArrayList<>(Arrays.asList(
                BoardSearchResponseDto.builder()
                        .id(1L)
                        .title("title")
                        .pictureUrl("S3 Picture Url")
                        .like(20)
                        .price(20000)
                        .place("Amsa")
                        .createDate(LocalDateTime.now())
                        .build(),
                BoardSearchResponseDto.builder()
                        .id(2L)
                        .title("title2")
                        .pictureUrl("S3 Picture Url2")
                        .like(21)
                        .price(40000)
                        .place("Cheonho")
                        .createDate(LocalDateTime.now())
                        .build()));
    }
}
