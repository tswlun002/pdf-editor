package com.documentservice.pdf;

import com.documentservice.email.EmailService;
import com.documentservice.exception.EntityNotFoundException;
import com.documentservice.exception.InvalidDocument;
<<<<<<< HEAD
import com.documentservice.exception.InvalidUser;
=======
>>>>>>> 8452314 (moved all the classes related to document creation to document service)
import com.itextpdf.text.DocumentException;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
<<<<<<< HEAD

=======
>>>>>>> 8452314 (moved all the classes related to document creation to document service)
import static org.springframework.http.HttpStatus.NOT_ACCEPTABLE;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping(value = "pdf-editor/documents/")
@RequiredArgsConstructor
@Validated
public class PDFController {
    private  final IPDF service;
    private  final EmailService emailService;

    @PostMapping("save")
    public ResponseEntity<?> saveDocument(@RequestParam @Email(message = "Email must be valid email address")
<<<<<<< HEAD
                                              String email) throws InvalidDocument, DocumentException, InvalidUser {
=======
                                              String email) throws InvalidDocument, DocumentException {
>>>>>>> 8452314 (moved all the classes related to document creation to document service)
        var pdf = GeneratePDF.generatePdfStream();
        var name =email.substring(0, email.indexOf("@"));
        var save= service.saveDocument(new UserDocument(email,pdf.toByteArray(),name+"_file_" ));
        return  new ResponseEntity<>(save?"Document is uploaded":"Failed to upload document",save?OK:NOT_ACCEPTABLE);
    }
    @GetMapping("download/{email}/{id}")
    public  ResponseEntity<?> downloadDocument(@PathVariable("email") String email,@PathVariable("id")String id){
        var doc=service.findByIdAndEmail(id,email).orElseThrow(
                ()->new EntityNotFoundException("Document is not found")
        );
        var isSent=emailService.sendEmail(doc);
        return  new ResponseEntity<>(isSent?"Document is sent to email.":"Failed to download document, please try again."
                ,isSent?OK:NOT_ACCEPTABLE);

    }

    @GetMapping("{email}")
    public  ResponseEntity<?> downloadDocument(@PathVariable("email") String email){
        var docs=service.findByEmail(email)
        ;
        if(docs.isEmpty()) throw new EntityNotFoundException("Document is not found");
        var docResponse = docs.stream().map(d->new DocumentResponse(d.getImage(), d.getId()));
        return  new ResponseEntity<>(docResponse,OK);

    }


}
