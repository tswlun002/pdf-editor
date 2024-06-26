package com.emailservice.email;

import com.emailservice.exception.MailSenderException;
import jakarta.activation.DataSource;
import jakarta.mail.Message;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.CharEncoding;
import org.springframework.core.env.Environment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.annotation.Validated;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.util.Objects;
import java.util.function.Function;
import static java.nio.charset.StandardCharsets.UTF_8;
@Slf4j
@Validated
@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    private final Environment environment;
    private Function<String,String> readFile =(path)->{
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        Resource resource = resourceLoader.getResource(path);
        return asString(resource);
    };
    public boolean sendEmail(@Email(message = "Enter valid email, e.g tswlun@gmail.com") String recipient, byte[] file  ) throws MailSenderException {

        MimeMessage message = mailSender.createMimeMessage();
        var sent =false;
        try {
            message.setFrom(new InternetAddress(Objects.requireNonNull(environment.getProperty("mail.username"))));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            message.setSubject("Edited Pdf File");
            String emailTemplate = readFile.apply("/emailTemplate.html");
            emailTemplate =emailTemplate.replace("${firstname}","customer") ;
            MimeMessageHelper helper =  new MimeMessageHelper(message,true, CharEncoding.UTF_8);
            final DataSource attachment = new ByteArrayDataSource(file, "application/octet-stream");
            helper.setText(emailTemplate,true);
            helper.addAttachment("updated.pdf", attachment);
            mailSender.send(message);
            sent=true;
            log.info("------------------> Sent message successfully to user email: {}",recipient);
        } catch ( Exception  e) {
           if(e instanceof  com.sun.mail.util.MailConnectException || e instanceof MailSendException
           ) {
               log.info("MailSenderException is thrown : {}", e.getMessage(), e);

               throw new MailSenderException(e.getCause().getMessage(), e);
           }
            log.info("DeadException is thrown : {}",e.getMessage(),e);
            throw new RuntimeException("Dead for mail sender exception",e);
        }
        return sent;
    }
    private String asString(Resource resource){
        try(Reader reader  = new InputStreamReader(resource.getInputStream(), UTF_8)){
            return FileCopyUtils.copyToString(reader);
        }catch (IOException e){
            throw  new UncheckedIOException(e);
        }
    }
}
