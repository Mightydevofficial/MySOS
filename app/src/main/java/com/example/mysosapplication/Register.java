package com.example.mysosapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    public static final String TAG = "TAG";
    EditText mFullName, mEmail, mPassword, mHelpMessage, mContact_1, mContact_2, mContact_3;
    TextView enPass;
    Button mRegisterButton, mAlreadyRegisterButton;
    FirebaseAuth fAuth;
    ProgressBar progressBar;
    FirebaseFirestore fStore;
    String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFullName = findViewById(R.id.fullName);
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mRegisterButton = findViewById(R.id.registerbutton);
        mAlreadyRegisterButton = findViewById(R.id.alreadyregisterbutton);
        mHelpMessage = findViewById(R.id.helpMessage);
        mContact_1 = findViewById(R.id.contact_1);
        mContact_2 = findViewById(R.id.contact_2);
        mContact_3 = findViewById(R.id.contact_3);
        enPass = findViewById(R.id.enPass);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        progressBar = findViewById(R.id.progressBar);

        if (fAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        //TODO : SHA256 encrypt method,SHA-1,SHA-224,SHA-256,SHA-384,SHA-512

        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                byte[] inputData = mPassword.getText().toString().getBytes();
                byte[] outputData = new byte[0];
                try {
                    outputData = Sha256.encryptSHA(inputData,"SHA-512");
                } catch (Exception e) {

                    e.printStackTrace();
                }
                BigInteger shaData = new BigInteger(1,outputData);
                enPass.setText(shaData.toString(16));


                //computeMD5Hash(mPassword.toString());
                String fullName = mFullName.getText().toString().trim();
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString();
                String helpMessage = mHelpMessage.getText().toString().trim();
                String contact_1 = mContact_1.getText().toString().trim();
                String contact_2 = mContact_2.getText().toString().trim();
                String contact_3 = mContact_3.getText().toString().trim();
                String enPassword = enPass.getText().toString().trim();

                if (TextUtils.isEmpty(fullName)) {
                    mFullName.setError("Email is Required.");
                    return;

                }
                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Email is Required.");
                    return;

                }

                if (TextUtils.isEmpty(password)) {
                    mPassword.setError("Password is Required");
                    return;

                }

                if (password.length() < 6) {
                    mPassword.setError("Password Must be more then 6 Character");
                    return;

                }

                progressBar.setVisibility(View.VISIBLE);

                //register the user in firebase

                fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Register.this, "User Created.", Toast.LENGTH_SHORT).show();
                            userID = fAuth.getCurrentUser().getUid();                                                 //upload user name to db
                            DocumentReference documentReference = fStore.collection("users").document(userID);
                            Map<String, Object> user = new HashMap<>();
                            user.put("fName", fullName);
                            user.put("fEmail", email);
                            user.put("fPassword", enPassword);
                            user.put("fHelp", helpMessage);
                            user.put("fContact1", contact_1);
                            user.put("fContact2", contact_2);
                            user.put("fContact3", contact_3);

                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "onSuccess: User Profile Is Created For " + userID);
                                }
                            });
                            startActivity(new Intent(getApplicationContext(), Login.class));
                        } else {
                            Toast.makeText(Register.this, "Error !!!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        mAlreadyRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });


    }

   /*public void computeMD5Hash(String password){

        try{
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(password.getBytes());
            byte messageDigest[] = digest.digest();

            StringBuffer MD5Hash = new StringBuffer();
            for (int i = 0;i< messageDigest.length;i++)
            {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2 )
                    h = "0" + h;
                MD5Hash.append(h);

            }
            enPass.setText(MD5Hash);
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
    }*/

}