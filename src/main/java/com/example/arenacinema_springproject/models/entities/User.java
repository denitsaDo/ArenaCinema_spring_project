package com.example.arenacinema_springproject.models.entities;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;


@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@ToString

public class User {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column
    private String firstName;
    @Column
    private String secondName;
    @Column
    private String lastName;
    @Column
    private String gender;
    @Column
    private String email;
    @Column
    private String password;
    @Column
    private Date dateOfBirth;
    @Column
    private boolean isAdmin;

    @OneToMany(mappedBy = "userRatesMovie")
    Set<UsersRateMovies> ratings;

}
