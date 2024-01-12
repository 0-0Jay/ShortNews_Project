package com.ShortNews.ShortNews.service;

import com.ShortNews.ShortNews.dto.*;
import com.ShortNews.ShortNews.entity.Recommend;
import com.ShortNews.ShortNews.repository.NewsRepository;
import com.ShortNews.ShortNews.repository.RecommendRepository;
import com.ShortNews.ShortNews.repository.ReplyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ActivityService {
    @Autowired
    private RecommendRepository recommendRepository;
    @Autowired
    private NewsRepository newsRepository;
    @Autowired
    private ReplyRepository replyRepository;

    public List<LikeDto> getLike(String id) {
        List<LikeDto> likedto = new ArrayList<>();
        List<Recommend> recommend_list = recommendRepository.getLike(id);

        for (Recommend recommend : recommend_list) {
            LikeDto likeDto = LikeDto.builder()
                    .title(newsRepository.selectTitle(recommend.getNews_id()))
                    .type(recommend.getType())
                    .date(recommend.getNews_id().substring(0, 8))
                    .build();
            likedto.add(likeDto);
        }

        return likedto;
    }

    public List<DisLikeDto> getDisLike(String id) {
        List<DisLikeDto> disLikedto = new ArrayList<>();
        List<Recommend> recommend_list = recommendRepository.getDisLike(id);

        for (Recommend recommend : recommend_list) {
            DisLikeDto dislikeDto = DisLikeDto.builder()
                    .title(newsRepository.selectTitle(recommend.getNews_id()))
                    .type(recommend.getType())
                    .date(recommend.getNews_id().substring(0, 8))
                    .build();
            disLikedto.add(dislikeDto);
        }

        return disLikedto;
    }

    public List<ActivityLikeDto> getReplyLike(String id) {
        List<ActivityLikeDto> activityLikeDtoList = new ArrayList<>();
        for (ActivityLikeInterface activityLikeInterface : recommendRepository.getReplyLike(id)) {
            ActivityLikeDto activityLikeDto = ActivityLikeDto.builder()
                    .id(activityLikeInterface.getId())
                    .content(activityLikeInterface.getContent())
                    .type(activityLikeInterface.getType())
                    .time(activityLikeInterface.getTime())
                    .build();
            activityLikeDtoList.add(activityLikeDto);
        }
        return activityLikeDtoList;
    }

    public List<ActivityDisLikeDto> getReplyDisLike(String id) {
        List<ActivityDisLikeDto> activityDisLikeDtoList = new ArrayList<>();
        for (ActivityDisLikeInterface activityDisLikeInterface : recommendRepository.getReplyDisLike(id)) {
            ActivityDisLikeDto activityDisLikeDto = ActivityDisLikeDto.builder()
                    .id(activityDisLikeInterface.getId())
                    .content(activityDisLikeInterface.getContent())
                    .type(activityDisLikeInterface.getType())
                    .time(activityDisLikeInterface.getTime())
                    .build();
            activityDisLikeDtoList.add(activityDisLikeDto);
        }
        return activityDisLikeDtoList;
    }

    public List<Map<String, Object>> reply(String id) {
        List<ActivityReplyDto> replyList = new ArrayList<>();
        for (ActivityReplyInterface activityReplyInterface : replyRepository.selectActivityReply(id)) {
            ActivityReplyDto dto = new ActivityReplyDto(
                    activityReplyInterface.getTitle(),
                    activityReplyInterface.getContent(),
                    activityReplyInterface.getTime()
            );
            replyList.add(dto);
        }

        List<Map<String, Object>> list = new ArrayList<>();
        for (ActivityReplyDto rp : replyList) {
            Map<String, Object> map = new HashMap<>();
            map.put("title", rp.getTitle());
            map.put("reply", rp.getContent());
            String tmp = rp.getTime();
            String date = tmp.substring(0, 4) + "-" +
                    tmp.substring(4, 6) + "-" +
                    tmp.substring(6, 8);
            map.put("date", date);
            list.add(map);
        }
        return list;
    }
}
