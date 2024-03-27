package com.userservice.user;

import com.userservice.utils.Constant;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import java.util.Objects;

@Table(name = "Users")
@Entity
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
   @GeneratedValue(generator = "user_seq"
   ,strategy = GenerationType.SEQUENCE)

    @Id
    private  int Id;
    @NonNull
    @Column(unique = true, nullable = false)
    @Email(message = Constant.EMAIL_VALID_MESS)
    private  String email;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return  Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }
}
