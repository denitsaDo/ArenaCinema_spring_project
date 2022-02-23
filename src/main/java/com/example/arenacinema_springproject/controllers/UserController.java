package com.example.arenacinema_springproject.controllers;


import com.example.arenacinema_springproject.exceptions.ConstraintValidationException;
import com.example.arenacinema_springproject.exceptions.CreatedException;
import com.example.arenacinema_springproject.exceptions.UnauthorizedException;
import com.example.arenacinema_springproject.models.dto.*;
import com.example.arenacinema_springproject.models.entities.Ticket;
import com.example.arenacinema_springproject.models.entities.User;
import com.example.arenacinema_springproject.services.TicketService;
import com.example.arenacinema_springproject.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.modelmapper.ModelMapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import javax.validation.Valid;

import java.util.List;
import java.util.stream.Stream;

@RestController
@Validated
public class UserController extends BaseController{


    @Autowired
    private UserService userService;
    @Autowired
    private TicketController ticketController;
    @Autowired
    private ModelMapper modelMapper;



    @PostMapping("/login")
    public UserResponseDTO login(@RequestBody User user, HttpSession session,  HttpServletRequest request){
        if (session.getAttribute(LOGGED)!=null){
            System.out.println("You are already logged!");
        }
        String email = user.getEmail();
        String password = user.getPassword();
        User u = userService.login(email,password);
        session.setAttribute(LOGGED, true);
        session.setAttribute(LOGGED_FROM, request.getRemoteAddr()); //chechk ip
        session.setAttribute(USER_ID, u.getId());
        session.setAttribute(ADMIN,u.isAdmin());
        UserResponseDTO dto = modelMapper.map(u, UserResponseDTO.class);
        return dto;
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponseDTO> getById(@PathVariable int id){
        User u = userService.getById(id);
        UserResponseDTO dto = modelMapper.map(u, UserResponseDTO.class);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/users")
    public List getAll(HttpServletRequest request){
        validateLogin(request);
        adminLogin(request);
        return userService.getAllUsers();
    }
    @PostMapping("/logout")
    public void logOut(HttpServletRequest request){
        request.getSession().invalidate();
    }

    @DeleteMapping("/users/{id}")
    public void deleteUserById(@PathVariable int id, HttpServletRequest request) {
        validateLogin(request);
        validateAccountOwner(id, request);
        User u = userService.getById(id);
        userService.deleteUserById(u);
    }

    @PostMapping("/reg")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody UserRegisterDTO user, BindingResult result) {
        if (result.hasErrors()) {
            throw new ConstraintValidationException(result.toString());
        }

        User u = userService.register(user);
        UserResponseDTO dto = modelMapper.map(u, UserResponseDTO.class);
        return ResponseEntity.ok(dto);
    }
    

    @PutMapping("/users")
    public ResponseEntity<UserResponseDTO> edit(@RequestBody UserEditDTO user, HttpServletRequest request) {
        validateLogin(request);
        validateAccountOwner(user.getId(), request);

        User u = userService.edit(user);
        UserResponseDTO dto = modelMapper.map(u, UserResponseDTO.class);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/users/changePassword")
    public ResponseEntity<UserResponseDTO> editPassword(@RequestBody UserPasswordEditDTO user, HttpServletRequest request) {
        validateLogin(request);
        validateAccountOwner(user.getId(), request);

        User u = userService.editPassword(user);
        UserResponseDTO dto = modelMapper.map(u, UserResponseDTO.class);
        return ResponseEntity.ok(dto);
    }

    private void validateAccountOwner(int id, HttpServletRequest request) {
        if((Integer) request.getSession().getAttribute(USER_ID) != id) {
            throw new UnauthorizedException("You can edit or delete only own account!");
        }
    }


    @PostMapping("/users/ticket")
    public void buyTicket(@RequestBody TicketAddDTO ticket, HttpServletRequest request){
        validateLogin(request);
        Ticket ticket1 = ticketController.add(ticket);
        User u = userService.getById(ticket.getUserId());
        u.getUserTickets().add(modelMapper.map(ticket1, Ticket.class));
        throw new CreatedException("Ticket added. This user has " + u.getUserTickets().size() + " tickets.");
    }


}
