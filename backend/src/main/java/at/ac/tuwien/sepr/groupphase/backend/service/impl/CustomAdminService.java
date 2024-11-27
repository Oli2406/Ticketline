package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.AdminService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomAdminService implements AdminService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomAdminService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void unlockUser(Long userId) {
        ApplicationUser user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("User not found"));
        user.setLocked(false);
        userRepository.save(user);
    }

    @Override
    public List<UserDetailDto> getAllUsers() {
        return userRepository.findAll()
            .stream()
            .map(
                user -> new UserDetailDto(user.getId(), user.getFirstName(), user.getLastName(),
                    user.getEmail(),
                    user.isLocked(), user.isLoggedIn()))
            .collect(Collectors.toList());
    }
}
