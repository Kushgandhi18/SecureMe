package friendlyitsolution.com.ms.vsecure;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.rengwuxian.materialedittext.MaterialEditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    String mVerificationId;
    private static final String TAG = "PhoneAuthActivity";
    MaterialEditText phoneno;
    ProgressDialog pd;
    Button btn;
    TextView btnsignup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_main);
        phoneno=(MaterialEditText)findViewById(R.id.etUser);
        pd=new ProgressDialog(MainActivity.this);
        pd.setCancelable(false);
        pd.setMessage("please wait");
        btn=(Button)findViewById(R.id.btnLogin);
        btnsignup=(TextView)findViewById(R.id.btnsignup);

        btnsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!phoneno.getText().toString().equals("")&&phoneno.getText().toString().length()==10)
                {

                    SharedPreferences.Editor edit=Myapp.pref.edit();
                    edit.putString("mynumber",phoneno.getText().toString());
                    edit.commit();
                    Myapp.mynumber=phoneno.getText().toString();
                    Intent i=new Intent(MainActivity.this,userinfo.class);
                    startActivity(i);
                    finish();

                }
                else
                {
                    phoneno.setError("please enter mobile no");
                }
            }
        });

        if(!Myapp.data.equals(""))
        {
            Intent i=new Intent(MainActivity.this,Home.class);
            startActivity(i);
            finish();
        }
        else  if(!Myapp.mynumber.equals(""))
        {
            Intent i=new Intent(MainActivity.this,userinfo.class);
            startActivity(i);
            finish();
        }


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!phoneno.getText().toString().equals("")&&phoneno.getText().toString().length()==10)
                {
                     pd.show();
                     startPhoneNumberVerification(phoneno.getText().toString());
                }
                else
                {
                    phoneno.setError("please enter mobile no");
                }
            }
        });
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                Log.d(TAG, "onVerificationCompleted:" + credential);
                pd.dismiss();
                Toast.makeText(getApplicationContext(),"Varified", Toast.LENGTH_SHORT).show();

                SharedPreferences.Editor edit=Myapp.pref.edit();
                edit.putString("mynumber",phoneno.getText().toString());
                edit.commit();
                Myapp.mynumber=phoneno.getText().toString();
                Intent i=new Intent(MainActivity.this,userinfo.class);
                startActivity(i);
                finish();
                // signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.w(TAG, "onVerificationFailed", e);
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    pd.dismiss();
                    phoneno.setError("Invalid phone number");
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    pd.dismiss();
                    Snackbar.make(findViewById(android.R.id.content), "Quota exceeded.",
                            Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {

                pd.setMessage("code sucessfully send");
            }
        };


checkWriteExternalPermission();
    }


    void checkWriteExternalPermission()
    {
        String permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        int res = getApplicationContext().checkCallingOrSelfPermission(permission);

        String permission1 = Manifest.permission.CAMERA;
        int res1 = getApplicationContext().checkCallingOrSelfPermission(permission1);


        if(res != PackageManager.PERMISSION_GRANTED || res1 != PackageManager.PERMISSION_GRANTED)
        {

            String[] permissions=new String[]{permission,permission1,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.CALL_PHONE,Manifest.permission.SEND_SMS};
            requestPermissions(permissions,101);


        }
    }


    private void startPhoneNumberVerification(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91"+phoneNumber,        // Phone number to verify
                120,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }



}
