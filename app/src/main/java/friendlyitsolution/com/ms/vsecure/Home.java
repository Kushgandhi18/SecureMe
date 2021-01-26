package friendlyitsolution.com.ms.vsecure;

import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Home extends AppCompatActivity {
    RelativeLayout track, familyhelp,tracku,complet;
    TextView name, loc;
    String fulladd = "";
    double lati, longi;
    ImageView img;

    private FusedLocationProviderClient mFusedLocationClient;

    Button btnedit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        track = (RelativeLayout) findViewById(R.id.trac);
        tracku=(RelativeLayout)findViewById(R.id.tracyou);

        btnedit=findViewById(R.id.editp);

        btnedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(Home.this,userinfo.class);
                startActivity(i);
            }
        });
        img=(ImageView)findViewById(R.id.img);
        familyhelp = (RelativeLayout) findViewById(R.id.but2);
        name = (TextView) findViewById(R.id.name);
        loc = (TextView) findViewById(R.id.location);
        complet=(RelativeLayout)findViewById(R.id.but3);

        getCurrLocation();
        try {
            name.setText(Myapp.userdata.get("name").toString());
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Please reoepn app", Toast.LENGTH_SHORT).show();
        }
        familyhelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMsgs();
            }
        });

        tracku.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                trackYouDiloug();
            }
        });

        track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isMyServiceRunning(MyLocationService.class)) {
                    img.setColorFilter(ContextCompat.getColor(Myapp.con, R.color.md_green_400), android.graphics.PorterDuff.Mode.SRC_IN);
                    startService(new Intent(Myapp.con, MyLocationService.class));
                    Toast.makeText(Myapp.con, "Start Track me", Toast.LENGTH_LONG).show();

                } else {
                    img.setColorFilter(ContextCompat.getColor(Myapp.con, R.color.md_red_400), android.graphics.PorterDuff.Mode.SRC_IN);

                    stopService(new Intent(Myapp.con, MyLocationService.class));
                    Toast.makeText(Myapp.con, "Stop Track me", Toast.LENGTH_LONG).show();

                }
            }
        });

        complet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent i=new Intent(getApplicationContext(),anonyscreen.class);
                startActivity(i);

            }
        });


    }



    void trackYouDiloug()
    {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.diloug_track);
        dialog.show();
        final ProgressBar pb=(ProgressBar)dialog.findViewById(R.id.pg);
        final EditText et=(EditText)dialog.findViewById(R.id.num);
        final Button btnInDialog = (Button) dialog.findViewById(R.id.btn);
        btnInDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et.getText().toString().equals(""))
                {
                    et.setError("Enter number");
                }
                else
                {
                    btnInDialog.setVisibility(View.GONE);
                    pb.setVisibility(View.VISIBLE);
                    et.setEnabled(false);
                    final DatabaseReference rrf=Myapp.ref.child("users").child(et.getText().toString());
                    rrf.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            rrf.removeEventListener(this);

                            if(dataSnapshot.getValue()==null)
                            {
                                et.setEnabled(true);
                                pb.setVisibility(View.GONE);
                                btnInDialog.setVisibility(View.VISIBLE);
                                et.setError("Enter Valid Number");
                            }
                            else
                            {
                                dialog.dismiss();
                                Myapp.frdnumber=et.getText().toString();
                                Map<String,Object> ssd=(Map<String, Object>)dataSnapshot.getValue();
                                Myapp.frdname=ssd.get("name")+"";
                                startActivity(new Intent(getApplicationContext(),MapsActivity.class));


                            }


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            rrf.removeEventListener(this);

                        }
                    });

                }


            }
        });


        final ImageView btnClose = (ImageView) dialog.findViewById(R.id.canclebtn);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });






    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
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
                                Calendar c = Calendar.getInstance();
                                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
                                String formattedDate = df.format(c.getTime());
                                lastloc.put("time",formattedDate);
                                Myapp.myref.child("lastlocation").setValue(lastloc);
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
                                String adds = add.getThoroughfare() + " , " + area + " , " + postalcode;
                                fulladd = fulladd + adds + "\n";
                                fulladd = fulladd + city + " , " + add.getAdminArea();

                                loc.setText(fulladd);
                                // Toast.makeText(getApplicationContext(), "geting :"+fulladd, Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), "Error 1 :" + e.getMessage(), Toast.LENGTH_LONG).show();

                            }
                        }
                    }
                });
    }

    void sendMsgs()
    {
        String num1=Myapp.userdata.get("num1").toString();
        String num2=Myapp.userdata.get("num2").toString();
        String num3=Myapp.userdata.get("num3").toString();
        sendSms(num1,getMsg());
        sendSms(num2,getMsg());
        sendSms(num3,getMsg());

    }

    String getMsg()
    {
        String urls="http://maps.google.com/maps?daddr="+lati+","+longi;

        String finalmsg="I need help my location is\n"+urls;

        return finalmsg;
    }


    void sendSms(String number, String msg)
    {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(number, null, msg, null, null);
            Toast.makeText(getApplicationContext(),"send", Toast.LENGTH_LONG).show();

        }
        catch(Exception e)
        {
            Toast.makeText(getApplicationContext(),"Try again", Toast.LENGTH_LONG).show();
        }
    }

}
