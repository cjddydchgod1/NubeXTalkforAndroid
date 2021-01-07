/*
 * Created By Jong Ho, Lee on  2021.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk.Manager.FireBase;

import android.net.Uri;

import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class FirebaseStorageManager {
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
