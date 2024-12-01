package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

public class LocationDetailDto {

    private Long locationId;
    private String name;
    private String street;
    private String city;
    private String postalCode;
    private String country;

    public LocationDetailDto(Long locationId, String name, String street, String city, String postalCode, String country) {
        this.locationId = locationId;
        this.name = name;
        this.street = street;
        this.city = city;
        this.postalCode = postalCode;
        this.country = country;
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
