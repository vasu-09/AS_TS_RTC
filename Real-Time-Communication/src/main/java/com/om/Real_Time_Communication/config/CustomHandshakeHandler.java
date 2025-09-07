package com.om.Real_Time_Communication.config;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;
import java.util.Set;

public class CustomHandshakeHandler extends DefaultHandshakeHandler {

    public CustomHandshakeHandler() {
        // Advertise support for the "bearer" subprotocol so clients can send the
        // JWT as an additional comma-separated value ("bearer,<token>") in the
        // Sec-WebSocket-Protocol header. Spring's DefaultHandshakeHandler will
        // echo the supported subprotocol back to the client which avoids
        // InvalidSubProtocol exceptions from strict WebSocket clients such as
        // Spring's Reactor Netty implementation.
        setSupportedProtocols("bearer");
    }
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
