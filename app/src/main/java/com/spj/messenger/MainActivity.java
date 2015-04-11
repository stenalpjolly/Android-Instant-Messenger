package com.spj.messenger;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.atomic.AtomicInteger;


public class MainActivity extends ActionBarActivity {


    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

//    private static final String SQL_URL = "http://192.168.43.19/hybrid/register.php?";
    private String SQL_URL;


    /**
     * Substitute you own sender ID here. This is the project number you got
     * from the API Console, as described in "Getting Started."
     */
    String SENDER_ID = "372785163108";

    /**
     * Tag used on log messages.
     */
    static final String TAG = "GCMDemo";

    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    SharedPreferences prefs;
    Context mContext;

    String regid, vUsername, vPhone, vEmailid, vImeiNumber, vPhoto_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SQL_URL = getResources().getString(R.string.SQL_Reg_Request);
        mContext = getApplicationContext();

        // Check device for Play Services APK. If check succeeds, proceed with
        //  GCM registration.
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            regid = getRegistrationId(mContext);

            if (regid.isEmpty()) {
                registerInBackground();
            }

        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }

        setContentView(R.layout.activity_main);
        Button btnUpdateProfile = (Button) findViewById(R.id.btnMsgUpdateProfile);
        btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean valid = true;
                EditText txtFirstName = (EditText) findViewById(R.id.txtMsgFirstName);
                EditText txtLastName = (EditText) findViewById(R.id.txtMsgLastName);
                EditText txtEmailId = (EditText) findViewById(R.id.txtMsgEmailId);

                String firstName = txtFirstName.getText().toString();
                if (firstName.equals("")) {
                    txtFirstName.setHintTextColor(Color.RED);
                    valid = false;
                }

                String lastName = txtLastName.getText().toString();
                if (lastName.equals("")) {
                    txtLastName.setHintTextColor(Color.RED);
                    valid = false;
                }
                String emailId = txtEmailId.getText().toString();
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                if (emailId.equals("") || !emailId.matches(emailPattern)) {
                    txtEmailId.setHintTextColor(Color.RED);
                    valid = false;
                }
                if (valid) {
                    SharedPreferences sharedPreferences = getSharedPreferences("hybrid_messenger", 0);
                    vUsername = firstName + "%20" + lastName;
                    vUsername = vUsername.replaceAll(" ", "%20");
                    vPhone = sharedPreferences.getString("phoneNumber", "");
                    vPhoto_url = "temp";

                    vEmailid = emailId;
                    TelephonyManager tm = (TelephonyManager)  getSystemService(Context.TELEPHONY_SERVICE);
                    vImeiNumber = tm.getDeviceId();
                    if (vImeiNumber == null) {
                        vImeiNumber = "-1";
                    }
                    Log.v("IMEI NUMBER", vImeiNumber);
                    sendRegistrationIdToBackend();
                } else
                    Toast.makeText(mContext, "Invalid Field Entry", Toast.LENGTH_LONG).show();
            }
        });

//        Commented for testing

//        SharedPreferences sharedPreferences = getSharedPreferences("messenger", 0);
//        Boolean isFirstTime = sharedPreferences.getBoolean("isFirstTime", true);
//        if(false){
////        if (isFirstTime) {
//            SharedPreferences.Editor editor = sharedPreferences.edit();
//            editor.putBoolean("isFirstTime", false);
//            Intent intent = new Intent(this, Registration.class);
//            startActivity(intent);
//            editor.commit();
//            finish();
//
//        }else{
//            Intent intent = new Intent(this, ContactList.class);
//            startActivity(intent);
//        }


//        DbHelper dbHelper = new DbHelper(this);
//        Toast.makeText(this, String.valueOf(dbHelper.getContactCount()), Toast.LENGTH_LONG).show();

    }

    /**
     * Gets the current registration ID for application on GCM service.
     * <p/>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     * registration ID.
     */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing registration ID is not guaranteed to work with
        // the new app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the registration ID in your app is up to you.
        return getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p/>
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
        (new AsyncTask<Void, Void, String>() {
            ProgressDialog pg;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pg = new ProgressDialog(MainActivity.this);
                pg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                pg.setMessage("It may take up to one minute.Please wait.");
                pg.setTitle("Requesting for Registration ID");
                pg.setIndeterminate(true);
                pg.setCancelable(false);
                pg.show();
            }

            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(mContext);
                    }
                    regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;

                    SharedPreferences sharedPreferences = getSharedPreferences("hybrid_messenger", 0);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("registration_id", String.valueOf(regid));
                    editor.commit();
                    // You should send the registration ID to your server over HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your app.
                    // The request to your server should be authenticated if your app
                    // is using accounts.

                    // For this demo: we don't need to send it because the device
                    // will send upstream messages to a server that echo back the
                    // message using the 'from' address in the message.

                    // Persist the registration ID - no need to register again.
                    storeRegistrationId(mContext, regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                pg.hide();
                Toast.makeText(MainActivity.this, msg + String.valueOf(regid), Toast.LENGTH_SHORT).show();
                if (regid == "") {
                    Context mContext = MainActivity.this;
                    RelativeLayout relativeLayout = new RelativeLayout(mContext);
                    relativeLayout.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    relativeLayout.setGravity(Gravity.CENTER);
                    relativeLayout.setBackgroundColor(Color.parseColor("#003a6c"));
                    Button btnRetry = new Button(mContext);
                    btnRetry.setText("Try Again");
                    btnRetry.setTextColor(Color.WHITE);
                    relativeLayout.addView(btnRetry);
                    setContentView(relativeLayout);

                    btnRetry.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finish();
                            startActivity(getIntent());
                        }
                    });
                }
            }

        }).execute();

    }

    /**
     * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP
     * or CCS to send messages to your app. Not needed for this demo since the
     * device sends upstream messages to a server that echoes back the message
     * using the 'from' address in the message.
     */
    private void sendRegistrationIdToBackend() {
        // Your implementation here.
        final Button btnUpdateProfile = (Button) findViewById(R.id.btnMsgUpdateProfile);
        final TextView lblMsg = (TextView) findViewById(R.id.lblMsgAgreement);
        final LinearLayout layoutUpdate = (LinearLayout) findViewById(R.id.layout_updateProfile);
        (new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                btnUpdateProfile.setVisibility(View.GONE);
                lblMsg.setVisibility(View.GONE);
                ProgressBar progressBar = new ProgressBar(mContext);
                progressBar.setIndeterminate(true);
                layoutUpdate.addView(progressBar);

            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                Toast.makeText(getBaseContext(), result, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getBaseContext(), RecentConversations.class);
                startActivity(intent);
                finish();
            }

            @Override
            protected String doInBackground(Void... params) {
                String result="";
                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    String requestString = SQL_URL + "&name="+vUsername+"&phone="+vPhone+"&email="+vEmailid+"&regId="+regid+"&photo_url="+vPhoto_url+"&imei_no="+vImeiNumber;
//                    URI uri = URI.create(requestString);
                    Log.v("SERVER_SQL_REQUEST", requestString);
                    HttpPost request = new HttpPost(requestString);
                    HttpResponse response = httpClient.execute(request);
                    HttpEntity httpEntity = response.getEntity();
                    result = EntityUtils.toString(httpEntity);
                } catch (IOException e) {
                    result = "Error";
                    e.printStackTrace();
                } finally {
                    return result;
                }
            }
        }).execute();
    }

    /**
     * Stores the registration ID and app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's mContext.
     * @param regId   registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();

    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i("MESSENGER", "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
