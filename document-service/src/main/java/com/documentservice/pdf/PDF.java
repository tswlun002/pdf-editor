package com.documentservice.pdf;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Arrays;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@Document(collection = "pdfs")
public class PDF {
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private String id;
    @NonNull
    @Indexed(unique = true)
    private String name;
    private byte[] image;
    private String email;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PDF pdf = (PDF) o;
        return Objects.equals(id,pdf.id) && Objects.equals(name, pdf.name) && Arrays.equals(image, pdf.image) && Objects.equals(email, pdf.email);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, name, email);
        result = 31 * result + Arrays.hashCode(image);
        return result;
    }
}
