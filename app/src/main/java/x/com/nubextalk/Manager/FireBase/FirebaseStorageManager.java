/*
 * Created By Jong Ho, Lee on  2021.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk.Manager.FireBase;

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

public class FirebaseStorageManager {
    public static void uploadProfileImg(Uri file, String uid) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference profilesImgRef = storage.getReference().child("profiles/"+uid);
        UploadTask uploadTask = profilesImgRef.putFile(file);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if(!task.isSuccessful()) {
                    throw task.getException();
                }
                return profilesImgRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if(task.isSuccessful()) {
                    Uri imgUri = task.getResult();
                    if (imgUri != null){
                        FirebaseStoreManager firebaseStoreManager = new FirebaseStoreManager();
                        firebaseStoreManager.updateProfileImg(imgUri.toString(), uid);
                    }
                    else
                        Log.i("FirebaseStorageManager", "uploadProfileImgFail");
                }
            }
        });
    }
    private FirebaseStorage mStorage;

    public FirebaseStorageManager(){ // 생성자
        this.mStorage = FirebaseStorage.getInstance();
    }
    public UploadTask uploadFile(Uri file , String path){ // 파일 업로드
        StorageReference storageRef = mStorage.getReference();
        StorageReference fileRef = storageRef.child(path+file.getLastPathSegment());
        UploadTask uploadTask = fileRef.putFile(file);
        return uploadTask;
    }
    public Task  downloadFile(String path){ // 일단은 url 로 불러오기
        StorageReference storageRef = mStorage.getReference();
        StorageReference fileRef = storageRef.child(path);
        Task downloadTask  = fileRef.getDownloadUrl();
        return downloadTask;
    }
}
