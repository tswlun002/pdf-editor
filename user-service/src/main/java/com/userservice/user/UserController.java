package com.userservice.user;

import com.userservice.email.IEmail;
import com.userservice.exeption.EntityNotFoundException;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pdf-editor/user")
@Validated
public class UserController {
    private  final IUser userService;
    private  final IEmail emailService;
     @PostMapping("/register/")
     public  ResponseEntity<?> registerUser(@RequestBody UserRegister register){
          return userService.createUser(register)?
                          new ResponseEntity<>("User registered successfully",HttpStatus.CREATED):
                          new ResponseEntity<>("Failed to register user",HttpStatus.EXPECTATION_FAILED);
     }
    @GetMapping("/{email}")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable("email")
                                                    @Email(message = "{com.userservice.utils.EMAIL_VALID_MESS}")
                                                    String email ) throws EntityNotFoundException {

        var user= userService.findUserByEmail(email).
                orElseThrow(()-> new EntityNotFoundException("User","Email",email));

        return new ResponseEntity<>(new UserResponse(user.getEmail()), HttpStatus.OK);
    }
    @GetMapping("/download/{email}")
    public  ResponseEntity<?> downloadPdf(@PathVariable("email") @Email(message = "{com.userservice.utils.EMAIL_VALID_MESS}")
                                         String email){
         var resp= emailService.sendEmail(email);
         var isDownloaded =resp.isPresent()&&resp.get();
         return  new ResponseEntity<>(isDownloaded?"Pdf is sent to email.":"Failed to send email.",
                 isDownloaded?HttpStatus.OK:HttpStatus.BAD_REQUEST);
    }
}
