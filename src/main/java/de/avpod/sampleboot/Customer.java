package de.avpod.sampleboot;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * @author Andrei.Podznoev
 * Date    21.11.2017.
 */
@Entity
public class Customer {
    @Id
    @GeneratedValue
    long id;

    @Column(nullable = false)
    int cardNumber;

    @Column(nullable = false)
    String fullName;

    public Customer() {
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setCardNumber(int cardNumber) {
        this.cardNumber = cardNumber;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
