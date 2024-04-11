package com.documentservice.email;

import com.documentservice.exception.InternalServerError;
import com.documentservice.pdf.PDF;
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

@Slf4j
@RequiredArgsConstructor
@Validated
@Service
public class EmailService implements  IEmail {
    private final RestTemplate restTemplate;

    @Override
    public boolean sendEmail(PDF pdf) {
        return getResponse(pdf);
    }

    private @Valid Boolean getResponse(PDF pdf) {
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
            body.add("pdf",  pdf.getImage());
            body.add("email", new EmailRequest(pdf.getEmail()));
            HttpEntity<MultiValueMap<String, Object>> requestEntity
                    = new HttpEntity<>(body, headers);
            response = restTemplate.postForEntity("http://localhost:8081/pdf-editor/email/send",requestEntity, String.class);

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                // Handle 404 error
                log.info("Error:-----------> message:{},status code:{}", e.getMessage(),4004);
                throw  new RuntimeException(e);
            }
        } catch (HttpServerErrorException e) {
                // Handle 500 error
                log.info("Error:-----------> message:{},status code:{}", e.getMessage(),500);
                throw new InternalServerError("Internal server error.");

        }
        return response != null && response.getStatusCode() == HttpStatus.OK;
    }
}
