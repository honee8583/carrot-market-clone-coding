package com.carrot.carrotmarketclonecoding.word.service;

import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.MEMBER_NOT_FOUND;
import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.MEMBER_WORD_OVER_LIMIT;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.carrot.carrotmarketclonecoding.common.exception.MemberNotFoundException;
import com.carrot.carrotmarketclonecoding.common.exception.MemberWordLimitException;
import com.carrot.carrotmarketclonecoding.member.domain.Member;
import com.carrot.carrotmarketclonecoding.member.repository.MemberRepository;
import com.carrot.carrotmarketclonecoding.word.domain.Word;
import com.carrot.carrotmarketclonecoding.word.dto.WordRequestDto.WordRegisterRequestDto;
import com.carrot.carrotmarketclonecoding.word.dto.WordResponseDto.WordListResponseDto;
import com.carrot.carrotmarketclonecoding.word.repository.WordRepository;
import com.carrot.carrotmarketclonecoding.word.service.impl.WordServiceImpl;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WordServiceTest {

    @Mock
    private WordRepository wordRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private WordServiceImpl wordService;

    @Nested
    @DisplayName("자주쓰는문구 추가 서비스 테스트")
    class AddWord {

        @Test
        @DisplayName("성공")
        void addWordSuccess() {
            // given
            Long memberId = 1L;
            Member mockMember = mock(Member.class);
            WordRegisterRequestDto registerRequestDto = new WordRegisterRequestDto("word");

            when(memberRepository.findById(anyLong())).thenReturn(Optional.of(mockMember));
            when(wordRepository.countByMember(any())).thenReturn(10);

            // when
            wordService.add(memberId, registerRequestDto);

            // then
            ArgumentCaptor<Word> argumentCaptor = ArgumentCaptor.forClass(Word.class);
            verify(wordRepository).save(argumentCaptor.capture());
            Word word = argumentCaptor.getValue();
            assertThat(word.getWord()).isEqualTo("word");
        }

        @Test
        @DisplayName("실패 - 사용자가 존재하지 않음")
        void addWordFailMemberNotFound() {
            // given
            Long memberId = 1L;
            WordRegisterRequestDto registerRequestDto = new WordRegisterRequestDto("word");

            when(memberRepository.findById(anyLong())).thenReturn(Optional.empty());

            // when
            // then
            assertThatThrownBy(() -> wordService.add(memberId, registerRequestDto))
                    .isInstanceOf(MemberNotFoundException.class)
                    .hasMessage(MEMBER_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("실패 - 자주쓰는문구의 개수가 30개를 초과함")
        void addWordFailMemberWordOverLimit() {
            // given
            Long memberId = 1L;
            Member mockMember = mock(Member.class);
            WordRegisterRequestDto registerRequestDto = new WordRegisterRequestDto("word");

            when(memberRepository.findById(anyLong())).thenReturn(Optional.of(mockMember));
            when(wordRepository.countByMember(any())).thenReturn(30);

            // when
            // then
            assertThatThrownBy(() -> wordService.add(memberId, registerRequestDto))
                    .isInstanceOf(MemberWordLimitException.class)
                    .hasMessage(MEMBER_WORD_OVER_LIMIT.getMessage());
        }
    }

    @Nested
    @DisplayName("자주쓰는문구 목록 조회 서비스 테스트")
    class WordList {

        @Test
        @DisplayName("성공")
        void getWordsSuccess() {
            // given
            Long memberId = 1L;
            List<Word> words = Arrays.asList(
                    Word.builder().id(1L).word("word1").build(),
                    Word.builder().id(2L).word("word2").build()
            );

            when(memberRepository.findById(anyLong())).thenReturn(Optional.of(mock(Member.class)));
            when(wordRepository.findAllByMember(any())).thenReturn(words);

            // when
            List<WordListResponseDto> result = wordService.list(memberId);

            // then
            assertThat(result.size()).isEqualTo(2);
            assertThat(result.get(0).getId()).isEqualTo(1L);
            assertThat(result.get(1).getId()).isEqualTo(2L);
            assertThat(result.get(0).getWord()).isEqualTo("word1");
            assertThat(result.get(1).getWord()).isEqualTo("word2");
        }

        @Test
        @DisplayName("실패 - 사용자가 존재하지 않음")
        void getWordsFailMemberNotFound() {
            // given
            Long memberId = 1L;

            when(memberRepository.findById(anyLong())).thenReturn(Optional.empty());

            // when
            // then
            assertThatThrownBy(() -> wordService.list(memberId))
                    .isInstanceOf(MemberNotFoundException.class)
                    .hasMessage(MEMBER_NOT_FOUND.getMessage());
        }
    }
}