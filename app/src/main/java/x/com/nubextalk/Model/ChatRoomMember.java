/*
 * Created By Jong Ho, Lee on  2020.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk.Model;

import androidx.annotation.NonNull;

import io.realm.Realm;
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

    public static void addChatRoomMember(Realm realm, String rid, String uid){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                ChatRoomMember chatRoomMember = new ChatRoomMember();
                ChatRoom chatRoom = realm.where(ChatRoom.class).equalTo("rid", rid).findFirst();
                chatRoomMember.setRid(rid);
                chatRoomMember.setUid(uid);
                chatRoom.setMemeberCount(chatRoom.getMemeberCount() + 1);
                realm.copyToRealmOrUpdate(chatRoom);
                realm.copyToRealm(chatRoomMember);
            }
        });
    }
}
