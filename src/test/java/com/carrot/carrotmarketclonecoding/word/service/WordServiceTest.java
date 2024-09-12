package com.carrot.carrotmarketclonecoding.word.service;

import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.*;
import static com.carrot.carrotmarketclonecoding.word.displayname.WordTestDisplayNames.MESSAGE.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.carrot.carrotmarketclonecoding.common.exception.MemberNotFoundException;
import com.carrot.carrotmarketclonecoding.common.exception.MemberWordLimitException;
import com.carrot.carrotmarketclonecoding.common.exception.WordNotFoundException;
import com.carrot.carrotmarketclonecoding.member.domain.Member;
import com.carrot.carrotmarketclonecoding.member.repository.MemberRepository;
import com.carrot.carrotmarketclonecoding.word.domain.Word;
import com.carrot.carrotmarketclonecoding.word.dto.WordRequestDto;
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
    @DisplayName(WORD_ADD_SERVICE_TEST)
    class AddWord {

        @Test
        @DisplayName(SUCCESS)
        void addWordSuccess() {
            // given
            Long memberId = 1L;
            Member mockMember = mock(Member.class);
            WordRequestDto wordRequestDto = new WordRequestDto("word");

            when(memberRepository.findByAuthId(anyLong())).thenReturn(Optional.of(mockMember));
            when(wordRepository.countByMember(any())).thenReturn(10);

            // when
            wordService.add(memberId, wordRequestDto);

            // then
            ArgumentCaptor<Word> argumentCaptor = ArgumentCaptor.forClass(Word.class);
            verify(wordRepository).save(argumentCaptor.capture());
            Word word = argumentCaptor.getValue();
            assertThat(word.getWord()).isEqualTo("word");
        }

        @Test
        @DisplayName(FAIL_MEMBER_NOT_FOUND)
        void addWordFailMemberNotFound() {
            // given
            Long memberId = 1L;
            WordRequestDto wordRequestDto = new WordRequestDto("word");

            when(memberRepository.findByAuthId(anyLong())).thenReturn(Optional.empty());

            // when
            // then
            assertThatThrownBy(() -> wordService.add(memberId, wordRequestDto))
                    .isInstanceOf(MemberNotFoundException.class)
                    .hasMessage(MEMBER_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName(FAIL_MEMBER_WORD_OVER_LIMIT)
        void addWordFailMemberWordOverLimit() {
            // given
            Long memberId = 1L;
            Member mockMember = mock(Member.class);
            WordRequestDto wordRequestDto = new WordRequestDto("word");

            when(memberRepository.findByAuthId(anyLong())).thenReturn(Optional.of(mockMember));
            when(wordRepository.countByMember(any())).thenReturn(30);

            // when
            // then
            assertThatThrownBy(() -> wordService.add(memberId, wordRequestDto))
                    .isInstanceOf(MemberWordLimitException.class)
                    .hasMessage(MEMBER_WORD_OVER_LIMIT.getMessage());
        }
    }

    @Nested
    @DisplayName(WORD_LIST_SERVICE_TEST)
    class WordList {

        @Test
        @DisplayName(SUCCESS)
        void getWordsSuccess() {
            // given
            Long memberId = 1L;
            List<Word> words = Arrays.asList(
                    Word.builder().id(1L).word("word1").build(),
                    Word.builder().id(2L).word("word2").build()
            );

            when(memberRepository.findByAuthId(anyLong())).thenReturn(Optional.of(mock(Member.class)));
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
        @DisplayName(FAIL_MEMBER_NOT_FOUND)
        void getWordsFailMemberNotFound() {
            // given
            when(memberRepository.findByAuthId(anyLong())).thenReturn(Optional.empty());

            // when
            // then
            assertThatThrownBy(() -> wordService.list(1L))
                    .isInstanceOf(MemberNotFoundException.class)
                    .hasMessage(MEMBER_NOT_FOUND.getMessage());
        }
    }

    @Nested
    @DisplayName(WORD_UPDATE_SERVICE_TEST)
    class UpdateWord {

        @Test
        @DisplayName(SUCCESS)
        void updateWordSuccess() {
            // given
            Long memberId = 1L;
            Long wordId = 1L;
            Member mockMember = mock(Member.class);
            Word mockWord = Word.builder().word("word1").build();

            when(memberRepository.findByAuthId(anyLong())).thenReturn(Optional.of(mockMember));
            when(wordRepository.findById(anyLong())).thenReturn(Optional.of(mockWord));

            // when
            wordService.update(memberId, wordId, new WordRequestDto("word2"));

            // then
            assertThat(mockWord.getWord()).isEqualTo("word2");
        }

        @Test
        @DisplayName(FAIL_MEMBER_NOT_FOUND)
        void updateWordFailMemberNotFound() {
            // given
            when(memberRepository.findByAuthId(anyLong())).thenReturn(Optional.empty());

            // when
            // then
            assertThatThrownBy(() -> wordService.update(1L, 1L, new WordRequestDto("word")))
                    .isInstanceOf(MemberNotFoundException.class)
                    .hasMessage(MEMBER_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName(FAIL_WORD_NOT_FOUND)
        void updateWordFailWordNotFound() {
            // given
            when(memberRepository.findByAuthId(anyLong())).thenReturn(Optional.of(mock(Member.class)));
            when(wordRepository.findById(anyLong())).thenReturn(Optional.empty());

            // when
            // then
            assertThatThrownBy(() -> wordService.update(1L, 1L, new WordRequestDto("word")))
                    .isInstanceOf(WordNotFoundException.class)
                    .hasMessage(WORD_NOT_FOUND.getMessage());
        }
    }

    @Nested
    @DisplayName(WORD_REMOVE_SERVICE_TEST)
    class RemoveWord {

        @Test
        @DisplayName(SUCCESS)
        void removeWordSuccess() {
            // given
            Long memberId = 1L;
            Long wordId = 1L;
            Member mockMember = mock(Member.class);
            Word mockWord = mock(Word.class);
            when(memberRepository.findByAuthId(anyLong())).thenReturn(Optional.of(mockMember));
            when(wordRepository.findById(anyLong())).thenReturn(Optional.of(mockWord));

            // when
            wordService.remove(memberId, wordId);

            // then
            verify(wordRepository).delete(mockWord);
        }

        @Test
        @DisplayName(FAIL_MEMBER_NOT_FOUND)
        void removeWordFailMemberNotFound() {
            // given
            when(memberRepository.findByAuthId(anyLong())).thenReturn(Optional.empty());

            // when
            // then
            assertThatThrownBy(() -> wordService.remove(1L, 1L))
                    .isInstanceOf(MemberNotFoundException.class)
                    .hasMessage(MEMBER_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName(FAIL_WORD_NOT_FOUND)
        void removeWordFailWordNotFound() {
            // given
            when(memberRepository.findByAuthId(anyLong())).thenReturn(Optional.of(mock(Member.class)));
            when(wordRepository.findById(anyLong())).thenReturn(Optional.empty());

            // when
            // then
            assertThatThrownBy(() -> wordService.remove(1L, 1L))
                    .isInstanceOf(WordNotFoundException.class)
                    .hasMessage(WORD_NOT_FOUND.getMessage());
        }
    }
}