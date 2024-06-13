package com.userservice.user;
import com.userservice.exeption.EntityNotFoundException;
import com.userservice.utils.Constant;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pdf-editor/users")
@Validated
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private  final IUser userService;
     @PostMapping("/register/")
     public  ResponseEntity<?> registerUser(@RequestHeader("trace-Id") String traceId,@RequestBody @Valid UserRegister register){
         log(traceId);
         return userService.createUser(traceId,register)?
                          new ResponseEntity<>("User registered successfully",HttpStatus.CREATED):
                          new ResponseEntity<>("Failed to register user",HttpStatus.EXPECTATION_FAILED);
     }
    @GetMapping("/{email}")
    public ResponseEntity<UserResponse> getUserByEmail(@RequestHeader("trace-Id") String traceId,@PathVariable("email")
                                                    @Email(message = Constant.EMAIL_VALID_MESS)
                                                    String email ) throws EntityNotFoundException {
       log(traceId);
        var user= userService.findUserByEmail(traceId,email).
                orElseThrow(()-> new EntityNotFoundException("User","Email",email));

        return new ResponseEntity<>(new UserResponse(user.getEmail()), HttpStatus.OK);
    }
    private  void log(String traceId){
        logger.info("UserController trace-Id: {}",traceId);
    }
}
