package com.example.vrml.camera;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Debug;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity  {

    OPEN_CAMERA open_camera;
    Intent openphoto;
    Intent opencamera;
    Intent opencrop;
    private int PHOTOID;
    private int CAMERAID;
    private int CROPID;
    private int MY_REQUEST_CODE = 50;

    enum Integer {PHOTOID, CAMERAID, CROPID}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initCAMERA();
    }

    private void initCAMERA() {
        opencamera = open_camera.opencamera();
        open_camera = new OPEN_CAMERA();
        openphoto = open_camera.openphoto();

        if (opencamera == null)
            Toast.makeText(MainActivity.this, "NULL", Toast.LENGTH_LONG).show();

        PHOTOID = open_camera.getPhotoID();
        CAMERAID = open_camera.getCameraID();
        CROPID = open_camera.getCropID();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA},
                        MY_REQUEST_CODE);
            } else {
                OPENCAMERA();
            }
        } else {
            OPENCAMERA();
        }
    }

    private void OPENCROP(Uri uri) {
        opencrop = open_camera.crop(uri);
        startActivityForResult(opencrop, CROPID);
    }

    private void OPENPHOTO() {
        startActivityForResult(openphoto, PHOTOID);
    }

    private void OPENCAMERA() {
        if (opencamera.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(opencamera, CAMERAID);
            Log.e("DEBUG", "");
        } else
            Toast.makeText(MainActivity.this, "Your Camera is Wrong", Toast.LENGTH_LONG).show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERAID) {
                if (data != null) {
                    Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver()
                                ,(Bitmap) data.getExtras().get("data"), null, null));
                    OPENCROP(uri);
                } else
                    Toast.makeText(this, "data == null", Toast.LENGTH_SHORT).show();
            } else if (requestCode == PHOTOID)
                OPENCROP(data.getData());
            else if (requestCode == CROPID) {
                Bitmap bitmap = data.getParcelableExtra("data");
                //RETURN TO 信慈
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Now user should be able to use camera
                OPENCAMERA();
                Toast.makeText(MainActivity.this, "Thanks ^^", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Fuck U ^^凸", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
