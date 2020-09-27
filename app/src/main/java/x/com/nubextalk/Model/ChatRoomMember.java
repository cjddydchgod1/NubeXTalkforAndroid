/*
 * Created By Jong Ho, Lee on  2020.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk.Model;

import androidx.annotation.NonNull;
import io.realm.RealmObject;

public class ChatRoomMember extends RealmObject {
    @NonNull
    private int rid;
    @NonNull
    private int uid;

    public void ChatRoomMember(@NonNull int rid, @NonNull int uid) {
        this.rid = rid;
        this.uid = uid;
    }

    @NonNull
    public int getRid() {
        return rid;
    }

    @NonNull
    public int getUid() {
        return uid;
    }
}
