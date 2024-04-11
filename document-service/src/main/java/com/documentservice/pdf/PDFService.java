package com.documentservice.pdf;

import com.documentservice.exception.InternalServerError;
import com.documentservice.exception.InvalidDocument;
<<<<<<< HEAD
import com.documentservice.exception.InvalidUser;
import com.documentservice.user.User;
=======
>>>>>>> 8452314 (moved all the classes related to document creation to document service)
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class PDFService implements IPDF{
    private final   PDFRepository repository;
<<<<<<< HEAD
    private final User user;

    @Override
    public boolean saveDocument(UserDocument userDocument) throws InvalidDocument, InvalidUser {
        if(userDocument==null)throw new InvalidDocument("Document try to save is invalid");
        var userDto = user.getUser(userDocument.email());
        var isSaved =false;
        try{

            var doc = repository.findByEmail(userDto.getEmail())
=======
    @Override
    public boolean saveDocument(UserDocument userDocument) throws InvalidDocument {
        if(userDocument==null)throw new InvalidDocument("Document try to save is invalid");

        var isSaved =false;
        try{
            var doc = repository.findByEmail(userDocument.email())
>>>>>>> 8452314 (moved all the classes related to document creation to document service)
                    .stream().filter(d->d.getName().equals(userDocument.name()))
                    .toList();

            var pdf = PDF.builder().email(userDocument.email()).image(userDocument.file()).name(
                    doc.isEmpty()?userDocument.name():userDocument.name()+(doc.size()+1)+"_").build();
            repository.save(pdf);
            isSaved=true;
        }
        catch (Exception e){
            log.info("\nInternal error:\n{},{}",e.getMessage(),e.getCause().toString());
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
