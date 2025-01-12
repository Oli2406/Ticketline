package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

public class ArtistDetailDto {

    private Long artistId;
    private String firstName;
    private String lastName;
    private String artistName;

    public ArtistDetailDto(Long artistId, String firstName, String lastName, String artistName) {
        this.artistId = artistId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.artistName = artistName;
    }

    public Long getArtistId() {
        return artistId;
    }

    public void setArtistId(Long artistId) {
        this.artistId = artistId;
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

    public void setLastName(String surname) {
        this.lastName = surname;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }
}
