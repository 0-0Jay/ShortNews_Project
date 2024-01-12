package com.ShortNews.ShortNews.service;

import com.ShortNews.ShortNews.entity.Member;
import com.ShortNews.ShortNews.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

    public void join(Member member) {
        // 중복체크 하고 해야함
        member.setAlarm(1);
        member.setSalt("22");
        member.setPhone("20231110ㅎㅇ");
        member.setMem_id("20231110성공");
        member.setNickname("안녕");
        member.setPw("44");
        memberRepository.save(member);
    }
}
