package com.example.arenacinema_springproject.models.repositories;

import com.example.arenacinema_springproject.models.entities.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Integer> {

    Movie findByDescription(String description);

}
