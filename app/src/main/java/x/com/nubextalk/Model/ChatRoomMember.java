/*
 * Created By Jong Ho, Lee on  2020.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk.Model;

import androidx.annotation.NonNull;
import io.realm.RealmObject;

public class ChatRoomMember extends RealmObject {
    @NonNull
    private String rid;
    @NonNull
    private String uid;

    @NonNull
    public String getRid() {
        return rid;
    }

    public void setRid(String rid) { this.rid = rid; }

    @NonNull
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) { this.uid = uid; }
}
