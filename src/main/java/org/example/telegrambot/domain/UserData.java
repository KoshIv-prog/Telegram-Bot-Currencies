package org.example.telegrambot.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@NoArgsConstructor
@Setter
@Getter
@Table(name = "user_data")
public class UserData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @OneToOne()
    @JoinColumn(name = "custom_user_id", referencedColumnName = "id")
    private CustomUser userData;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "username")
    private String username;

    @Column(name = "date")
    private Date date;

    @Column(name = "transaction")
    private String transaction;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "notified")
    private Boolean notified;

    public UserData(Long id,
                    CustomUser userData,
                    String firstName,
                    String lastName,
                    String username,
                    Date date,
                    String transaction,
                    Double amount) {
        this.id = id;
        this.userData = userData;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.date = date;
        this.transaction = transaction;
        this.amount = amount;
        this.notified = false;
    }
}
