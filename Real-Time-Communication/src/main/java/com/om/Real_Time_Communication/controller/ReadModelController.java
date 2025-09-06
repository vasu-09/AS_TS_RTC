package com.om.Real_Time_Communication.controller;


import org.springframework.web.bind.annotation.*;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Map;

@RestController
@RequestMapping("/api/readmodel")
@CrossOrigin(origins = "${cors.allowed-origins}")
public class ReadModelController {
    private final StringRedisTemplate redis;
    public ReadModelController(StringRedisTemplate redis) { this.redis = redis; }

    @GetMapping("/rooms/{roomId}/last")
    public String last(@PathVariable Long roomId) {
        return redis.opsForValue().get("room:last:" + roomId);
    }

    @GetMapping("/rooms/{roomId}/unread/{userId}")
    public long unread(@PathVariable Long roomId, @PathVariable Long userId) {
        Object v = redis.opsForHash().get("room:unread:" + roomId, String.valueOf(userId));
        return v == null ? 0 : Long.parseLong(v.toString());
    }
}