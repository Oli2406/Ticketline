package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.AdminService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
        ApplicationUser currentUser = getCurrentUser();
        if (currentUser.getId().equals(userId)) {
            throw new IllegalArgumentException("Admins cannot unlock themselves.");
        }
        ApplicationUser user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("User not found"));
        user.setLocked(false);
        userRepository.save(user);
    }

    @Override
    public void lockUser(Long userId) {
        ApplicationUser currentUser = getCurrentUser();
        if (currentUser.getId().equals(userId)) {
            throw new IllegalArgumentException("Admins cannot lock themselves.");
        }

        ApplicationUser user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("User not found"));
        user.setLocked(true);
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

    private ApplicationUser getCurrentUser() {
        String currentUserEmail = (String) SecurityContextHolder.getContext().getAuthentication()
            .getPrincipal();
        return userRepository.findUserByEmail(currentUserEmail)
            .orElseThrow(() -> new NotFoundException("Current user not found."));
    }
}
