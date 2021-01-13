package km.abdere.fahad.projetarduino;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class Main5Activity extends AppCompatActivity {

    private ImageView imageView;
    private  String url;
    FirebaseStorage storage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main5);


        imageView=findViewById(R.id.image);


        url=getIntent().getStringExtra("image");

        Toast.makeText(this,url,Toast.LENGTH_LONG).show();
        imageView.setImageURI(Uri.parse(url));
        uploadPhotoInFirebaseAndSendMessage(Uri.parse("file://"+url));


    }

    private void uploadPhotoInFirebaseAndSendMessage( Uri urlphoto) {
        String uuid = UUID.randomUUID().toString(); // GENERATE UNIQUE STRING
        // A - UPLOAD TO GCS
        StorageReference mImageRef = FirebaseStorage.getInstance().getReference(uuid);
        mImageRef.putFile(urlphoto)
                .addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        String pathSavedInFirebase=taskSnapshot.getMetadata().getPath();
                        Toast.makeText(getApplicationContext(), "Upload Effecture", Toast.LENGTH_LONG).show();

                        //String pathImageSavedInFirebase = taskSnapshot.getMetadata().getDownloadUrl().toString();
                        // B - SAVE MESSAGE IN FIRESTORE
                       // MessageHelper.createMessageWithImageForChat(pathImageSavedInFirebase, message, currentChatName, modelCurrentUser).addOnFailureListener(onFailureListener());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();

                        Log.e("firebase",e.getCause().toString());
                    }
                });
    }
}
