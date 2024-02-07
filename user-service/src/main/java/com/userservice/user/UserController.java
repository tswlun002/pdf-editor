package com.userservice.user;

import com.userservice.exeption.EntityNotFoundException;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/pdf-editor/user")
@Validated
public class UserController {
    private  final IUser userService;
     @PostMapping("/register/")
     public  ResponseEntity<?> registerUser(@RequestBody UserRegister register){
          return userService.createUser(register)?
                          new ResponseEntity<>("User registered successfully",HttpStatus.CREATED):
                          new ResponseEntity<>("Failed to register user",HttpStatus.EXPECTATION_FAILED);
     }
    @GetMapping("/{email}")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable("email")
                                                    @Email(message = "Enter valid email, e.g tswlun@gmail.com")
                                                    String email ) throws EntityNotFoundException {

        var user= userService.findUserByEmail(email).
                orElseThrow(()-> new EntityNotFoundException("User","Email",email));

        return new ResponseEntity<>(new UserResponse(user.getEmail()), HttpStatus.OK);
    }
}
