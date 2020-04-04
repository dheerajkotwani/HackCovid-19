package project.dheeraj.hackcovid_19.Model;

public class StateData {
    private String statename;
    private String cases;
    private String cured;
    private String death;
    private String subheading1;
    private String subheading2;
    private String subheading3;
    private String helpline;

    public StateData(String statename, String cases, String cured, String death, String subheading1, String subheading2, String subheading3) {
        this.statename = statename;
        this.cases = cases;
        this.cured = cured;
        this.death = death;
        this.subheading1 = subheading1;
        this.subheading2 = subheading2;
        this.subheading3 = subheading3;
    }

    public StateData(String state, String phone) {
        this.statename = state;
        this.helpline = phone;
    }
    public String getHelpline() {
        return helpline;
    }

    public void setHelpline(String helpline) {
        this.helpline = helpline;
    }

    public String getStatename() {
        return statename;
    }

    public void setStatename(String statename) {
        this.statename = statename;
    }

    public String getCases() {
        return cases;
    }

    public void setCases(String cases) {
        this.cases = cases;
    }

    public String getCured() {
        return cured;
    }

    public void setCured(String cured) {
        this.cured = cured;
    }

    public String getDeath() {
        return death;
    }

    public void setDeath(String death) {
        this.death = death;
    }


    public String getSubheading1() {
        return subheading1;
    }

    public void setSubheading1(String subheading1) {
        this.subheading1 = subheading1;
    }

    public String getSubheading2() {
        return subheading2;
    }

    public void setSubheading2(String subheading2) {
        this.subheading2 = subheading2;
    }

    public String getSubheading3() {
        return subheading3;
    }

    public void setSubheading3(String subheading3) {
        this.subheading3 = subheading3;
    }
}
