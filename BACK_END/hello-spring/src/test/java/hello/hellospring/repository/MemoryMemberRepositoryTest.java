package hello.hellospring.repository;

import hello.hellospring.domain.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class MemoryMemberRepositoryTest{

    static MemoryMemberRepository repository = new MemoryMemberRepository();
    @AfterEach
    public void afterEach(){
        repository.clearStore();
    }


    @Test
    public void save(){
        Member member = new Member();
        member.setName("spring");

        repository.save(member);

        Member result = repository.findById(member.getId()).get();
        Assertions.assertThat(member).isEqualTo(result);
    }

    @Test
    public void findByName(){
        Member member = new Member();
        member.setName("spring1");
        repository.save(member);

        Member result = repository.findByName("spring1").get();
        Assertions.assertThat(result).isEqualTo(member);
    }

    @Test
    public void findAll(){
        Member member = new Member();
        member.setName("spring1");
        repository.save(member);

        Member member2 = new Member();
        member.setName("spring2");
        repository.save(member2);
        List <Member> list = new ArrayList<>();
        list = repository.findAll();//멤버 다 찾아서 리스트로 반환
        Assertions.assertThat(list.size()).isEqualTo(2);
    }




}
