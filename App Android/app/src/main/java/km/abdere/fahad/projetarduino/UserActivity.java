
package km.abdere.fahad.projetarduino;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutionException;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

public class UserActivity extends AppCompatActivity {

    private static final String TAG = UserActivity.class.getSimpleName();
    BroadcastReceiver mRegistrationBroadcastReceiver;
    private String url;


    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        imageView=findViewById(R.id.photo);


        url=getIntent().getStringExtra("msg");
        //Toast.makeText(UserActivity.this,url,Toast.LENGTH_LONG).show();

        Glide.with(UserActivity.this) //SHOWING PREVIEW OF IMAGE
                .load(url)
                .into(imageView);




    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save,menu);

        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.save:
                Toast.makeText(UserActivity.this,"Photo Sauvegarder",Toast.LENGTH_LONG).show();

                File image =CameraActivity.getOutputMediaFile(MEDIA_TYPE_IMAGE);

                ImageSaveTask saveTask=new ImageSaveTask(getApplicationContext());

                saveTask.execute(url,image.getAbsolutePath());

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }



    public class ImageSaveTask extends AsyncTask<String, Void, Void> {
        private Context context;

        public ImageSaveTask(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(String... params) {
            if (params == null || params.length < 2) {
                throw new IllegalArgumentException("You should offer 2 params, the first for the image source url, and the other for the destination file save path");
            }

            String src = params[0];
            String dst = params[1];

            try {
                File file = Glide.with(context)
                        .load(src)
                        .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                        .get();

                File dstFile = new File(dst);
                if (!dstFile.exists()) {
                    boolean success = dstFile.createNewFile();
                    if (!success) {
                        return null;
                    }
                }

                InputStream in = null;
                OutputStream out = null;

                try {
                    in = new BufferedInputStream(new FileInputStream(file));
                    out = new BufferedOutputStream(new FileOutputStream(dst));

                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    out.flush();
                } finally {
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }

                    if (out != null) {
                        try {
                            out.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            } catch (InterruptedException | ExecutionException | IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }



}



