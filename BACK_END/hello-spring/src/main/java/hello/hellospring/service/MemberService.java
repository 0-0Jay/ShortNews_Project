package hello.hellospring.service;

import hello.hellospring.domain.Member;
import hello.hellospring.repository.MemberRepository;

import java.util.*;
public class MemberService { //비즈니스 메서드

    //memberservice에서 사용하는 MemoryMemberRepository랑 test케이스 에서 만든 MemoryMemberRepository에서 만든
    //거는 다른 거임 지금 상황은 static이기 때문에 문제가 없지만 static이 아니면 문제가 생김
    //그래서 지금은 다른 거를 이용하고 있는거임 그래서 같은 인스턴스를 쓸 수 있게 만들려면 외부에서 넣어주게 바꾸기
    //이런거를 DI
    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }


    /**
     * 회원 가입
     */
    public Long join(Member member){
        memberRepository.findByName(member.getName())
                .ifPresent(m -> {
                    throw new IllegalStateException("이미 존재하는 회원입니다.");
                });
        memberRepository.save(member);
        return member.getId();
    }

    /**
     * 전체 회원 조회
     */
    public List<Member> findMembers(){
        return memberRepository.findAll();
    }


    public Optional<Member> findOne(Long memberId){
        return memberRepository.findById(memberId);
    }

    /**
     *
     * 삭제
     */
    public void delete(Long id) { //id를 받아와서
        memberRepository.deleteById(id);
    }

}
