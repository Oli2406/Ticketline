package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

public class ArtistSearchDto {

    private String firstName;
    private String lastName;
    private String artistName;

    public ArtistSearchDto(String firstName, String lastName, String artistName) {
        this.firstName = firstName;
        this.lastName = lastName;
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

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }
}