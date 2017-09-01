package com.transport.transpotation;

        import android.content.Intent;

        import android.content.res.Configuration;
        import android.graphics.drawable.Drawable;
        import android.os.Build;
        import android.os.Handler;
        import android.os.Message;

        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.text.method.ScrollingMovementMethod;
        import android.util.Log;

        import android.view.KeyEvent;
        import android.view.View;
        import android.view.Window;
        import android.widget.AdapterView;
        import android.widget.ArrayAdapter;
        import android.widget.Button;
        import android.widget.Spinner;
        import android.widget.TextView;
        import android.widget.Toast;

        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;
        import org.json.JSONTokener;

        import java.util.ArrayList;
        import java.util.HashMap;
        import java.util.List;
        import java.util.regex.Pattern;

        import NetTools.HttpCallbackListener;
        import NetTools.HttpUtils;

public class MainActivity extends AppCompatActivity {
    private Spinner teamNumberSpinner= null;  //车队编号
    private Spinner carNumberSpinner= null;     //小车队内编号
    ArrayAdapter<String> teamNumberAdapter= null;  //车队编号适配器
    ArrayAdapter<String> carNumberAdapter= null;    //小车队内编号适配器
    private String teamSelected=null;   //小车队内编号选项值
    private String carSelected=null;   //小车队内编号选项值
    private List<String>  team = new ArrayList<String>();
    private List<String> car = new ArrayList<String>();
    //开始加载车队编号下拉框时第一个值
    private static String firstNum = null;
    private TextView separate;
    private String order;

    Handler mHandler = new Handler(){//处理从服务器获得的车队编号
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
//            Toast.makeText(MainActivity.this,result,Toast.LENGTH_SHORT).show();
            JSONArray json = null;
          try {
//               json = (JSONArray) new JSONTokener(msg.obj.toString()).nextValue();
              json = new JSONArray(msg.obj.toString());
           } catch (JSONException e) {
               e.printStackTrace();
            }

            JSONObject job = null; // 遍历 jsonarray 数组，把每一个对象转成 json 对象
            team.add("全部");
            if(json !=null){
                for(int i=0;i<json.length();i++){
                    try {
                        job = json.getJSONObject(i);
                        team.add(job.getString("group_num"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                Util.showToast(MainActivity.this, "未从服务器获得车队编号");
            }
            StringBuilder sb = new StringBuilder();
            sb.append(team.get(0));
            firstNum = sb.toString();

            if(firstNum.equals("全部")) {
                car.clear();
                car.add("全部");
                ArrayAdapter<String> adapterc = new ArrayAdapter<String>(MainActivity.this,R.layout.simple_spinner_item,car);
                carNumberSpinner.setAdapter(adapterc);
                carNumberSpinner.setSelection(0,true);
            } else {
                getCarNum(firstNum);
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this,R.layout.simple_spinner_item,team);
            teamNumberSpinner.setAdapter(adapter);
            teamNumberSpinner.setSelection(0,true);
        }
    };

    Handler cHandler = new Handler(){//处理从服务器获得的车队内小车编号
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
            JSONArray json = null;
            try {
                json = (JSONArray) new JSONTokener(msg.obj.toString()).nextValue();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JSONObject job = null; // 遍历 jsonarray 数组，把每一个对象转成 json 对象
            car.clear();
            car.add("全部");
            if(json.length()>0){
                for(int i=0;i<json.length();i++){
                    try {
                        job = json.getJSONObject(i);
                        car.add(job.getString("car_num"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
            else {
                Util.showToast(MainActivity.this, "未从服务器获得小车队内编号");
            }
            ArrayAdapter<String> adapterc = new ArrayAdapter<String>(MainActivity.this,R.layout.simple_spinner_item,car);
            carNumberSpinner.setAdapter(adapterc);
            carNumberSpinner.setSelection(0,true);

        }
    };

    //处理合流、分流操作从服务器反馈回来的信息
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
//            Util.showToast(MainActivity.this,result);
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
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        Button button_affirm = (Button) MainActivity.this.findViewById(R.id.button_affirm);
        separate = (TextView) MainActivity.this.findViewById(R.id.separate0);

        getTeamNum();
        setSpinner();


        Intent intent =getIntent();
        String order1 = intent.getStringExtra("order");
        System.out.println("order1: "+order1);
        if(order1 != null && !order1.isEmpty()) {
            separate.setText(order1);
        }
        order = separate.getText().toString();

        //确认按钮点击响应事件
        button_affirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object ot = teamNumberSpinner.getSelectedItem();
                String teamN = ot.toString();
                Object oc = carNumberSpinner.getSelectedItem();
                String carN = oc.toString();


                if(!teamN.equals("") && team != null && !carN.equals("") && carN != null) {
                    if(carSelected.equals("全部")&&!teamSelected.isEmpty())
                    {

                        Intent intent=new Intent(MainActivity.this,AllCarActivity.class);
                        intent.putExtra("teamNumber",teamN);  //向下一活动传递车队编号
                        intent.putExtra("order",order);
                        startActivity(intent);

                    } else {

                        Intent intent=new Intent(MainActivity.this,SingleCarActivity.class);
                        intent.putExtra("teamNumber",teamN);  //向下一活动传递小车队内编号
                        intent.putExtra("carNumber",carN);  //向下一活动传递小车队内编号
                        intent.putExtra("order",order);
                        startActivity(intent);
                    }

                } else {
                    System.out.println("下拉框选择无效！请检查与服务器的连接！");
                }


            }
        });

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

//        runOnUiThread(new Runnable() { @Override public void run() {
//            onBackPressed(); }
//        });

    }

    //点击返回键返回桌面而不是退出程序
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //处理车队编号和小车队内编号下拉框
    private void setSpinner()
    {
        //添加下拉框控件
        teamNumberSpinner= (Spinner)MainActivity.this.findViewById(R.id.spin_TeamNumber);
        carNumberSpinner= (Spinner)MainActivity.this.findViewById(R.id.spin_CarNumber);


        //车队编号下拉框监听
        teamNumberSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            // 表示选项被改变的时候触发此方法，主要实现办法：动态改变地级适配器的绑定值
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3)
            {
                //position为当前车队编号所选中的值`

                //将小车队内适配器的值改变为car[position]中的值

                String teamNumber = team.get(position);
                teamSelected = teamNumber;
                if(teamSelected.equals("全部")) {
                    car.clear();
                    car.add("全部");
                    carNumberSpinner.setSelection(0,true);

                    Util.showToast(MainActivity.this,"已选择对所有车队进行操作");
                } else {
                    getCarNum(teamNumber);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0)
            {

            }

        });

        carNumberSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object cc = carNumberSpinner.getSelectedItem();
                carSelected=cc.toString();
                if(carSelected.equals("全部") && !carSelected.equals("")){
                    Util.showToast(MainActivity.this,"已选择对车队进行操作");

                } else if(isInteger(carSelected) && !carSelected.isEmpty()){
                    Util.showToast(MainActivity.this,"已选择对单个小车进行操作");
                } else{
                    Util.showToast(MainActivity.this,"队内编号选择出错");
                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    /*
* 判断字符串是否为整数
* @param str 传入的字符串
* @return 是整数返回true,否则返回false
*/
    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }


    public void getTeamNum(){
        String originAddress = "http://192.168.3.25:8080/TServer/GetTeamNum";
        HashMap<String, String> params = new HashMap<String, String>();
        try {
            String compeletedURL = HttpUtils.getURLWithParams(originAddress, params);
            HttpUtils.sendHttpRequest(compeletedURL, new HttpCallbackListener() {
                @Override
                public void onFinish(String response) {
                    Message message = new Message();
                    message.obj = response;
                    mHandler.sendMessage(message);
                }

                @Override
                public void onError(Exception e) {
                    Message message = new Message();
                    message.obj = e.toString();
                    mHandler.sendMessage(message);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void getCarNum(String Number){
        String originAddress = "http://192.168.3.25:8080/TServer/getCarNum";
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("teamNumber",Number);
        try {
            String compeletedURL = HttpUtils.getURLWithParams(originAddress, params);
            HttpUtils.sendHttpRequest(compeletedURL, new HttpCallbackListener() {
                @Override
                public void onFinish(String response) {
                    Message message = new Message();
                    message.obj = response;
                    cHandler.sendMessage(message);
                }

                @Override
                public void onError(Exception e) {
                    Message message = new Message();
                    message.obj = e.toString();
                    cHandler.sendMessage(message);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
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

}
