/*
 * Created By Jong Ho, Lee on  2020.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk.Model;

import androidx.annotation.NonNull;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;

public class ChatRoomMember extends RealmObject {
    @NonNull
    private String rid;
    @NonNull
    private String uid;

    @NonNull
    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    @NonNull
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public static void addChatRoomMember(Realm realm, String rid, String uid) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                //기존 채팅방 멤버인지 확인
                if (!realm.where(ChatRoomMember.class).
                        equalTo("rid", rid).
                        and().
                        equalTo("uid", uid).findAll().isEmpty()) {
                    ChatRoomMember chatRoomMember = new ChatRoomMember();
                    ChatRoom chatRoom = realm.where(ChatRoom.class).equalTo("rid", rid).findFirst();
                    chatRoomMember.setRid(rid);
                    chatRoomMember.setUid(uid);
                    chatRoom.setMemeberCount(chatRoom.getMemeberCount() + 1);
                    realm.copyToRealmOrUpdate(chatRoom);
                    realm.copyToRealm(chatRoomMember);
                }
            }
        });
    }

    public static void addChatRoomMember(Realm realm, String rid, String[] uid) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (String id : uid) {
                    ChatRoomMember chatRoomMember = new ChatRoomMember();
                    ChatRoom chatRoom = realm.where(ChatRoom.class).equalTo("rid", rid).findFirst();
                    chatRoomMember.setRid(rid);
                    chatRoomMember.setUid(id);
                    chatRoom.setMemeberCount(chatRoom.getMemeberCount() + 1);
                    realm.copyToRealmOrUpdate(chatRoom);
                    realm.copyToRealm(chatRoomMember);
                }

            }
        });
    }

    public static void deleteChatRoomMember(Realm realm, String rid, String uid) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                ChatRoomMember chatRoomMember = realm.where(ChatRoomMember.class).equalTo("rid", rid).equalTo("uid", uid).findFirst();
                chatRoomMember.deleteFromRealm();
                ChatRoom chatRoom = realm.where(ChatRoom.class).equalTo("rid", rid).findFirst();
                if(chatRoom != null){
                    int orgMemberCount = chatRoom.getMemeberCount();
                    chatRoom.setMemeberCount(orgMemberCount - 1);
                }
            }
        });
    }
}
