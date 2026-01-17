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
        List<ActivityNewsDto> list = new ArrayList<>();
        List<ActivityNewsInterface> like_list = recommendRepository.selectLikeNews(id, type);
        for (ActivityNewsInterface activityLike : like_list) {
            String news_imgs = activityLike.getImgs();
            String imgs = null;
            if (news_imgs != null) {
                String[] img_list = news_imgs.split(",");
                imgs = img_list[0];
            }

            ActivityNewsDto activityLikeDto = ActivityNewsDto.builder()
                    .news_id(activityLike.getNews_id())
                    .cate_id(activityLike.getCate_id())
                    .views(activityLike.getViews())
                    .like(activityLike.getGood())
                    .dislike(activityLike.getBad())
                    .title(activityLike.getTitle())
                    .reply(activityLike.getReply())
                    .imgs(imgs)
                    .bookmark(activityLike.getBookmark())
                    .build();
            list.add(activityLikeDto);
        }

        return list;
    }

    public List<ActivityReplyDto> reply(String id) {
        List<ActivityReplyDto> reply_list = new ArrayList<>();
        List<ActivityReplyInterface> replies = replyRepository.selectActivityReply(id);
        for (ActivityReplyInterface ari : replies) {
            ActivityReplyDto ard = new ActivityReplyDto();
            ard.setNews_id(ari.getNews_id());
            ard.setTitle(ari.getTitle());
            String news_imgs = ari.getImgs();
            String imgs = null;
            if (news_imgs != null) {
                String[] img_list = news_imgs.split(",");
                imgs = img_list[0];
            }
            ard.setImgs(imgs);
            if (ari.getUprid() == null) {
                ard.setReply_id(ari.getLowrid());
                ard.setId(ari.getLowuid());
                ard.setNickname(ari.getLownick());
                ard.setContent(ari.getLowcnt());
            } else {
                ard.setReply_id(ari.getUprid());
                ard.setId(ari.getUpuid());
                ard.setNickname(ari.getUpnick());
                ard.setContent(ari.getUpcnt());
                ard.setLow_rid(ari.getLowrid());
                ard.setLow_uid(ari.getLowuid());
                ard.setLow_nickname(ari.getLownick());
                ard.setLow_content(ari.getLowcnt());
            }
            reply_list.add(ard);
        }
        return reply_list;
    }
}