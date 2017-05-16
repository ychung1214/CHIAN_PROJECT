package com.example.wennlab.camera_cut;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.jar.Attributes;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements OnClickListener {
    //以下是老羅第二次
   // private Button selectImageBtn;
    private Button cutImageBtn;
    //以下是第一次的
    private Button creama=null;
    private ImageView img_creama=null;
    //聲明兩個靜態的整型變量，主要是用於意圖的返回的標誌

    private static final int IMAGE_SELECT=4; //選擇圖片
    private static final int IMAGE_CUT=5; //裁剪圖片

    private TextView text=null;
    private File tempFile = new File(Environment.getExternalStorageDirectory(),
            getPhotoFileName());

    private static final int PHOTO_REQUEST_TAKEPHOTO = 1;// 拍照
    private static final int PHOTO_REQUEST_GALLERY = 2;// 從相冊中選擇
    private static final int PHOTO_REQUEST_CUT = 3;// 結果
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
     //   selectImageBtn = (Button)this.findViewById(R.id.selectImageBtn);
        cutImageBtn = (Button)this.findViewById(R.id.cutImageBtn);
    //    selectImageBtn.setOnClickListener(this);
        cutImageBtn.setOnClickListener(this);
        //imageView.setImageBitmap(bm);
        init(); //imageview的註冊在init函式裡
        Log.i("TAG-->", ""+Environment.getExternalStorageDirectory());
    }


    // 使用系統當前日期加以調整作為照片的名稱
    private String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "'IMG'_yyyyMMdd_HHmmss");
        return dateFormat.format(date) + ".jpg";
    }
    private void init() {
        creama=(Button) findViewById(R.id.btn_creama);
        img_creama=(ImageView) findViewById(R.id.imageview); //原本是img_creama
        creama.setOnClickListener(listener);
        text=(TextView) findViewById(R.id.text);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK){
            //處理圖片按照手機的螢幕大小顯示
            if (requestCode==IMAGE_SELECT) {
                Uri uri = data.getData();//獲得圖片路徑
                int dw = getWindowManager().getDefaultDisplay().getWidth();//獲得螢幕寬度
                int dh = getWindowManager().getDefaultDisplay().getHeight() / 2;//獲得螢幕寬度
                try {
                    //實現對圖片的裁剪的類別，是一個暱名的類別
                    BitmapFactory.Options factory = new BitmapFactory.Options();
                    factory.inJustDecodeBounds = true; //true=允許圖片不用依照像素分配給內存

                    Bitmap bmp = BitmapFactory.decodeStream(
                            getContentResolver().openInputStream(uri),
                            null,factory);
                    //對圖片的寬高對應手機的螢幕進行匹配

                    int hRatio = (int)Math.ceil(factory.outHeight/(float)dh);
                    //如果hRatio大於1 表示圖片高度大於手機螢幕高度
                    int wRatio = (int)Math.ceil(factory.outWidth/(float)dw);
                    //如果hRatio大於1 表示圖片高度大於手機螢幕寬度
                    //縮放到1/radio的尺寸和1/radio^2像素
                    if (hRatio > 1 || wRatio > 1){
                        if (hRatio>wRatio){
                            factory.inSampleSize = hRatio;
                        }else{
                            factory.inSampleSize = wRatio;
                        }
                    }

                    factory.inJustDecodeBounds = false;
                    //使用bitmapFactory對圖片進行適屏的操作
                    bmp = BitmapFactory.decodeStream(getContentResolver().
                            openInputStream(uri),null,factory);
                    img_creama.setImageBitmap(bmp);
                } catch (Exception e){

                }
                //表示裁切圖片
            }else if (requestCode==IMAGE_CUT){
                Bitmap bitmap = data.getParcelableExtra("data");
                img_creama.setImageBitmap(bitmap);
            }
        }
        switch (requestCode) {
            case PHOTO_REQUEST_TAKEPHOTO:// 當選擇拍照時調用
                startPhotoZoom(Uri.fromFile(tempFile));
                break;
            case PHOTO_REQUEST_GALLERY:// 當選擇從本地獲取圖片時
                // 做非空判斷，當我們覺得不滿意想重新剪裁的時候便不會報異常，下同
                if (data != null)
                    startPhotoZoom(data.getData());
                break;
            case PHOTO_REQUEST_CUT:// 返回的結果
                if (data != null)
                    // setPicToView(data);
                    sentPicToNext(data);
                break;
        }
    }

    public void onClick(View v){
        switch (v.getId()){
           /* case R.id.selectImageBtn:
                //如何提取手機圖片庫並且進行選擇圖片功能
                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,IMAGE_SELECT);
                break; */
            case R.id.cutImageBtn:
                Intent intent2 = getImageClipIntent();
                startActivityForResult(intent2,IMAGE_CUT);
                break;
        }
    }

    private Intent getImageClipIntent() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT,null);
        //來實現對圖片的裁剪，必須要設置圖片的屬性和大小
        intent.setType("image/*"); //去獲取任意的圖片類型
        intent.putExtra("crop","true"); //滑動選取圖片
        // aspectX aspectY 是寬高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        // outputX,outputY 是剪裁圖片的寬高
        intent.putExtra("outputX", 100);
        intent.putExtra("outputY", 100);
        intent.putExtra("return-data", true);
        intent.putExtra("noFaceDetection", true);
        return intent;

    }

    private OnClickListener listener=new OnClickListener(){

        @Override
        public void onClick(View arg0) {

            Intent cameraintent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // 指定調用相機拍照後照片的儲存路徑
            cameraintent.putExtra(MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(tempFile));
            startActivityForResult(cameraintent, PHOTO_REQUEST_TAKEPHOTO);

        }};


  //  public boolean onCreateOptionsMenu(Menu menu) {
       // getMenuInflater().inflate(R.menu.activity_crama, menu);
     //   return true;
   // }



    private void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop為true是設置在開啟的intent中設置顯示的view可以剪裁
        intent.putExtra("crop", "true");

        // aspectX aspectY 是寬高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        // outputX,outputY 是剪裁圖片的寬高
        intent.putExtra("outputX", 100);
        intent.putExtra("outputY", 100);
        intent.putExtra("return-data", true);
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }

    // 將進行剪裁後的圖片傳遞到下一個界面上
    private void sentPicToNext(Intent picdata) {
        Bundle bundle = picdata.getExtras();
        if (bundle != null) {
            Bitmap photo = bundle.getParcelable("data");
            if (photo==null) {
                img_creama.setImageResource(R.drawable.top);
            }else {
                img_creama.setImageBitmap(photo);
//                設置文本內容为    圖片絕對路徑和名字
                text.setText(tempFile.getAbsolutePath());
            }

            ByteArrayOutputStream baos = null;
            try {
                baos = new ByteArrayOutputStream();
                photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] photodata = baos.toByteArray();
                System.out.println(photodata.toString());
                // Intent intent = new Intent();
                // intent.setClass(RegisterActivity.this, ShowActivity.class);
                // intent.putExtra("photo", photodata);
                // startActivity(intent);
                // finish();
            } catch (Exception e) {
                e.getStackTrace();
            } finally {
                if (baos != null) {
                    try {
                        baos.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void show (View v)
    {
        RadioGroup rg = (RadioGroup)findViewById(R.id.share);

        //依選取項目顯示不同訊息
        switch(rg.getCheckedRadioButtonId()){
            case R.id.radioButton_yes:
                break;
            case R.id.radioButton_no:
                break;
        }
    }
}
