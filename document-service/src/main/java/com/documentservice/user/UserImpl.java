package com.documentservice.user;

import com.documentservice.exception.InvalidUser;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
@Slf4j
@Service
@AllArgsConstructor
public class UserImpl implements User {
    private final UserApi userApi;
    private  final ModelMapper mapper;
    @Override
    public UserDto getUser(String username) throws InvalidUser {
        var userResponse = userApi.getUser(username);
        return mapper.map(userResponse.getBody(), UserDto.class);
    }
}
