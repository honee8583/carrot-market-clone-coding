package com.carrot.carrotmarketclonecoding.word.service.impl;

import com.carrot.carrotmarketclonecoding.common.exception.MemberNotFoundException;
import com.carrot.carrotmarketclonecoding.common.exception.MemberWordLimitException;
import com.carrot.carrotmarketclonecoding.common.exception.WordNotFoundException;
import com.carrot.carrotmarketclonecoding.member.domain.Member;
import com.carrot.carrotmarketclonecoding.member.repository.MemberRepository;
import com.carrot.carrotmarketclonecoding.word.domain.Word;
import com.carrot.carrotmarketclonecoding.word.dto.WordRequestDto;
import com.carrot.carrotmarketclonecoding.word.dto.WordResponseDto.WordListResponseDto;
import com.carrot.carrotmarketclonecoding.word.repository.WordRepository;
import com.carrot.carrotmarketclonecoding.word.service.WordService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class WordServiceImpl implements WordService {
    private final WordRepository wordRepository;
    private final MemberRepository memberRepository;

    private static final int WORD_LIMIT = 30;

    @Override
    public void add(Long authId, WordRequestDto wordRequestDto) {
        Member member = memberRepository.findByAuthId(authId).orElseThrow(MemberNotFoundException::new);
        isWordTotalOverLimit(wordRepository.countByMember(member));
        Word word = Word.createWord(wordRequestDto, member);
        wordRepository.save(word);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WordListResponseDto> list(Long authId) {
        Member member = memberRepository.findByAuthId(authId).orElseThrow(MemberNotFoundException::new);
        List<Word> words = wordRepository.findAllByMember(member);
        return words.stream().map(WordListResponseDto::createWordListResponseDto).toList();
    }

    @Override
    public void update(Long authId, Long wordId, WordRequestDto wordRequestDto) {
        memberRepository.findByAuthId(authId).orElseThrow(MemberNotFoundException::new);
        Word word = wordRepository.findById(wordId).orElseThrow(WordNotFoundException::new);
        word.update(wordRequestDto);
    }

    @Override
    public void remove(Long authId, Long wordId) {
        memberRepository.findByAuthId(authId).orElseThrow(MemberNotFoundException::new);
        Word word = wordRepository.findById(wordId).orElseThrow(WordNotFoundException::new);
        wordRepository.delete(word);
    }

    private void isWordTotalOverLimit(int total) {
        if (total >= WORD_LIMIT) {
            throw new MemberWordLimitException();
        }
    }
}
