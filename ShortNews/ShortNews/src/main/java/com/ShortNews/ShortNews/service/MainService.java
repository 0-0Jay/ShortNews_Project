package com.ShortNews.ShortNews.service;

import com.ShortNews.ShortNews.JavaCode;
import com.ShortNews.ShortNews.dto.*;
import com.ShortNews.ShortNews.entity.*;
import com.ShortNews.ShortNews.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.*;

@Service
public class MainService {

    @Autowired
    private NewsRepository newsRepository;
    @Autowired
    private BookmarkRepository bookmarkRepository;
    @Autowired
    private RecommendRepository recommendRepository;
    @Autowired
    private JavaCode javaCode;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private ReplyRepository replyRepository;
    @Autowired
    private ReportRepository reportRepository;

    public Map<String, Object> News(String category, String date, String id) {
        List<News> list = newsRepository.selectNews(category, date);
        List<Map<String, Object>> all_list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        for (News news : list) {
            Map<String, Object> news_map = new HashMap<>();
            BookmarkKey bookmarkKey = BookmarkKey.builder()
                    .news_id(news.getNews_id())
                    .id(id).build();
            Optional<Bookmark> bookmark_list = bookmarkRepository.findById(bookmarkKey);
            if (bookmark_list.isEmpty()) {
                news_map.put("bookmark", false);
            } else {
                news_map.put("bookmark", true);
            }
            List<Recommend> recommend_list = recommendRepository.likeCount(news.getNews_id());
            List<Recommend> recommend2_list = recommendRepository.dislikeCount(news.getNews_id());
            int count = replyRepository.replyCount(news.getNews_id());

            news_map.put("news_id", news.getNews_id());     
            news_map.put("title", news.getTitle());
            news_map.put("views", news.getViews());
            news_map.put("img", news.getImgs());
            news_map.put("reply", count);
            news_map.put("like", recommend_list.size());
            news_map.put("dislike", recommend2_list.size());
            all_list.add(news_map);
        }

        map.put("news_list", all_list);
        return map;
    }

    public Map<String, Object> search(String keyword) {
        Map<String, Object> map = new HashMap<>();
        List<News> list = newsRepository.selectKeyword(keyword);
        List<String> news_list = new ArrayList<>();
        List<String> title_list = new ArrayList<>();

        for (News news : list) {
            news_list.add(news.getNews_id());
            title_list.add(news.getTitle());
        }

        map.put("news_id", news_list);
        map.put("title", title_list);
        return map;
    }

    public NewsDto selectNews(String id, String news_id) {
        NewsDto newsDto = new NewsDto();
        BookmarkKey bookmarkKey = BookmarkKey.builder()
                    .id(id)
                    .news_id(news_id)
                    .build();
        Optional<Bookmark> bookmark_list = bookmarkRepository.findById(bookmarkKey);

        if (bookmark_list.isEmpty()) {
            newsDto.setBookmark(false);
        } else {
            newsDto.setBookmark(true);
        }
        newsRepository.updateViews(news_id);
        List<Recommend> recommend_list = recommendRepository.likeCheck(id, news_id);

        if (recommend_list.isEmpty()) {
            newsDto.setType(-1);
        } else {
            newsDto.setType(recommend_list.get(0).getType());
        }

        Optional<News> news_list = newsRepository.findById(news_id);
        newsDto.setTitle(news_list.get().getTitle());
        newsDto.setImgs(news_list.get().getImgs());
        newsDto.setContent(news_list.get().getContent());
        newsDto.setViews(news_list.get().getViews());


        newsDto.setLike((int) recommendRepository.likeCount(news_id).stream().count());
        newsDto.setDislike((int) recommendRepository.dislikeCount(news_id).stream().count());

        return newsDto;
    }

    public boolean like(String id, String type, String before, String after, String what) {
        String time = javaCode.getTime();
        String str = "select recommend_seq.nextval from dual";
        Query query = entityManager.createNativeQuery(str);
        String num = query.getSingleResult().toString();
        String rec_id = time + num;

        if (what.equals("news")) {
            if (before.equals("-1") && after.equals("1")) {
                recommendRepository.onLike(rec_id, id, type);
            } else if (before.equals("-1") && after.equals("0")) {
                recommendRepository.onDisLike(rec_id, id, type);
            } else if (before.equals("1") && after.equals("0") || before.equals("0") && after.equals("1")) {
                recommendRepository.updateLike(type, after);
            } else if (before.equals("1") && after.equals("1") || before.equals("0") && after.equals("0")) {
                recommendRepository.deleteLike(type, id);
            } else {
                return false;
            }
        } else {
            if (before.equals("-1") && after.equals("1")) {
                recommendRepository.onReplyLike(rec_id, id, type);
            } else if (before.equals("-1") && after.equals("0")) {
                recommendRepository.onReplyDisLike(rec_id, id, type);
            } else if (before.equals("1") && after.equals("0") || before.equals("0") && after.equals("1")) {
                recommendRepository.updateReplyLike(type, after);
            } else if (before.equals("1") && after.equals("1") || before.equals("0") && after.equals("0")) {
                recommendRepository.deleteReplyLike(type, id);
            } else {
                return false;
            }
        }


        return true;
    }

    public void Bookmark(String id, String news_id, Boolean type) {
        if (!type) {
            BookmarkKey bookmarkKey = BookmarkKey.builder()
                    .id(id)
                    .news_id(news_id)
                    .build();

            Bookmark bookmark = Bookmark.builder()
                    .bookmarkKey(bookmarkKey)
                    .build();

            bookmarkRepository.save(bookmark);
        } else {
            bookmarkRepository.delete(id, news_id);
        }
    }

    public String selectLink(String news_id) {
        Optional<News> news = newsRepository.findById(news_id);
        if (news.isEmpty()) {
            return null;
        } else {
            return news.get().getUrl();
        }
    }


    public List<Map<String, Object>> reply(String news_id) {
        // 인터페이스로 댓글, 댓글에 대한 추천 테이블 join 결과 리스트로 변환
        List<ReplyDto> replyList = new ArrayList<>();
        for (ReplyInterface replyInterface : replyRepository.selectReply(news_id)) {
            ReplyDto replyDto = new ReplyDto(
                    replyInterface.getReply_id(),
                    replyInterface.getNews_id(),
                    replyInterface.getContent(),
                    replyInterface.getUpper_id(),
                    replyInterface.getId(),
                    replyInterface.getGood(),
                    replyInterface.getBad()
            );
            replyList.add(replyDto);
        }

        // 부모 댓글 리스트, 자식 댓글 리스트, 현재 부모용 map 객체 생성
        List<Map<String, Object>> parentReply = new ArrayList<>();
        List<Map<String, Object>> childReply = new ArrayList<>();
        Map<String, Object> nowParent = new HashMap<>();;
        String parent = "";

        for (ReplyDto rp : replyList) {
            if (rp.getUpper_id() == null) {  // 부모 댓글이 없으면 부모 댓글로 간주하고 이전 그룹을 parentReply에 저장 및 부모 및 자식리스트 객체 초기화
                if (!parent.equals("") && !rp.getReply_id().equals(parent)) {
                    nowParent.put("lower", childReply);
                    parentReply.add(nowParent);
                    childReply = new ArrayList<>();

                }
                parent = rp.getReply_id();
                nowParent = new HashMap<>();
                nowParent.put("reply_id", rp.getReply_id());
                nowParent.put("news_id", rp.getNews_id());
                nowParent.put("id", rp.getId());
                nowParent.put("content", rp.getContent());
                nowParent.put("like", rp.getGood());
                nowParent.put("hate", rp.getBad());
            } else {
                Map<String, Object> now = new HashMap<>();
                now.put("reply_id", rp.getReply_id());
                now.put("news_id", rp.getNews_id());
                now.put("id", rp.getId());
                now.put("content", rp.getContent());
                now.put("like", rp.getGood());
                now.put("hate", rp.getBad());
                childReply.add(now);
            }
        }
        // 반복 문에서 저장되지 않는 마지막 부모 댓글 삽입
        if (!parent.equals("")) {
            nowParent.put("lower", childReply);
            parentReply.add(nowParent);
        }

        return parentReply;
    }

    public void write(ReplyDto reply) {
        String text = reply.getContent();  // 혹시나 이스케이프 문자 관련 문제 있을 수 있어 일단 분리해 둠.
        replyRepository.insertReply(reply.getNews_id(), reply.getId(), text, reply.getUpper_id());
    }

    public void update(String reply_id, String text) {
        replyRepository.updateReply(reply_id, text);
    }

    public void delete(String reply_id) {
        replyRepository.deleteById(reply_id);
    }

    public void report(String id, String content, String type, String reply_id, String news_id) {
        System.out.println(id);
        System.out.println(content);
        System.out.println(type);
        System.out.println(reply_id);
        System.out.println(news_id);
        if (reply_id == null) {
            reportRepository.insertReport(id, type, content, news_id);
        } else {
            reportRepository.insertReplyReport(id, type, content, reply_id, news_id);
        }
    }
}