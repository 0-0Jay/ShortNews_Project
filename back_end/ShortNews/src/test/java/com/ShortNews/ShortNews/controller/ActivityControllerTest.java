package com.ShortNews.ShortNews.controller;

import com.ShortNews.ShortNews.repository.RecommendRepository;
import com.ShortNews.ShortNews.service.ActivityService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class ActivityControllerTest {


    @Autowired
    private ActivityService activityService;
    @Autowired
    private RecommendRepository recommendRepository;



}