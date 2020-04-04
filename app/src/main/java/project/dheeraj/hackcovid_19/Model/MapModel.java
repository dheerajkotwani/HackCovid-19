package project.dheeraj.hackcovid_19.Model;

public class MapModel {

    private String state;
    private Double radius;

    public MapModel(String state, Double radius) {
        this.state = state;
        this.radius = radius;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Double getRadius() {
        return radius;
    }

    public void setRadius(Double radius) {
        this.radius = radius;
    }
}
