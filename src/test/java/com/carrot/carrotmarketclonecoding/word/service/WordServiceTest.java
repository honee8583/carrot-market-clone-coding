package com.carrot.carrotmarketclonecoding.word.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.carrot.carrotmarketclonecoding.common.exception.MemberNotFoundException;
import com.carrot.carrotmarketclonecoding.common.response.FailedMessage;
import com.carrot.carrotmarketclonecoding.member.domain.Member;
import com.carrot.carrotmarketclonecoding.member.repository.MemberRepository;
import com.carrot.carrotmarketclonecoding.word.domain.Word;
import com.carrot.carrotmarketclonecoding.word.dto.WordRequestDto.WordRegisterRequestDto;
import com.carrot.carrotmarketclonecoding.word.repository.WordRepository;
import com.carrot.carrotmarketclonecoding.word.service.impl.WordServiceImpl;
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
                    .hasMessage(FailedMessage.MEMBER_NOT_FOUND.getMessage());
        }
    }
}