package com.prz.edu.pl.ogloszone.health;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/health")
public class HealthCheckController {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public HealthCheckController(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @GetMapping
    public ResponseEntity<Health> checkHealth() {
        try {
            mongoTemplate.executeCommand("{ ping: 1 }");
            return ResponseEntity.ok(
                    Health.up()
                            .withDetail("database", "MongoDB is running")
                            .withDetail("checkedAt", LocalDateTime.now().toString())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(503)
                    .body(
                            Health.down()
                                    .withDetail("error", "MongoDB connection failed: " + e.getMessage())
                                    .withDetail("checkedAt", LocalDateTime.now().toString())
                                    .build()
                    );
        }
    }
}