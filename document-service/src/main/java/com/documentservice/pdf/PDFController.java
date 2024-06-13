package com.documentservice.pdf;

import com.documentservice.email.EmailService;
import com.documentservice.exception.EntityNotFoundException;
import com.documentservice.exception.InvalidDocument;
import com.documentservice.exception.InvalidUser;
import com.itextpdf.text.DocumentException;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.NOT_ACCEPTABLE;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping(value = "pdf-editor/documents/")
@RequiredArgsConstructor
@Validated
public class PDFController {
    private  final IPDF service;
    private  final EmailService emailService;
    private static  final Logger logger =LoggerFactory.getLogger(PDFController.class);

    @PostMapping("save")
    public ResponseEntity<?> saveDocument(@RequestHeader("trace-Id") String traceId,@RequestParam @Email(message = "Email must be valid email address")
                                              String email) throws InvalidDocument, DocumentException, InvalidUser {
        log(traceId);
        var pdf = GeneratePDF.generatePdfStream();
        var name =email.substring(0, email.indexOf("@"));
        var save= service.saveDocument(traceId,new UserDocument(email,pdf.toByteArray(),name+"_file_" ));
        return  new ResponseEntity<>(save?"Document is uploaded":"Failed to upload document",save?OK:NOT_ACCEPTABLE);
    }
    @GetMapping("download/{email}/{id}")
    public  ResponseEntity<?> downloadDocument(@RequestHeader("trace-Id") String traceId,@PathVariable("email") String email,@PathVariable("id")String id){
        log(traceId);
        var doc=service.findByIdAndEmail(id,email).orElseThrow(
                ()->new EntityNotFoundException("Document is not found")
        );
        var isSent=emailService.sendEmail(doc);
        return  new ResponseEntity<>(isSent?"Document is sent to email.":"Failed to download document, please try again."
                ,isSent?OK:NOT_ACCEPTABLE);

    }

    @GetMapping("{email}")
    public  ResponseEntity<?> downloadDocument(@RequestHeader("trace-Id") String traceId,@PathVariable("email") String email){
        log(traceId);
        var docs=service.findByEmail(email)
        ;
        if(docs.isEmpty()) throw new EntityNotFoundException("Document is not found");
        var docResponse = docs.stream().map(d->new DocumentResponse(d.getImage(), d.getId()));
        return  new ResponseEntity<>(docResponse,OK);

    }
    private void log(String traceId){
        logger.info("PDFController trace-Id:{}",traceId);
    }


}
