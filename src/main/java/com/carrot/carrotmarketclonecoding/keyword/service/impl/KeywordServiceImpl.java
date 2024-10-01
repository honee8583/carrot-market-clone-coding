package com.carrot.carrotmarketclonecoding.keyword.service.impl;

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
import com.carrot.carrotmarketclonecoding.keyword.service.KeywordService;
import com.carrot.carrotmarketclonecoding.member.domain.Member;
import com.carrot.carrotmarketclonecoding.member.repository.MemberRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class KeywordServiceImpl implements KeywordService {
    private final KeywordRepository keywordRepository;
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;

    private static final int KEYWORD_LIMIT = 30;

    @Override
    public void add(Long authId, KeywordCreateRequestDto keywordCreateRequestDto) {
        Member member = memberRepository.findByAuthId(authId).orElseThrow(MemberNotFoundException::new);
        isKeywordCountOverLimit(keywordRepository.countByMember(member));
        keywordRepository.save(Keyword.createKeyword(keywordCreateRequestDto, member));
    }

    @Override
    public void edit(Long authId, Long keywordId, KeywordEditRequestDto keywordEditRequestDto) {
        Member member = memberRepository.findByAuthId(authId).orElseThrow(MemberNotFoundException::new);
        Keyword keyword = keywordRepository.findById(keywordId).orElseThrow(KeywordNotFoundException::new);
        isMemberOfKeyword(keyword, member);

        Category category = null;
        Long categoryId = keywordEditRequestDto.getCategoryId();
        if (isCategoryIdExists(categoryId)) {
            category = categoryRepository.findById(categoryId).orElseThrow(CategoryNotFoundException::new);
        }

        keyword.modify(category, keywordEditRequestDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<KeywordDetailResponseDto> getAllKeywords(Long authId) {
        Member member = memberRepository.findByAuthId(authId).orElseThrow(MemberNotFoundException::new);
        List<Keyword> keywords = keywordRepository.findAllByMember(member);
        return keywords.stream().map(KeywordDetailResponseDto::createDetail).toList();
    }

    @Override
    public void delete(Long authId, Long keywordId) {
        Member member = memberRepository.findByAuthId(authId).orElseThrow(MemberNotFoundException::new);
        Keyword keyword = keywordRepository.findById(keywordId).orElseThrow(KeywordNotFoundException::new);
        isMemberOfKeyword(keyword, member);
        keywordRepository.delete(keyword);
    }

    private void isKeywordCountOverLimit(int count) {
        if (count > KEYWORD_LIMIT) {
            throw new KeywordOverLimitException();
        }
    }

    private void isMemberOfKeyword(Keyword keyword, Member member) {
        if (keyword.getMember() != member) {
            throw new UnauthorizedAccessException();
        }
    }

    private boolean isCategoryIdExists(Long categoryId) {
        if (categoryId != null && categoryId > 0) {
            return true;
        }
        return false;
    }
}
