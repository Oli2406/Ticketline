package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

public class ArtistDetailDto {

    private Long id;
    private String firstName;
    private String surname;
    private String artistName;

    public ArtistDetailDto(Long id, String firstName, String surname, String artistName) {
        this.id = id;
        this.firstName = firstName;
        this.surname = surname;
        this.artistName = artistName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }
}
