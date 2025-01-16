package at.ac.tuwien.sepr.groupphase.backend.integrationtest.endpoint;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserRegistrationDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
class AppllicationUserEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void registerUser_WhenValidInput_Returns200() throws Exception {
        UserRegistrationDto userRegistrationDto = new UserRegistrationDto();
        userRegistrationDto.setFirstName("John");
        userRegistrationDto.setLastName("Doe");
        userRegistrationDto.setEmail("john.doe@example.com");
        userRegistrationDto.setPassword("password123");

        Mockito.when(userService.register(Mockito.any(UserRegistrationDto.class))).thenReturn(null);

        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/api/v1/register")
                    .content(
                        "{\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"john.doe@example.com\",\"password\":\"password123\"}")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    void registerUser_WhenEmptyFieldsInUserDto_Returns422() throws Exception {
        UserRegistrationDto userRegistrationDto = new UserRegistrationDto();
        userRegistrationDto.setFirstName("");
        userRegistrationDto.setLastName("");
        userRegistrationDto.setEmail("");
        userRegistrationDto.setPassword("");

        doThrow(new ValidationException("Validation error", new ArrayList<>()))
            .when(userService)
            .register(any(UserRegistrationDto.class));

        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/api/v1/register")
                    .content(
                        "{\"firstName\":\"\",\"lastName\":\"\",\"email\":\"\",\"password\":\"\"}")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void registerUser_WhenConflictOccurs_Returns409() throws Exception {
        UserRegistrationDto userRegistrationDto = new UserRegistrationDto();
        userRegistrationDto.setFirstName("John");
        userRegistrationDto.setLastName("Doe");
        userRegistrationDto.setEmail("john.doe@example.com");
        userRegistrationDto.setPassword("password123");

        doThrow(new ConflictException("Conflict error", new ArrayList<>()))
            .when(userService)
            .register(any(UserRegistrationDto.class));

        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/api/v1/register")
                    .content(
                        "{\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"john.doe@example.com\",\"password\":\"password123\"}")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isConflict());
    }
}
