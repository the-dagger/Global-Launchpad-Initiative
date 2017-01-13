package com.dagger.globalinfo.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.dagger.globalinfo.GlobalInfoApplication;
import com.dagger.globalinfo.R;
import com.dagger.globalinfo.adapter.SectionsPagerAdapter;
import com.dagger.globalinfo.model.InfoObject;
import com.dagger.globalinfo.service.FetchInfoService;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ui.ResultCodes;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.dagger.globalinfo.R.id.firstName;

public class BaseActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();
    public static final String URL_REGEX = "^((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$";
    private static final int REQUEST_INVITE = 100;
    private static final int RC_SIGN_IN = 123;
    private static final int REQUEST_THEME = 345;
    private static final int IMG_RESULT = 420;
    int defaultNightMode;
    InterstitialAd interstitialAd;

    ArrayAdapter<String> arrayAdapter;
    String author;
    String category;
    String[] categories = {"Educational", "Hackathons", "Meetups", "Technical Talks"};
    SectionsPagerAdapter mSectionsPagerAdapter;
    View profileView;
    Uri updatedPhotoURI;

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
    @BindView(R.id.profileImage)
    CircleImageView profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        ButterKnife.bind(profileImage, toolbar);
        setSupportActionBar(toolbar);

        defaultNightMode = AppCompatDelegate.getDefaultNightMode();
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        interstitialAd = new InterstitialAd(getApplicationContext());
        interstitialAd.setAdUnitId(getResources().getString(R.string.banner_ad_unit_id));
        requestNewInterstitial();
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                requestNewInterstitial();
            }
        });

        // Set up the ViewPager with the sections adapter.
        mViewPager.setAdapter(mSectionsPagerAdapter);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profileView = LayoutInflater.from(BaseActivity.this).inflate(R.layout.update_profile, null);
                CircleImageView profilePic = ButterKnife.findById(profileView, R.id.userImage);
                final EditText fName = ButterKnife.findById(profileView, firstName);
                final EditText lName = ButterKnife.findById(profileView, R.id.lastName);
                final EditText email = ButterKnife.findById(profileView, R.id.email);
                ImageView upload = ButterKnife.findById(profileView, R.id.imagePicker);

                upload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, IMG_RESULT);
                    }
                });

                FirebaseUser user = GlobalInfoApplication.getAuth().getCurrentUser();
                if (user != null) {
                    updatedPhotoURI = user.getPhotoUrl();
                    String[] fullName = new String[2];
                    try {
                        fullName = user.getDisplayName().split(" ");
                    } catch (Exception e) {
                        fullName[0] = "";
                        fullName[1] = "";
                        e.printStackTrace();
                    }
                    fName.setText(fullName[0]);
                    lName.setText(fullName[1]);
                    Picasso.with(BaseActivity.this).load(user.getPhotoUrl()).placeholder(R.drawable.default_pic).error(R.drawable.default_pic).into(profilePic);
                    email.setText(user.getEmail());
                }
                MaterialDialog materialDialog = new MaterialDialog.Builder(BaseActivity.this)
                        .customView(profileView, true)
                        .positiveText("Update")
                        .title("Update Your Profile")
                        .cancelable(false)
                        .negativeText("Cancel")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                updateProfile(fName.getText().toString(),lName.getText().toString(),email.getText().toString(),updatedPhotoURI);
                            }
                        })
                        .build();
                materialDialog.show();
            }
        });

        tabLayout.setupWithViewPager(mViewPager);
        Log.d("DeviceID", getDeviceId());
        FirebaseAuth auth = GlobalInfoApplication.getAuth();
        if (auth.getCurrentUser() != null) {
            author = auth.getCurrentUser().getDisplayName();
            Picasso.with(this).load(auth.getCurrentUser().getPhotoUrl()).placeholder(R.drawable.default_pic).error(R.drawable.default_pic).into(profileImage);
        } else {
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setTheme(R.style.AppTheme_Preference)
                            .setIsSmartLockEnabled(false)
                            .setProviders(Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                    new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
                                    new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build(),
                                    new AuthUI.IdpConfig.Builder(AuthUI.TWITTER_PROVIDER).build()))
                            .build(),
                    RC_SIGN_IN);
            return;
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        interstitialAd.loadAd(adRequest);
    }

    public static String getMD5(String inputText) {
        String md5 = "";
        try {
            MessageDigest digester = MessageDigest.getInstance("MD5");
            digester.update(inputText.getBytes());
            md5 = new BigInteger(1, digester.digest()).toString(16);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return md5;
    }


    public String getDeviceId() {
        String androidID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        String deviceID = getMD5(androidID).toUpperCase();
        return deviceID;
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

        } else if (requestCode == REQUEST_INVITE) {
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
        } else if (requestCode == REQUEST_THEME) {
            if (defaultNightMode != AppCompatDelegate.getDefaultNightMode()) {
                recreate();
                defaultNightMode = AppCompatDelegate.getDefaultNightMode();
            }
        } else if (requestCode == IMG_RESULT && data!=null){
            updatedPhotoURI = data.getData();
            Log.d("URICamera", String.valueOf(updatedPhotoURI));
            ImageView updatedProfile = ButterKnife.findById(profileView,R.id.userImage);
            Picasso.with(this).load(updatedPhotoURI).placeholder(R.drawable.default_pic).error(R.drawable.default_pic).into(updatedProfile);
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
            Intent intent = new Intent(this, PreferenceActivity.class);
            startActivityForResult(intent, REQUEST_THEME);
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
            Log.d("Invite sending", "true");
            return true;
        }
        if (id == R.id.action_log_out) {
            MaterialDialog dialog = new MaterialDialog.Builder(this)
                    .title("Log out?")
                    .content("Do you want to log out?")
                    .negativeText("No")
                    .positiveText("Yes")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            AuthUI.getInstance().signOut(BaseActivity.this);
                            startActivity(new Intent(BaseActivity.this,MainActivity.class));
                            finish();
                        }
                    })
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            dialog.dismiss();
                        }
                    })
                    .build();
            dialog.show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        GlobalInfoApplication.getJobDispatcher().cancelAll();
        Log.d("AdLoadingStatus", String.valueOf(interstitialAd.isLoading()));
        Log.d("AdID", String.valueOf(interstitialAd.getAdUnitId()));
        Log.d("AdLoaded", String.valueOf(interstitialAd.isLoaded()));
        GlobalInfoApplication.incrementCount();
        if (interstitialAd.isLoaded() && GlobalInfoApplication.getCount() % 10 == 0) {
            interstitialAd.show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(getClass().getSimpleName(), "onPause Called");
        if (GlobalInfoApplication.getSharedPreferences().getInt("preferenceNotifTime", 60) != 0)
            scheduleJob(GlobalInfoApplication.getSharedPreferences().getInt("preferenceNotifTime", 60));
    }

    public void scheduleJob(int preferenceNotifTime) {
        final int periodicity = (int) TimeUnit.MINUTES.toSeconds(preferenceNotifTime); // Every given minutes periodicity
        final int toleranceInterval = (int) TimeUnit.MINUTES.toSeconds(10); // a small(ish) window of time when triggering is OK
        Log.d(getClass().getSimpleName(), "Job will execute in" + periodicity + " or " + periodicity + toleranceInterval);
        Job myJob = GlobalInfoApplication.getJobDispatcher().newJobBuilder()
                .setService(FetchInfoService.class)
                .setTag("FetchInfoServiceTag")
                .setTrigger(Trigger.executionWindow(periodicity, periodicity + toleranceInterval))
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setReplaceCurrent(true)
                .build();

        int result = GlobalInfoApplication.getJobDispatcher().schedule(myJob);
        if (result != FirebaseJobDispatcher.SCHEDULE_RESULT_SUCCESS) {
            Log.e(getClass().getSimpleName(), "Error executing task");
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
                                photoUrl = GlobalInfoApplication.getAuth().getCurrentUser().getPhotoUrl().toString();
                            } catch (NullPointerException ignored) {
                            }
                            InfoObject infoObject = new InfoObject(title.getText().toString(), url.getText().toString().trim(),
                                    desc.getText().toString(), author, category, formattedDate, GlobalInfoApplication.getAuth().getCurrentUser().getEmail(), photoUrl, System.currentTimeMillis());


                            switch (category) {
                                case "Educational":
                                    pushData(GlobalInfoApplication.getEduDbReference(), infoObject);
                                    break;
                                case "Hackathons":
                                    pushData(GlobalInfoApplication.getHackDbReference(), infoObject);
                                    break;
                                case "Meetups":
                                    pushData(GlobalInfoApplication.getMeetDbReference(), infoObject);
                                    break;
                                case "Technical Talks":
                                    pushData(GlobalInfoApplication.getTechDbReference(), infoObject);
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

    private void updateProfile(String firstName, String lastName, String email, @Nullable Uri imageUri) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(firstName + " " + lastName)
                    .setPhotoUri(imageUri)
                    .build();
            Log.d("PushURI", String.valueOf(imageUri));
            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Snackbar.make(findViewById(R.id.fab),"Profile updated, please login again to view changes",Snackbar.LENGTH_LONG).setAction("Login", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        AuthUI.getInstance().signOut(BaseActivity.this);
                                        startActivity(new Intent(BaseActivity.this,MainActivity.class));
                                        finish();
                                    }
                                }).show();
                                Log.d(TAG, "User profile updated.");
                            }
                        }
                    });
            user.updateEmail(email);
        }
    }

    private void pushData(final DatabaseReference reference, final InfoObject infoObject) {
        final DatabaseReference tempReference = GlobalInfoApplication.getContentDbReference().push();
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
