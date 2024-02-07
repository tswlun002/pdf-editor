package com.emailservice.email;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.Part;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.core.env.Environment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import java.io.*;
import java.util.Objects;
import java.util.Properties;
import java.util.function.BiFunction;
import java.util.function.Function;
import static java.nio.charset.StandardCharsets.UTF_8;
@Slf4j
@Service
@AllArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    private final Environment environment;

    public boolean sendEmail(@Email String recipient, Part filePart  ) {

        Function<String,String> readFile =(path)->{
            ResourceLoader resourceLoader = new DefaultResourceLoader();
            Resource resource = resourceLoader.getResource(path);
            return asString(resource);
        };

        BiFunction<Part, String, File> partToFile = (part, filename)->{
            try {
                var data=  part.getInputStream().readAllBytes();
                var file = new File(filename);
                 FileUtils.writeByteArrayToFile(file,data);
                 return  file;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };

        MimeMessage message = mailSender.createMimeMessage();
        Properties properties = System.getProperties();
        properties.setProperty("mail.smtp.host", Objects.requireNonNull(environment.getProperty("mail.protocol")));

        var sent =false;
        try {

            message.setFrom(new InternetAddress(Objects.requireNonNull(environment.getProperty("mail.host"))));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            message.setSubject("EDITED PDF FILE");
            String emailTemplate = readFile.apply("emailTemplate.html");
            emailTemplate =emailTemplate.replace("${firstname}","Name") ;
            message.setContent(emailTemplate,"text/html; charset=utf-8");

            MimeMessageHelper helper =  new MimeMessageHelper(message);
            helper.addAttachment("Name-edited.pdf",partToFile.apply(filePart,"Name-edited.pdf"));
            sent=true;
            log.info("------------------> Sent message successfully to user email: {}",recipient);
        } catch (
                MessagingException mex) {
            log.info("------------------> Failed to send email  with attachment to user: {}",recipient);
            mex.printStackTrace();
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
