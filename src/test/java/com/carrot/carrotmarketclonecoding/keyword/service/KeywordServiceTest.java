package com.carrot.carrotmarketclonecoding.keyword.service;

import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.KEYWORD_OVER_LIMIT;
import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.MEMBER_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.carrot.carrotmarketclonecoding.common.exception.KeywordOverLimitException;
import com.carrot.carrotmarketclonecoding.common.exception.MemberNotFoundException;
import com.carrot.carrotmarketclonecoding.keyword.domain.Keyword;
import com.carrot.carrotmarketclonecoding.keyword.dto.KeywordRequestDto.KeywordCreateRequestDto;
import com.carrot.carrotmarketclonecoding.keyword.repository.KeywordRepository;
import com.carrot.carrotmarketclonecoding.keyword.service.impl.KeywordServiceImpl;
import com.carrot.carrotmarketclonecoding.member.domain.Member;
import com.carrot.carrotmarketclonecoding.member.repository.MemberRepository;
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
class KeywordServiceTest {

    @Mock
    private KeywordRepository keywordRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private KeywordServiceImpl keywordService;

    private static final Long AUTH_ID = 1111L;

    @Nested
    @DisplayName("키워드 추가 서비스 테스트")
    class Add {

        @Test
        @DisplayName("성공")
        void addSuccess() {
            // given
            Member mockMember = mock(Member.class);
            when(memberRepository.findByAuthId(anyLong())).thenReturn(Optional.of(mockMember));

            // when
            KeywordCreateRequestDto createRequestDto = new KeywordCreateRequestDto("keyword");
            keywordService.add(AUTH_ID, createRequestDto);

            // then
            ArgumentCaptor<Keyword> argumentCaptor = ArgumentCaptor.forClass(Keyword.class);
            verify(keywordRepository, times(1)).save(argumentCaptor.capture());
            Keyword keyword = argumentCaptor.getValue();
            assertThat(keyword.getName()).isEqualTo("keyword");
        }

        @Test
        @DisplayName("사용자가 존재하지 않을 경우 예외 발생")
        void addFailMemberNotFound() {
            // given
            when(memberRepository.findByAuthId(anyLong())).thenReturn(Optional.empty());

            // when
            // then
            assertThatThrownBy(() -> keywordService.add(AUTH_ID, mock(KeywordCreateRequestDto.class)))
                    .isInstanceOf(MemberNotFoundException.class)
                    .hasMessage(MEMBER_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("사용자의 키워드개수가 30개를 넘어갈경우 예외 발생")
        void addFailKeywordOverLimit() {
            // given
            when(memberRepository.findByAuthId(anyLong())).thenReturn(Optional.of(mock(Member.class)));
            when(keywordRepository.countByMember(any(Member.class))).thenReturn(31);

            // when
            // then
            assertThatThrownBy(() -> keywordService.add(AUTH_ID, mock(KeywordCreateRequestDto.class)))
                    .isInstanceOf(KeywordOverLimitException.class)
                    .hasMessage(KEYWORD_OVER_LIMIT.getMessage());
        }
    }

    @Test
    @DisplayName("사용자의 키워드 리스트 조회 서비스 성공 테스트")
    void getKeywordsSuccess() {
        // given
        // when
        // then
    }

    @Test
    @DisplayName("사용자의 키워드 리스트 조회 서비스 실패 테스트 - 사용자가 존재하지 않을 경우 예외 발생")
    void getKeywordsFailMemberNotFound() {
        // given
        // when
        // then
    }

    @Test
    @DisplayName("키워드 편집 서비스 성공 테스트")
    void editSuccess() {
        // given
        // when
        // then
    }

    @Test
    @DisplayName("키워드 편집 서비스 실패 테스트 - 사용자가 존재하지 않을 경우 예외 발생")
    void editFailMemberNotFound() {
        // given
        // when
        // then
    }

    @Test
    @DisplayName("키워드 편집 서비스 실패 테스트 - 편집할 키워드가 존재하지 않을 경우 예외 발생")
    void editFailKeywordNotFound() {
        // given
        // when
        // then
    }

    @Test
    @DisplayName("키워드 편집 서비스 실패 테스트 - 저장할 카테고리가 존재하지 않을 경우 예외 발생")
    void editFailCategoryNotFound() {
        // given
        // when
        // then
    }


    @Test
    @DisplayName("키워드 삭제 서비스 성공 테스트")
    void deleteSuccess() {
        // given
        // when
        // then
    }

    @Test
    @DisplayName("키워드 삭제 서비스 실패 테스트 - 존재하지 않는 사용자일 경우 예외 발생")
    void deleteFailMemberNotFound() {
        // given
        // when
        // then
    }

    @Test
    @DisplayName("키워드 삭제 서비스 실패 테스트 - 삭제할 키워드가 존재하지 않을 경우 예외 발생")
    void deleteFailKeywordNotFound() {
        // given
        // when
        // then
    }
}