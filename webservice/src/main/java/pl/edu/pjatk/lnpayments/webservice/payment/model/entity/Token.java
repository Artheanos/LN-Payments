package pl.edu.pjatk.lnpayments.webservice.payment.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sequence;

    private boolean delivered;

    public Token(String sequence) {
        this.sequence = sequence;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Token token = (Token) o;
        return sequence.equals(token.sequence);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sequence);
    }

}
