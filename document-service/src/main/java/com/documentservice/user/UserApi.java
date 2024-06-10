package com.documentservice.user;

import com.documentservice.exception.InvalidUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("gatewayserver")
public interface UserApi {
    @GetMapping(value = "/pdf-editor/users/{username}")
     ResponseEntity<UserDto> getUser(@PathVariable("username") String username) throws InvalidUser;
}
