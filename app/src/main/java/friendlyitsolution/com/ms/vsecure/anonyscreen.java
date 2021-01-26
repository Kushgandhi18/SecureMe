package friendlyitsolution.com.ms.vsecure;

import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import gun0912.tedbottompicker.TedBottomPicker;

public class anonyscreen extends AppCompatActivity {
    EditText et;
    ImageView iv;
    Button btn;
    TextView tv;
    double lati=0,longi=0;
    String fulladd=" ";
    ProgressDialog pd;
    private FusedLocationProviderClient mFusedLocationClient;
    Uri imguri=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anonyscreen);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        pd=new ProgressDialog(anonyscreen.this);

        pd.setMessage("please wait");
        pd.setCancelable(false);
        tv=(TextView)findViewById(R.id.location);
        btn=(Button)findViewById(R.id.btn);
        iv=(ImageView)findViewById(R.id.img);
        et=(EditText)findViewById(R.id.etdes);


        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TedBottomPicker tedBottomPicker = new TedBottomPicker.Builder(anonyscreen.this)
                        .setOnImageSelectedListener(new TedBottomPicker.OnImageSelectedListener() {
                            @Override
                            public void onImageSelected(Uri uri) {

                                Toast.makeText(getApplicationContext(),"get : "+uri, Toast.LENGTH_LONG).show();
                                if(uri!=null)
                                {
                                    imguri=uri;
                                    iv.setImageURI(uri);
                                }
                            }
                        })
                        .create();

                tedBottomPicker.show(getSupportFragmentManager());

            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(et.getText().toString().equals(""))
                {
                    et.setError("Please enter something");
                }
                else
                {
                    uploadAnonymous();
                }

            }
        });

        getCurrLocation();
    }


    void uploadAnonymous()
    {
        pd.show();

        if(imguri!=null)
        {
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            sendImage(imguri);

        }
        else
        {
            Map<String,String> data=new HashMap<>();
            data.put("url"," ");
            data.put("uid",Myapp.mynumber);
            data.put("des",et.getText().toString());
            data.put("longi",longi+"");
            data.put("lati",""+lati);
            data.put("address",""+fulladd);
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
            String formattedDate = df.format(c.getTime());
            data.put("time",formattedDate);
            Myapp.ref.child("complaint").push().setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    pd.dismiss();
                    btn.setText("Submited successfully");
                    btn.setEnabled(false);

                    Myapp.sendNotificationTo1("admin",fulladd,et.getText().toString());
                    Toast.makeText(getApplicationContext(),"Successfully sent", Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    pd.dismiss();
                    Toast.makeText(getApplicationContext(),"Try again later", Toast.LENGTH_LONG).show();
                }
            });

        }

    }







    private void sendImage(final Uri uri) {
        //if there is a file to upload
        if (uri != null) {

            //  bnp.setVisibility(View.VISIBLE);
            final String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            StorageReference mStorageRef=   mStorageRef = FirebaseStorage.getInstance().getReference();
           final StorageReference riversRef = mStorageRef.child("img/"+Myapp.mynumber+"/IMG_"+timeStamp+".jpg");

            riversRef.putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                            riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    final String downloadUri = uri+"";


                                    pd.setMessage("almost done...");
                                   
                                    Map<String,String> data=new HashMap<>();
                                    data.put("url",downloadUri+"");
                                    data.put("uid",Myapp.mynumber);
                                    data.put("des",et.getText().toString());
                                    data.put("longi",longi+"");
                                    data.put("lati",""+lati);
                                    data.put("address",""+fulladd);
                                    Calendar c = Calendar.getInstance();
                                    SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
                                    final String formattedDate = df.format(c.getTime());
                                    data.put("time",formattedDate);
                                    Myapp.ref.child("complaint").push().setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            pd.dismiss();

                                            btn.setText("Submited successfully");
                                            btn.setEnabled(false);
                                            Myapp.sendNotificationTo1("admin",fulladd,et.getText().toString());

                                            Toast.makeText(getApplicationContext(),"Successfully sent", Toast.LENGTH_LONG).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                            pd.dismiss();
                                            Toast.makeText(getApplicationContext(),"Try again later", Toast.LENGTH_LONG).show();
                                        }
                                    });





                                }});





                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {

                            pd.dismiss();
                            Toast.makeText(getApplicationContext(),"Try again", Toast.LENGTH_LONG).show();

                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            pd.setProgress(((int) progress));

                        }
                    });
        }

        else {
            Toast.makeText(getApplicationContext(),"Try again ..", Toast.LENGTH_LONG).show();

        }
    }

    void getCurrLocation() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(),"Please Start Gps", Toast.LENGTH_LONG).show();
            return;
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            try {
                                lati = location.getLatitude();
                                longi = location.getLongitude();
                                Map<String,String> lastloc=new HashMap<>();
                                lastloc.put("lati",lati+"");
                                lastloc.put("longi",longi+"");

                                Geocoder code = new Geocoder(getApplicationContext(), Locale.ENGLISH);
                                List<Address> list = code.getFromLocation(lati, longi, 10);
                                Address add;
                                String myadd[];
                                add = list.get(0);
                                myadd = new String[add.getMaxAddressLineIndex()];
                                //fulladd=fulladd+"\nlati"+lati+" longi:"+longi;


                                String postalcode = add.getPostalCode();
                                String city = add.getLocality();
                                String area = add.getSubLocality();
                                String adds = add.getThoroughfare() + " , " + area + "\n";
                                fulladd = fulladd + adds ;
                                fulladd = fulladd + postalcode+" - " + city + " , " + add.getAdminArea();

                                tv.setText(fulladd);
                                // Toast.makeText(getApplicationContext(), "geting :"+fulladd, Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), "Error 1 :" + e.getMessage(), Toast.LENGTH_LONG).show();

                            }
                        }
                    }
                });
    }

}
