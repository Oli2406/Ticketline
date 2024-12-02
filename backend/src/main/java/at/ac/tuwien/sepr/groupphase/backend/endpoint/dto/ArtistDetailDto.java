package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

public class ArtistDetailDto {

    private Long artistId;
    private String firstName;
    private String surname;
    private String artistName;

    public ArtistDetailDto(Long artistId, String firstName, String surname, String artistName) {
        this.artistId = artistId;
        this.firstName = firstName;
        this.surname = surname;
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
