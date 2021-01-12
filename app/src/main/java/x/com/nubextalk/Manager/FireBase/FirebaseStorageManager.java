/*
 * Created By Jong Ho, Lee on  2021.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk.Manager.FireBase;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import x.com.nubextalk.Model.User;

import android.net.Uri;

import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class FirebaseStorageManager {
    private static FirebaseStorage storage = FirebaseStorage.getInstance();

    public static UploadTask uploadFile(Uri file , String path){ // 파일 업로드
        StorageReference storageRef = storage.getReference();
        StorageReference fileRef = storageRef.child(path);
        UploadTask uploadTask = fileRef.putFile(file);
        return uploadTask;
    }
    public static UploadTask uploadFile(Bitmap bitmap , String path){
        StorageReference storageRef = storage.getReference();
        StorageReference fileRef = storageRef.child(path);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = fileRef.putBytes(data);

        return uploadTask;
    }


    public static Task  downloadFile(String path){ // 일단은 url 로 불러오기
        StorageReference storageRef = storage.getReference();
        StorageReference fileRef = storageRef.child(path);
        Task downloadTask  = fileRef.getDownloadUrl();
        return downloadTask;
    }
}
