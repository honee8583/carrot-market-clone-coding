package com.carrot.carrotmarketclonecoding.health;

import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HealthController {
    private final Environment env;

    @GetMapping("/hc")
    public ResponseEntity<?> healthCheck() {
        Map<String, String> map = new HashMap<>();
        map.put("env", env.getProperty("server.env"));
        map.put("port", env.getProperty("server.port"));
        map.put("serverAddress", env.getProperty("server.serverAddress"));
        map.put("serverName", env.getProperty("serverName"));

        return ResponseEntity.ok().body(map);
    }

    @GetMapping("/env")
    public ResponseEntity<?> env() {
        return ResponseEntity.ok().body(env.getProperty("server.env"));
    }
}
