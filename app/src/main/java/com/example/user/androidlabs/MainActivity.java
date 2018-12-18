package com.example.user.androidlabs;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import com.example.user.androidlabs.database.UserProfile;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        ProfileFragment.OnFragmentInteractionListener {

    private FirebaseUser user;

    private NavController navController = null;
    private NavigationView navigationView;

    private String HomeFragmentPageNumber = "1";
    private String ProfileFragmentPageNumber = "2";
    private String RssFragmentPageNumber = "3";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null){
            startAuthActivity();
            return;
        }

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        navController = Navigation.findNavController(findViewById(R.id.nav_host_fragment));

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        setProfileEmail(user.getEmail());
        navigationView.setNavigationItemSelectedListener(this);

        DatabaseReference dbReference = FirebaseDatabase.getInstance().getReference()
                .child("userProfiles").child(user.getUid());
        dbReference.addValueEventListener(profileEventListener);
    }

    private ValueEventListener profileEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
            if (userProfile!= null){
                getFullNameTextViewFromNavView().setText(userProfile.getFullName());
                StorageReference reference = FirebaseStorage.getInstance().getReference().child(user.getUid());
                reference.getBytes(Long.MAX_VALUE)
                        .addOnSuccessListener(successImageLoadListener)
                        .addOnFailureListener(failureImageLoadListener);
            }
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Log.d("ProfileImage", databaseError.getMessage());
        }
    };

    private OnSuccessListener<byte[]> successImageLoadListener = new OnSuccessListener<byte[]>() {
        @Override
        public void onSuccess(byte[] bytes) {
            Bitmap bmp = BitmapFactory.decodeByteArray(bytes,0, bytes.length);
            getProfileImageViewFromNavView().setImageBitmap(bmp);
        }
    };

    private OnFailureListener failureImageLoadListener = new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception exception) {
            Log.d("ProfileImage", exception.getMessage());
        }
    };

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            navController.navigate(R.id.homeFragment);
        } else if (id == R.id.nav_profile) {
            navController.navigate(R.id.profileFragment);
        } else if (id == R.id.nav_rss) {
            navController.navigate(R.id.rssFragment);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void startAuthActivity(){
        startActivity(new Intent(this, AuthActivity.class));
        finish();
    }

    private void setProfileEmail(String email) {
        View headerView = navigationView.getHeaderView(0);
        TextView textView = headerView.findViewById(R.id.nav_header_profile_email);
        textView.setText(email);
    }

    private TextView getFullNameTextViewFromNavView() {
        View headerView = navigationView.getHeaderView(0);
        return headerView.findViewById(R.id.nav_header_profile_fullname);
    }

    private ImageView getProfileImageViewFromNavView() {
        View headerView = navigationView.getHeaderView(0);
        return headerView.findViewById(R.id.nav_header_profile_image);
    }

    public void cleanArticlesCache(){
        getSharedPreferences("data", Context.MODE_PRIVATE)
                .edit()
                .putString("articles", "")
                .apply();
    }
}
