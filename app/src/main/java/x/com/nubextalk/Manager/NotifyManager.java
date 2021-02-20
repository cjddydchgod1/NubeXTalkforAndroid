/*
 * Created By Jong Ho, Lee on  2021.
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
import x.com.nubextalk.Model.User;
import x.com.nubextalk.R;

import static x.com.nubextalk.Module.CodeResources.NOTIFICATION_CHANNEL_DESCRIPTION;
import static x.com.nubextalk.Module.CodeResources.NOTIFICATION_CHANNEL_ID;
import static x.com.nubextalk.Module.CodeResources.NOTIFICATION_CHANNEL_NAME;
import static x.com.nubextalk.Module.CodeResources.NOTIFICATION_GROUP;
import static x.com.nubextalk.Module.CodeResources.TAG;

public class NotifyManager {
    private Realm mRealm;

    private Context mContext;
    private NotificationManager mNotificationManager;
    private ChatContent mChatContent;
    private ChatRoom mChatRoom;
    private User mUser;

    public NotifyManager(Context context, Realm realm) {
        this.mContext = context;
        this.mRealm = realm;
        mNotificationManager = mContext.getSystemService(NotificationManager.class);
    }

    public void notify(String chatContentId) {
        mChatContent = mRealm.where(ChatContent.class).equalTo("cid", chatContentId).findFirst();
        mChatRoom = mRealm.where(ChatRoom.class).equalTo("rid", mChatContent.getRid()).findFirst();
        mUser = mRealm.where(User.class).equalTo("userId", mChatContent.getUid()).findFirst();

        createNotificationChannel();

        NotificationCompat.Builder builder = createBuilder();
        NotificationCompat.Builder summaryBuilder = createSummaryBuilder();

        mNotificationManager.notify(Integer.parseInt(mChatRoom.getNotificationId()), builder.build());
        mNotificationManager.notify(0, summaryBuilder.build());

        mRealm.close();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String name = NOTIFICATION_CHANNEL_NAME;
            String description = NOTIFICATION_CHANNEL_DESCRIPTION;
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance);
            channel.setDescription(description);

            mNotificationManager.createNotificationChannel(channel);
        }
    }

    private NotificationCompat.Builder createBuilder() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, NOTIFICATION_CHANNEL_ID);
        Intent intent = new Intent(mContext, ChatRoomActivity.class);

        intent.putExtra("rid", mChatContent.getRid());
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, new Random().nextInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Bitmap largeIcon = new ImageManager(mContext).getImageFromURL(mUser.getAppImagePath());
        builder.setContentTitle(mChatRoom.getRoomName())
                .setSmallIcon(R.drawable.nube_x_logo)
                .setLargeIcon(largeIcon)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setChannelId(NOTIFICATION_CHANNEL_ID)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setGroup(NOTIFICATION_GROUP);
        if (mChatContent.getType() == 1) {
            builder.setContentText("사진");
        } else {
            builder.setContentText(mChatContent.getContent());
        }
        return builder;
    }

    private NotificationCompat.Builder createSummaryBuilder() {
        NotificationCompat.Builder summaryBuilder = new NotificationCompat.Builder(mContext, NOTIFICATION_CHANNEL_ID);
        summaryBuilder.setContentTitle(TAG)
                .setSmallIcon(R.drawable.nube_x_logo)
                .setGroup(NOTIFICATION_GROUP)
                .setGroupSummary(true)
                .setAutoCancel(true)
                .setNotificationSilent();
        return summaryBuilder;
    }
}
