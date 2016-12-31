package com.dagger.globalinfo.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.dagger.globalinfo.model.InfoObject;
import com.dagger.globalinfo.R;
import com.dagger.globalinfo.adapter.SectionsPagerAdapter;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ui.ResultCodes;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_INVITE = 100;
    ArrayAdapter<String> arrayAdapter;
    public static FirebaseAuth auth;
    String author;
    private static final int RC_SIGN_IN = 123;
    FirebaseDatabase firebaseDatabase;
    public static DatabaseReference eduDbReference, hackDbReference, meetDbReference, techDbReference;

    String category;
    static boolean calledPersistance = false;

    public static ArrayList<String> admins = new ArrayList<>();
    public static final String URL_REGEX = "^((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$";
    public static final String EDUCATION = "education";
    public static final String HACKATHONS = "hackathons";
    public static final String MEETUPS = "meetups";
    public static final String TECHNICAL = "technical";
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
        toolbar.setTitle("Welcome, " + author);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

    }

    public void addAdmins(String... strings) {
        int i = 0;
        while (i < strings.length)
            admins.add(strings[i++]);
    }

    public void removeAdmins(String... strings) {
        int i = 0;
        while (i < strings.length) {
            if (admins.contains(strings[i]))
                admins.remove(strings[i++]);
        }
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
        if (requestCode == REQUEST_INVITE) {
            Log.e("Result Code", String.valueOf(resultCode));
            if (resultCode == RESULT_OK) {
                // Get the invitation IDs of all sent messages
                String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
                for (String id : ids) {
                    Log.d(getClass().getSimpleName(), "onActivityResult: sent invitation " + id);
                }
            } else {
                // Sending failed or it was canceled, show failure message to the user
                // ...
                Snackbar.make(findViewById(android.R.id.content),"Failed to send invite",Snackbar.LENGTH_SHORT).show();
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

        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_share){
            Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invite_title))
                    .setMessage(getString(R.string.invite_message))
                    .setDeepLink(Uri.parse(getString(R.string.invitation_deep_link)))
                    .setCustomImage(Uri.parse(getString(R.string.invitation_custom_image)))
                    .setCallToActionText(getString(R.string.invitation_cta))
                    .build();
            startActivityForResult(intent, REQUEST_INVITE);
            Log.e("Invite sending","true");
            return true;
        }
        if (id == R.id.action_log_out){
            AuthUI.getInstance().signOut(this);
            finish();
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
                            String photoUrl = "";
                            try {
                                photoUrl = auth.getCurrentUser().getPhotoUrl().toString();
                            }catch (NullPointerException ignored){
                            }
                            InfoObject infoObject = new InfoObject(title.getText().toString(), url.getText().toString(),
                                    desc.getText().toString(), author, category, formattedDate,auth.getCurrentUser().getEmail(),photoUrl);

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
}
