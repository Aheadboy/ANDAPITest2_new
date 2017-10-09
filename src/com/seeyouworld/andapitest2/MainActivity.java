package com.seeyouworld.andapitest2;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.helloworld.andapitest.aidl.IRemoteService;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";
    private IRemoteService mRemoteService = null;
    private boolean mBind = false;


    //region 绑定另一个进程的一个服务，通过隐式启动方式启动另一个进程的Service
    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent();
        intent.setAction("com.helloworld.andapitest.service");
        intent.setPackage("com.helloworld.andapitest");
        bindService(intent, mConnection, BIND_AUTO_CREATE);

    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(mConnection);
        mBind = false;
    }
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Toast.makeText(MainActivity.this, service.toString(), Toast.LENGTH_SHORT).show();
            mRemoteService = IRemoteService.Stub.asInterface(service);
            mBind = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mRemoteService = null;
            mBind = false;
        }
    };
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //对方进程死亡状态下
//		03-20 22:27:19.879 20019-20019/com.helloworld.andapitest E/FirstIntentService: onDestroy: FirstIntentService//第一次点击
//		03-20 22:27:43.455 20019-20810/com.helloworld.andapitest E/FirstIntentService: onCreate: CurProcess Name is com.helloworld.andapitest//第二次
//		03-20 22:27:43.455 20019-20810/com.helloworld.andapitest E/FirstIntentService: onHandleIntent: other app give me message is Hello I'm other app//第二次
//		03-20 22:27:43.459 20019-20810/com.helloworld.andapitest E/FirstIntentService: onHandleIntent: IntentService[this is work thread name]//第二次
//		03-20 22:27:46.459 20019-20019/com.helloworld.andapitest E/FirstIntentService: onDestroy: FirstIntentService//第二次
        //进程存活，service ondestroy之后
//		03-20 22:27:43.455 20019-20810/com.helloworld.andapitest E/FirstIntentService: onCreate: CurProcess Name is com.helloworld.andapitest//第一次
//		03-20 22:27:43.455 20019-20810/com.helloworld.andapitest E/FirstIntentService: onHandleIntent: other app give me message is Hello I'm other app//第一次
//		03-20 22:27:43.459 20019-20810/com.helloworld.andapitest E/FirstIntentService: onHandleIntent: IntentService[this is work thread name]//第一次
//		03-20 22:27:46.459 20019-20019/com.helloworld.andapitest E/FirstIntentService: onDestroy: FirstIntentService//第一次
        findViewById(R.id.btn_startOthAppIntentSer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //要注意异常的捕捉，因为对方的service如果没有同时配置android:enabled="true" android:exported="true"属性，启动者app会崩溃的。
                startService(new Intent().setComponent(new ComponentName("com.helloworld.andapitest", "com.helloworld.andapitest.service.FirstIntentService")).putExtra("data", "Hello I'm start IntentService app"));
            }
        });
        findViewById(R.id.btn_startOthAppNormalSer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //要注意异常的捕捉，因为对方的service如果没有同时配置android:enabled="true" android:exported="true"属性，启动者app会崩溃的。
                startService(new Intent().setComponent(new ComponentName("com.helloworld.andapitest", "com.helloworld.andapitest.service.firstService")).putExtra("data", "Hello I'm start normal app"));
            }
        });
        //终止另一个app的service
        findViewById(R.id.btn_stopOthAppNormalSer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //要注意异常的捕捉，因为对方的service如果没有同时配置android:enabled="true" android:exported="true"属性，启动者app会崩溃的。
                stopService(new Intent().setComponent(new ComponentName("com.helloworld.andapitest", "com.helloworld.andapitest.service.firstService")).putExtra("data", "Hello I'm start normal app"));
            }
        });
        findViewById(R.id.btn_startMessengerClientActivity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,MessengerClientActivity.class));
            }
        });

        findViewById(R.id.btn_startAIDLService).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Toast.makeText(MainActivity.this, mRemoteService.getPid().toString(), Toast.LENGTH_SHORT).show();
                    Toast.makeText(MainActivity.this, Process.myPid()+"", Toast.LENGTH_SHORT).show();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
