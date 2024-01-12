package com.ShortNews.ShortNews.service;

import com.ShortNews.ShortNews.JavaCode;
import com.ShortNews.ShortNews.Token.RefreshTokenRepository;
import com.ShortNews.ShortNews.dto.BookmarkDto;
import com.ShortNews.ShortNews.dto.BookmarkInterface;
import com.ShortNews.ShortNews.entity.Member;
import com.ShortNews.ShortNews.entity.Preference;
import com.ShortNews.ShortNews.entity.PreferenceKey;
import com.ShortNews.ShortNews.entity.Reason;
import com.ShortNews.ShortNews.repository.BookmarkRepository;
import com.ShortNews.ShortNews.repository.MemberRepository;
import com.ShortNews.ShortNews.repository.PreferenceRepository;
import com.ShortNews.ShortNews.repository.ReasonRepository;
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
        Optional<Member> list = memberRepository.findById(id);
        Reason reason = Reason.builder()
                        .id(reasonRepository.selectId())
                        .cre_date(list.get().getCre_date())
                        .content(content)
                        .build();
        reasonRepository.save(reason);
        memberRepository.deleteById(id);
        refreshTokenRepository.deleteById(id);
    }

    public void updateCate(String id, List<Boolean> list) {
        // 카테고리 순서 100(정치), 101(경제), 102(사회), 105(IT/과학), 104(세계), 107(스포츠), 106(연예), 103(생활/문화)
        String[] cate_list = new String[]{"100", "101", "102", "105", "104", "107", "106", "103"};
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

    public Map<String, Object> bookmark(String id) {
        List<BookmarkInterface> bookmarks = bookmarkRepository.selectBookmark(id);
        Map<String, Object> map = new HashMap<>();
        for (BookmarkInterface b : bookmarks) {
            BookmarkDto bookmarkDto = new BookmarkDto(
                    b.getId(),
                    b.getCate_id(),
                    b.getNews_id(),
                    b.getTitle()
            );
            if (!map.containsKey(bookmarkDto.getCate_id())) {
                map.put(bookmarkDto.getCate_id(), new ArrayList<Map<String, String>>());
            }
            ArrayList<Map<String, String>> arr = (ArrayList<Map<String, String>>) map.get(bookmarkDto.getCate_id());
            Map<String, String> tmp = new HashMap<>();
            tmp.put("news_id", bookmarkDto.getNews_id());
            tmp.put("title", bookmarkDto.getTitle());
            arr.add(tmp);
        }
        return map;
    }

    public void deleteBookmark(String id, String news_id) {
        bookmarkRepository.delete(id, news_id);
    }
}