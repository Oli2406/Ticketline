package at.ac.tuwien.sepr.groupphase.backend.unittests.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.*;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ArtistMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.ArtistRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.*;
import at.ac.tuwien.sepr.groupphase.backend.service.validators.ArtistValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class ArtistServiceImplTest {

    private ArtistServiceImpl artistService;

    @Mock
    private ArtistRepository artistRepository;

    @Mock
    private ArtistValidator artistValidator;

    @Mock
    private ArtistMapper artistMapper;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        artistService = new ArtistServiceImpl(artistRepository, artistValidator, artistMapper);
    }

    @Test
    void createOrUpdateArtist_ShouldSaveArtist_WhenValidInput()
        throws ValidationException, ConflictException {
        ArtistCreateDto dto = new ArtistCreateDto("Doe", "John", "JohnDoe");

        when(artistRepository.save(any(Artist.class))).thenAnswer(invocation -> {
            Artist a = invocation.getArgument(0);
            a.setArtistId(1L);
            return a;
        });

        ArtistDetailDto created = artistService.createArtist(dto);

        assertNotNull(created, "Created artist DTO should not be null");
        assertAll(
            () -> assertNotNull(created.getArtistId(), "Artist ID should not be null"),
            () -> assertEquals(dto.getFirstName(), created.getFirstName(),
                "First name should match"),
            () -> assertEquals(dto.getLastName(), created.getLastName(), "Surname should match"),
            () -> assertEquals(dto.getArtistName(), created.getArtistName(),
                "Artist name should match")
        );

        verify(artistValidator, times(1)).validateArtist(dto);
        verify(artistRepository, times(1)).save(any(Artist.class));
    }

    @Test
    void getAllArtists_ShouldReturnArtistList() {
        List<Artist> artists = List.of(new Artist("Doe", "John", "JohnDoe"));
        when(artistRepository.findAll()).thenReturn(artists);

        List<ArtistDetailDto> result = artistService.getAllArtists();

        assertFalse(result.isEmpty(), "Resulting artist list should not be empty");
        assertEquals(1, result.size(), "Result list size should match");
        assertEquals("JohnDoe", result.getFirst().getArtistName(), "Artist name should match");

        verify(artistRepository, times(1)).findAll();
    }

    @Test
    void getArtistById_ShouldThrowException_WhenArtistNotFound() {
        when(artistRepository.findById(anyLong())).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> artistService.getArtistById(1L),
            "Should throw exception for non-existent ID");

        assertEquals("Artist not found with ID: 1", exception.getMessage(),
            "Exception message should match");
        verify(artistRepository, times(1)).findById(1L);
    }

    @Test
    void searchArtistByNameReturnsMatchingEvent() {
        ArtistDetailDto artist1DetailDto = new ArtistDetailDto(1L, "Name1", "Last1", "Artist1");
        Artist artist1 = new Artist("Name1", "Last1", "Artist1");
        Artist artist2 = new Artist("Name2", "Last2", "Artist2");
        when(artistRepository.findAll()).thenReturn(List.of(artist1, artist2));
        when(artistMapper.artistToArtistDetailDto(artist1)).thenReturn(artist1DetailDto);

        ArtistSearchDto searchDto = new ArtistSearchDto("1", null, null);

        List<ArtistDetailDto> result = artistService.search(searchDto).toList();

        assertEquals(1, result.size(), "Should return only one event");
        verify(artistRepository, times(1)).findAll();
        verify(artistMapper, times(1)).artistToArtistDetailDto(artist1);
        verify(artistMapper, never()).artistToArtistDetailDto(artist2);
    }

    @Test
    void searchArtistByNameReturnsNoEventsWhenNoMatch() {
        Artist artist1 = new Artist("Name1", "Last1", "Artist1");
        Artist artist2 = new Artist("Name2", "Last2", "Artist2");
        when(artistRepository.findAll()).thenReturn(List.of(artist1, artist2));

        ArtistSearchDto searchDto = new ArtistSearchDto("3", null, null);

        List<ArtistDetailDto> result = artistService.search(searchDto).toList();

        assertEquals(0, result.size(), "Should return no events");
        verify(artistRepository, times(1)).findAll();
        verify(artistMapper, never()).artistToArtistDetailDto(any(Artist.class));
    }

    @Test
    void getArtistById_ShouldReturnArtist_WhenValidId() {
        Artist artist = new Artist("John", "Doe", "JohnDoe");
        artist.setArtistId(1L);
        when(artistRepository.findById(1L)).thenReturn(Optional.of(artist));
        when(artistMapper.artistToArtistDetailDto(artist)).thenReturn(
            new ArtistDetailDto(1L, "John", "Doe", "JohnDoe")
        );

        ArtistDetailDto result = artistService.getArtistById(1L);

        assertNotNull(result, "Artist should not be null");
        assertEquals("JohnDoe", result.getArtistName(), "Artist name should match");

        verify(artistRepository, times(1)).findById(1L);
    }

    @Test
    void deleteArtist_ShouldRemoveArtist_WhenValidId() {
        Long artistId = 1L;
        when(artistRepository.existsById(artistId)).thenReturn(true);
        doNothing().when(artistRepository).deleteById(artistId);

        artistService.deleteArtist(artistId);

        verify(artistRepository, times(1)).deleteById(artistId);
    }

    @Test
    void deleteArtist_ShouldThrowException_WhenArtistNotFound() {
        Long artistId = 999L;
        when(artistRepository.existsById(artistId)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> artistService.deleteArtist(artistId),
            "Should throw exception when trying to delete a non-existent artist");

        assertEquals("Artist not found with ID: 999", exception.getMessage());
        verify(artistRepository, never()).deleteById(artistId);
    }

    @Test
    void searchArtistByFullNameReturnsMatchingArtists() {
        Artist artist = new Artist("John", "Doe", "JohnDoe");
        when(artistRepository.findAll()).thenReturn(List.of(artist));
        when(artistMapper.artistToArtistDetailDto(artist)).thenReturn(
            new ArtistDetailDto(1L, "John", "Doe", "JohnDoe")
        );

        ArtistSearchDto searchDto = new ArtistSearchDto("John", "Doe", null);
        List<ArtistDetailDto> result = artistService.search(searchDto).toList();

        assertEquals(1, result.size(), "Should return one matching artist");
        assertEquals("JohnDoe", result.getFirst().getArtistName(), "Artist name should match");

        verify(artistRepository, times(1)).findAll();
    }

    @Test
    void searchArtistByLastNameReturnsMatchingArtists() {
        Artist artist = new Artist("Jane", "Doe", "JaneDoe");
        when(artistRepository.findAll()).thenReturn(List.of(artist));
        when(artistMapper.artistToArtistDetailDto(artist)).thenReturn(
            new ArtistDetailDto(2L, "Jane", "Doe", "JaneDoe")
        );

        ArtistSearchDto searchDto = new ArtistSearchDto(null, "Doe", null);
        List<ArtistDetailDto> result = artistService.search(searchDto).toList();

        assertEquals(1, result.size(), "Should return one matching artist");
        assertEquals("JaneDoe", result.getFirst().getArtistName(), "Artist name should match");

        verify(artistRepository, times(1)).findAll();
    }

    @Test
    void searchArtistByArtistNameReturnsMatchingArtists() {
        Artist artist = new Artist("John", "Smith", "JohnSmith");
        when(artistRepository.findAll()).thenReturn(List.of(artist));
        when(artistMapper.artistToArtistDetailDto(artist)).thenReturn(
            new ArtistDetailDto(3L, "John", "Smith", "JohnSmith")
        );

        ArtistSearchDto searchDto = new ArtistSearchDto(null, null, "JohnSmith");
        List<ArtistDetailDto> result = artistService.search(searchDto).toList();

        assertEquals(1, result.size(), "Should return one matching artist");
        assertEquals("JohnSmith", result.getFirst().getArtistName(), "Artist name should match");

        verify(artistRepository, times(1)).findAll();
    }

}
