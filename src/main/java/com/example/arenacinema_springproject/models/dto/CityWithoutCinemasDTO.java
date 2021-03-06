package com.example.arenacinema_springproject.models.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.JoinColumn;

@Getter
@Setter
@NoArgsConstructor
public class CityWithoutCinemasDTO {

    private int id;
    private String name;

}
