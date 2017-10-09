package com.seeyouworld.andapitest2;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.*;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by babycomingin100days on 2017/3/28.
 */
public class MessengerClientActivity extends Activity {

    private static final int MSG_SUM = 0x110;

    private Button mBtnAdd;
    private LinearLayout mLyContainer;
    //��ʾ����״̬
    private TextView mTvState;

    private Messenger mService;
    private boolean isConn;

    private Messenger mMessenger = new Messenger(new Handler() {
        @Override
        public void handleMessage(Message msgFromServer) {
            Toast.makeText(MessengerClientActivity.this, "msgFromServerRcved", Toast.LENGTH_SHORT).show();
            switch (msgFromServer.what) {
                case MSG_SUM:
                    TextView tv = (TextView) mLyContainer.findViewById(msgFromServer.arg1);
                    tv.setText(tv.getText() + "=>" + msgFromServer.arg2);
                    break;
            }
            super.handleMessage(msgFromServer);
        }
    });
    //�����󶨿���̵�MessengerService
    private ServiceConnection mConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = new Messenger(service);
            isConn = true;
            mTvState.setText("connected!");
        }

        /**
         * �Է�����ò�ƻᴥ����������������������з���app��ʱ�򣬻ص����ҳ����ʾ�ľ���disconnected
         * @param name
         */
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            isConn = false;
            mTvState.setText("disconnected!");
        }
    };
    private int mA;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_messenger_client);
        //��ʼ�󶨷���
        bindServiceInvoked();

        mTvState = (TextView) findViewById(R.id.id_tv_callback);
        mBtnAdd = (Button) findViewById(R.id.id_btn_add);
        mLyContainer = (LinearLayout) findViewById(R.id.id_ll_container);

        mBtnAdd.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    int a = mA++;
                    int b = (int) (Math.random() * 100);

                    //����һ��tv,��ӵ�LinearLayout��
                    TextView tv = new TextView(MessengerClientActivity.this);
                    tv.setText(a + " + " + b + " = caculating ...");
                    tv.setId(a);
                    mLyContainer.addView(tv);

                    Message msgFromClient = Message.obtain(null, MSG_SUM, a, b);
                    msgFromClient.replyTo = mMessenger;
                    if (isConn)
                    {
                        //������˷�����Ϣ
                        mService.send(msgFromClient);
                        Toast.makeText(MessengerClientActivity.this, "clientMsgHasSended", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(MessengerClientActivity.this, "notConnYet", Toast.LENGTH_SHORT).show();
                                            }
                } catch (RemoteException e)
                {
                    e.printStackTrace();
                }
            }
        });
    }
    private void bindServiceInvoked()
    {
        Intent intent = new Intent();
        intent.setAction("com.zhy.aidl.calc");
        bindService(new Intent().setComponent(new ComponentName("com.helloworld.andapitest", "com.helloworld.andapitest.service.MessengerService")), mConn, Context.BIND_AUTO_CREATE);
       // Log.e(TAG, "bindService invoked !");
        Toast.makeText(this, "bindService invoked !", Toast.LENGTH_SHORT).show();
    }
}
