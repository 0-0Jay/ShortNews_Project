package com.ShortNews.ShortNews.service;

import com.ShortNews.ShortNews.JavaCode;
import com.ShortNews.ShortNews.Token.Refreshtoken;
import com.ShortNews.ShortNews.Token.RefreshTokenRepository;
import com.ShortNews.ShortNews.dto.SessionDto;
import com.ShortNews.ShortNews.entity.Member;
import com.ShortNews.ShortNews.entity.Member_tts;
import com.ShortNews.ShortNews.entity.Preference;
import com.ShortNews.ShortNews.repository.LoginRepository;
import com.ShortNews.ShortNews.repository.Member_ttsRepository;
import com.ShortNews.ShortNews.repository.PreferenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LoginService {

    @Autowired
    private LoginRepository loginRepository;
    @Autowired
    private PreferenceRepository preferenceRepository;
    @Autowired
    private Member_ttsRepository member_ttsRepository;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private JavaCode javaCode;

    public boolean idCheck(String id) {
        Optional<Member> list = loginRepository.findById(id);
        if(list.isEmpty()){
            return false;
        }else {
            return true;
        }
    }

    public SessionDto loginCheck(String id, String pw) throws NoSuchAlgorithmException {
        Optional<Member> list = loginRepository.findById(id);

        SessionDto sessionDto;
        if (list.isEmpty()) {
            return SessionDto.builder().build();
        } else {
            String findId = list.get().getId();
            String findPw = list.get().getPw();
            String salt = list.get().getSalt();
            String newPw = javaCode.makePw(pw, salt);

            if (id.equals(findId) && newPw.equals(findPw)) {
                List<Preference> preference_list = preferenceRepository.selectCate(id);
                List<String> session_cate_list = new ArrayList<>();
                Optional<Member_tts> tts_list = member_ttsRepository.findById(id);
                String model = null, speed = null;
                if (! tts_list.isEmpty()){
                    model = tts_list.get().getModel_id();
                    speed = tts_list.get().getSpeed();
                }
                if (model == null || speed == null) {
                    model = "_male";
                    speed = "1";
                }
                if(!tts_list.isEmpty()){
                    model = tts_list.get().getModel_id();
                    speed = tts_list.get().getSpeed();
                }

                for (Preference preference : preference_list) {
                    session_cate_list.add(preference.getPreferenceKey().getCate_id());
                }

                sessionDto = SessionDto.builder()
                        .phone(list.get().getPhone())
                        .nickname(list.get().getNickname())
                        .id(list.get().getId())
                        .category(session_cate_list)
                        .model(model)
                        .speed(speed)
                        .platform(list.get().getPlatform())
                        .alarm(list.get().getAlarm())
                        .build();

                return sessionDto;
            } else {
                return SessionDto.builder().build();
            }
        }
    }

    public boolean findPassword(String id, String phone) {
        Optional<Member> list = loginRepository.findById(id);
        if (list.isEmpty()) {
            return false;
        } else {
            String findId = list.get().getId();
            String findPhone = list.get().getPhone();
            if (id.equals(findId) && phone.equals(findPhone)) {
                return true;
            } else {
                return false;
            }
        }
    }

    public String findId(String phone) {
        Optional<Member> list = loginRepository.findByPhone(phone);
        if (list.isEmpty()) {
            return null;
        } else {
            return list.get().getId();
        }
    }

    public void updatePassword(String id, String pw) throws NoSuchAlgorithmException {
        Optional<Member> list = loginRepository.findById(id);
        String salt = list.get().getSalt();
        String newPw = javaCode.makePw(pw, salt);
        loginRepository.updatePw(id, newPw);
    }


}