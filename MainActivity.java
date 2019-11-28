package com.developer.gkweb.knowlocation.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.developer.gkweb.knowlocation.Fragment.ChatsFragment;
import com.developer.gkweb.knowlocation.Fragment.ProfileFragment;
import com.developer.gkweb.knowlocation.Fragment.UsersFragment;
import com.developer.gkweb.knowlocation.Model.Chat;
import com.developer.gkweb.knowlocation.Model.User;
import com.developer.gkweb.knowlocation.R;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    //vars:
    CircleImageView profile_image;
    TextView username;
    TabLayout tabLayout;
    ViewPager viewpager;
    Toolbar toolbar;

    //Firebase:
    FirebaseUser mFirebaseUser;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_main);

        initialize ();

        mFirebaseUser = FirebaseAuth.getInstance ().getCurrentUser ();
        reference = FirebaseDatabase.getInstance ().getReference ("User").child (mFirebaseUser.getUid ());

        reference.addValueEventListener (new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue (User.class);

                assert user != null;
                username.setText (user.getName ());

                if (user.getImageUrl ().equals ("default")){

                    profile_image.setImageResource (R.drawable.ic_boy);

                } else {

                    Glide.with (getApplicationContext ()).load (user.getImageUrl ()).into(profile_image);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        reference = FirebaseDatabase.getInstance ().getReference ("Chats");
        reference.addValueEventListener (new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter (getSupportFragmentManager ());
                int unread = 0;

                for (DataSnapshot snapshot : dataSnapshot.getChildren ()){

                    Chat chat = snapshot.getValue (Chat.class);
                    if (chat.getReceiver ().equals (mFirebaseUser.getUid ()) && !chat.isIsseen ()) {

                        unread ++ ;

                    }

                }

                if (unread == 0) {

                    viewPagerAdapter.addFragment (new ChatsFragment(), "Chats");

                } else {

                    viewPagerAdapter.addFragment (new ChatsFragment(), "("+unread+")Chats");

                }


                viewPagerAdapter.addFragment (new UsersFragment (), "Users");
                viewPagerAdapter.addFragment (new ProfileFragment (), "Profile");

                viewpager.setAdapter (viewPagerAdapter);
                tabLayout.setupWithViewPager (viewpager);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void initialize() {

        profile_image = findViewById (R.id.profile_image);
        username = findViewById (R.id.user_logged_in);
        tabLayout = findViewById (R.id.tab_layout);
        viewpager = findViewById (R.id.viewpager);
        toolbar = findViewById (R.id.toolbar);
    }

    public class ViewPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;

        public ViewPagerAdapter(@NonNull FragmentManager fm) {
            super (fm);

            this.fragments = new ArrayList<> ();
            this.titles = new ArrayList<> ();

        }

        @NonNull
        @Override
        public Fragment getItem(int position) {

            return fragments.get (position);

        }

        @Override
        public int getCount() {

            return fragments.size ();

        }

        public void addFragment(Fragment fragment, String title){

            fragments.add (fragment);
            titles.add (title);

        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get (position);
        }
    }

    private void status(String status) {

        reference = FirebaseDatabase.getInstance ().getReference ("User").child (mFirebaseUser.getUid ());

        HashMap<String, Object> hashMap = new HashMap<> ();
        hashMap.put ("status", status);

        reference.updateChildren (hashMap);

    }

    @Override
    protected void onResume() {
        super.onResume ();
        status ("online");
    }

    @Override
    protected void onPause() {
        super.onPause ();
        status ("offline");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater ().inflate (R.menu.menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId ()) {

            case R.id.logout:

                FirebaseAuth.getInstance ().signOut ();
                startActivity (new Intent (MainActivity.this, LoginActivity.class));
                finish ();
                return true;

        }

        return false;
    }
}
