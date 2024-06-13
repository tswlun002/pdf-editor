package com.documentservice.email;

import com.documentservice.exception.InternalServerError;
import com.documentservice.kafka.DownloadEventProducer;
import com.documentservice.pdf.DownLoadDocumentEvent;
import com.documentservice.pdf.PDF;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@RequiredArgsConstructor
@Validated
@Service
public class EmailService implements  IEmail {
    private final DownloadEventProducer producer;
    private static  final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);


    @Override
    public boolean sendEmail(String traceId,PDF pdf) {
        return getResponse(traceId,pdf);
    }
    private @Valid Boolean getResponse(String traceId,PDF pdf) {
      var response=false;
        try {
            response=producer.sendDownloadEvent(new DownLoadDocumentEvent(traceId,pdf.getEmail(), pdf.getImage()));
        } catch (Exception e) {
                LOGGER.error("Internal server error. Trace-Id: {}", traceId,e);
                throw new InternalServerError("Internal server error.");
        }
        return response;
    }
}
