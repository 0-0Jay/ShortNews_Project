package hello.hellospring.controller;

import hello.hellospring.domain.Member;
import hello.hellospring.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class MemberController {
    //스프링 컨테이너로부터 받아서 쓰도록 바꿔야함 new를 쓰지 말고.. 그래서 스프링 컨테이너에 등록

    private final MemberService memberService;

    @Autowired //스프링 컨테이너에서 멤버서비스를 가져다가 연결을 시켜줌
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/members/new") //members/new에 접근한다면 > members/creatememberform으로 이동
    public String createForm(){
        return "members/createMemberForm";
    }


    @PostMapping("/members/new") //제출을 한다면(버튼 클릭을 한다면)
    public String create(MemberForm form){ //MemberForm 클래스
        Member member = new Member();
        member.setName(form.getName());

        memberService.join(member); //memberService에 join(jpa에 짜여져있는 db쿼리)에 접근
                                    //join은 jpa에 접근해서 가져옴

        return "redirect:/";
    }

    @GetMapping("/members")
    public String list(Model model){
        List<Member> members = memberService.findMembers();
        model.addAttribute("members", members);
        return "members/memberList";
    }

    @GetMapping("/deleteMember")
    public String delete(){
        return "members/deleteMemberForm";
    }

    @PostMapping("/deleteMember")
    public String deleteMember(@RequestParam("memberId") Long memberId) {
        // MemberService를 통해 memberId를 사용하여 삭제 작업 수행
        // ...
        Member member = new Member();
        memberService.delete(memberId); //memberService에 있는 delete
        return "redirect:/";
    }

}
