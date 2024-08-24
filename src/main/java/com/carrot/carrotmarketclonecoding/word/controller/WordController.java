package com.carrot.carrotmarketclonecoding.word.controller;

import static com.carrot.carrotmarketclonecoding.common.response.SuccessMessage.*;

import com.carrot.carrotmarketclonecoding.common.response.ResponseResult;
import com.carrot.carrotmarketclonecoding.word.dto.WordRequestDto;
import com.carrot.carrotmarketclonecoding.word.dto.WordResponseDto.WordListResponseDto;
import com.carrot.carrotmarketclonecoding.word.service.WordService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/word")
@RequiredArgsConstructor
public class WordController {
    private final WordService wordService;

    @PostMapping
    public ResponseEntity<?> add(@RequestBody @Valid WordRequestDto wordRequestDto) {
        // TODO memberId -> JWT.getMemberId()
        Long memberId = 1L;
        wordService.add(memberId, wordRequestDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ResponseResult.success(HttpStatus.CREATED, ADD_WORD_SUCCESS.getMessage(), null));
    }

    @GetMapping
    public ResponseEntity<?> list() {
        // TODO memberId -> JWT.getMemberId()
        Long memberId = 1L;
        List<WordListResponseDto> words = wordService.list(memberId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseResult.success(HttpStatus.OK, GET_MEMBER_WORDS.getMessage(), words));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") Long wordId, @RequestBody WordRequestDto wordRequestDto) {
        // TODO memberId -> JWT.getMemberId()
        Long memberId = 1L;
        wordService.update(memberId, wordId, wordRequestDto);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseResult.success(HttpStatus.OK, UPDATE_WORD_SUCCESS.getMessage(), null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> remove(@PathVariable("id") Long wordId) {
        // TODO memberId -> JWT.getMemberId()
        Long memberId = 1L;
        wordService.remove(memberId, wordId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseResult.success(HttpStatus.OK, REMOVE_WORD_SUCCESS.getMessage(), null));
    }
}
