package com.transport.transpotation;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.HashMap;

import NetTools.HttpCallbackListener;
import NetTools.HttpUtils;
import NetTools.SendDistance;
import NetTools.SendSpeed;
import NetTools.SendText;

public class AllCarActivity extends AppCompatActivity {
    private ImageButton speedEdit; //速度设定
    private ImageButton distanceEdit;//车间距设定
    private Button speedDefault;//默认车速设定
    private Button distanceDefault;//默认车间距设定
    private TextView carInfo; //车辆信息设定
    private TextView getSpeed;//获取速度输入值
    private TextView getDistance;//获取车间距输入值
    private Button affirm;//确认键
    private TextView backToMain;//返回键
    private TextView separate;
    private SendText sendText = new SendText();
    private SendSpeed sendSpeed = new SendSpeed();
    private SendDistance sendDistance = new SendDistance();
    private SetPoint setPoint = new SetPoint();

    private String carno = "no";
    private String angleno ="no";
    private String speed2="0.0";
    private String distance2="1.0";
    private String order;

    Handler sHandler = new Handler(){//处理从服务器获得的
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String result = "";
            if ("OK".equals(msg.obj.toString())){
                result = "success";
            }else if ("Wrong".equals(msg.obj.toString())){
                result = "fail";
            }else {
                result = msg.obj.toString();
            }
            Util.showToast(AllCarActivity.this,result);
        }
    };

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if(newConfig.orientation== Configuration.ORIENTATION_LANDSCAPE) {
            //横向
            //setContentView(R.layout.activity_main);
        } else {
            //竖向
            // setContentView(R.layout.activity_main);

        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_car);

        carInfo=(TextView)findViewById(R.id.car_info);
        speedEdit=(ImageButton) findViewById(R.id.speed_edit);
        distanceEdit=(ImageButton) findViewById(R.id.distance_edit);
        speedDefault=(Button)findViewById(R.id.quick_set_speed);
        distanceDefault=(Button)findViewById(R.id.quick_set_distance);
        getSpeed=(TextView)findViewById(R.id.get_speed) ;
        getDistance=(TextView)findViewById(R.id.get_distance);
        affirm=(Button)findViewById(R.id.affirmButton);
        backToMain=(TextView)findViewById(R.id.back2) ;
        separate=(TextView)findViewById(R.id.separate2);


        //获取从MainActivity中传来的车队编号并设置在TextView中
        Intent intent =getIntent();
        final String teamM = intent.getStringExtra("teamNumber");
        String number=teamM+"车队    全体车辆";
        carInfo.setText(number);
        order = intent.getStringExtra("order");
        separate.setText(order);


        separate.setMovementMethod(ScrollingMovementMethod.getInstance());
        separate.setClickable(true);
        separate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String text = separate.getText().toString();
                if (text.equals("分流")) {
                    sendSeperate("1");
                    separate.setText("合流");
                    order = "合流";
                    Drawable combine=getResources().getDrawable(R.drawable.combine);

                    combine.setBounds(0, 0, combine.getMinimumWidth(), combine.getMinimumHeight());

                    separate.setCompoundDrawables(null, null, combine, null);
                } else {
                    sendSeperate("2");
                    separate.setText("分流");
                    order = "分流";
                    Drawable split=getResources().getDrawable(R.drawable.split);

                    split.setBounds(0, 0, split.getMinimumWidth(), split.getMinimumHeight());

                    separate.setCompoundDrawables(null, null, split, null);
                }
            }
        });

        getSpeed.setText(speed2);//设置初始速度
        speedDefault.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String speedValue="10.0";
                getSpeed.setText(speedValue);
            }
        });


        speedEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //创建并设置速度输入框
                LayoutInflater layoutInflater=(LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final LinearLayout layout=(LinearLayout)layoutInflater.inflate(R.layout.speed_edit, null);
                final EditText et3=(EditText) layout.findViewById(R.id.speedEdit);
                setPoint.setPoint(et3);
                AlertDialog alertDialog1 = new AlertDialog.Builder(AllCarActivity.this)
                        .setTitle("请输入速度（单位：m/s）")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setView(layout)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //对速度输入是否为空进行判断
                                if(speed2.length()!=0&&!TextUtils.isEmpty(et3.getText())){
                                    speed2=et3.getText().toString();
                                    if(isNumber(speed2)){
                                        try
                                        {

                                            Field field = dialog.getClass()
                                                    .getSuperclass().getDeclaredField(
                                                            "mShowing" );
                                            field.setAccessible( true );
                                            //   将mShowing变量设为false，表示对话框已关闭
                                            field.set(dialog, true );
                                            dialog.dismiss();
                                        }
                                        catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        double a = Double.parseDouble(speed2);
                                        String b = String.valueOf(a);
                                        getSpeed.setText(b);
                                        if("全部".equals(teamM)){
                                            sendSpeed.sendSpeed("all","all",b);
                                        } else {
                                            sendSpeed.sendSpeed(teamM,"all",b);
                                        }


                                    }else{
                                        AlertDialog dialog1 = new AlertDialog.Builder(AllCarActivity.this)
                                                .setTitle("警告")
                                                .setMessage("请输入数字！")
                                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                    }
                                                }).create();
                                        dialog1.show();
                                    }
                                } else {
                                    AlertDialog dialog2 = new AlertDialog.Builder(AllCarActivity.this)
                                            .setTitle("警告")
                                            .setMessage("速度不能为空！请重新输入！")
                                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                }
                                            }).create();
                                    dialog2.show();
                                }
                                try
                                {

                                    Field field = dialog.getClass()
                                            .getSuperclass().getDeclaredField(
                                                    "mShowing" );
                                    field.setAccessible( true );
                                    //   将mShowing变量设为false，表示对话框已关闭
                                    field.set(dialog, false );
                                    dialog.dismiss();
                                }
                                catch (Exception e)
                                {
                                    e.printStackTrace();
                                }
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try
                                {
                                    Field field = dialog.getClass()
                                            .getSuperclass().getDeclaredField(
                                                    "mShowing" );
                                    field.setAccessible( true );
                                    //   将mShowing变量设为false，表示对话框已关闭
                                    field.set(dialog, true );
                                    dialog.dismiss();
                                }
                                catch (Exception e)
                                {
                                }

                            }
                        }).create();
                //   显示对话框
                alertDialog1.show();
            }
        });

        getDistance.setText(distance2);//设置初始车间距
        distanceDefault.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String speedValue="2.0";
                getDistance.setText(speedValue);
            }
        });

        distanceEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //创建并设置车间距输入框
                LayoutInflater layoutInflater=(LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                LinearLayout layout=(LinearLayout)layoutInflater.inflate(R.layout.distance_edit, null);
                final EditText et4=(EditText) layout.findViewById(R.id.distanceEdit);
                setPoint.setPoint(et4);//监听输入事件，限制小数后只输入一位
                AlertDialog alertDialog2 = new AlertDialog.Builder(AllCarActivity.this)
                        .setTitle("请输入车间距（单位：m）")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setView(layout)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                //对车间距输入是否为空进行判断
                                if( distance2.length() !=0 && !TextUtils.isEmpty(et4.getText())) {
                                    distance2=et4.getText().toString();
                                    if(isNumber(distance2)) {
                                        try {
                                            Field field = dialog.getClass()
                                                    .getSuperclass().getDeclaredField(
                                                            "mShowing");
                                            field.setAccessible(true);
                                            //   将mShowing变量设为false，表示对话框已关闭
                                            field.set(dialog, true);
                                            dialog.dismiss();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        double a = Double.parseDouble(distance2);
                                        String b = String.valueOf(a);
                                        getDistance.setText(b);
                                        if("全部".equals(teamM)){
                                            sendDistance.sendDistance("AllCar",getSpeed.getText().toString(),b);
                                        } else {
                                            sendDistance.sendDistance(teamM,getSpeed.getText().toString(),b);
                                        }

                                    } else {
                                        AlertDialog dialog4 = new AlertDialog.Builder(AllCarActivity.this)
                                                .setTitle("警告")
                                                .setMessage("请输入数字！")
                                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                    }
                                                }).create();
                                        dialog4.show();
                                    }

                                } else {
                                    AlertDialog dialog3 = new AlertDialog.Builder(AllCarActivity.this)
                                            .setTitle("警告")
                                            .setMessage("车间距不能为空！请重新输入！")
                                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                }
                                            }).create();
                                    dialog3.show();
                                }
                                try {
                                    Field field = dialog.getClass()
                                            .getSuperclass().getDeclaredField(
                                                    "mShowing");
                                    field.setAccessible(true);
                                    //   将mShowing变量设为false，表示对话框已关闭
                                    field.set(dialog, false);
                                    dialog.dismiss();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    Field field = dialog.getClass()
                                            .getSuperclass().getDeclaredField(
                                                    "mShowing");
                                    field.setAccessible(true);
                                    //   将mShowing变量设为false，表示对话框已关闭
                                    field.set(dialog, true);
                                    dialog.dismiss();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).create();
                //显示车间距输入框
                alertDialog2.show();
            }
        });

        backToMain.setMovementMethod(ScrollingMovementMethod.getInstance());
        backToMain.setClickable(true);
        backToMain.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
               Intent intent =new Intent(AllCarActivity.this,MainActivity.class);
                intent.putExtra("order",order);
               startActivity(intent);

            }
        });

        affirm.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                AlertDialog.Builder builder=new AlertDialog.Builder(AllCarActivity.this); //先得到构造器 

                builder.setTitle("提示"); //设置标题  
                builder.setMessage("是否确认信息?"); //设置内容  
                builder.setIcon(R.drawable.warning);//设置图标，图片id即可 
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() { //设置确定按钮  
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(teamM.equals("全部")) {
                            sendText.sendText("All",carno,speed2,distance2,angleno,"no");
                        } else {
                            sendText.sendText(teamM, carno, speed2, distance2, angleno, "no");
                        }
                        int i=0;
                        while(("").equals(sendText.getResult()) || sendText.getResult() == null){
                            i = i+1;
                            if(i>20) {
                                break;
                            }
                        }
                        if(("complex success").equals(sendText.getResult())) {
                            Util.showToast(AllCarActivity.this, "操作成功！");
                        } else {
                            Util.showToast(AllCarActivity.this,"请检查网络连接！");
                        }


                        dialog.dismiss(); //关闭dialog                                     
                    }
                });
                builder.setNegativeButton("取消",new DialogInterface.OnClickListener() {//设置取消按钮  
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Util.showToast(AllCarActivity.this, "取消成功");
                    }
                });//参数都设置完成了，创建并显示出来  
                builder.create().show();
            }
        });
    }

    public void sendSeperate(String order){
        String originAddress = "http://192.168.3.25:8080/TServer/toSeperate";
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("order",order);
        try {
            String compeletedURL = HttpUtils.getURLWithParams(originAddress, params);
            HttpUtils.sendHttpRequest(compeletedURL, new HttpCallbackListener() {
                @Override
                public void onFinish(String response) {
                    Message message = new Message();
                    message.obj = response;
                    sHandler.sendMessage(message);
                }

                @Override
                public void onError(Exception e) {
                    Message message = new Message();
                    message.obj = e.toString();
                    sHandler.sendMessage(message);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断输入的字符串是否为数字
     * @param value
     * @return
     */
    public boolean isNumber(String value) {
        return isInteger(value) || isDouble(value);
    }

    /**
     * 判断字符串是否为整数
     * @param value
     * @return
     */
    public boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 判断字符串是否为Double型
     * @param value
     * @return
     */
    public boolean isDouble(String value) {
        try {
            Double.parseDouble(value);
            if (value.contains("."))
                return true;
            return false;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
