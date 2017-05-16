package com.example.vrml.camera;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;

import java.io.File;
import java.text.SimpleDateFormat;

/**
 * Created by VRML on 2017/5/14.
 */

public class OPEN_CAMERA {

    public enum PICID{
        PHOTO_REQUEST_TAKEPHOTO(1),
        PHOTO_REQUEST_GALLERY(2),
        PHOTO_REQUEST_CUT(3),
        IMAGE_CUT(5);
        private final int id;
        PICID(final int id){
            this.id = id;
        }
        public int getId(){return this.id;}
    }

    private static final int PHOTO_REQUEST_TAKEPHOTO = 1;// 拍照(CAMERA)
    private static final int PHOTO_REQUEST_GALLERY = 2;// 從相冊中選擇(PHOTO)
//    private static final int IMAGE_SELECT=4; //選擇圖片
    private static final int IMAGE_CUT=5; //裁剪圖片

    public Intent openphoto(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        return intent;
    }

    public Intent opencamera(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 指定調用相機拍照後照片的儲存路徑

        return takePictureIntent;
    }


    public Intent crop (Intent intent) {
        // intent.setType("image/*");
        intent.putExtra("crop","true");
        intent.putExtra("aspectX",1);
        intent.putExtra("aspectY",1);
        intent.putExtra("outputX",100);
        intent.putExtra("outputY",100);
        intent.putExtra("return-date",true);
        intent.putExtra("noFaceDetection",true);
        return intent;
    }

    public Intent crop(Uri uri){
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri,"image/*");
        return crop(intent);
    }



    public int getCameraID(){return PHOTO_REQUEST_TAKEPHOTO;}
    public int getPhotoID(){return PHOTO_REQUEST_GALLERY;}
    public int getImageCut() {return IMAGE_CUT;}
    public int getCropID() {return IMAGE_CUT;}
}
