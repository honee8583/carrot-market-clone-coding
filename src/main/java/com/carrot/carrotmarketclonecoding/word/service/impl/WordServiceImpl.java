package com.carrot.carrotmarketclonecoding.word.service.impl;

import com.carrot.carrotmarketclonecoding.common.exception.MemberNotFoundException;
import com.carrot.carrotmarketclonecoding.member.domain.Member;
import com.carrot.carrotmarketclonecoding.member.repository.MemberRepository;
import com.carrot.carrotmarketclonecoding.word.domain.Word;
import com.carrot.carrotmarketclonecoding.word.dto.WordRequestDto.WordRegisterRequestDto;
import com.carrot.carrotmarketclonecoding.word.repository.WordRepository;
import com.carrot.carrotmarketclonecoding.word.service.WordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class WordServiceImpl implements WordService {
    private final WordRepository wordRepository;
    private final MemberRepository memberRepository;

    @Override
    public void add(Long memberId, WordRegisterRequestDto registerRequestDto) {
        Member member = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
        Word word = Word.createWord(registerRequestDto, member);
        wordRepository.save(word);
    }
}
