package com.dagger.globalinfo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ui.ResultCodes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ArrayAdapter<String> arrayAdapter;
    FirebaseAuth auth;
    String author;
    private static final int RC_SIGN_IN = 123;
    FirebaseDatabase firebaseDatabase;
    static DatabaseReference eduDbReference, hackDbReference, meetDbReference, techDbReference;

    String category;
    static boolean calledPersistance = false;

    public static ArrayList<String> admins = new ArrayList<>();
    public static final String URL_REGEX = "^((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$";
    private static final String EDUCATION = "education";
    private static final String HACKATHONS = "hackathons";
    private static final String MEETUPS = "meetups";
    private static final String TECHNICAL = "technical";
    String[] categories = {"Educational", "Hackathons", "Meetups", "Technical Talks"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        if (!calledPersistance) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            calledPersistance = true;
        }

        addAdmins("manasbagula@gmail.com", "akashshkl01@gmail.com");

        firebaseDatabase = FirebaseDatabase.getInstance();
        eduDbReference = firebaseDatabase.getReference().child(EDUCATION);
        hackDbReference = firebaseDatabase.getReference().child(HACKATHONS);
        meetDbReference = firebaseDatabase.getReference().child(MEETUPS);
        techDbReference = firebaseDatabase.getReference().child(TECHNICAL);

        // Set up the ViewPager with the sections adapter.
        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        auth = FirebaseAuth.getInstance();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (auth.getCurrentUser() != null) {
            if (admins.contains(auth.getCurrentUser().getEmail()))
                fab.setVisibility(View.VISIBLE);
            author = auth.getCurrentUser().getDisplayName();
        } else {
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setTheme(R.style.AppTheme)
                            .setIsSmartLockEnabled(false)
                            .setProviders(Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                    new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
                                    new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build(),
                                    new AuthUI.IdpConfig.Builder(AuthUI.TWITTER_PROVIDER).build()))
                            .build(),
                    RC_SIGN_IN);
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

    }

    public void addAdmins(String... string) {
        int i = 0;
        while (i < string.length)
            admins.add(string[i++]);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                // user is signed in!
                startActivity(new Intent(this, MainActivity.class));
                finish();
                return;
            }

            // Sign in canceled
            if (resultCode == RESULT_CANCELED) {
                Snackbar.make(findViewById(android.R.id.content), "Sign in Cancelled", Snackbar.LENGTH_SHORT).show();
                finish();
            }

            // No network
            if (resultCode == ResultCodes.RESULT_NO_NETWORK) {
                Snackbar.make(findViewById(android.R.id.content), "No Internet Connection", Snackbar.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showDialog() {
        View v = LayoutInflater.from(this).inflate(R.layout.add_dialog, null);
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, categories);
        final Spinner categorySpinner = (Spinner) v.findViewById(R.id.contentSpinners);
        final EditText title = (EditText) v.findViewById(R.id.contentTitle);
        final EditText url = (EditText) v.findViewById(R.id.contentURL);
        final EditText desc = (EditText) v.findViewById(R.id.contentDesc);
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                category = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                category = adapterView.getItemAtPosition(0).toString();
            }
        });
        categorySpinner.setAdapter(arrayAdapter);
        final Pattern p = Pattern.compile(URL_REGEX);
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title("Add a new Resource")
                .customView(v, true)
                .positiveText("Submit")
                .negativeText("Cancel")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        if (!p.matcher(url.getText()).find()) {
                            Snackbar.make(findViewById(android.R.id.content), "Please enter correct URL", Snackbar.LENGTH_SHORT).show();
                        } else if (title.getText().toString().isEmpty()) {
                            Snackbar.make(findViewById(android.R.id.content), "Title can't be blank", Snackbar.LENGTH_SHORT).show();
                        } else {
                            Calendar c = Calendar.getInstance();

                            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                            String formattedDate = df.format(c.getTime());

                            InfoObject infoObject = new InfoObject(title.getText().toString(), url.getText().toString(),
                                    desc.getText().toString(), author, category, formattedDate);

                            switch (category) {
                                case "Educational":
                                    eduDbReference.push().setValue(infoObject);
                                    break;
                                case "Hackathons":
                                    hackDbReference.push().setValue(infoObject);
                                    break;
                                case "Meetups":
                                    meetDbReference.push().setValue(infoObject);
                                    break;
                                case "Technical Talks":
                                    techDbReference.push().setValue(infoObject);
                                    break;
                                default:
                                    Log.e(getClass().getSimpleName(), "Unable to push due to category mismatch");
                            }
                        }
                    }
                })
                .build();

        dialog.show();

    }

    private static final String ARG_SECTION_NUMBER = "section_number";

    public static class PlaceholderFragment extends Fragment {

        ArrayList<InfoObject> infoObjectList = new ArrayList<>();
        InfoAdapter infoAdapter = new InfoAdapter(infoObjectList, getContext());
        SwipeRefreshLayout swipeRefreshLayout;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerViewContent);
            swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(layoutManager);
            final int position = getArguments().getInt(ARG_SECTION_NUMBER);
            Log.e("Position", String.valueOf(position));
            fetchData(position);
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

                @Override
                public void onRefresh() {
                    fetchData(position);
                }
            });
            recyclerView.setAdapter(infoAdapter);
            return rootView;
        }

        public void fetchData(int position) {
            switch (position) {
                case 0:
                    eduDbReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            infoObjectList.clear();

                            for (DataSnapshot infoDataSnapshot : dataSnapshot.getChildren()) {
                                InfoObject note = infoDataSnapshot.getValue(InfoObject.class);
                                infoObjectList.add(0, note);
                            }
                            infoAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    break;
                case 1:
                    hackDbReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            infoObjectList.clear();

                            for (DataSnapshot infoDataSnapshot : dataSnapshot.getChildren()) {
                                InfoObject note = infoDataSnapshot.getValue(InfoObject.class);
                                infoObjectList.add(0, note);
                            }
                            infoAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    break;
                case 2:
                    meetDbReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            infoObjectList.clear();

                            for (DataSnapshot infoDataSnapshot : dataSnapshot.getChildren()) {
                                InfoObject note = infoDataSnapshot.getValue(InfoObject.class);
                                infoObjectList.add(0, note);
                            }
                            infoAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    break;
                case 3:
                    techDbReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            infoObjectList.clear();

                            for (DataSnapshot infoDataSnapshot : dataSnapshot.getChildren()) {
                                InfoObject note = infoDataSnapshot.getValue(InfoObject.class);
                                infoObjectList.add(0, note);
                            }
                            infoAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    break;
            }
            if (swipeRefreshLayout.isRefreshing())
                swipeRefreshLayout.setRefreshing(false);

        }

    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, position);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "educational";
                case 1:
                    return "hackathons";
                case 2:
                    return "meetups";
                case 3:
                    return "technical talks";
            }
            return null;
        }
    }
}
