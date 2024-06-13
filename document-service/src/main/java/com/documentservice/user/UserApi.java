package com.documentservice.user;

import com.documentservice.exception.InvalidUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient("gatewayserver")
public interface UserApi {
    @GetMapping(value = "/pdf-editor/users/{username}")
     ResponseEntity<UserDto> getUser(@RequestHeader("trace-Id") String traceId, @PathVariable("username") String username) throws InvalidUser;
}
