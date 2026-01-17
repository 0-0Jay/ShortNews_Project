package com.ShortNews.ShortNews.service;

import com.ShortNews.ShortNews.JavaCode;
import com.ShortNews.ShortNews.SHA256;
import com.ShortNews.ShortNews.dto.MemberDto;
import com.ShortNews.ShortNews.entity.Member;
import com.ShortNews.ShortNews.entity.Member_tts;
import com.ShortNews.ShortNews.entity.Preference;
import com.ShortNews.ShortNews.repository.Member_ttsRepository;
import com.ShortNews.ShortNews.repository.PreferenceRepository;
import com.ShortNews.ShortNews.repository.SignupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.ShortNews.ShortNews.entity.PreferenceKey;

@Service
public class SignupService {

    @Autowired
    private SignupRepository signupRepository;
    @Autowired
    private PreferenceRepository preferenceRepository;
    @Autowired
    private Member_ttsRepository member_ttsRepository;
    @Autowired
    private JavaCode javaCode;

    public boolean idCheck(String id) {
        Optional<Member> memberlist = signupRepository.findById(id);
        if (memberlist.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    public String phoneCheck(String phone) {
        Optional<Member> memberlist = signupRepository.findByPhone(phone);
        if (memberlist.isEmpty()) {
            String code = javaCode.makeCode();
            javaCode.sendSMS(phone, code);
            return code;
        } else {
            return "";
        }
    }

    public boolean nicknameCheck(String nickname) {
        Optional<Member> memberlist = signupRepository.findByNickname(nickname);
        if (memberlist.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }
    public String join(MemberDto memberDto, String platform) throws NoSuchAlgorithmException {
        String nickname = memberDto.getNickname();
        Optional<Member> memberlist = signupRepository.findByNickname(nickname);
        if (memberlist.isEmpty()) {
            String salt = javaCode.makeSalt();
            String pw = javaCode.makePw(memberDto.getPw(), salt);
            String cre_date = javaCode.getTime();
            if (nickname.isEmpty()) {
                nickname = memberDto.getId();
            }

            Member member = memberDto.toEntity(platform, salt, pw, nickname, cre_date);
            signupRepository.save(member);
            Member_tts member_tts = Member_tts.builder()
                    .id(member.getId())
                    .model_id("_male")
                    .speed("1")
                    .build();
            member_ttsRepository.save(member_tts);
            return member.getId();

        } else {
            return "";
        }
    }

    public List<String> selectCategory(List<Boolean> category, String id) {
        // 카테고리 순서 100(정치), 101(경제), 102(사회), 103(생활/문화), 104(세계), 105(IT/과학), 106(연예), 107(스포츠)
        String[] cate_list = new String[]{"100", "101", "102", "103", "104", "105", "106", "107"};
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            if (category.get(i)) {
                // 값이 true면 cate_list와 mem_id를 DB에 저장
                PreferenceKey pk = new PreferenceKey();
                pk.setCate_id(cate_list[i]);
                pk.setId(id);

                Preference pref = new Preference();
                pref.setPreferenceKey(pk);
                preferenceRepository.save(pref);
                list.add(cate_list[i]);
            }
        }
        return list;
    }

//    public void saveTts(String id) {
//        Member_tts member_tts = Member_tts.builder()
//                .id(id)
//                .model_id("_male")
//                .speed("1")
//                .build();
//        member_ttsRepository.save(member_tts);
//    }
}
