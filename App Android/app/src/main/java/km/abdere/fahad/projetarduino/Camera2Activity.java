package km.abdere.fahad.projetarduino;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


public class Camera2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera2);

        if (null == savedInstanceState) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, Camera2BasicFragment.newInstance())
                    .commit();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();



    }
}
