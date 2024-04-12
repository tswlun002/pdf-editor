package com.documentservice.pdf;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PDFRepository extends MongoRepository<PDF,Integer> {
    List<PDF> findByEmail(String email);

    Optional<PDF> findByIdAndEmail(String id, String email);
}
