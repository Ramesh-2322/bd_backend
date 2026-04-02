package com.bdms.service.impl;

import com.bdms.service.RealtimeNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RealtimeNotificationServiceImpl implements RealtimeNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void publish(String topic, Object payload) {
        messagingTemplate.convertAndSend(topic, payload);
    }
}
