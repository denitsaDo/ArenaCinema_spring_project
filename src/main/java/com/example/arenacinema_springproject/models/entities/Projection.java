package com.example.arenacinema_springproject.models.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "projections")
@Getter
@Setter
@NoArgsConstructor
public class Projection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "movie_id")
    private Movie movieForProjection;

    @ManyToOne
    @JoinColumn(name = "hall_id")
    private Hall hallForProjection;

    @ManyToOne
    @JoinColumn(name = "type_id")
    private Type typeForProjection;

    @Column
    private LocalDateTime startTime;

    @OneToMany(mappedBy = "projectionIdForTicket")
    private Set<Ticket> projectionTickets;
}
