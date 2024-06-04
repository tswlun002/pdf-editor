package com.documentservice.user;

import com.documentservice.exception.InvalidUser;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserImpl implements User {
    private final UserApi userApi;
    private  final ModelMapper mapper;
    @Override
    public UserDto getUser(String username) throws InvalidUser {
        var userResponse = userApi.getUser(username);
        if(userResponse.getStatusCode()!= HttpStatus.OK) throw new InvalidUser("Invalid user was given\n");
        return mapper.map(userResponse.getBody(), UserDto.class);
    }
}
