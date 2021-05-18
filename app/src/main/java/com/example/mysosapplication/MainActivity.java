package com.example.mysosapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    Button LocationButton;
    EditText sosMessage, C1, C2, C3;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseUser user;
    StorageReference storageReference;
    String userId;
    TextView latitude, longitude, countryName, locality, Address;
    FusedLocationProviderClient fusedLocationProviderClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        latitude = findViewById(R.id.latitude);
        longitude = findViewById(R.id.longitude);
        countryName = findViewById(R.id.countryName);
        locality = findViewById(R.id.locality);
        Address = findViewById(R.id.Address);
        LocationButton = findViewById(R.id.LocationButton);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        LocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                    showLocation();
                else
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
            }
        });

        sosMessage = findViewById(R.id.sosMessage);
        C1 = findViewById(R.id.C1);
        C2 = findViewById(R.id.C2);
        C3 = findViewById(R.id.C3);


        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userId = fAuth.getCurrentUser().getUid();
        user = fAuth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();

        DocumentReference documentReference = fStore.collection("users").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {


                sosMessage.setText(documentSnapshot.getString("fHelp"));
                C1.setText(documentSnapshot.getString("fContact1"));
                C2.setText(documentSnapshot.getString("fContact2"));
                C3.setText(documentSnapshot.getString("fContact3"));


            }
        });


        //create hooks
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        //toolbar support


        //navigation drawer menu
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS, Manifest.permission.READ_SMS}, PackageManager.PERMISSION_GRANTED);


    }

    private void showLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if (location != null) {
                    Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                    try {
                        List<android.location.Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        latitude.setText(" .Latitude = " + addressList.get(0).getLatitude());
                        longitude.setText(" .Longitude = " + addressList.get(0).getLongitude());
                        Address.setText(" .Address = " + addressList.get(0).getAddressLine(0));
                        locality.setText(" .State = " + addressList.get(0).getLocality());
                        countryName.setText(" .Country = " + addressList.get(0).getCountryName());

                    } catch (IOException e) {
                        e.printStackTrace();

                    }
                } else {
                    Toast.makeText(MainActivity.this, "Location Error", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    public void EmergencyButton(View view) {

        String message = sosMessage.getText().toString();
        String c1 = C1.getText().toString();
        String c2 = C2.getText().toString();
        String c3 = C3.getText().toString();
        String c4 =latitude.getText().toString();
        String c5 =longitude.getText().toString();
        String c6 =Address.getText().toString();
        String c8 =countryName.getText().toString();



        try {
            SmsManager mySmsManager = SmsManager.getDefault();
            mySmsManager.sendTextMessage(c1, null,(message +  c6 ), null, null );
            mySmsManager.sendTextMessage(c2, null,(message +  c6 ) , null, null);
            mySmsManager.sendTextMessage(c3, null,(message +  c6 ), null, null);
            Toast.makeText(MainActivity.this, "Message Sent.", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "Message Failed", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {

            case R.id.nav_account:
                Intent acc = new Intent(MainActivity.this, Account.class);
                startActivity(acc);
                break;

            case R.id.nav_note:
                Intent not = new Intent(MainActivity.this, Notes.class);
                startActivity(not);
                break;

            case R.id.nav_help:
                Intent hel = new Intent(MainActivity.this, Help.class);
                startActivity(hel);
                break;

            case R.id.nav_rate:
                rateMe(this);

                break;
            case R.id.nav_share:
                shareMe(this);

                break;

            case R.id.nav_logout:
                logout(this);
                //FirebaseAuth.getInstance().signOut();
                //startActivity(new Intent(getApplicationContext(), Login.class));
                //  finish();
                //Intent log = new Intent(MainActivity.this, Login.class);
                //startActivity(log);

                break;


        }


        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void shareMe(MainActivity mainActivity) {

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        String appName = "MySOS Download Now =";
        String appLink = " com.android.chrome";
        intent.putExtra(Intent.EXTRA_TEXT, appName + appLink);
        startActivity(Intent.createChooser(intent, "Share Now"));


    }

    private void rateMe(MainActivity mainActivity) {

        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "com.android.chrome")));
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
        }


    }

    private void logout(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Logout");
        builder.setMessage("Are you sure want to logout ?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), Login.class));
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();


    }
}