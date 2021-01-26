package friendlyitsolution.com.ms.vsecure;

import androidx.fragment.app.FragmentActivity;

import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.polyak.iconswitch.IconSwitch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {


    private GoogleMap mMap;
    TextView name,fromd,tod,locn;
    IconSwitch iw;
    DatabaseReference currloc,changedloc;
    ChildEventListener chd;
    PolylineOptions lines=new PolylineOptions();
    ArrayList<LatLng> points=new ArrayList<>();
    Map<String,Object> alllatlong=new HashMap<>();
    Polyline p;
    Marker m=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        locn=(TextView)findViewById(R.id.location);
        name=(TextView)findViewById(R.id.name);
        fromd=(TextView)findViewById(R.id.fromd);
        tod=(TextView)findViewById(R.id.tod);
        iw=(IconSwitch)findViewById(R.id.icon_switch);
        name.setText(Myapp.frdname);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        changedloc=Myapp.ref.child("users").child(Myapp.frdnumber).child("location");

        iw.setCheckedChangeListener(new IconSwitch.CheckedChangeListener() {
            @Override
            public void onCheckChanged(IconSwitch.Checked current) {

                String ss=""+current;
                if(ss.equals("RIGHT"))
                {
                    getAllLocation();
                }
                else
                {
                    getCurrentLocation();
                }
            }
        });

        initChd();

    }
    void initChd()
    {

        chd=new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                if(dataSnapshot.getKey().equals("start"))
                {
                    fromd.setText(dataSnapshot.getValue().toString());
                }
                else if(dataSnapshot.getKey().equals("end"))
                {
                    tod.setText(dataSnapshot.getValue().toString());
                }
                else
                {
                    try {
                        Map<String, String> latlon = (Map<String, String>) dataSnapshot.getValue();

                        alllatlong.put(dataSnapshot.getKey(),latlon);

                        double lati= Double.parseDouble(latlon.get("lati"));
                        double longi= Double.parseDouble(latlon.get("longi"));
                        Geocoder code = new Geocoder(getApplicationContext(), Locale.ENGLISH);
                        List<Address> list = code.getFromLocation(lati, longi, 10);
                        Address add;
                        String myadd[];
                        add = list.get(0);
                        myadd = new String[add.getMaxAddressLineIndex()];

                        String fulladd="";
                        String postalcode = add.getPostalCode();
                        String city = add.getLocality();
                        String area = add.getSubLocality();
                        String adds = add.getThoroughfare() + " , " + area + " , " + postalcode;
                        fulladd = fulladd + adds ;
                        fulladd = fulladd + city + " , " + add.getAdminArea();

                        locn.setText(fulladd);
                        LatLng cu=new LatLng(lati,longi);
                        if(m!=null) {
                            m.remove();
                            m = mMap.addMarker(new MarkerOptions().position(cu).title(fulladd));
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(cu));
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lati, longi), 15));
                        }
                        else
                        {
                            m = mMap.addMarker(new MarkerOptions().position(cu).title(fulladd));
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(cu));
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lati, longi), 15));

                        }
                    }
                    catch(Exception e)
                    {
                        Toast.makeText(getApplicationContext(),"Unknown place", Toast.LENGTH_LONG).show();
                    }
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.getKey().equals("start"))
                {
                    fromd.setText(dataSnapshot.getValue().toString());
                }
                else if(dataSnapshot.getKey().equals("end"))
                {
                    tod.setText(dataSnapshot.getValue().toString());
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        changedloc.addChildEventListener(chd);

    }

    void getCurrentLocation()
    {
        p.remove();

    }
    void getAllLocation()
    {

        List<String> allk=new ArrayList<>(alllatlong.keySet());
        for(int i=0;i<allk.size();i++)
        {
            Map<String,String> all=(Map<String, String>)alllatlong.get(allk.get(i));
            double lati= Double.parseDouble(all.get("lati"));
            double longi= Double.parseDouble(all.get("longi"));
            LatLng sydney = new LatLng(lati, longi);
            points.add(sydney);
        }
        lines.addAll(points);
        lines.width(10);
        lines.color(Color.RED);
        p= mMap.addPolyline(lines);
    }

    @Override
    public void onBackPressed() {
        changedloc.removeEventListener(chd);
        super.onBackPressed();

    }
}
