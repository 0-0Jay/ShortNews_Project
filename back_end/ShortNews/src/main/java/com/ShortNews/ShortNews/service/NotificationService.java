package com.ShortNews.ShortNews.service;

import com.ShortNews.ShortNews.controller.NotificationController;
import com.ShortNews.ShortNews.dto.AlarmDto;
import com.ShortNews.ShortNews.dto.AlarmInterface;
import com.ShortNews.ShortNews.entity.Notification;
import com.ShortNews.ShortNews.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    public SseEmitter subscribe(String id) {

        // 현재 클라이언트를 위한 SseEmitter 생성
        SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);
        try {
            // 연결!!
            sseEmitter.send(SseEmitter.event().name("connect"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // user의 pk값을 key값으로 해서 SseEmitter를 저장
        NotificationController.sseEmitters.put(id, sseEmitter);

        sseEmitter.onCompletion(() -> NotificationController.sseEmitters.remove(id));
        sseEmitter.onTimeout(() -> NotificationController.sseEmitters.remove(id));
        sseEmitter.onError((e) -> NotificationController.sseEmitters.remove(id));

        return sseEmitter;
    }

    public List<Object> getAlarm(String id) {
        List<AlarmInterface> list = notificationRepository.selectAlarm(id);
        List<Object> alarm_list = new ArrayList<>();
        for (AlarmInterface notification : list) {
            AlarmDto alarmDto = AlarmDto.builder()
                    .time(notification.getTime())
                    .link(notification.getLink())
                    .type(notification.getType())
                    .status(notification.getStatus())
                    .target_id(notification.getTarget_id())  // target_id가 알람을 받는 사람의 id
                    .nickname(notification.getNickname())  // nickname도 알람을 주는 사람의 nickname
                    .build();
            alarm_list.add(alarmDto);
        }

        return alarm_list;
    }
}
