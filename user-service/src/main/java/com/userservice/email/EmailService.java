package com.userservice.email;

import com.userservice.pdffile.PDF;
import com.userservice.user.IUser;
import com.userservice.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
@Slf4j
@RequiredArgsConstructor
@Validated
@Service
public class EmailService implements  IEmail {
    private final IUser user;
    private final RestTemplate restTemplate;

    @Override
    public Optional<Boolean> sendEmail( String username) {
        return user.findUserByEmail(username).map(this::getResponse);
    }

    private @Valid Boolean getResponse(@Valid User user) {
        ResponseEntity<String> response = null;
         record EmailRequest(
                String recipient

        ) {
        }
        try {

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            MultiValueMap<String, Object> body
                    = new LinkedMultiValueMap<>();
            body.add("pdf", PDF.generatePdfStream().toByteArray());
            body.add("email", new EmailRequest(user.getEmail()));
            HttpEntity<MultiValueMap<String, Object>> requestEntity
                    = new HttpEntity<>(body, headers);

           // var url = String.format("http://localhost:8081/pdf-editor/email/send/%s",user.getEmail());
            response = restTemplate.postForEntity("http://localhost:8081/pdf-editor/email/send",requestEntity, String.class);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.OK) {
                // Handle 404 error
                log.info("Error:-----------> message:{},status code:{}", e.getMessage(),4004);
            }
        } catch (HttpServerErrorException e) {
            if (e.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
                // Handle 500 error
                log.info("Error:-----------> message:{},status code:{}", e.getMessage(),500);
            }

        } catch (Exception e) {
            log.info("Error:-----------> message:{}", e.getMessage());
        }


        return response != null && response.getStatusCode() == HttpStatus.OK;
    }
}
