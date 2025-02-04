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

        @Builder.Default
        private List<PictureResponseDto> pictures = new ArrayList<>();
        private int chat;
        private int like;
        private int visit;

        public static BoardDetailResponseDto createBoardDetail(Board board, int like, int chatRoomCnt) {
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
                    .chat(chatRoomCnt)
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

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @ToString
    public static class BoardSearchResponseDto {
        private Long id;
        private String pictureUrl;
        private String title;
        private String place;
        private LocalDateTime createDate;
        private int price;
        private int like;

        public static BoardSearchResponseDto getSearchResult(Board board) {
            return BoardSearchResponseDto.builder()
                    .id(board.getId())
                    .pictureUrl(board.getBoardPictures() != null && board.getBoardPictures().size() > 0 ? board.getBoardPictures().get(0).getPictureUrl() : null)
                    .title(board.getTitle())
                    .place(board.getPlace())
                    .createDate(board.getCreateDate())
                    .price(board.getPrice())
                    .like(board.getBoardLikes().size())
                    .build();
        }
    }

    @Getter
    @Setter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BoardNotificationResponseDto {
        private Long id;
        private String title;
        private Integer price;
        private String place;
        private LocalDateTime createDate;
        private String pictureUrl;

        public BoardNotificationResponseDto(Board board) {
            this.id = board.getId();
            this.title = board.getTitle();
            this.price = board.getPrice();
            this.createDate = board.getCreateDate();

            List<BoardPicture> boardPictures = board.getBoardPictures();
            this.pictureUrl = boardPictures != null && boardPictures.size() > 0 ? boardPictures.get(0).getPictureUrl() : null;
        }
    }
}
