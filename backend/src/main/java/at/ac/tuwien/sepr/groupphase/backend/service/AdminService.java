package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import java.util.List;

public interface AdminService {

    /**
     * Unlocks a user, allowing them to log in again.
     *
     * @param userId the ID of the user
     * @throws NotFoundException if the user does not exist
     */
    void unlockUser(Long userId);

    /**
     * Locks a user, preventing them from logging in.
     *
     * @param userId the ID of the user
     * @throws NotFoundException if the user does not exist
     */
    void lockUser(Long userId);

    /**
     * Retrieves all users from the database.
     *
     * @return a list of user details
     */
    List<UserDetailDto> getAllUsers();
}
