package com.ShortNews.ShortNews.service;

import com.ShortNews.ShortNews.JavaCode;
import com.ShortNews.ShortNews.Token.RefreshTokenRepository;
import com.ShortNews.ShortNews.dto.ActivityNewsDto;
import com.ShortNews.ShortNews.dto.ActivityNewsInterface;
import com.ShortNews.ShortNews.entity.Member;
import com.ShortNews.ShortNews.entity.Preference;
import com.ShortNews.ShortNews.entity.PreferenceKey;
import com.ShortNews.ShortNews.entity.Reason;
import com.ShortNews.ShortNews.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.util.*;

@Service
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ReasonRepository reasonRepository;
    @Autowired
    private PreferenceRepository preferenceRepository;
    @Autowired
    private BookmarkRepository bookmarkRepository;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private Member_ttsRepository member_ttsRepository;
    @Autowired
    private JavaCode javaCode;

    public boolean updatePw(String id, String pw, String new_pw) throws NoSuchAlgorithmException {
        Optional<Member> member_list = memberRepository.findById(id);
        String salt = member_list.get().getSalt();
        String hash_pw = member_list.get().getPw();
        String find_pw = javaCode.makePw(pw, salt);

        if (hash_pw.equals(find_pw)) {
            memberRepository.updatePassword(id, javaCode.makePw(new_pw, salt));
            return true;
        } else {
            return false;
        }
    }

    public boolean updateNickname(String id, String nickname) {
        List<Member> list = memberRepository.findByNickname(nickname);
        if (list.isEmpty()) {
            memberRepository.updateNickname(id, nickname);
            return true;
        } else {
            return false;
        }
    }

    public void delete(String id, String content) {
        System.out.println(id);
        refreshTokenRepository.deleteById(id);
        System.out.println(id);
        memberRepository.deleteById(id);
        reasonRepository.insertReason(content);
    }

    public void updateCate(String id, List<Boolean> list) {
        // 카테고리 순서 100(정치), 101(경제), 102(사회), 105(IT/과학), 104(세계), 107(스포츠), 106(연예), 103(생활/문화)
        String[] cate_list = new String[]{"100", "101", "102", "103", "104", "105", "106", "107"};
        for (int i = 0; i < list.size(); i++) {
            PreferenceKey preferenceKey = new PreferenceKey();
            preferenceKey.setId(id);
            preferenceKey.setCate_id(cate_list[i]);
            if (list.get(i)) {
                Optional<Preference> pre_list = preferenceRepository.findById(preferenceKey);
                if (pre_list.isEmpty()) {
                    Preference preference = new Preference();
                    preference.setPreferenceKey(preferenceKey);
                    preferenceRepository.save(preference);
                }
            } else {
                Optional<Preference> pre_list = preferenceRepository.findById(preferenceKey);
                if (!pre_list.isEmpty()) {
                    preferenceRepository.deleteById(preferenceKey);
                }
            }
        }
    }

    public List<ActivityNewsDto> bookmark(String id) {
        List<ActivityNewsDto> list = new ArrayList<>();
        List<ActivityNewsInterface> bookmark_list = bookmarkRepository.selectBookmark(id);
        for (ActivityNewsInterface activityLike : bookmark_list) {
            String news_imgs = activityLike.getImgs();
            String imgs = null;
            if (news_imgs != null) {
                String[] img_list = news_imgs.split(",");
                imgs = img_list[0];
            }

            ActivityNewsDto activityNewsDto = ActivityNewsDto.builder()
                    .news_id(activityLike.getNews_id())
                    .cate_id(activityLike.getCate_id())
                    .views(activityLike.getViews())
                    .like(activityLike.getGood())
                    .dislike(activityLike.getBad())
                    .title(activityLike.getTitle())
                    .reply(activityLike.getReply())
                    .imgs(imgs)
                    .bookmark(1)
                    .build();
            list.add(activityNewsDto);
        }
        return list;
    }

    public void deleteBookmark(String id, String news_id) {
        bookmarkRepository.delete(id, news_id);
    }

    public void deleteAllBookmark(String id) { bookmarkRepository.deleteAll(id); }

    public void updateMemberTts(String id, String model, String speed) {
        member_ttsRepository.updateTts(id, model, speed);
    }

    public String checkPhone(String phone){
        List<Member> list = memberRepository.findByPhone(phone);
        if (list.isEmpty()) {
            String code = javaCode.makeCode();
            javaCode.sendSMS(phone, code);
            return code;
        } else {
            return "";
        }
    }

    public void updatePhone(String id, String phone) {
        memberRepository.updatePhone(id, phone);
    }
}