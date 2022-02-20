package com.example.arenacinema_springproject.controllers;


import com.example.arenacinema_springproject.models.dto.*;
import com.example.arenacinema_springproject.models.entities.Cinema;
import com.example.arenacinema_springproject.models.entities.City;
import com.example.arenacinema_springproject.models.repositories.CinemaRepository;
import com.example.arenacinema_springproject.services.CinemaService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
public class CinemaController extends BaseController{

    @Autowired
    private CinemaService cinemaService;
    @Autowired
    private CinemaRepository cinemaRepository;
    @Autowired
    private ModelMapper modelMapper;

    @PostMapping("/cinemas")
    public ResponseEntity<CinemaWithCityAndHallsDTO> add(@RequestBody CinemaAddDTO cinema, HttpServletRequest request) {
        validateLogin(request);
        adminLogin(request);
        CinemaWithCityAndHallsDTO c = cinemaService.add(cinema);
        return ResponseEntity.ok(c);
    }

    @DeleteMapping("/cinemas/{id}")
    public void delete(@PathVariable int id, HttpServletRequest request) {
        validateLogin(request);
        adminLogin(request);
        Cinema c = cinemaService.getCinemaById(id);
        cinemaService.delete(c);

    }

    @PutMapping("/cinemas")
    public ResponseEntity<CinemaWithCityAndHallsDTO> edit(@RequestBody CinemaEditDTO cinema, HttpServletRequest request) {
        validateLogin(request);
        adminLogin(request);
        CinemaWithCityAndHallsDTO c = cinemaService.edit(cinema);
        return ResponseEntity.ok(c);
    }

    @GetMapping("/cinemas/{id}")
    public ResponseEntity <CinemaWithCityAndHallsDTO> getById(@PathVariable int id){
        CinemaWithCityAndHallsDTO c = cinemaService.getCinemaWithCityAndHallsDById(id);
        return ResponseEntity.ok(c);
    }


    @GetMapping("/cinemas")
    public ResponseEntity<List<CinemaWithHallsDTO>> getAll (@RequestBody City city) {
        List<CinemaWithHallsDTO> cinemas = cinemaService.getAllByCityName(city);
        return ResponseEntity.ok(cinemas);

    }
}
