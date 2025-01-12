package at.ac.tuwien.sepr.groupphase.backend.unittests.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.ArtistEndpoint;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ArtistCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ArtistDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ArtistSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.ArtistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ArtistEndpointTest {

    private final ArtistDetailDto artistDetailDto1 = new ArtistDetailDto(1L, "John", "Doe", "John Doe");
    private final ArtistDetailDto artistDetailDto2 = new ArtistDetailDto(2L, "Jane", "Doe", "Jane Doe");
    private final List<ArtistDetailDto> mockArtists = List.of(artistDetailDto1, artistDetailDto2);
    @Mock
    private ArtistService artistService;
    @InjectMocks
    private ArtistEndpoint artistEndpoint;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createArtistWhenValidInputReturnsSuccess() throws ValidationException, ConflictException {
        ArtistCreateDto artistCreateDto = new ArtistCreateDto("John", "Doe", "John Doe");

        when(artistService.createArtist(any(ArtistCreateDto.class))).thenReturn(artistDetailDto1);

        ResponseEntity<ArtistDetailDto> response = artistEndpoint.createOrUpdateArtist(artistCreateDto);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(artistDetailDto1, response.getBody());
    }

    @Test
    void createArtistWhenInvalidInputThrowsValidationException() throws ValidationException, ConflictException {
        ArtistCreateDto artistCreateDto = new ArtistCreateDto("John", "Doe", "John Doe");
        List<String> validationErrors = List.of("Name is required", "Name cannot exceed 64 Characters");
        doThrow(new ValidationException("Invalid input", validationErrors)).when(artistService).createArtist(any());

        ValidationException exception = assertThrows(ValidationException.class, () -> artistEndpoint.createOrUpdateArtist(artistCreateDto));

        assertTrue(exception.getMessage().contains("Invalid input"));
        assertEquals(validationErrors.toString(), exception.getErrors());
    }

    @Test
    void getAllArtistsReturnsAllArtists() {
        when(artistService.getAllArtists()).thenReturn(mockArtists);

        ResponseEntity<List<ArtistDetailDto>> response = artistEndpoint.getAllArtists();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockArtists, response.getBody());
    }

    @Test
    void getArtistByIdReturnsArtistWithGivenId() {
        Long artistId = 1L;
        when(artistService.getArtistById(artistId)).thenReturn(artistDetailDto1);

        ResponseEntity<ArtistDetailDto> response = artistEndpoint.getArtistById(artistId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(artistDetailDto1, response.getBody());
    }

    @Test
    void deleteArtistDeletesArtistWithGivenId() {
        Long artistId = 1L;

        ResponseEntity<Void> response = artistEndpoint.deleteArtist(artistId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(artistService, times(1)).deleteArtist(artistId);
    }

    @Test
    void searchArtistReturnsMatchingArtist() {
        ArtistSearchDto artistSearchDto = new ArtistSearchDto("John", "Doe", "");

        when(artistService.search(artistSearchDto)).thenReturn(Stream.of(artistDetailDto1));

        ResponseEntity<Stream<ArtistDetailDto>> response = artistEndpoint.search(artistSearchDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        List<ArtistDetailDto> result = Objects.requireNonNull(response.getBody()).toList();
        assertEquals(1, result.size());
        assertEquals(artistDetailDto1, result.getFirst());
    }
}
