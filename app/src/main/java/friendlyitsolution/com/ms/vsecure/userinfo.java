package friendlyitsolution.com.ms.vsecure;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class userinfo extends AppCompatActivity {
    MaterialEditText fname,lname,num1,num2,num3;
    Button btn;

    //FirebaseDatabase database = FirebaseDatabase.getInstance();
    //DatabaseReference myRef = database.getReferenceFromUrl("https://v-5fb6d-default-rtdb.asia-southeast1.firebasedatabase.app/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);

        fname=(MaterialEditText)findViewById(R.id.etfirstname);
        lname=(MaterialEditText)findViewById(R.id.etlastname);
        num1=(MaterialEditText)findViewById(R.id.num1);
        num2=(MaterialEditText)findViewById(R.id.num2);
        num3=(MaterialEditText)findViewById(R.id.num3);
        btn=(Button)findViewById(R.id.reg);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                register();

            }
        });

        if(Myapp.userdata!=null)
        {
            btn.setText("Update Profile");
            num1.setText(Myapp.userdata.get("num1").toString());
            num2.setText(Myapp.userdata.get("num2").toString());
            num3.setText(Myapp.userdata.get("num3").toString());

            String st[]=Myapp.userdata.get("name").toString().split(" ");
            fname.setText(st[0]);
            try{
                lname.setText(st[1]);
            }
            catch(Exception e)
            {

            }

        }
    }

    void addData()
    {
        final ProgressDialog pd=new ProgressDialog(userinfo.this);
        pd.setCancelable(false);
        pd.setMessage("please wait");
        pd.show();
        HashMap<String,Object> userdata=new HashMap<>();
        userdata.put("name",fname.getText().toString()+" "+lname.getText().toString());
        userdata.put("num1",num1.getText().toString());
        userdata.put("num2",num2.getText().toString());
        userdata.put("num3",num3.getText().toString());

        // Map<String,String> lastloc=new HashMap<>();
        // lastloc.put("lati","0");
        // lastloc.put("longi","0");
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        // lastloc.put("time",formattedDate);
        // userdata.put("lastlocation",lastloc);

        Log.d("correct", "correct");
        //Log.d("mynumber", Myapp.mynumber + " ");



        Myapp.ref.child("users").child(Myapp.mynumber).setValue(userdata).addOnCompleteListener(new OnCompleteListener<Void>() {

            @Override
            public void onComplete(@NonNull Task<Void> task) {

                Log.d("onComplete", "onComplete");

                pd.dismiss();
                SharedPreferences.Editor edit=Myapp.pref.edit();
                edit.putString("data","yes");
                edit.commit();

                Intent i=new Intent(getApplicationContext(),Home.class);
                startActivity(i);
                finish();

            }
        });


    }

    void register()
    {
        boolean isok=true;
        if(fname.getText().toString().isEmpty())
        {
            fname.setError("Enter input");
            isok=false;
        }
        else if(lname.getText().toString().isEmpty())
        {
            isok=false;
            lname.setError("Enter input");
        }
        else if(num1.getText().toString().isEmpty())
        {  isok=false;
            num1.setError("Enter input");
        }
        else if(num2.getText().toString().isEmpty())
        {  isok=false;
            num2.setError("Enter input");
        }
        else if(num3.getText().toString().isEmpty())
        {  isok=false;
            num3.setError("Enter input");
        }

        if(isok)
        {
            addData();
        }
    }
}
