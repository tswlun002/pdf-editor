package com.documentservice.user;

import com.documentservice.exception.InvalidUser;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
@Slf4j
@Service
@AllArgsConstructor
public class UserImpl implements User {
    private final UserApi userApi;
    private  final ModelMapper mapper;
    private static Logger logger = LoggerFactory.getLogger(UserImpl.class);
    @Override
    public UserDto getUser(String traceId,String username) throws InvalidUser {
        logger.info("Getting user by username {}, trace-Id: {}", username,traceId);
        var userResponse = userApi.getUser(traceId,username);
        return mapper.map(userResponse.getBody(), UserDto.class);
    }
}
