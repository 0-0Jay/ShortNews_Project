package com.ShortNews.ShortNews.service;

import com.ShortNews.ShortNews.dto.*;
import com.ShortNews.ShortNews.repository.RecommendRepository;
import com.ShortNews.ShortNews.repository.ReplyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ActivityService {
    @Autowired
    private RecommendRepository recommendRepository;
    @Autowired
    private ReplyRepository replyRepository;

    public List<ActivityNewsDto> getLikeOrDisLike(String id, int type) {
        System.out.println(id);
        List<ActivityNewsDto> list = new ArrayList<>();
        List<ActivityNewsInterface> like_list = recommendRepository.selectLikeNews(id, type);
        for (ActivityNewsInterface activityLike : like_list) {
            ActivityNewsDto activityLikeDto = ActivityNewsDto.builder()
                    .news_id(activityLike.getNews_id())
                    .cate_id(activityLike.getCate_id())
                    .views(activityLike.getViews())
                    .like(activityLike.getGood())
                    .dislike(activityLike.getBad())
                    .title(activityLike.getTitle())
                    .reply(activityLike.getReply())
                    .img(activityLike.getImgs())
                    .bookmark(activityLike.getBookmark())
                    .build();
            list.add(activityLikeDto);
        }
        return list;
    }

    public List<Map<String, Object>> reply(String id) {
        List<ActivityReplyDto> replyList = new ArrayList<>();
        for (ActivityReplyInterface activityReplyInterface : replyRepository.selectActivityReply(id)) {
            ActivityReplyDto dto = new ActivityReplyDto(
                    activityReplyInterface.getNews_id(),
                    activityReplyInterface.getTitle(),
                    activityReplyInterface.getContent(),
                    activityReplyInterface.getTime(),
                    activityReplyInterface.getImgs()
            );
            replyList.add(dto);
        }

        List<Map<String, Object>> list = new ArrayList<>();
        for (ActivityReplyDto rp : replyList) {
            Map<String, Object> map = new HashMap<>();
            map.put("news_id", rp.getNews_id());
            map.put("title", rp.getTitle());
            map.put("reply", rp.getContent());
            String tmp = rp.getTime();
            String date = tmp.substring(0, 4) + "-" +
                    tmp.substring(4, 6) + "-" +
                    tmp.substring(6, 8);
            map.put("date", date);
            map.put("img", rp.getImg());
            list.add(map);
        }
        return list;
    }
}