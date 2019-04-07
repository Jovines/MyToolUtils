package com.tree.mytoolutils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.tree.mytoolutils.image.ImageLoader;

import java.util.concurrent.ExecutionException;


public class MainActivity extends AppCompatActivity {

    final int UPDATE_IMAGE = 1;

    ImageView imageView1;
    ImageView imageView2;
    ImageView imageView3;
    ImageView imageView4;
    ImageView imageView5;
    ImageView imageView6;

    Button button;
    final int READ_EXTERNAL = 6;
    final int WRITE_EXTERNAL = 7;


    String uri = "https://img5.duitang.com/uploads/item/201406/28/20140628145613_VWnXL.jpeg";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView1 = (ImageView) findViewById(R.id.image1);
        imageView2 = (ImageView) findViewById(R.id.image2);
        imageView3 = (ImageView) findViewById(R.id.image3);
        imageView4 = (ImageView) findViewById(R.id.image4);
        imageView5 = (ImageView) findViewById(R.id.image5);
        imageView6 = (ImageView) findViewById(R.id.image6);

        button = (Button) findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL);
                } else {
                    ImageLoader.with(button.getContext()).getImage(uri).into(imageView1);
                    ImageLoader.with(button.getContext()).getImage(uri).into(imageView2);
                    ImageLoader.with(button.getContext()).getImage(uri).into(imageView3);
                    ImageLoader.with(button.getContext()).getImage(uri).into(imageView4);
                    ImageLoader.with(button.getContext()).getImage(uri).into(imageView5);
                    ImageLoader.with(button.getContext()).getImage(uri).into(imageView6);

                }
//                ImageLoader.with(button.getContext()).getImage("http://img2.imgtn.bdimg.com/it/u=1122649470,955539824&fm=26&gp=0.jpg").into(imageView);
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case WRITE_EXTERNAL:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                }
                break;
            case READ_EXTERNAL:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                }
        }
    }
}
