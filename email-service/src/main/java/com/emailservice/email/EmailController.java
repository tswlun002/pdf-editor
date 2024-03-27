package com.emailservice.email;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

import static org.springframework.http.HttpStatus.NOT_ACCEPTABLE;
import static org.springframework.http.HttpStatus.OK;

@RestController
@AllArgsConstructor
@RequestMapping("pdf-editor/email")
@Validated
public class EmailController {
    private final  EmailService emailService;
    @PostMapping(value = "/send",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?>  sendEmail(HttpServletRequest request) throws ServletException, IOException {

        Part sendEmailRequest = request.getPart("email");
       Part pdfPart = request.getPart("pdf");
       if(sendEmailRequest==null) throw  new RuntimeException("Null email request");
       EmailRequest emailBody = new ObjectMapper().readValue(sendEmailRequest.getInputStream().readAllBytes(),EmailRequest.class);
       return emailService.sendEmail(emailBody.recipient(),pdfPart)?
               new ResponseEntity<>("Edited pdf file is sent to your email",OK):
               new ResponseEntity<>("Failed to send pdf to email", NOT_ACCEPTABLE);
    }
}
