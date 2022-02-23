package com.example.arenacinema_springproject.services;


import com.example.arenacinema_springproject.exceptions.BadRequestException;
import com.example.arenacinema_springproject.exceptions.NotFoundException;
import com.example.arenacinema_springproject.models.dto.*;
import com.example.arenacinema_springproject.models.entities.Cinema;
import com.example.arenacinema_springproject.models.entities.Hall;
import com.example.arenacinema_springproject.models.repositories.CinemaRepository;
import com.example.arenacinema_springproject.models.repositories.HallRepository;
import org.aspectj.asm.IModelFilter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HallService {

    @Autowired
    private HallRepository hallRepository;
    @Autowired
    private CinemaRepository cinemaRepository;
    @Autowired
    private ModelMapper modelMapper;

    public HallWithCinemaDTO addHall(HallAddDTO hall) {
        validateHallData(hall);

        Hall h = new Hall();
        h.setName(hall.getName());
        h.setRowsNumber(hall.getRowsNumber());
        h.setSeatsPerRow(hall.getSeatsPerRow());
        h.setCinemaIn(cinemaRepository.findById(hall.getCinemaId()).orElseThrow(() -> new NotFoundException("Cinema not found")));
        hallRepository.save(h);
        HallWithCinemaDTO dto = new HallWithCinemaDTO();
        modelMapper.map(h,dto);
        dto.setCinemaForThisHall(modelMapper.map(h.getCinemaIn(), CinemaWithoutHallDTO.class));
        return dto;

    }


    public Hall getHallById(int id) {
        return hallRepository.findById(id).orElseThrow(() -> new NotFoundException("Hall not found"));
    }


    public void delete(Hall hall) {
        hallRepository.delete(hallRepository.findById(hall.getId()).orElseThrow(() -> new NotFoundException("No such hall.")));
    }

    public HallWithCinemaDTO edit(HallEditDTO hall) {
//        validateHallData(modelMapper.map(hall, HallAddDTO.class));
        Optional<Hall> opt = hallRepository.findById(hall.getId());
        if(opt.isPresent()){
            Hall h = modelMapper.map(hall, Hall.class);
            h.setCinemaIn(cinemaRepository.findById(hall.getCinemaId()).orElseThrow(()-> new BadRequestException("No such cinema")));
            hallRepository.save(h);
            HallWithCinemaDTO dto = new HallWithCinemaDTO();
            modelMapper.map(h,dto);
            dto.setCinemaForThisHall(modelMapper.map(h.getCinemaIn(), CinemaWithoutHallDTO.class));
            return dto;
        }
        else{
            throw new NotFoundException("Hall not found.");
        }
    }

    private void validateHallData(HallAddDTO hall) {
        if (hall.getName() == null || hall.getName().isBlank()) {
            throw new BadRequestException("Cinema name is mandatory!");
        }
        if (hall.getRowsNumber() <= 0 ||  hall.getSeatsPerRow() <= 0) {
            throw new BadRequestException("Seats and rows should be greater than 0.");
        }
    }

}