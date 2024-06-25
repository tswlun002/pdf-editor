package com.documentservice.user;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(value = "users",fallbackFactory = UserServiceFallbackFactory.class)
public interface UserApi {
    @GetMapping(value = "/pdf-editor/users/{username}")
    ResponseEntity<?> getUser(@RequestHeader("trace-Id") String traceId, @PathVariable("username") String username);

}
