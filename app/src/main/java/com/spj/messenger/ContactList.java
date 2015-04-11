package com.spj.messenger;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;


public class ContactList extends ActionBarActivity {

    String myPhoneNumber;
    //    private String SQL_URL = "http://takshak14.com/hybrid/getallcontact.php";
    ArrayList<UserDetails> detailsArrayList = new ArrayList<UserDetails>();
    //    private String SQL_URL = "http://192.168.43.19/hybrid/getallcontact.php";
    private String SQL_URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SQL_URL = getResources().getString(R.string.SQL_Contact_Request);
        setContentView(R.layout.activity_contact_list);
        SharedPreferences sharedPreferences = getSharedPreferences("hybrid_messenger", 0);

        myPhoneNumber = sharedPreferences.getString("phoneNumber", "");

        showUpdatedContactList();
        getContactListFromWeb();
    }

    public void getContactListFromWeb() {
        (new AsyncTask<Void, Void, String>() {

            @Override
            protected void onPostExecute(String out) {
                super.onPostExecute(out);

                try {
                    JSONObject jsonObject = new JSONObject(out.toString());
                    JSONArray jsonArray = jsonObject.getJSONArray("contacts");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject explrObject = jsonArray.getJSONObject(i);
                        String username = explrObject.getString("name");
                        String phone = explrObject.getString("number");
                        String email = explrObject.getString("emailid");
                        String regId = explrObject.getString("regid");
                        if (!phone.equals(myPhoneNumber)) {
                            UserDetails tempDetails = new UserDetails(username, email, phone, regId);
                            detailsArrayList.add(tempDetails);
                        }
                    }

                    DbHelper dbHelper = new DbHelper(getBaseContext());
                    dbHelper.addNewContacts(detailsArrayList);
                    showUpdatedContactList();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    Toast.makeText(getBaseContext(), "Contacts Updated", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            protected String doInBackground(Void... params) {
                final StringBuilder out = new StringBuilder();
                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost request = new HttpPost(SQL_URL);
                    HttpResponse response = httpClient.execute(request);
                    HttpEntity entity = response.getEntity();
                    if (entity != null) {
                        InputStream inputStream = entity.getContent();
                        final char[] buffer = new char[2];
                        Reader in = new InputStreamReader(inputStream, "UTF-8");
                        for (; ; ) {
                            int rsz = in.read(buffer, 0, buffer.length);
                            if (rsz < 0)
                                break;
                            out.append(buffer, 0, rsz);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    return out.toString();
                }
            }
        }).execute();
    }

    private void showUpdatedContactList() {
        ListView lvContactList = (ListView) findViewById(R.id.lvContactList);
        DbHelper dbHelper = new DbHelper(this);

//        final ArrayList<String> contactArrayList = dbHelper.getContactList();
//        ArrayAdapter<String> contactArrayAdapter = new ArrayAdapter<String>(getBaseContext(), R.layout.support_simple_spinner_dropdown_item, contactArrayList);
//        lvContactList.setAdapter(contactArrayAdapter);
//        lvContactList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent convIntent = new Intent(ContactList.this, Conversation.class);
//                convIntent.putExtra("name", contactArrayList.get(position));
//                startActivity(convIntent);
//            }
//        });

        final ArrayList<UserDetails> contactArrayList = dbHelper.getContactList();
        final ContactAdapter contactAdapter = new ContactAdapter(ContactList.this, contactArrayList);
        lvContactList.setAdapter(contactAdapter);
        lvContactList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent convIntent = new Intent(ContactList.this, Conversation.class);
                convIntent.putExtra("name", contactArrayList.get(position).getvUsername());
                convIntent.putExtra("phone_number", contactArrayList.get(position).getvPhone());
                convIntent.putExtra("regId", contactArrayList.get(position).getvRegID());
                convIntent.putExtra("emailId", contactArrayList.get(position).getvEmailId());
                startActivity(convIntent);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_contact_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            getContactListFromWeb();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
