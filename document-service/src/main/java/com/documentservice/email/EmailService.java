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
            response=producer.sendDownloadEvent(new DownLoadDocumentEvent(pdf.getEmail(), pdf.getImage()));
        } catch (Exception e) {
                log.info("Error:-----------> message:{},status code:{}", e.getMessage(),500);
                throw new InternalServerError("Internal server error.");
        }
        return response;
    }
}
