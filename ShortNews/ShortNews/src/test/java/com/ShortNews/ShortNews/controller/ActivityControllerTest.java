package com.ShortNews.ShortNews.controller;

import com.ShortNews.ShortNews.repository.RecommendRepository;
import com.ShortNews.ShortNews.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ActivityControllerTest {


    @Autowired
    private ActivityService activityService;
    @Autowired
    private RecommendRepository recommendRepository;



}