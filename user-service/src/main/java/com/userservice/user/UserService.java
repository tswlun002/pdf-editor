package com.userservice.user;

import com.userservice.exeption.EntityAlreadyExistException;
import com.userservice.exeption.InvalidEntityException;
import com.userservice.utils.ExceptionMessages;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;


@Service
@RequiredArgsConstructor
@Validated
public class UserService implements IUser {
    private  final UserRepository repository;
    private  static  final Logger logger = LoggerFactory.getLogger(UserService.class);
    @Override
    public boolean createUser(String traceId,UserRegister userRegister)  {
        logger.info("Create trace-Id: {}, user: {}",traceId,userRegister);
        if(userRegister==null)
        {
            var error = ExceptionMessages.FORMAT_EXCEPTION_MESSAGE.apply(ExceptionMessages.INVALID_RESOURCE, new String[]{"User"});
            logger.info(error,"trace-Id: {}",traceId);
            throw  new InvalidEntityException("User");
        }

        repository.findUserByEmail(userRegister.email()).
                ifPresent(user-> {
                    var error = ExceptionMessages.FORMAT_EXCEPTION_MESSAGE.apply(ExceptionMessages.RESOURCE_EXIST, new String[]{"User","email",user.getEmail()});
                    logger.error(error,"trace-Id: {}",traceId);
                    throw new EntityAlreadyExistException("User","email",user.getEmail());
                });

        var saved = false;
        try{
             repository.save(User.builder().email(userRegister.email()).build());
             saved=true;
            logger.info("User is saved to db. trace-Id: {}, user: {}",traceId,userRegister);
        }catch (Exception e){
          logger.error("Internal error {}, trace-Id: {}",e.getMessage(),traceId);
        }
        return saved;
    }

    @Override
    public Optional<User> findUserByEmail(String traceId,String email) {
        logger.info("Fetch user  trace-Id: {}, email: {}",traceId,email);
        return repository.findUserByEmail(email);
    }
}
