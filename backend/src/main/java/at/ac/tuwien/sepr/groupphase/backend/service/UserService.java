package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserLoginDto;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserUpdateReadNewsDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserService extends UserDetailsService {

    /**
     * Find a user in the context of Spring Security based on the email address.
     * <br>
     * For more information have a look at this tutorial:
     * https://www.baeldung.com/spring-security-authentication-with-a-database
     *
     * @param email the email address
     * @return a Spring Security user
     * @throws UsernameNotFoundException is thrown if the specified user does not exists
     */
    @Override
    UserDetails loadUserByUsername(String email) throws UsernameNotFoundException;

    /**
     * Find an application user based on the email address.
     *
     * @param email the email address
     * @return a application user
     */
    ApplicationUser findApplicationUserByEmail(String email);

    /**
     * Log in a user.
     *
     * @param userLoginDto login credentials
     * @return the JWT, if successful
     */
    String login(UserLoginDto userLoginDto);

    /**
     * Log out an user.
     *
     * @param userLogoutDto logout credentials
     */
    void logout(UserLogoutDto userLogoutDto);

    /**
     * Registers a new user using the provided UserRegistrationDto.
     *
     * <p>
     * This method accepts a {@code UserRegistrationDto} containing the user's registration details,
     * such as first name, last name, email, password, and whether the user is an admin.
     *
     * @param userRegistrationDto the data transfer object containing user registration details such
     *                            as first name, last name, email, password, and admin status
     * @return the created JWT Token, if successful
     */
    String register(UserRegistrationDto userRegistrationDto)
        throws ValidationException, ConflictException;


    /**
     * Marks a news article as read for the specified user.
     *
     * @param userUpdateReadNewsDto A DTO containing the ID of the news article to be marked as read
     *                              and the email address of the user who read the article.
     */
    void updateReadNews(UserUpdateReadNewsDto userUpdateReadNewsDto);
}
