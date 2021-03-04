/*
 * Created By Jong Ho, Lee on  2021.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk.Module.Fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import io.realm.Realm;
import x.com.nubextalk.Model.ChatRoom;
import x.com.nubextalk.R;

public class RoomNameModificationDialogFragment extends DialogFragment {

    private String chatRoomId;
    private Realm realm1;

    public RoomNameModificationDialogFragment(Realm realm, String rid) {
        chatRoomId = rid;
        realm1 = realm;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.dialog_change_roomname, null))

                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText roomNameView = getDialog().findViewById(R.id.modified_room_name);
                        String roomName = roomNameView.getText().toString();
                        if (!roomName.equals("")) {
                            Log.d("dialog", "roomname: " + roomName);
                            changeRoomName(chatRoomId, roomName);
                        } else {
                            Toast.makeText(getContext(), "채팅방 이름을 입력하세요.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        return builder.create();
    }

    public void changeRoomName(String chatRoomId, String name) {
        ChatRoom chatRoom = realm1.where(ChatRoom.class).equalTo("rid", chatRoomId).findFirst();
        if (chatRoom != null) {
            realm1.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    chatRoom.setRoomName(name);
                    realm.copyToRealmOrUpdate(chatRoom);
                }
            });
        }
    }

}
