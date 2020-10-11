package x.com.nubextalk;

import java.util.Date;

public class ChatList {
    private int profileUrl;
    private int status; //on -> 1, off -> 0
    private boolean notify = true; //default: true, off -> false
    private boolean fixTop = false; //default: false,
    private String name;
    private String msg;
    private String remain;
    private Date time;

    public ChatList(int url, String name, String msg, Date time, String remain, int status) {
        this.profileUrl = url;
        this.name = name;
        this.msg = msg;
        this.time = time;
        this.remain = remain;
        this.status = status;
    }

    public void setFixTop(boolean status) {
        fixTop = status;
    }

    public Boolean getFixTop() {
        return fixTop;
    }

    public void setNotify(boolean status) {
        notify = status;
    }

    public Boolean getNotify() {
        return notify;
    }

    public String getName() {
        return name;
    }

    public String getMsg() {
        return msg;
    }

    public Date getTime() {
        return time;
    }

    public String getRemain() {
        return remain;
    }

    public int getProfileUrl() {
        return profileUrl;
    }

    public int getStatus() {
        return status;
    }
}
