package project.dheeraj.hackcovid_19.Model;

public class NearbyModel {

    private String name, address, mobile, type, city;

    public NearbyModel(String name, String address, String mobile, String type, String city) {
        this.name = name;
        this.address = address;
        this.mobile = mobile;
        this.type = type;
        this.city = city;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
