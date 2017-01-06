package com.dagger.globalinfo.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
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
import com.dagger.globalinfo.R;
import com.dagger.globalinfo.adapter.SectionsPagerAdapter;
import com.dagger.globalinfo.model.InfoObject;
import com.dagger.globalinfo.service.FetchInfoService;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ui.ResultCodes;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    public static final String URL_REGEX = "^((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$";
    public static final String EDUCATION = "education";
    public static final String HACKATHONS = "hackathons";
    public static final String MEETUPS = "meetups";
    public static final String TECHNICAL = "technical";
    public static final String CONTENT = "content";
    private static final int REQUEST_INVITE = 100;
    private static final int RC_SIGN_IN = 123;
    public static FirebaseAuth auth;
    public static DatabaseReference eduDbReference, hackDbReference, meetDbReference, techDbReference, contentDbReference;
    public static ArrayList<String> admins = new ArrayList<>();
    static boolean calledPersistance = false;
    FirebaseJobDispatcher dispatcher;
    ArrayAdapter<String> arrayAdapter;
    String author;
    FirebaseDatabase firebaseDatabase;
    String category;
    String[] categories = {"Educational", "Hackathons", "Meetups", "Technical Talks"};

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.container)
    ViewPager mViewPager;
    @BindView(R.id.tabs)
    TabLayout tabLayout;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.main_content)
    CoordinatorLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        if (!calledPersistance) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            calledPersistance = true;
        }

        dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));

        addAdmins("manasbagula@gmail.com", "akashshkl01@gmail.com", "singhalsaurabh95@gmail.com");

        firebaseDatabase = FirebaseDatabase.getInstance();
        eduDbReference = firebaseDatabase.getReference().child(EDUCATION);
        hackDbReference = firebaseDatabase.getReference().child(HACKATHONS);
        meetDbReference = firebaseDatabase.getReference().child(MEETUPS);
        techDbReference = firebaseDatabase.getReference().child(TECHNICAL);
        contentDbReference = firebaseDatabase.getReference().child(CONTENT);

        // Set up the ViewPager with the sections adapter.
        mViewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout.setupWithViewPager(mViewPager);

        auth = FirebaseAuth.getInstance();
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
                Snackbar.make(root, "Sign in Cancelled", Snackbar.LENGTH_SHORT).show();
                finish();
            }

            // No network
            if (resultCode == ResultCodes.RESULT_NO_NETWORK) {
                Snackbar.make(root, "No Internet Connection", Snackbar.LENGTH_SHORT).show();
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
                Snackbar.make(root, "Failed to send invite", Snackbar.LENGTH_SHORT).show();
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
        if (id == R.id.action_share) {
            Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invite_title))
                    .setMessage(getString(R.string.invite_message))
                    .setDeepLink(Uri.parse(getString(R.string.invitation_deep_link)))
                    .setCustomImage(Uri.parse(getString(R.string.invitation_custom_image)))
                    .setCallToActionText(getString(R.string.invitation_cta))
                    .build();
            startActivityForResult(intent, REQUEST_INVITE);
            Log.e("Invite sending", "true");
            return true;
        }
        if (id == R.id.action_log_out) {
            AuthUI.getInstance().signOut(this);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        dispatcher.cancelAll();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(getClass().getSimpleName(),"onPause Called");
        final int periodicity = (int) TimeUnit.MINUTES.toSeconds(60); // Every 10 minutes periodicity
        final int toleranceInterval = (int)TimeUnit.MINUTES.toSeconds(10); // a small(ish) window of time when triggering is OK
        Log.e(getClass().getSimpleName(),"Job will execute in" + "or");
        Job myJob = dispatcher.newJobBuilder()
                .setService(FetchInfoService.class)
                .setTag("FetchInfoServiceTag")
                .setTrigger(Trigger.executionWindow(periodicity, periodicity + toleranceInterval))
//                .setTrigger(Trigger.executionWindow(60,75))
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setReplaceCurrent(true)
                .build();

        int result = dispatcher.schedule(myJob);
        if (result != FirebaseJobDispatcher.SCHEDULE_RESULT_SUCCESS) {
            Log.e(getClass().getSimpleName(),"Error executing task");
        }
    }

    public void showDialog() {
        View v = LayoutInflater.from(this).inflate(R.layout.add_dialog, null);
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, categories);
        final Spinner categorySpinner = ButterKnife.findById(v, R.id.contentSpinners);
        final EditText title = ButterKnife.findById(v, R.id.contentTitle);
        final EditText url = ButterKnife.findById(v, R.id.contentURL);
        final EditText desc = ButterKnife.findById(v, R.id.contentDesc);

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
                            Snackbar.make(root, "Please enter correct URL", Snackbar.LENGTH_SHORT).show();
                        } else if (title.getText().toString().isEmpty()) {
                            Snackbar.make(root, "Title can't be blank", Snackbar.LENGTH_SHORT).show();
                        } else {
                            Calendar c = Calendar.getInstance();

                            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                            String formattedDate = df.format(c.getTime());
                            String photoUrl = "";
                            try {
                                photoUrl = auth.getCurrentUser().getPhotoUrl().toString();
                            } catch (NullPointerException ignored) {
                            }
                            InfoObject infoObject = new InfoObject(title.getText().toString(), url.getText().toString(),
                                    desc.getText().toString(), author, category, formattedDate, auth.getCurrentUser().getEmail(), photoUrl, System.currentTimeMillis());


                            switch (category) {
                                case "Educational":
                                    pushData(eduDbReference, infoObject);
                                    break;
                                case "Hackathons":
                                    pushData(hackDbReference, infoObject);
                                    break;
                                case "Meetups":
                                    pushData(meetDbReference, infoObject);
                                    break;
                                case "Technical Talks":
                                    pushData(techDbReference, infoObject);
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

    private void pushData(final DatabaseReference reference, final InfoObject infoObject) {
        final DatabaseReference tempReference = contentDbReference.push();
        tempReference.setValue(infoObject).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //Get Key from content and push to actual db.
                String contentKey = tempReference.getKey();
                infoObject.setContentKey(contentKey);
                reference.push().setValue(infoObject);
            }
        });
    }
}
