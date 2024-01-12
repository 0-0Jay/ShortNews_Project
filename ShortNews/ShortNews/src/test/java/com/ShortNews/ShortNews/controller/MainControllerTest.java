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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

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
    void selectNews() {

        //given
        String id = "jongho";
        String news_id = "2023122620145";

        //when
        NewsDto newsDto = mainService.selectNews(id, news_id);

        //then
        Assertions.assertThat(newsDto.getViews()).isEqualTo(4);
    }

    @Test
    void mainBookmark() {

        //given
        String id = "jongho12";
        String news_id = "202312262083";

        //when
        mainService.insertBookmark(id, news_id);

        BookmarkKey bookmarkKey = BookmarkKey.builder()
                .id(id)
                .news_id(news_id)
                .build();
        Optional<Bookmark> list = bookmarkRepository.findById(bookmarkKey);

        //then
        Assertions.assertThat(list.isEmpty()?0:1).isEqualTo(1);

    }

    @Test
    void mainDisLike() {

        //given
        String id = "jongho";
        String news_id = "2023122620144";
        String type = "off";

        //when
        mainService.disLike(id, news_id, type);
        Optional<Recommend> list = recommendRepository.findById("2023122822");

        //then
        Assertions.assertThat(list.isEmpty()?0:1).isEqualTo(1);
    }

    @Test
    void mainLink() {

        //given
        String news_id = "2023122620144";

        //when

        //then
        Assertions.assertThat(mainService.selectLink(news_id)).isEqualTo("https://entertain.naver.com/now/read?oid=213&aid=0001281400");

    }

}