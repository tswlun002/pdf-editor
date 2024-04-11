package com.documentservice.email;

import com.documentservice.exception.InternalServerError;
import com.documentservice.kafka.DownloadEventProducer;
import com.documentservice.pdf.DownLoadDocumentEvent;
import com.documentservice.pdf.PDF;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
<<<<<<< HEAD
=======
import org.springframework.web.client.RestTemplate;
>>>>>>> 6fc2089 (removed redundent catch block)

@Slf4j
@RequiredArgsConstructor
@Validated
@Service
public class EmailService implements  IEmail {
    private final DownloadEventProducer producer;


    @Override
    public boolean sendEmail(PDF pdf) {
        return getResponse(pdf);
    }
    private @Valid Boolean getResponse(PDF pdf) {
      var response=false;
        try {
<<<<<<< HEAD
            response=producer.sendDownloadEvent(new DownLoadDocumentEvent(pdf.getEmail(), pdf.getImage()));
        } catch (Exception e) {
                log.info("Error:-----------> message:{},status code:{}", e.getMessage(),500,e);
=======

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            MultiValueMap<String, Object> body
                    = new LinkedMultiValueMap<>();
            body.add("pdf",  pdf.getImage());
            body.add("email", new EmailRequest(pdf.getEmail()));
            HttpEntity<MultiValueMap<String, Object>> requestEntity
                    = new HttpEntity<>(body, headers);
            response = restTemplate.postForEntity("http://localhost:8081/pdf-editor/email/send",requestEntity, String.class);

        } catch (Exception e) {
                log.info("Error:-----------> message:{},status code:{}", e.getMessage(),500);
>>>>>>> 6fc2089 (removed redundent catch block)
                throw new InternalServerError("Internal server error.");
        }
        return response;
    }
}
