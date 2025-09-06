package com.om.Real_Time_Communication.config;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;
import java.util.Set;

public class CustomHandshakeHandler extends DefaultHandshakeHandler {
    @Override
    protected Principal determineUser(ServerHttpRequest req, WebSocketHandler h, Map<String,Object> attrs) {
        var p = (Principal) attrs.get("principal");
        if (p != null) return p;
        Long userId = (Long) attrs.get("userId");
        @SuppressWarnings("unchecked") Set<String> roles = (Set<String>) attrs.get("roles");
        String tenant = (String) attrs.get("tenant");
        return (userId!=null) ? new JwtHandshakeInterceptor.WsUserPrincipal(userId, roles, tenant) : null;
    }


}
