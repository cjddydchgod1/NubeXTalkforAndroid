package x.com.nubextalk.item;

public class Profile {

    private int profileImage;
    private int status;
    private String name;


    public Profile(int profileImage, int status, String name) {
        this.profileImage = profileImage;
        this.status = status;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getProfileImage() {
        return profileImage;
    }

    public int getStatus() {
        return status;
    }
}

