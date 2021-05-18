package com.example.mysosapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.database.Cursor;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class EditProfile extends AppCompatActivity {

    public static final String TAG = "TAG";

    ImageView profileImage;
    EditText profileFullName, profileEmail, profileHelpMessage, profileContact1, profileContact2, profileContact3;
    Button profileSave;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseUser user;
    StorageReference storageReference;
    static final int RESULT_PICK_CONTACT = 1;
    static final int RESULT_PICK_CONTACT2 = 2;
    static final int RESULT_PICK_CONTACT3 = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Intent data = getIntent();
        String fullName = data.getStringExtra("fullName");
        String email = data.getStringExtra("email");
        String helpMessage = data.getStringExtra("helpMessage");
        String contact1 = data.getStringExtra("contact1");
        String contact2 = data.getStringExtra("contact2");
        String contact3 = data.getStringExtra("contact3");

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        user = fAuth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();


        profileFullName = findViewById(R.id.profileFullName);
        profileEmail = findViewById(R.id.profileEmail);
        profileHelpMessage = findViewById(R.id.profileHelpMessage);
        profileContact1 = findViewById(R.id.profileContact1);
        profileContact2 = findViewById(R.id.profileContact2);
        profileContact3 = findViewById(R.id.profileContact3);
        profileImage = findViewById(R.id.profileImage);
        profileSave = findViewById(R.id.profileSave);


        profileContact1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(in, RESULT_PICK_CONTACT);
            }
        });

        profileContact2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(in, RESULT_PICK_CONTACT2);
            }
        });

        profileContact3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(in, RESULT_PICK_CONTACT3);
            }
        });


        StorageReference profileRef = storageReference.child("users/" + fAuth.getCurrentUser().getUid() + "profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImage);
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent, 1000);
            }
        });

        profileSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (profileFullName.getText().toString().isEmpty() ||
                        profileEmail.getText().toString().isEmpty() ||
                        profileHelpMessage.getText().toString().isEmpty() ||
                        profileContact1.getText().toString().isEmpty() ||
                        profileContact2.getText().toString().isEmpty() ||
                        profileContact3.getText().toString().isEmpty()) {
                    Toast.makeText(EditProfile.this, "One or Many fields are empty.", Toast.LENGTH_SHORT).show();
                    return;
                }

                String email = profileEmail.getText().toString();
                user.updateEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {


                        DocumentReference docRef = fStore.collection("users").document(user.getUid());
                        Map<String, Object> edited = new HashMap<>();
                        edited.put("fEmail", email);
                        edited.put("fName", profileFullName.getText().toString());
                        edited.put("fHelp", profileHelpMessage.getText().toString());
                        edited.put("fContact1", profileContact1.getText().toString());
                        edited.put("fContact2", profileContact2.getText().toString());
                        edited.put("fContact3", profileContact3.getText().toString());


                        docRef.update(edited).addOnSuccessListener(new OnSuccessListener<Void>() {

                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(EditProfile.this, "Saved", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(EditProfile.this, "Saving Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditProfile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }



        });

        profileFullName.setText(fullName);
        profileEmail.setText(email);
        profileHelpMessage.setText(helpMessage);
        profileContact1.setText(contact1);
        profileContact2.setText(contact2);
        profileContact3.setText(contact3);

        Log.d(TAG, "onCreate:" + fullName);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {
                Uri imageUri = data.getData();
                uploadImageToFirebase(imageUri);
            }
        }
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RESULT_PICK_CONTACT:
                    contactPicked(data);
                    break;
            }
        }
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RESULT_PICK_CONTACT2:
                    contactPicked2(data);
                    break;
            }
        }
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RESULT_PICK_CONTACT3:
                    contactPicked3(data);
                    break;
            }
        }

    }

    private void contactPicked(Intent data) {
        Cursor cursor = null;

        try {
            String phoneNo = null;
            Uri uri = data.getData();
            cursor = getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
            int phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            phoneNo = cursor.getString(phoneIndex);
            profileContact1.setText(phoneNo);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void contactPicked2(Intent data2) {
        Cursor cursor2 = null;

        try {
            String phoneNo2 = null;
            Uri uri2 = data2.getData();
            cursor2 = getContentResolver().query(uri2, null, null, null, null);
           cursor2.moveToFirst();
            int phoneIndex2 = cursor2.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            phoneNo2 = cursor2.getString(phoneIndex2);
            profileContact2.setText(phoneNo2);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void contactPicked3(Intent data3) {
        Cursor cursor3 = null;

        try {
            String phoneNo3 = null;
            Uri uri3 = data3.getData();
            cursor3 = getContentResolver().query(uri3, null, null, null, null);
            cursor3.moveToFirst();
            int phoneIndex3 = cursor3.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            phoneNo3 = cursor3.getString(phoneIndex3);
            profileContact3.setText(phoneNo3);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void uploadImageToFirebase(Uri imageUri) {

        StorageReference fileRef = storageReference.child("users/" + fAuth.getCurrentUser().getUid() + "profile.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(profileImage);
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditProfile.this, "Upload Failed.", Toast.LENGTH_SHORT).show();
            }
        });
    }




}