package project.dheeraj.hackcovid_19.Model;

public class SymptomsModel {

    private int image;
    private String text;

    public SymptomsModel(int image, String text) {
        this.image = image;
        this.text = text;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
