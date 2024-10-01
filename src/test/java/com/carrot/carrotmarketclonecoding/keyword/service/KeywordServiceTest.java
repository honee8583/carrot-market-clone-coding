package com.carrot.carrotmarketclonecoding.keyword.service;

import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.CATEGORY_NOT_FOUND;
import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.KEYWORD_NOT_FOUND;
import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.KEYWORD_OVER_LIMIT;
import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.MEMBER_NOT_FOUND;
import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.UNAUTHORIZED_ACCESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.carrot.carrotmarketclonecoding.category.domain.Category;
import com.carrot.carrotmarketclonecoding.category.repository.CategoryRepository;
import com.carrot.carrotmarketclonecoding.common.exception.CategoryNotFoundException;
import com.carrot.carrotmarketclonecoding.common.exception.KeywordNotFoundException;
import com.carrot.carrotmarketclonecoding.common.exception.KeywordOverLimitException;
import com.carrot.carrotmarketclonecoding.common.exception.MemberNotFoundException;
import com.carrot.carrotmarketclonecoding.common.exception.UnauthorizedAccessException;
import com.carrot.carrotmarketclonecoding.keyword.domain.Keyword;
import com.carrot.carrotmarketclonecoding.keyword.dto.KeywordRequestDto.KeywordCreateRequestDto;
import com.carrot.carrotmarketclonecoding.keyword.dto.KeywordRequestDto.KeywordEditRequestDto;
import com.carrot.carrotmarketclonecoding.keyword.dto.KeywordResponseDto.KeywordDetailResponseDto;
import com.carrot.carrotmarketclonecoding.keyword.repository.KeywordRepository;
import com.carrot.carrotmarketclonecoding.keyword.service.impl.KeywordServiceImpl;
import com.carrot.carrotmarketclonecoding.member.domain.Member;
import com.carrot.carrotmarketclonecoding.member.repository.MemberRepository;
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
class KeywordServiceTest {

    @Mock
    private KeywordRepository keywordRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private CategoryRepository categoryRepository;

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

    @Nested
    @DisplayName("사용자의 키워드 목록 조회 서비스 테스트")
    class GetKeywords {

        @Test
        @DisplayName("성공")
        void getKeywordsSuccess() {
            // given
            Member mockMember = Member.builder()
                    .authId(AUTH_ID)
                    .build();
            when(memberRepository.findByAuthId(anyLong())).thenReturn(Optional.of(mockMember));

            Category mockCategory = Category.builder()
                    .id(1L)
                    .build();
            List<Keyword> keywords = Arrays.asList(
                    Keyword.builder()
                            .category(mockCategory)
                            .build(),
                    Keyword.builder()
                            .category(mockCategory)
                            .build()
            );
            when(keywordRepository.findAllByMember(any(Member.class))).thenReturn(keywords);

            // when
            List<KeywordDetailResponseDto> result = keywordService.getAllKeywords(AUTH_ID);

            // then
            assertThat(result.size()).isEqualTo(2);
        }

        @Test
        @DisplayName("사용자가 존재하지 않을 경우 예외 발생")
        void getKeywordsFailMemberNotFound() {
            // given
            when(memberRepository.findByAuthId(anyLong())).thenReturn(Optional.empty());

            // when
            // then
            assertThatThrownBy(() -> keywordService.getAllKeywords(AUTH_ID))
                    .isInstanceOf(MemberNotFoundException.class)
                    .hasMessage(MEMBER_NOT_FOUND.getMessage());
        }
    }

    @Nested
    @DisplayName("키워드 편집 서비스 테스트")
    class Edit {

        @Test
        @DisplayName("성공")
        void editSuccess() {
            // given
            Member mockMember = Member.builder()
                    .id(1L)
                    .authId(AUTH_ID)
                    .build();
            when(memberRepository.findByAuthId(anyLong())).thenReturn(Optional.of(mockMember));

            Keyword mockKeyword = Keyword.builder()
                    .id(1L)
                    .name("keyword")
                    .member(mockMember)
                    .category(Category.builder().id(1L).build())
                    .build();
            when(keywordRepository.findById(anyLong())).thenReturn(Optional.of(mockKeyword));

            Category mockCategory = Category.builder()
                    .id(2L)
                    .build();
            when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(mockCategory));

            // when
            KeywordEditRequestDto editRequestDto = KeywordEditRequestDto.builder()
                    .name("edited keyword")
                    .categoryId(1L)
                    .minPrice(0)
                    .maxPrice(100000)
                    .build();
            keywordService.edit(AUTH_ID, 1L, editRequestDto);

            // then
            assertThat(mockKeyword.getName()).isEqualTo(editRequestDto.getName());
            assertThat(mockKeyword.getMinPrice()).isEqualTo(editRequestDto.getMinPrice());
            assertThat(mockKeyword.getMaxPrice()).isEqualTo(editRequestDto.getMaxPrice());
            assertThat(mockKeyword.getCategory().getId()).isEqualTo(2L);
        }

        @Test
        @DisplayName("사용자가 존재하지 않을 경우 예외 발생")
        void editFailMemberNotFound() {
            // given
            when(memberRepository.findByAuthId(anyLong())).thenReturn(Optional.empty());

            // when
            // then
            assertThatThrownBy(() -> keywordService.edit(AUTH_ID, 1L, mock(KeywordEditRequestDto.class)))
                    .isInstanceOf(MemberNotFoundException.class)
                    .hasMessage(MEMBER_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("편집할 키워드가 존재하지 않을 경우 예외 발생")
        void editFailKeywordNotFound() {
            // given
            when(memberRepository.findByAuthId(anyLong())).thenReturn(Optional.of(mock(Member.class)));
            when(keywordRepository.findById(anyLong())).thenReturn(Optional.empty());

            // when
            // then
            assertThatThrownBy(() -> keywordService.edit(AUTH_ID, 1L, mock(KeywordEditRequestDto.class)))
                    .isInstanceOf(KeywordNotFoundException.class)
                    .hasMessage(KEYWORD_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("사용자의 키워드가 아닐경우 예외 발생")
        void editFailMemberNotEqualKeywordMember() {
            // given
            Member mockMember = Member.builder()
                    .id(1L)
                    .authId(AUTH_ID)
                    .build();
            when(memberRepository.findByAuthId(anyLong())).thenReturn(Optional.of(mockMember));

            Keyword mockKeyword = Keyword.builder()
                    .id(1L)
                    .member(Member.builder().id(2L).build())
                    .build();
            when(keywordRepository.findById(anyLong())).thenReturn(Optional.of(mockKeyword));

            // when
            // then
            assertThatThrownBy(() -> keywordService.edit(AUTH_ID, 1L, mock(KeywordEditRequestDto.class)))
                    .isInstanceOf(UnauthorizedAccessException.class)
                    .hasMessage(UNAUTHORIZED_ACCESS.getMessage());
        }

        @Test
        @DisplayName("저장할 카테고리가 존재하지 않을 경우 예외 발생")
        void editFailCategoryNotFound() {
            // given
            Member mockMember = Member.builder()
                    .authId(AUTH_ID)
                    .build();
            when(memberRepository.findByAuthId(anyLong())).thenReturn(Optional.of(mockMember));

            Keyword mockKeyword = Keyword.builder()
                    .member(mockMember)
                    .build();
            when(keywordRepository.findById(anyLong())).thenReturn(Optional.of(mockKeyword));
            when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

            // when
            // then
            KeywordEditRequestDto editRequestDto = KeywordEditRequestDto
                    .builder()
                    .categoryId(1L)
                    .build();
            assertThatThrownBy(() -> keywordService.edit(AUTH_ID, 1L, editRequestDto))
                    .isInstanceOf(CategoryNotFoundException.class)
                    .hasMessage(CATEGORY_NOT_FOUND.getMessage());
        }
    }

    @Nested
    @DisplayName("키워드 삭제 서비스 테스트")
    class Delete {

        @Test
        @DisplayName("성공")
        void deleteSuccess() {
            // given
            Member mockMember = Member.builder()
                    .authId(AUTH_ID)
                    .build();
            when(memberRepository.findByAuthId(anyLong())).thenReturn(Optional.of(mockMember));

            Keyword mockKeyword = Keyword.builder()
                    .member(mockMember)
                    .build();
            when(keywordRepository.findById(anyLong())).thenReturn(Optional.of(mockKeyword));

            // when
            keywordService.delete(AUTH_ID, 1L);

            // then
            verify(keywordRepository, times(1)).delete(mockKeyword);
        }

        @Test
        @DisplayName("존재하지 않는 사용자일 경우 예외 발생")
        void deleteFailMemberNotFound() {
            // given
            when(memberRepository.findByAuthId(anyLong())).thenReturn(Optional.empty());

            // when
            // then
            assertThatThrownBy(() -> keywordService.delete(AUTH_ID, 1L))
                    .isInstanceOf(MemberNotFoundException.class)
                    .hasMessage(MEMBER_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("삭제할 키워드가 존재하지 않을 경우 예외 발생")
        void deleteFailKeywordNotFound() {
            // given
            when(memberRepository.findByAuthId(anyLong())).thenReturn(Optional.of(mock(Member.class)));
            when(keywordRepository.findById(anyLong())).thenReturn(Optional.empty());

            // when
            // then
            assertThatThrownBy(() -> keywordService.delete(AUTH_ID, 1L))
                    .isInstanceOf(KeywordNotFoundException.class)
                    .hasMessage(KEYWORD_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("삭제할 키워드가 사용자의 키워드가 아닐 경우 예외 발생")
        void deleteFailMemberIsNotKeywordMember() {
            // given
            Member mockMember = Member.builder()
                    .id(1L)
                    .authId(AUTH_ID)
                    .build();
            when(memberRepository.findByAuthId(anyLong())).thenReturn(Optional.of(mockMember));

            Keyword mockKeyword = Keyword.builder()
                    .id(1L)
                    .member(Member.builder().id(2L).build())
                    .build();
            when(keywordRepository.findById(anyLong())).thenReturn(Optional.of(mockKeyword));

            // when
            // then
            assertThatThrownBy(() -> keywordService.delete(AUTH_ID, 1L))
                    .isInstanceOf(UnauthorizedAccessException.class)
                    .hasMessage(UNAUTHORIZED_ACCESS.getMessage());
        }
    }
}