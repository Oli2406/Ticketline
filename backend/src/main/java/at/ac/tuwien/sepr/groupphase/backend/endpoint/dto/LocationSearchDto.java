package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

public class LocationSearchDto {
    private String name;
    private String street;
    private String city;
    private String country;
    private String postalCode;

    public LocationSearchDto(String name, String street, String city, String country, String postalCode) {
        this.name = name;
        this.street = street;
        this.city = city;
        this.country = country;
        this.postalCode = postalCode;
    }

    @Override
    public String toString() {
        return "LocationSearchDto{"
            + "name='" + name + '\''
            + ", street='" + street + '\''
            + ", city='" + city + '\'' + ", country='" + country + '\''
            + ", postalCode=" + postalCode
            + '}';
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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
}
