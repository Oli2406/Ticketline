package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

public class ArtistSearchDto {

    private String firstName;
    private String surname;
    private String artistName;

    public ArtistSearchDto(String firstName, String surname, String artistName) {
        this.firstName = firstName;
        this.surname = surname;
        this.artistName = artistName;
    }

    public ArtistSearchDto() {
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