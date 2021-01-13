package km.abdere.fahad.projetarduino;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class Main2Activity extends AppCompatActivity {

    private Button autoriser,refuser;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        toolbar=findViewById(R.id.toolbar);

        autoriser=findViewById(R.id.recevoir);
        refuser=findViewById(R.id.non_recevoir);

        setSupportActionBar(toolbar);


        autoriser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseMessaging.getInstance().subscribeToTopic("proprietaires")
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                String msg = "Inscription reussie";
                                if (!task.isSuccessful()) {
                                    msg = "inscription echouer";
                                }
                                Log.d("topic", msg);
                                Toast.makeText(Main2Activity.this, msg, Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });
        refuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseMessaging.getInstance().unsubscribeFromTopic("proprietaires")
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                String msg = "reussie";
                                if (!task.isSuccessful()) {
                                    msg = " echouer";
                                }
                                Log.d("topic", msg);
                                Toast.makeText(Main2Activity.this, msg, Toast.LENGTH_SHORT).show();
                            }
                        });


            }
        });

        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {

            Bundle extras = intent.getExtras();
            String url= extras.getString("url");

            //Toast.makeText(this,url,Toast.LENGTH_LONG).show();
            Intent in=new Intent(Main2Activity.this,UserActivity.class);
            in.putExtra("msg",url);
            startActivity(in);
            finish();

        }
    }


}
