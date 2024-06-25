package com.gatewayserver.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/pdf-editor/fallback")
public class FallbackController {

    @GetMapping("/contact-team")
    public Mono<String> contactTeam() {
        return Mono.just("An error occurred, please try again or contact the team for support.");
    }
}
