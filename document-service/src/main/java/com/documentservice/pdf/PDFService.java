package com.documentservice.pdf;

import com.documentservice.exception.InternalServerError;
import com.documentservice.exception.InvalidDocument;
import com.documentservice.exception.InvalidUser;
import com.documentservice.user.User;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PDFService implements IPDF{
    private final   PDFRepository repository;
    private final User user;
    private static final Logger logger= LoggerFactory.getLogger(PDFService.class);

    @Override
    public boolean saveDocument(String traceId,UserDocument userDocument) throws InvalidDocument, InvalidUser {
        if(userDocument==null){
            logger.error("Document try to save is invalid. traceId: {}, userDocument: {}", traceId,null);
            throw new InvalidDocument("Document try to save is invalid.");
        }
        var userDto = user.getUser(traceId,userDocument.email());
        var isSaved =false;
        try{

            var doc = repository.findByEmail(userDto.getEmail())
                    .stream().filter(d->d.getName().equals(userDocument.name()))
                    .toList();

            var pdf = PDF.builder().email(userDocument.email()).image(userDocument.file()).name(
                    doc.isEmpty()?userDocument.name():userDocument.name()+(doc.size()+1)+"_").build();
            repository.save(pdf);
            isSaved=true;
            logger.info("PDFService saveDocument, traceId: {}, userDocument: {}", traceId, userDocument);
        }
        catch (Exception e){
            logger.error("trace-Id: {}\nInternal error:\n{},{}",traceId,e.getMessage(),e.getCause().toString());
            throw new InternalServerError("Internal server error");
        }
        return isSaved ;
    }

    @Override
    public DocumentResponse updateDocument(String email) {
        return null;
    }

    @Override
    public Optional<PDF> findByIdAndEmail(String id, String email) {
        return repository.findByIdAndEmail(id,email);
    }

    @Override
    public List<PDF> findByEmail(String email) {
        return repository.findByEmail(email);
    }

}
