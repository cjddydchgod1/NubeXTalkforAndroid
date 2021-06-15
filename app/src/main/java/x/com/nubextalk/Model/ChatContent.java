/*
 * Created By JONGHO, LEE, Researcher of Institute of Biomedical Engineering, TechHeim Co.,Ltd.
 * on  2021.
 * This software is not stable health-care system and not certificated any health-care authority(like CE, FDA)
 * Prohibit redistribution, modification, commercial use.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk.Model;

import androidx.annotation.NonNull;

import java.util.Date;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.PrimaryKey;
import x.com.nubextalk.Manager.DateManager;

import static x.com.nubextalk.Module.CodeResources.DATE_FINAL;
import static x.com.nubextalk.Module.CodeResources.DATE_FORMAT3;
import static x.com.nubextalk.Module.CodeResources.DATE_FORMAT4;

public class ChatContent extends RealmObject {
    @NonNull
    @PrimaryKey
    private String cid;
    @NonNull
    private String rid;
    @NonNull
    private String uid;
    @NonNull
    private int type; // 0 = 일반 텍스트, 1 = 사진 , 9 = system
    @NonNull
    private String content;

    private Date sendDate;

    @NonNull
    private Boolean isRead;

    @NonNull
    private Boolean isFirst;

    private String ext1;
    private String ext2;
    private String ext3;

    @NonNull
    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    @NonNull
    public String getRid() {
        return rid;
    }

    public void setRid(@NonNull String rid) {
        this.rid = rid;
    }

    @NonNull
    public String getUid() {
        return uid;
    }

    public void setUid(@NonNull String uid) {
        this.uid = uid;
    }

    public int getType() {
        return type;
    }

    public void setType(@NonNull int type) {
        this.type = type;
    }

    @NonNull
    public String getContent() {
        return content;
    }

    public void setContent(@NonNull String content) {
        this.content = content;
    }

    @NonNull
    public Date getSendDate() {
        return sendDate;
    }

    public void setSendDate(Date sendDate) {
        this.sendDate = sendDate;
    }

    @NonNull
    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    @NonNull
    public Boolean getFirst() {
        return isFirst;
    }

    public void setFirst(@NonNull Boolean first) {
        isFirst = first;
    }

    public String getExt1() {
        return ext1;
    }

    public void setExt1(String ext1) {
        this.ext1 = ext1;
    }

    public String getExt2() {
        return ext2;
    }

    public void setExt2(String ext2) {
        this.ext2 = ext2;
    }

    public String getExt3() {
        return ext3;
    }

    public void setExt3(String ext3) {
        this.ext3 = ext3;
    }


    public static RealmResults<ChatContent> getAll(Realm realm) {
        return realm.where(ChatContent.class).findAll();
    }

    /**
     * realm ChatContent 생성 함수
     *
     * @param realm
     * @param data  채팅 메세지 생성을 위한 데이터 Map
     */
    public static void createChat(Realm realm, Map data) {
        String rid = data.get("rid").toString();
        String uid = data.get("uid").toString();
        String cid = data.get("cid").toString();
        String content = data.get("content").toString();
        Integer type = Integer.parseInt(data.get("type").toString());
        Date sendDate = data.get("sendDate") == null ? DateManager.convertDatebyString(DATE_FINAL,DATE_FORMAT3) : DateManager.convertDatebyString(data.get("sendDate").toString(), DATE_FORMAT4);
        Boolean isFirst = data.get("isFirst") != null && Boolean.parseBoolean(data.get("isFirst").toString());
        Boolean isRead = data.get("isRead") == null ? Config.getMyAccount(realm).getExt1().equals(uid) : Boolean.parseBoolean(data.get("isRead").toString());

        String ext1 = data.get("ext1") == null ? null : data.get("ext1").toString();
        String ext2 = data.get("ext2") == null ? null : data.get("ext2").toString();
        String ext3 = data.get("ext3") == null ? null : data.get("ext3").toString();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                ChatContent chatContent = new ChatContent();
                chatContent.setRid(rid);
                chatContent.setUid(uid);
                chatContent.setCid(cid);
                chatContent.setContent(content);
                chatContent.setType(type);
                chatContent.setSendDate(sendDate);
                chatContent.setFirst(isFirst);
                chatContent.setIsRead(isRead);

                chatContent.setExt1(ext1);
                chatContent.setExt2(ext2);
                chatContent.setExt3(ext3);

                realm.copyToRealmOrUpdate(chatContent);

                if (!realm.where(ChatRoom.class).equalTo("rid", rid).findAll().isEmpty()) {
                    ChatRoom chatRoom = realm.where(ChatRoom.class).equalTo("rid", rid).findFirst();
                    chatRoom.setUpdatedDate(sendDate);
                    realm.copyToRealmOrUpdate(chatRoom);
                }
            }
        });
    }
}
