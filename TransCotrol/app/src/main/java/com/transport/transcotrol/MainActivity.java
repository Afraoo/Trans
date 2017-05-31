package com.transport.transcotrol;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

//    private Spinner spinner = null;
//    private ArrayAdapter<CharSequence> adapter = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);//setContentView();显示在MainActivity这个布局文件中要显示的内容,即activity_main.xml中的内容

        //spinner = (Spinner) findViewById(R.id.spinner1);
       // adapter = new ArrayAdapter<CharSequence>(this,R.layout.simple_spinner_item,data_list);
        //spinner.setAdapter(adapter);
    }
}
