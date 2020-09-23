package x.com.nubextalk;

// 채팅방 내 채팅 클래스 구현
import java.text.SimpleDateFormat;
import java.util.Date;

public class Chat {
    private int id;
    public int profileImage;
    public String profileName;
    public String chat;
    private String time;


    public Chat(int id, int Image, String name ,String chat){
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        Date time = new Date();


        this.id = id;
        this.profileImage = Image;
        this.profileName = name;
        this.chat = chat;
        this.time = format.format(time);
    }


    public int getProfileImage() {
        return this.profileImage;
    }

    public String getProfile_name() {
        return profileName;
    }

    public String getChat() {
        return chat;
    }

    public String getTime() { return time; }

    public int getId() { return id; }
}
