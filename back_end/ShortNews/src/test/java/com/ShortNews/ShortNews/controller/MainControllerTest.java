package com.ShortNews.ShortNews.controller;

import com.ShortNews.ShortNews.dto.NewsDto;
import com.ShortNews.ShortNews.entity.Bookmark;
import com.ShortNews.ShortNews.entity.BookmarkKey;
import com.ShortNews.ShortNews.entity.Recommend;
import com.ShortNews.ShortNews.repository.BookmarkRepository;
import com.ShortNews.ShortNews.repository.NewsRepository;
import com.ShortNews.ShortNews.repository.RecommendRepository;
import com.ShortNews.ShortNews.service.MainService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@SpringBootTest
class MainControllerTest {

    @Autowired
    private MainService mainService;
    @Autowired
    private BookmarkRepository bookmarkRepository;
    @Autowired
    private RecommendRepository recommendRepository;
    @Autowired
    private NewsRepository newsRepository;



    @Test
    void mainLink() {

        //given
        String news_id = "2023122620144";

        //when

        //then
        Assertions.assertThat(mainService.selectLink(news_id)).isEqualTo("https://entertain.naver.com/now/read?oid=213&aid=0001281400");

    }

}