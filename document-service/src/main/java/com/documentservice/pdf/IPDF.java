package com.documentservice.pdf;

import com.documentservice.exception.InvalidDocument;

import java.util.List;
import java.util.Optional;

public interface IPDF {

    boolean saveDocument(UserDocument userDocument) throws InvalidDocument;
    DocumentResponse updateDocument(String email);

    Optional<PDF> findByIdAndEmail(String id, String email);
    List<PDF> findByEmail(String email);
}
