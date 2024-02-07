package com.userservice.user;

import com.userservice.exeption.EntityAlreadyExistException;
import com.userservice.exeption.InvalidEntityException;
import com.userservice.utils.ExceptionMessages;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserService implements IUser {
    private  final UserRepository repository;
    @Override
    public boolean createUser(UserRegister userRegister)  {
        if(userRegister==null)
        {
            var error = ExceptionMessages.FORMAT_EXCEPTION_MESSAGE.apply(ExceptionMessages.INVALID_RESOURCE, new String[]{"User"});
            log.info(error);
            throw  new InvalidEntityException("User");
        }

        repository.findUserByEmail(userRegister.email()).
                ifPresent(user-> {
                    var error = ExceptionMessages.FORMAT_EXCEPTION_MESSAGE.apply(ExceptionMessages.RESOURCE_EXIST, new String[]{"User","email",user.getEmail()});
                    log.info(error);
                    throw new EntityAlreadyExistException("User","email",user.getEmail());
                });

        var saved = false;
        try{
             repository.save(User.builder().email(userRegister.email()).build());
             saved=true;
        }catch (Exception e){
          log.info("Internal error {}",e.getMessage());
        }
        if(saved) log.info("User is saved db.");
        return saved;
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        return repository.findUserByEmail(email);
    }
}
