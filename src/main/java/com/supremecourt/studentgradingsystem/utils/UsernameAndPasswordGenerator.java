package com.supremecourt.studentgradingsystem.utils;

import com.supremecourt.studentgradingsystem.dao.repository.UserRepository;
import com.supremecourt.studentgradingsystem.model.request.UserRegistrationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Slf4j
@RequiredArgsConstructor
@Service
public class UsernameAndPasswordGenerator {
    private final UserRepository userRepository;

    public String generateUsername(UserRegistrationDto user) {
        log.info("ActionLog.generateUsername.start");

        String baseUsername = user.getFirstName().toLowerCase().charAt(0) + user.getLastName().toLowerCase() + "." + user.getRole().toLowerCase().charAt(0);
        String topUsername = userRepository.findTopByUsernameLikeOrderByUsernameDesc(baseUsername + "%");

        if (topUsername == null) {
            log.info("ActionLog.generateUsername.end username {}", baseUsername);
            return baseUsername;
        }
        String numberPart = "";
        if (topUsername.matches(".*\\d+$")) {
            numberPart = topUsername.replaceAll("\\D+", "");
        }
        int counter = 1;
        if (!numberPart.isEmpty()) {
            counter = Integer.parseInt(numberPart) + 1;
        }
        String newUsername = baseUsername + counter;
        log.info("ActionLog.generateUsername.end username {}", newUsername);
        return newUsername;
    }

    public String generatePassword(UserRegistrationDto user) {
        log.info("ActionLog.generatePassword.start");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy");
        String birthYear = user.getBirthDate().format(formatter);
        log.info("ActionLog.generatePassword.end");
        return user.getFirstName() + birthYear;
    }
}
