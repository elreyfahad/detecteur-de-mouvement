package km.abdere.fahad.projetarduino;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    Button listen,listDevices;
    ListView listView;
    TextView status;
    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice[] btArray;
    SendReceive sendReceive;
    static final int STATE_LISTENING=1;
    static final int STATE_CONNECTING=2;
    static final int STATE_CONNECTED=3;
    static final int STATE_CONNECTION_FAILED=4;
    static final int STATE_MESSAGE_RECEIVED=5;
    int REQUEST_ENABLE_BLUETOOTH=1;
    static final String APP_NAME="MY_APP";
    static final UUID MY_UUID=UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    BroadcastReceiver mRegistrationBroadcastReceiver;



    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        listen=findViewById(R.id.btnLISTEN);

        //imageView=findViewById(R.id.image_capture);
        listDevices=findViewById(R.id.btnLIST);
        status=findViewById(R.id.tvSTATUS);
        //msg_box=findViewById(R.id.tvMESSAGE);
        listView=findViewById(R.id.lv1);


        imageView=findViewById(R.id.photo);



        bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        if(!bluetoothAdapter.isEnabled()){
            Intent enableIntent=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent,REQUEST_ENABLE_BLUETOOTH);
        }

        listDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Set<BluetoothDevice> bt=bluetoothAdapter.getBondedDevices();
                String[] strings=new String[bt.size()];
                btArray=new BluetoothDevice[bt.size()];
                int index=0;
                if(bt.size()>0){
                    for(BluetoothDevice device:bt){
                        btArray[index]=device;
                        strings[index]=device.getName();
                        index++;
                    }
                    ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,strings);
                    listView.setAdapter(arrayAdapter);
                }
            }
        });
//        listen.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ServerClass serverClass=new ServerClass();
//                serverClass.start();
//            }
//        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ClientClass clientClass=new ClientClass(btArray[position]);
                clientClass.start();
                status.setText("Connecting");
            }
        });





        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {

            Bundle extras = intent.getExtras();
            String url= extras.getString("url");

            //Toast.makeText(this,url,Toast.LENGTH_LONG).show();
            Intent in=new Intent(MainActivity.this,UserActivity.class);
            in.putExtra("msg",url);
            startActivity(in);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==10 && resultCode== Activity.RESULT_OK){


           // imageView.setImageBitmap((Bitmap) data.getExtras().get("data"));
        }
    }

    Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case STATE_LISTENING: status.setText("Listening");break;
                case STATE_CONNECTING: status.setText("Connecting");break;
                case STATE_CONNECTED: status.setText("connected");break;
                case STATE_CONNECTION_FAILED: status.setText("Connection Failed");break;
                case STATE_MESSAGE_RECEIVED:
                    byte[] readBuffer= (byte[]) msg.obj;
                    String tempMsg=new String(readBuffer,0,msg.arg1);
                    //if(tempMsg.equals("message")){

                      //msg_box.setText(tempMsg);
                     Intent intent =new Intent(MainActivity.this,Camera2Activity.class);
                     //intent.putExtra("android.intent.extra.quickCapture",true);

                     startActivityForResult(intent,10);
                     finish();



                   // }

                    break;
            }
            return true;
        }
    });

    private class ClientClass extends Thread{
        private BluetoothDevice device;
        private BluetoothSocket socket;

        public ClientClass(BluetoothDevice device1) {
            this.device = device1;

            try {
                socket=device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                socket.connect();
                Message message=Message.obtain();
                message.what=STATE_CONNECTED;
                handler.sendMessage(message);
                SendReceive sendReceive=new SendReceive(socket);
                sendReceive.start();
            } catch (IOException e) {
                e.printStackTrace();
                Message message=Message.obtain();
                message.what=STATE_CONNECTION_FAILED;
                handler.sendMessage(message);
            }
        }
    }

    private class SendReceive extends Thread{
        private final BluetoothSocket bluetoothSocket;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        public SendReceive(BluetoothSocket bluetoothSocket) {
            this.bluetoothSocket = bluetoothSocket;
            InputStream tempIn=null;
            OutputStream tempOut=null;

            try {
                tempIn=bluetoothSocket.getInputStream();
                tempOut=bluetoothSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            inputStream=tempIn;
            outputStream=tempOut;
        }

        @Override
        public void run() {
            byte[] buffer=new byte[1024];
            int bytes;
            while (true) {
                try {
                    bytes=inputStream.read(buffer);
                    handler.obtainMessage(STATE_MESSAGE_RECEIVED,bytes,-1,buffer).sendToTarget();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }


}


