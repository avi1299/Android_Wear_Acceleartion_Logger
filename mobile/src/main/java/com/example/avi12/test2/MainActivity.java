package com.example.avi12.test2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        }

    public void toastON(View view){
        Toast Mytoast = Toast.makeText(this,"Data Collection ON",Toast.LENGTH_SHORT);
        Mytoast.show();
    }

    public void toastOFF(View view){
        Toast Mytoast = Toast.makeText(this,"Data Collection OFF",Toast.LENGTH_SHORT);
        Mytoast.show();
    }


}

