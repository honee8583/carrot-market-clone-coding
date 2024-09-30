package com.carrot.carrotmarketclonecoding.keyword.service.impl;

import com.carrot.carrotmarketclonecoding.common.exception.KeywordOverLimitException;
import com.carrot.carrotmarketclonecoding.common.exception.MemberNotFoundException;
import com.carrot.carrotmarketclonecoding.keyword.domain.Keyword;
import com.carrot.carrotmarketclonecoding.keyword.dto.KeywordRequestDto.KeywordCreateRequestDto;
import com.carrot.carrotmarketclonecoding.keyword.dto.KeywordRequestDto.KeywordEditRequestDto;
import com.carrot.carrotmarketclonecoding.keyword.dto.KeywordResponseDto;
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

    private static final int KEYWORD_LIMIT = 30;

    @Override
    public void add(Long authId, KeywordCreateRequestDto keywordCreateRequestDto) {
        Member member = memberRepository.findByAuthId(authId).orElseThrow(MemberNotFoundException::new);
        isKeywordCountOverLimit(keywordRepository.countByMember(member));
        keywordRepository.save(Keyword.createKeyword(keywordCreateRequestDto, member));
    }

    @Override
    public void edit(Long authId, Long keywordId, KeywordEditRequestDto keywordEditRequestDto) {

    }

    @Override
    public List<KeywordResponseDto> getAllKeywords(Long authId) {
        return null;
    }

    @Override
    public void delete(Long authId, Long keywordId) {

    }

    private void isKeywordCountOverLimit(int count) {
        if (count > KEYWORD_LIMIT) {
            throw new KeywordOverLimitException();
        }
    }
}
