package com.carrot.carrotmarketclonecoding.board.helper.board;

import com.carrot.carrotmarketclonecoding.board.domain.Board;
import com.carrot.carrotmarketclonecoding.board.domain.BoardPicture;
import com.carrot.carrotmarketclonecoding.board.domain.enums.Method;
import com.carrot.carrotmarketclonecoding.board.domain.enums.Status;
import com.carrot.carrotmarketclonecoding.board.dto.BoardRequestDto.BoardRegisterRequestDto;
import com.carrot.carrotmarketclonecoding.board.dto.BoardRequestDto.BoardUpdateRequestDto;
import com.carrot.carrotmarketclonecoding.board.dto.BoardResponseDto.BoardDetailResponseDto;
import com.carrot.carrotmarketclonecoding.board.dto.BoardResponseDto.BoardSearchResponseDto;
import com.carrot.carrotmarketclonecoding.board.dto.validation.BoardRegisterValidationMessage.MESSAGE;
import com.carrot.carrotmarketclonecoding.category.domain.Category;
import com.carrot.carrotmarketclonecoding.common.response.PageResponseDto;
import com.carrot.carrotmarketclonecoding.member.domain.Member;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

@TestComponent
public class BoardDtoFactory {

    public BoardRegisterRequestDto createRegisterRequestDto() {
        return BoardRegisterRequestDto.builder()
                .title("title")
                .categoryId(1L)
                .method(Method.SELL)
                .price(200000)
                .suggest(true)
                .description("description")
                .place("amsa")
                .build();
    }

    public MockMultipartFile createRegisterRequestMultipartFile(BoardRegisterRequestDto registerRequestDto) throws Exception {
        String registerRequestJson = new ObjectMapper().writeValueAsString(registerRequestDto);
        return new MockMultipartFile(
                "registerRequest",
                "registerRequest.json",
                "application/json",
                registerRequestJson.getBytes()
        );
    }

    public Map<String, String> createInputInvalidResponseData() {
            Map<String, String> responseData = new HashMap<>();
            responseData.put("title", MESSAGE.TITLE_NOT_VALID);
            responseData.put("categoryId", MESSAGE.CATEGORY_NOT_VALID);
            responseData.put("method", "잘못된 입력입니다!");
            responseData.put("price", MESSAGE.PRICE_NOT_VALID);
            responseData.put("suggest", MESSAGE.SUGGEST_NOT_VALID);
            responseData.put("description", MESSAGE.DESCRIPTION_NOT_VALID);
            responseData.put("place", MESSAGE.PLACE_NOT_VALID);
            return responseData;
    }

    public MockMultipartFile createUpdateRequest() throws Exception {
        String updateRequestJson = new ObjectMapper().writeValueAsString(createUpdateRequestDto());
        return new MockMultipartFile(
                "updateRequest",
                "updateRequest.json",
                "application/json",
                updateRequestJson.getBytes()
        );
    }

    public BoardUpdateRequestDto createUpdateRequestDto() {
        return BoardUpdateRequestDto.builder()
                .title("Sell My MacBook")
                .categoryId(2L)
                .method(Method.SELL)
                .price(1000000)
                .suggest(false)
                .description("It's my MacBook description")
                .place("Amsa")
                .removePictures(new Long[]{1L, 2L, 3L})
                .build();
    }

    public MockMultipartFile[] createMockMultipartFiles(String paramName, int size) {
        MockMultipartFile[] pictures = new MockMultipartFile[size];
        for (int i = 0; i < size; i++) {
            pictures[i] = new MockMultipartFile(
                    paramName,
                    "picture " + i + ".png",
                    "image/png",
                    ("picture " + i).getBytes());
        };
        return pictures;
    }

    public BoardDetailResponseDto createBoardDetailResponseDto() {
        return BoardDetailResponseDto.builder()
                .id(1L)
                .writer("User1")
                .place("Kangnam")
                .profileUrl("http://S3-ProfileUrl")
                .status(Status.SELL)
                .title("Sell My Keyboards")
                .category("가전기기")
                .method(Method.SELL)
                .price(100000)
                .suggest(false)
                .createDate(LocalDateTime.now())
                .description("This is my keyboard description")
                .pictures(new ArrayList<>())
                .chat(2)
                .like(10)
                .visit(100)
                .build();
    }

    public List<BoardSearchResponseDto> createBoardSearchResponseDtos(int size) {
        List<BoardSearchResponseDto> searchResponseDtos = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            searchResponseDtos.add(BoardSearchResponseDto.builder()
                    .id((long) i + 1)
                    .title("Sell My MacBook" + (i + 1))
                    .price(2000000)
                    .place("Amsa")
                    .createDate(LocalDateTime.now())
                    .pictureUrl("S3 Picture Url")
                    .build());
        }
        return searchResponseDtos;
    }

    public PageResponseDto<BoardSearchResponseDto> createBoardSearchResponse(int page, int size, int total, List<BoardSearchResponseDto> searchResponseDtos) {
        return new PageResponseDto<>(new PageImpl<>(searchResponseDtos, PageRequest.of(page, size), total));
    }

    public MultipartFile[] createFiles(int size) {
        return IntStream.range(0, size)
                .mapToObj(i -> new MockMultipartFile(
                        "file" + i,
                        "file" + i + ".png",
                        "text/png",
                        ("Picture" + i).getBytes()
                ))
                .toArray(MultipartFile[]::new);
    }

    public List<BoardPicture> createBoardPictures(int size) {
        List<BoardPicture> boardPictures = new ArrayList<>();
        for (long i = 0; i < size; i++) {
            boardPictures.add(BoardPicture.builder().id(i + 1).build());
        }
        return boardPictures;
    }

    public Board createMockBoard(Long boardId, Member mockMember, Category mockCategory, List<BoardPicture> boardPictures) {
        return Board.builder()
                .id(boardId)
                .title("title")
                .member(mockMember)
                .category(mockCategory)
                .method(Method.SELL)
                .price(20000)
                .suggest(false)
                .description("description")
                .place("place")
                .visit(10)
                .status(Status.SELL)
                .tmp(false)
                .boardPictures(boardPictures)
                .build();
    }
}
