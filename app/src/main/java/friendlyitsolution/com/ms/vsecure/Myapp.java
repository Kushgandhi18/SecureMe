package friendlyitsolution.com.ms.vsecure;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;



public class Myapp extends Application
{
    public static String serverkey="AAAAUfuia7s:APA91bHK_bw75rXczxHOS7bjp6C_XutdleE1wGgoRGySX4HwO94zTrdizZ6xHCzJZecjefvj3jJvtDDuehxFB2vz1OjBE0itb_gbFmmwfET8rFxABhIQB3oc4ZrXt7K1qXPKdp4zz69A" ;
    public static String senderid="352114076603";

    static String sender="";
    static String key="";

    public static FirebaseDatabase db;
    public static DatabaseReference ref;
    public static SharedPreferences pref;
    public static String frdnumber,frdname;
    public static String mynumber,data;
    public static DatabaseReference myref;
    public static Map<String, Object> userdata;

    private static final Object TAG = "ss";
    public static Context con;


    private static Myapp sInstance;
    private RequestQueue mRequestQueue;

    @Override
    public void onCreate() {
        super.onCreate();
        db= FirebaseDatabase.getInstance();
        db.setPersistenceEnabled(true);
        try{
            ref=db.getReferenceFromUrl("https://v-5fb6d-default-rtdb.asia-southeast1.firebasedatabase.app/");
        }
        catch (Exception e){
            Log.d("exception", e.toString());
        }
        sInstance=this;
        con=getApplicationContext();
        pref=getSharedPreferences("myinfo",MODE_PRIVATE);
        data=pref.getString("data","");
       mynumber=pref.getString("mynumber","");
       if(!data.equals(""))
       {
           getUserdata();
       }

    }

    public static void sendNotificationTo1(final String to,final String title,final String msg) {


        class GetImage extends AsyncTask<String,Void,String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected void onPostExecute(String st) {
                super.onPostExecute(st);

                //Myapp.showMsg("Msg : "+st);

            }

            @Override
            protected String doInBackground(String... params) {

                String data2="";

                if (android.os.Build.VERSION.SDK_INT > 9) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                }

                String msg1=msg.replace(" ","%20");
                String title1=title.replace(" ","%20");
                Webservices ws=new Webservices();
                ws.setUrl("https://m19.io/meet/Default.aspx?topic="+to+"&title="+title1+"&msg="+msg1+"&key="+serverkey+"&senderid="+senderid);
                //  ws.addParam("title",nn);
                // ws.addParam("topic",to);
                // ws.addParam("msg",mm);
                ws.connect();
                String dd=ws.getData();


                return dd;
            }
        }

        GetImage gi = new GetImage();
        gi.execute("");



    }



    void getUserdata()
    {
        myref=db.getReference("users").child(mynumber);
        myref.keepSynced(true);
        myref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                userdata=(Map<String, Object>)dataSnapshot.getValue();
             //   Toast.makeText(getApplicationContext(),"Data : "+userdata,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
