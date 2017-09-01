package com.transport.transpotation;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.res.Configuration;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;


import org.w3c.dom.Text;

import java.lang.reflect.Field;
import java.util.HashMap;

import NetTools.HttpCallbackListener;
import NetTools.HttpUtils;
import NetTools.SendAngle;
import NetTools.SendSpeed;
import NetTools.SendText;

public class SingleCarActivity extends AppCompatActivity {
    private Paint paint = new Paint();
    int center = 0;
    int innerRadius = 0;
    private float innerCircleRadius = 0;
    private float smallCircle = 10;
    public CircleView.Dir dir = CircleView.Dir.CENTER;

    private String distanceno ="no";
    private String speed1="0.0";
    private String angle1="0";
    private String angle;
    private String speed;
    private String order;
    private SendText sendText = new SendText();
    private SendSpeed sendSpeed = new SendSpeed();
    private SendAngle sendAngle = new SendAngle();
    private SetPoint setPoint = new SetPoint();

    private TextView carInfo; //车量信息设定
    private TextView speedSet; //速度设定
    private TextView angleSet;//角度设定
    private TextView stopSet;//急停
    private TextView backToMain;
    private TextView separate;


    Handler nHandler = new Handler() {// 急停的信息返回和处理
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Util.showToast(SingleCarActivity.this, Util.showMessage(msg));
        }
    };

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
            Util.showToast(SingleCarActivity.this,result);
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
        setContentView(R.layout.activity_single_car);

        carInfo=(TextView)findViewById(R.id.car_info);
        speedSet = (TextView) findViewById(R.id.speed_set);
        angleSet = (TextView) findViewById(R.id.angle_set);
        stopSet = (TextView) findViewById(R.id.stop_set);
        backToMain=(TextView)findViewById(R.id.back1) ;
        separate=(TextView)findViewById(R.id.separate1);


        backToMain.setMovementMethod(ScrollingMovementMethod.getInstance());
        backToMain.setClickable(true);
        backToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              Intent intent =new Intent(SingleCarActivity.this,MainActivity.class);
                intent.putExtra("order",order);
                startActivity(intent);
            }
        });

       separate.setMovementMethod(ScrollingMovementMethod.getInstance());
        separate.setClickable(true);
        separate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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


        //获取从MainActivity中传来的两个编号并设置在TextView中
        Intent intent =getIntent();
        final String teamM = intent.getStringExtra("teamNumber");
        final String carM = intent.getStringExtra("carNumber");
        carInfo.setText(teamM+"号车队    "+carM+"号车辆");
        order = intent.getStringExtra("order");
        separate.setText(order);

        //创建并设置速度输入框
        speed=speed1+"m/s";
        speedSet.setText(speed);
        speedSet.setMovementMethod(ScrollingMovementMethod.getInstance());
        speedSet.setClickable(true);
        speedSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater layoutInflater=(LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                LinearLayout layout=(LinearLayout)layoutInflater.inflate(R.layout.speed_edit, null);
                final EditText et1=(EditText) layout.findViewById(R.id.speedEdit);
                setPoint.setPoint(et1);
                AlertDialog alertDialog1 = new AlertDialog.Builder(SingleCarActivity.this)
                        .setTitle("请输入速度（单位：m/s）")
                        .setIcon(R.drawable.warning)
                        .setView(layout)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //对速度输入是否为空进行判断
                                if(speed1.length() !=0 && !TextUtils.isEmpty(et1.getText())) {
                                    speed1=et1.getText().toString();
                                    if(isNumber(speed1)) {
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
                                            e.printStackTrace();
                                        }
                                        double a = Double.parseDouble(speed1);
                                        String b = String.valueOf(a);
                                        String speed=b+"m/s";
                                        speedSet.setText(speed);
                                        sendSpeed.sendSpeed(teamM,carM,b);
                                    } else {
                                        AlertDialog dialog1 = new AlertDialog.Builder(SingleCarActivity.this)
                                                .setTitle("警告")
                                                .setMessage("请输入数字")
                                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                    }
                                                }).create();
                                        dialog1.show();
                                    }

                                } else {
                                    AlertDialog dialog2 = new AlertDialog.Builder(SingleCarActivity.this)
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
                                    e.printStackTrace();
                                }
                            }
                        }).create();
                alertDialog1.show();
            }
        });

//创建并设置角度输入框
        angle=angle1+"°";
        angleSet.setText(angle);
        angleSet.setMovementMethod(ScrollingMovementMethod.getInstance());
        angleSet.setClickable(true);
        angleSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater=(LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                LinearLayout layout=(LinearLayout)layoutInflater.inflate(R.layout.angle_edit, null);
                final RegionNumberEditText et2=(RegionNumberEditText)layout.findViewById(R.id.angleEdit);

                et2.setRegion(20,0);
                SpannableString ss = new SpannableString("请输入0-20的整数");//定义hint的值
                AbsoluteSizeSpan ass = new AbsoluteSizeSpan(15,true);//设置字体大小 true表示单位是sp
                ss.setSpan(ass, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                et2.setHint(new SpannedString(ss));
                et2.setTextWatcher();

                AlertDialog alertDialog2 = new AlertDialog.Builder(SingleCarActivity.this)
                        .setTitle("警告")
                        .setMessage("请输入角度（单位：度）")
                        .setIcon(R.drawable.warning)
                        .setView(layout)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //对角度输入是否为空进行判断
                                if( angle1.length() !=0 && !TextUtils.isEmpty(et2.getText())) {
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
                                        e.printStackTrace();
                                    }
                                    angle1=et2.getText().toString();
                                    int a = Integer.parseInt(angle1);
                                    String b = String.valueOf(a);
                                    String angle = b+"°";
                                    angleSet.setText(angle);
                                    sendAngle.sendAngle(teamM,carM,b);
                                } else {
                                    AlertDialog dialog3 = new AlertDialog.Builder(SingleCarActivity.this)
                                            .setTitle("警告")
                                            .setMessage("角度不能为空！请重新输入！")
                                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                }
                                            }).create();
                                    dialog3.show();
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
                                    e.printStackTrace();
                                }
                            }
                        }).create();
                alertDialog2.show();
            }
        });
        /*
             *  * 设置方向盘
                  *  */
        final CircleView cv = (CircleView)findViewById(R.id.cvm);
        cv.setDir(CircleView.Dir.CENTER);
//        sendText.sendText(teamM,carM,speed1,distanceno,angle1,"center");

        cv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_DOWN) { //点击一次时导致onTouch被调用了两次是由于手指离开和靠近时都调用了该方法，为了区分，添加判断
                    CircleView.Dir tmp = CircleView.Dir.CENTER;
                    if ((tmp = checkDir(event.getX(), event.getY())) != CircleView.Dir.UNDEFINE) {
                        dir = tmp;
                        cv.setDir(dir);
                        cv.invalidate();
                    }
                    return true;
                } else { //手指离开时不调用该操作
                    return true;
                }

            }

            /**
             * 检测方向
             *
             * @param x
             * @param y
             * @return
             */
            private CircleView.Dir checkDir(float x, float y) {
                CircleView.Dir dir = CircleView.Dir.UNDEFINE;

                if (Math.sqrt(Math.pow(y - cv.center, 2) + Math.pow(x - cv.center, 2)) < innerCircleRadius) {// 判断在中心圆圈内
                    dir = CircleView.Dir.CENTER;
                    sendText.sendText(teamM,carM,speed1,distanceno,angle1,"center");
                    System.out.println("----中央");
                }
                else if (y < x && y + x < 2 * cv.center) {
                    dir = CircleView.Dir.UP;
                    sendText.sendText(teamM,carM,speed1,distanceno,angle1,"up");
                    System.out.println("----向上");
                }
                else if (y < x && y + x > 2 * cv.center) {
                    dir = CircleView.Dir.RIGHT;
                    sendText.sendText(teamM,carM,speed1,distanceno,angle1,"right");
                    System.out.println("----向右");
                } else if (y > x && y + x < 2 * cv.center) {
                    dir = CircleView.Dir.LEFT;
                    sendText.sendText(teamM,carM,speed1,distanceno,angle1,"left");
                    System.out.println("----向左");
                }
//                else if (y > x && y + x > 2 * cv.center) {
//                    dir = CircleView.Dir.DOWN;
//                    sendText(teamM,carM,speed1,distanceno,angle1,"down");
//                    System.out.println("----向下");
//                }

                return dir;
            }

        });


        stopSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setStop(teamM,carM);

            }
        });

    }

    public void setStop(String teamNumber, String carNumber){
        String originAddress = "http://192.168.3.25:8080/TServer/toStop";
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("teamNumber",teamNumber);
        params.put("carNumber",carNumber);
        try {
            String compeletedURL = HttpUtils.getURLWithParams(originAddress, params);
            HttpUtils.sendHttpRequest(compeletedURL, new HttpCallbackListener() {
                @Override
                public void onFinish(String response) {
                    Message message = new Message();
                    message.obj = response;
                    nHandler.sendMessage(message);
                }

                @Override
                public void onError(Exception e) {
                    Message message = new Message();
                    message.obj = e.toString();
                    nHandler.sendMessage(message);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public void sendText(String teamNumber, String carNumber, String speed, String distance, String angle, String direction) {
//        String originAddress = "http://192.168.1.203/TServer/getText";
//        HashMap<String, String> params = new HashMap<String, String>();
//        params.put("teamNumber", teamNumber);
//        params.put("carNumber", carNumber);
//        params.put("speed", speed);
//        params.put("distance", distance);
//        params.put("angle", angle);
//        params.put("direction", direction);
//        try {
//            String compeletedURL = HttpUtils.getURLWithParams(originAddress, params);
//            HttpUtils.sendHttpRequest(compeletedURL, new HttpCallbackListener() {
//                @Override
//                public void onFinish(String response) {
//                    Message message = new Message();
//                    message.obj = response;
//                    tHandler.sendMessage(message);
//                }
//
//                @Override
//                public void onError(Exception e) {
//                    Message message = new Message();
//                    message.obj = e.toString();
//                    tHandler.sendMessage(message);
//                }
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

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
