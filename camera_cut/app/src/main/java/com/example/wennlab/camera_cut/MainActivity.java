package com.example.wennlab.camera_cut;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private Button creama=null;
    private ImageView img=null;
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
        init();
        Log.i("TAG-->", ""+Environment.getExternalStorageDirectory());
    }
    private void init() {

        creama=(Button) findViewById(R.id.btn_creama);

        img=(ImageView) findViewById(R.id.img_creama);

        creama.setOnClickListener(listener);
        text=(TextView) findViewById(R.id.text);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
        super.onActivityResult(requestCode, resultCode, data);
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
        // crop为true是設置在開启的intent中設置顯示的view可以剪裁
        intent.putExtra("crop", "true");

        // aspectX aspectY 是寬高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        // outputX,outputY 是剪裁圖片的寬高
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
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
                img.setImageResource(R.drawable.top);
            }else {
                img.setImageBitmap(photo);
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

    // 使用系統當前日期加以調整作为照片的名稱
    private String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "'IMG'_yyyyMMdd_HHmmss");
        return dateFormat.format(date) + ".jpg";
    }
}
