package com.carrot.carrotmarketclonecoding.notification.controller;

import static com.carrot.carrotmarketclonecoding.common.response.SuccessMessage.GET_ALL_NOTIFICATIONS_SUCCESS;

import com.carrot.carrotmarketclonecoding.auth.dto.LoginUser;
import com.carrot.carrotmarketclonecoding.common.response.ResponseResult;
import com.carrot.carrotmarketclonecoding.notification.dto.NotificationResponseDto;
import com.carrot.carrotmarketclonecoding.notification.service.SseEmitterService;
import com.carrot.carrotmarketclonecoding.notification.service.impl.NotificationServiceImpl;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
public class NotificationController {
    private final SseEmitterService sseEmitterService;
    private final NotificationServiceImpl notificationService;

    @GetMapping("/notification")
    public ResponseEntity<?> getAllNotifications(@AuthenticationPrincipal LoginUser loginUser) {
        List<NotificationResponseDto> notifications =
                notificationService.getAllNotifications(Long.parseLong(loginUser.getUsername()));
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseResult.success(HttpStatus.OK, GET_ALL_NOTIFICATIONS_SUCCESS.getMessage(), notifications));
    }

    @GetMapping(value = "/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> connect(@AuthenticationPrincipal LoginUser loginUser) {
        Long id = Long.parseLong(loginUser.getUsername());
        return ResponseEntity.ok(sseEmitterService.add(id));
    }

    // TODO 읽음처리

}