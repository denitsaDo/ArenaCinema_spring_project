package com.example.arenacinema_springproject.services;
import com.example.arenacinema_springproject.exceptions.NoContentException;
import com.example.arenacinema_springproject.exceptions.NotFoundException;
import com.example.arenacinema_springproject.models.dto.UserEditDTO;
import com.example.arenacinema_springproject.models.dto.UserPasswordEditDTO;
import com.example.arenacinema_springproject.models.dto.UserRegisterDTO;
import com.example.arenacinema_springproject.models.entities.User;
import com.example.arenacinema_springproject.models.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.arenacinema_springproject.exceptions.BadRequestException;
import com.example.arenacinema_springproject.exceptions.UnauthorizedException;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.Year;
import java.time.chrono.ChronoLocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ModelMapper modelMapper;


    public User login(String email, String password) {
        if (email == null || email.isBlank()) {
            throw new BadRequestException("Email is mandatory!");
        }
        if (password == null || password.isBlank()) {
            throw new BadRequestException("Password is mandatory!");
        }
        User u = userRepository.findByEmail(email);
        if (u == null) {
            throw new NotFoundException("Wrong credentials.");
        } else {
            if (!passwordEncoder.matches(password, u.getPassword())) {
                throw new UnauthorizedException("Wrong credentials.");
            }
            return u;
        }
    }

    public User getById(int id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            return user.get();
        } else {
            throw new NotFoundException("User not found");
        }
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void deleteUserById(User user) {
        Optional<User> optional = userRepository.findById(user.getId());   //check if id is >0
        if (optional.isPresent()) {
            userRepository.delete(user);
            throw new NoContentException();
        } else {
            throw new NotFoundException("No such user.");
        }
    }

    public User register(UserRegisterDTO user) {
        validateMandatoryFields(user);
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new BadRequestException("All fields are mandatory");
        }
        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new BadRequestException("User already exists!");
        }

        validateStrongPassword(user.getPassword());
        validateMatchingPasswords(user.getPassword(), user.getPassword2());


        User u = new User();
        u.setFirstName(user.getFirstName());
        u.setSecondName(user.getSecondName());
        u.setLastName(user.getLastName());
        u.setGender(user.getGender());
        u.setEmail(user.getEmail());
        u.setDateOfBirth(user.getDateOfBirth());
        u.setAdmin(false);
        u.setPassword(passwordEncoder.encode(user.getPassword()));  // bcrypt password
        userRepository.save(u);
        return u;
    }



    public User edit(UserEditDTO user) {
        Optional<User> opt = userRepository.findById(user.getId());
        if(opt.isPresent()){
            validateMandatoryFields(modelMapper.map(user, UserRegisterDTO.class));
            User u = opt.get();
            u.setFirstName(user.getFirstName());
            u.setSecondName(user.getSecondName());
            u.setLastName(user.getLastName());
            u.setGender(user.getGender());
            u.setDateOfBirth(user.getDateOfBirth());
            userRepository.save(u);
            return u;
        }
        else{
            throw new NotFoundException("User not found");
        }
    }


    public User editPassword(UserPasswordEditDTO user) {
        Optional<User> opt = userRepository.findById(user.getId());
        if(opt.isPresent()){
            User u = opt.get();

            if(!passwordEncoder.matches(user.getOldPassword(),u.getPassword())){
                throw new BadRequestException("Wrong password!");
            }
            validateMatchingPasswords(user.getNewPassword(), user.getNewPassword2());
            validateStrongPassword(user.getNewPassword());


            u.setPassword(passwordEncoder.encode(user.getNewPassword())); // bcrypt password
            userRepository.save(u);
            return u;
        }
        else{
            throw new NotFoundException("User not found");
        }
    }

    private void validateMandatoryFields(UserRegisterDTO user) {
        if (user.getFirstName() == null || user.getFirstName().isBlank() || user.getSecondName() == null || user.getSecondName().isBlank() || user.getLastName() == null || user.getLastName().isBlank() ||
                user.getGender() == null || user.getGender().isBlank()  || user.getDateOfBirth() == null) {
            throw new BadRequestException("All fields are mandatory");
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(user.getDateOfBirth());

        if (Calendar.getInstance().before(cal)) {
            throw new BadRequestException("Please enter a valid birth date.");
        }
        if ((Calendar.getInstance().get(Calendar.YEAR) - cal.get(Calendar.YEAR)) <16){
            throw new BadRequestException("You should be at least 16yo.");
        }
    }
    private void validateStrongPassword(String password) {
        if (!password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")) {
            throw new BadRequestException("Your password must be at least 8 symbols and have at least one lowercase letter , " +
                    "one uppercase letter, one digit and one special character");
        }
    }
    private void validateMatchingPasswords(String password, String password2) {
        if (!password.equals(password2)) {
            throw new BadRequestException("Passwords mismatch.");
        }
    }
}
