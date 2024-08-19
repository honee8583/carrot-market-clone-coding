package com.carrot.carrotmarketclonecoding.board.dto;

import com.carrot.carrotmarketclonecoding.board.domain.Board;
import com.carrot.carrotmarketclonecoding.board.domain.BoardPicture;
import com.carrot.carrotmarketclonecoding.board.domain.enums.Method;
import com.carrot.carrotmarketclonecoding.board.domain.enums.Status;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

public class BoardResponseDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @ToString
    public static class BoardDetailResponseDto {
        private Long id;
        private String writer;
        private String place;
        private String profileUrl;
        private Status status;
        private String title;
        private String category;
        private Method method;
        private int price;
        private Boolean suggest;
        private LocalDateTime createDate;
        private String description;
        private List<PictureResponseDto> pictures = new ArrayList<>();
        private int chat;
        private int like;
        private int visit;

        public static BoardDetailResponseDto createBoardDetail(Board board, int like) {
            return BoardDetailResponseDto.builder()
                    .id(board.getId())
                    .writer(board.getMember().getNickname())
                    .place(board.getPlace())
                    .profileUrl(board.getMember().getProfileUrl())
                    .status(board.getStatus())
                    .title(board.getTitle())
                    .category(board.getCategory().getName())
                    .method(board.getMethod())
                    .price(board.getPrice())
                    .suggest(board.getSuggest())
                    .createDate(board.getCreateDate())
                    .description(board.getDescription())
                    .pictures(board.getBoardPictures().stream()
                            .map(PictureResponseDto::createPictureDetail)
                            .collect(Collectors.toList()))
                    .like(like)
                    .visit(board.getVisit())
                    .build();
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @ToString
    public static class PictureResponseDto {
        private Long id;
        private String pictureUrl;

        public static PictureResponseDto createPictureDetail(BoardPicture boardPicture) {
            return PictureResponseDto.builder()
                    .id(boardPicture.getId())
                    .pictureUrl(boardPicture.getPictureUrl())
                    .build();
        }
    }
}
