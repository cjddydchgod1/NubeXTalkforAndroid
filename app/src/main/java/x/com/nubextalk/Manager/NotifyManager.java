/*
 * Created By JONGHO, LEE, Researcher of Institute of Biomedical Engineering, TechHeim Co.,Ltd.
 * on  2021.
 * This software is not stable health-care system and not certificated any health-care authority(like CE, FDA)
 * Prohibit redistribution, modification, commercial use.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk.Manager;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import java.util.Random;

import io.realm.Realm;
import x.com.nubextalk.ChatRoomActivity;
import x.com.nubextalk.Model.ChatContent;
import x.com.nubextalk.Model.ChatRoom;
import x.com.nubextalk.Model.Config;
import x.com.nubextalk.Model.User;
import x.com.nubextalk.R;

import static x.com.nubextalk.Module.CodeResources.DESCRIPTION_NOTIFICATION_CHANNEL;
import static x.com.nubextalk.Module.CodeResources.ID_NOTIFICATION_CHANNEL;
import static x.com.nubextalk.Module.CodeResources.ID_NOTIFICATION_GROUP;
import static x.com.nubextalk.Module.CodeResources.NAME_NOTIFICATION_CHANNEL;
import static x.com.nubextalk.Module.CodeResources.PICTURE;
import static x.com.nubextalk.Module.CodeResources.TAG;

public class NotifyManager {
    private Realm mRealm;

    private Context mContext;
    private NotificationManager mNotificationManager;
    private ChatContent mChatContent;
    private ChatRoom mChatRoom;
    private User mUser;
    private Config mAlarm;

    public NotifyManager(Context context, Realm realm) {
        this.mContext = context;
        this.mRealm = realm;
        mNotificationManager = mContext.getSystemService(NotificationManager.class);
    }

    public void notify(String chatContentId) {
        mAlarm = Config.getAlarm(mRealm);
        if (Boolean.parseBoolean(mAlarm.getExt1())) {
            mChatContent = mRealm.where(ChatContent.class).equalTo("cid", chatContentId).findFirst();
            mChatRoom = mRealm.where(ChatRoom.class).equalTo("rid", mChatContent.getRid()).findFirst();
            mUser = mRealm.where(User.class).equalTo("uid", mChatContent.getUid()).findFirst();
            if(mChatRoom.getSettingAlarm()) {
                createNotificationChannel();

                NotificationCompat.Builder builder = createBuilder();
                NotificationCompat.Builder summaryBuilder = createSummaryBuilder();

                mNotificationManager.notify(Integer.parseInt(mChatRoom.getNotificationId()), builder.build());
                mNotificationManager.notify(0, summaryBuilder.build());
            }
        }
        mRealm.close();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String name = NAME_NOTIFICATION_CHANNEL;
            String description = DESCRIPTION_NOTIFICATION_CHANNEL;
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(ID_NOTIFICATION_CHANNEL, name, importance);
            channel.setDescription(description);

            mNotificationManager.createNotificationChannel(channel);
        }
    }

    private NotificationCompat.Builder createBuilder() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, ID_NOTIFICATION_CHANNEL);
        Intent intent = new Intent(mContext, ChatRoomActivity.class);

        intent.putExtra("rid", mChatContent.getRid());
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, new Random().nextInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Bitmap largeIcon = new ImageManager(mContext).getImageFromURL(mUser.getAppImagePath());
        builder.setContentTitle(mChatRoom.getRoomName())
                .setSmallIcon(R.mipmap.ic_app_icon_round)
                .setLargeIcon(largeIcon)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setChannelId(ID_NOTIFICATION_CHANNEL)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setGroup(ID_NOTIFICATION_GROUP);
        if (mChatContent.getType() == 1) {
            builder.setContentText(PICTURE);
        } else {
            builder.setContentText(mChatContent.getContent());
        }
        return builder;
    }

    private NotificationCompat.Builder createSummaryBuilder() {
        NotificationCompat.Builder summaryBuilder = new NotificationCompat.Builder(mContext, ID_NOTIFICATION_CHANNEL);
        summaryBuilder.setContentTitle(TAG)
                .setSmallIcon(R.mipmap.ic_app_icon_round)
                .setGroup(ID_NOTIFICATION_GROUP)
                .setGroupSummary(true)
                .setAutoCancel(true)
                .setNotificationSilent();
        return summaryBuilder;
    }
}
