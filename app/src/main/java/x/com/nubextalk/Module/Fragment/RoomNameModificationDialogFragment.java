/*
 * Created By JONGHO, LEE, Researcher of Institute of Biomedical Engineering, TechHeim Co.,Ltd.
 * on  2021.
 * This software is not stable health-care system and not certificated any health-care authority(like CE, FDA)
 * Prohibit redistribution, modification, commercial use.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk.Module.Fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import io.realm.Realm;
import x.com.nubextalk.Model.ChatRoom;
import x.com.nubextalk.R;

import static x.com.nubextalk.Module.CodeResources.CANCEL;
import static x.com.nubextalk.Module.CodeResources.CONFIRM;
import static x.com.nubextalk.Module.CodeResources.EMPTY;
import static x.com.nubextalk.Module.CodeResources.MSG_EMPTY_ROOM_NAME;

public class RoomNameModificationDialogFragment extends DialogFragment {

    private String mChatRoomId;
    private Realm mRealm;
    private NameModifiedListener listener;

    public interface NameModifiedListener{
        void onChange(String name);

    }

    public RoomNameModificationDialogFragment(Realm realm, String rid) {
        mChatRoomId = rid;
        mRealm = realm;
    }

    public void setNameModifiedListener(NameModifiedListener listener){
        this.listener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.fragment_dialog_change_roomname, null))

                .setPositiveButton(CONFIRM, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText roomNameView = getDialog().findViewById(R.id.modified_room_name);
                        String roomName = roomNameView.getText().toString();
                        if (!roomName.equals(EMPTY)) {
                            changeRoomName(mChatRoomId, roomName);
                        } else {
                            Toast.makeText(getContext(), MSG_EMPTY_ROOM_NAME, Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton(CANCEL, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        return builder.create();
    }

    public void changeRoomName(String chatRoomId, String name) {
        ChatRoom chatRoom = mRealm.where(ChatRoom.class).equalTo("rid", chatRoomId).findFirst();
        if (chatRoom != null) {
            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    chatRoom.setRoomName(name);
                    realm.copyToRealmOrUpdate(chatRoom);
                    if(listener != null){
                        listener.onChange(name);
                    }
                }
            });
        }
    }

}
