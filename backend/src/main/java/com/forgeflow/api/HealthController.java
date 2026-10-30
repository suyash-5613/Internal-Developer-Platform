package com.forgeflow.api;

import com.forgeflow.github.GitHubService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    private final JdbcTemplate jdbcTemplate;
    private final GitHubService gitHubService;

    public HealthController(JdbcTemplate jdbcTemplate, GitHubService gitHubService) {
        this.jdbcTemplate = jdbcTemplate;
        this.gitHubService = gitHubService;
    }

    @GetMapping("/api/health")
    public ResponseEntity<HealthResponse> health() {
        String githubStatus = gitHubService.isConfigured() ? "Connected" : "Not connected";
        return ResponseEntity.ok(new HealthResponse("UP", databaseStatus(), githubStatus, "ForgeFlow API is running"));
    }

    private String databaseStatus() {
        try {
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            return "UP";
        } catch (RuntimeException exception) {
            return "UNAVAILABLE";
        }
    }

    public record HealthResponse(String status, String database, String github, String message) {
    }
}
