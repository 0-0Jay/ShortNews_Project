package com.ShortNews.ShortNews.service;

import com.ShortNews.ShortNews.JavaCode;
import com.ShortNews.ShortNews.controller.NotificationController;
import com.ShortNews.ShortNews.dto.*;
import com.ShortNews.ShortNews.entity.*;
import com.ShortNews.ShortNews.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

@Service
public class MainService {

    @Autowired
    private NewsRepository newsRepository;
    @Autowired
    private MemberRepository memberRepository;
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
    @Autowired
    private User_logRepository user_logRepository;
    @Autowired
    private NotificationRepository notificationRepository;

    public Map<String, Object> News(String category, String date, String id) {
        List<NewsListInterface> list = newsRepository.findNews(category, date, id);
        List<NewsListDto> news_list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        for (NewsListInterface news : list) {
            NewsListDto newsListDto = new NewsListDto();

            String news_imgs = news.getImgs();
            String imgs = null;
            if (news_imgs != null) {
                String[] img_list = news_imgs.split(",");
                imgs = img_list[0];
            }
            newsListDto.setBookmark(news.getBookmark());
            newsListDto.setNews_id(news.getNews_id());
            newsListDto.setTitle(news.getTitle());
            newsListDto.setViews(news.getViews());
            newsListDto.setImgs(imgs);
            newsListDto.setLike(news.getGood());
            newsListDto.setDislike(news.getBad());
            newsListDto.setReply(news.getReply());
            newsListDto.setReport(news.getReport());
            news_list.add(newsListDto);
        }

        map.put("news_list", news_list);
        return map;
    }

    public Map<String, Object> search(String id, String keyword, int st, int ed) {
        Map<String, Object> map = new HashMap<>();
        List<NewsListInterface> list = newsRepository.selectKeyword(id,'%' + keyword + '%', st, ed);
        List<NewsListDto> news_list = new ArrayList<>();
        for (NewsListInterface news : list) {
            NewsListDto newsListDto = new NewsListDto();

            String news_imgs = news.getImgs();
            String imgs = null;
            if (news_imgs != null) {
                String[] img_list = news_imgs.split(",");
                imgs = img_list[0];
            }
            newsListDto.setBookmark(news.getBookmark());
            newsListDto.setNews_id(news.getNews_id());
            newsListDto.setCate_id(news.getCate_id());
            newsListDto.setTitle(news.getTitle());
            newsListDto.setViews(news.getViews());
            newsListDto.setImgs(imgs);
            newsListDto.setLike(news.getGood());
            newsListDto.setDislike(news.getBad());
            newsListDto.setReply(news.getReply());
            newsListDto.setReport(news.getReport());
            news_list.add(newsListDto);
        }

        map.put("news_list", news_list);
        return map;
    }

    public NewsDto selectNews(String id, String news_id) throws IOException {
        Optional<NewsInterface> news = newsRepository.selectNews(news_id, id);
        NewsDto newsDto = new NewsDto();
        if (news.isEmpty()) return null;
        newsRepository.updateViews(news_id);  // 조회수 올리기
        user_logRepository.insertLog(id, news_id, news.get().getCate_id());  // 로그 남기기

        String news_imgs = news.get().getImgs();
        String[] img_list = {};
        if (news_imgs != null) {
            img_list = news_imgs.split(",");
        }
        newsDto.setBookmark(news.get().getBookmark());
        newsDto.setTitle(news.get().getTitle());
        newsDto.setImgs(img_list);
        newsDto.setContent(news.get().getContent());
        newsDto.setViews(news.get().getViews());
        newsDto.setReply(news.get().getReply());
        newsDto.setLike(news.get().getGood());
        newsDto.setDislike(news.get().getBad());
        newsDto.setType(news.get().getType());

        return newsDto;
    }

    public void like(String id, String news_id, String reply_id, int type) {
        Optional<String> res;
        if (reply_id == null) {
            res = recommendRepository.selectNewsLike(id, news_id);
        } else {
            res = recommendRepository.selectReplyLike(id, news_id, reply_id);
        }
        String recom_id = res.orElse(null);
        if (type == 0) {  // type이 0이면 좋아요/싫어요 취소 -> 삭제
            recommendRepository.deleteLike(recom_id);
            return;
        } else {
            if (recom_id != null) {
                recommendRepository.updateLike(recom_id, type);
            } else {
                recommendRepository.insertLike(id, news_id, reply_id, type);
            }
        }

        if (reply_id != null) {
            ReplyAlarmInterface replyInfo = replyRepository.selectReplyAlarm(reply_id);
            ReplyAlarmDto replyAlarmDto = ReplyAlarmDto.builder()
                    .id(replyInfo.getId())
                    .reply_id(replyInfo.getReply_id())
                    .alarm(replyInfo.getAlarm())
                    .build();
            String origin_id = replyAlarmDto.getId();
            int alarm = replyAlarmDto.getAlarm();
            // 알림 여부가 1이라면 밑에꺼 수행
            if (alarm == 1 && !origin_id.equals(id)) {
                try {
                    SseEmitter sseEmitter = NotificationController.sseEmitters.get(origin_id);
                    if (type != 0) {  // type이 1이면 좋아요, -1이면 싫어요 누른 상태
                        // 수신자가 로그아웃 중이라면 db에 저장
                        NotificationKey notificationKey = NotificationKey.builder()
                                .id(id)
                                .time(new Timestamp(Instant.now().toEpochMilli() + 9))
                                .build();

                        Notification notification = Notification.builder()
                                .notificationKey(notificationKey)
                                .link(news_id)
                                .status(0)
                                .type(type)
                                .target_id(origin_id)
                                .build();
                        notificationRepository.save(notification);
                    }
                    if(sseEmitter != null) {
                        // 수신자가 로그인 중이라면 db에 저장 후 알림 보내기
                        Map<String, Object> map = new HashMap<>();
                        map.put("news_id", news_id);
                        map.put("nickname", memberRepository.findById(id).get().getNickname());
                        map.put("type", type);
                        if (type != 0) {
                            sseEmitter.send(SseEmitter.event().name("notification").data(map));
                        }
                    }

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
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
        return news.map(News::getUrl).orElse(null);
    }


    public List<Map<String, Object>> reply(String news_id, String id) {
        // 인터페이스로 댓글, 댓글에 대한 추천 테이블 join 결과 리스트로 변환
        List<ReplyDto> replyList = new ArrayList<>();
        for (ReplyInterface replyInterface : replyRepository.selectReply(news_id, id)) {
            ReplyDto replyDto = new ReplyDto(
                    replyInterface.getReply_id(),
                    replyInterface.getNews_id(),
                    replyInterface.getContent(),
                    replyInterface.getUpper_id(),
                    replyInterface.getId(),
                    replyInterface.getGood(),
                    replyInterface.getBad(),
                    replyInterface.getNickname(),
                    replyInterface.getType(),
                    replyInterface.getReport(),
                    replyInterface.getEdited()
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
                nowParent.put("nickname", rp.getNickname());
                nowParent.put("content", rp.getContent());
                nowParent.put("like", rp.getGood());
                nowParent.put("hate", rp.getBad());
                nowParent.put("type", rp.getType());
                nowParent.put("report", rp.getReport());
                nowParent.put("edited", rp.getEdited());
            } else {
                Map<String, Object> now = new HashMap<>();
                now.put("reply_id", rp.getReply_id());
                now.put("news_id", rp.getNews_id());
                now.put("id", rp.getId());
                now.put("nickname", rp.getNickname());
                now.put("content", rp.getContent());
                now.put("like", rp.getGood());
                now.put("hate", rp.getBad());
                now.put("type", rp.getType());
                now.put("report", rp.getReport());
                now.put("edited", rp.getEdited());
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

    public void write(ReplyDto reply, String upper_id) {  // id는 상위 댓글 작성자. 없으면 null
        String text = reply.getContent();  // 혹시나 이스케이프 문자 관련 문제 있을 수 있어 일단 분리해 둠.
        try {
            if (upper_id != null && !upper_id.equals(reply.getId())) {  // 만약 원댓 작성자와 답댓 작성자 같으면 알람 X
                                NotificationKey notificationKey = NotificationKey.builder()
                        .id(reply.getId())
                        .time(new java.sql.Timestamp(Instant.now().toEpochMilli()))
                        .build();

                Notification notification = Notification.builder()
                        .notificationKey(notificationKey)
                        .link(reply.getNews_id())
                        .status(0)
                        .type(0)  // type이 0이면 댓글 작성
                        .target_id(upper_id)
                        .build();
                notificationRepository.save(notification);

                SseEmitter sseEmitter = NotificationController.sseEmitters.get(upper_id);
                if (sseEmitter != null) {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("link", reply.getNews_id());
                    map.put("nickname", memberRepository.findById(reply.getId()).get().getNickname());
                    map.put("type", 0);
                    sseEmitter.send(SseEmitter.event().name("notification").data(map));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        replyRepository.insertReply(reply.getNews_id(), reply.getId(), text, reply.getUpper_id());
    }

    public void update(String reply_id, String text) {
        replyRepository.updateReply(reply_id, text);
    }

    public void delete(String reply_id) {
        replyRepository.deleteById(reply_id);
    }

    public boolean report(String id, String content, String type, String reply_id, String news_id) {
        if (reportRepository.findReport(id, news_id, reply_id).isEmpty()) {
            reportRepository.insertReport(id, type, content, reply_id, news_id);
            return true;
        } else{
            return false;
        }
    }

    public void alarmSwitch(String id, Integer type) {
        Integer before, after;
        before = type;
        if (before == 1) {
            after = 0;
        } else {
            after = 1;
        }
        memberRepository.updateAlarm(id, after);
    }

    public void alarmSelect(String id, String time, String news_id) {
        notificationRepository.updateStatus(id, time, news_id);
    }

    public void alarmRemove(String id, String time, String news_id) {
        notificationRepository.deleteAlarm(id, time, news_id);
    }

    public void alarmDrop(String id) {
        notificationRepository.allDeleteAlarm(id);
    }

    public NewsDto shareNews(String news_id) {
        Optional<NewsInterface> news = newsRepository.shareNews(news_id);
        if (news.isEmpty()) return null;
        newsRepository.updateViews(news_id);  // 조회수 올리기

        String news_imgs = news.get().getImgs();
        String[] img_list = {};
        if (news_imgs != null) {
            img_list = news_imgs.split(",");
        }
        NewsDto newsDto = NewsDto.builder()
                .title(news.get().getTitle())
                .content(news.get().getContent())
                .imgs(img_list)
                .views(news.get().getViews())
                .reply(news.get().getReply())
                .like(news.get().getGood())
                .dislike(news.get().getBad())
                .build();

        return newsDto;
    }
}