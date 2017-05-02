package buyerseller.cs646.sdsu.edu.sellit;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import static android.content.ContentValues.TAG;
public class UserRegistrationActivity extends AppCompatActivity {
    Button mGetLocation, mReset, mSave;
    private static final int INTENT_LOCATION_REQUEST = 125;
    private EditText mUsername, mPassword, mPhone, mAddress, mLatitude, mLongitude;
    String username, password, phone, address, selectedLat, selectedLon;
    static User userData;
    private FirebaseAuth mAuth;
    static FirebaseUser firebaseUser;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);
        mUsername = (EditText) this.findViewById(R.id.newloginTextId);
        mPassword = (EditText) this.findViewById(R.id.newpasswordTextId);
        mPhone = (EditText) this.findViewById(R.id.phoneTextId);
        mAddress = (EditText) this.findViewById(R.id.addressTextId);
        mLatitude = (EditText) this.findViewById(R.id.latTextId);
        mLongitude = (EditText) this.findViewById(R.id.lonTextId);
        mUsername.setSelection(mUsername.getText().length());

        mGetLocation = (Button) this.findViewById(R.id.getLonLatButtonId);
        mSave = (Button) this.findViewById(R.id.saveBtn);

        mReset = (Button) this.findViewById(R.id.resetBtn);
        mReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearFields();
            }
        });

        mAuth = FirebaseAuth.getInstance();

        // [START auth_state_listener]
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    // User is signed in
                    addUserToDatabase(userData,firebaseUser);
                    Log.d("Firebase User", firebaseAuth.getCurrentUser().toString());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    //  Toast.makeText(UserHelperActivity.this,"Login User Null",Toast.LENGTH_LONG).show();
                }
               /* // [START_EXCLUDE]
                updateUI(user);
                // [END_EXCLUDE]*/
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

    }
    // [END on_start_add_listener]

    // [START on_stop_remove_listener]
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void saveUserData(View button){
        boolean isValid = true;
        username = mUsername.getText().toString();
        password = mPassword.getText().toString();
        phone = mPhone.getText().toString();
        address = mAddress.getText().toString();
        selectedLat = mLatitude.getText().toString();
        selectedLon = mLongitude.getText().toString();


        if(TextUtils.isEmpty(username)){
            mUsername.setError("Username Required");
        }
        /*if(isUserExists(mUsername.getText().toString(), getBaseContext())){
            //Toast.makeText(getBaseContext(), "Please Enter new Nickname", Toast.LENGTH_LONG).show();
            mUsername.setError("Nickname Already Exists");
            isValid = false;
        }*/
        if(password.length()<6){
            mPassword.setError("Must be more than 5 characters");
            isValid = false;
        }
        if(TextUtils.isEmpty(password)){
            //Toast.makeText(getBaseContext(), "Please Enter Password", Toast.LENGTH_LONG).show();
            mPassword.setError("Password Required");
            isValid = false;
        }
        if(TextUtils.isEmpty(phone)){
            //Toast.makeText(getBaseContext(), "Please Enter Password", Toast.LENGTH_LONG).show();
            mPassword.setError("Phone number Required");
            isValid = false;
        }
        if(TextUtils.isEmpty(selectedLat)|| TextUtils.isEmpty((selectedLon))){
            Toast.makeText(getBaseContext(), "Please Select Location", Toast.LENGTH_LONG).show();
            isValid = false;
        }


        if (isValid) {
            Log.i("Data Validity","All enter data Valid");
            userData = new User();
            userData.name = username;
            userData.address = address;
            userData.password = password;
            userData.phone = Long.parseLong(phone);
            userData.latitude = Double.parseDouble(selectedLat);
            userData.longitude = Double.parseDouble(selectedLon);
            createAccount(userData);
            //addUserToDatabase(userData);
            //clearFields();
            Toast.makeText(getBaseContext(), "User Data Saved", Toast.LENGTH_LONG).show();

            FirebaseAuth.getInstance().signOut();
        }
    }

    private void createAccount(User userDataModel){
        final String email = userDataModel.name.toLowerCase() + "@gmail.com";
        final String password = userDataModel.password;
        Log.i("Email and Password", email + " " + userDataModel.password);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                        //addUserToDatabase(userData,firebaseUser);
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            //FirebaseAuthException e = (FirebaseAuthException)task.getException();
                            Toast.makeText(UserRegistrationActivity.this,"Login Unsuccessful",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


    public void addUserToDatabase(User userDataModel, FirebaseUser fbUser) {
        //Log.i("FINAL I m here",userDataModel.nickname + "  "+ firebaseUser.getEmail());
        userDataModel.email = fbUser.getEmail();
        userDataModel.uid = fbUser.getUid();
        //User user = new User(userDataModel.latitude,fbUser.getUid(),userDataModel.name,userDataModel.longitude, fbUser.getEmail());
        FirebaseDatabase.getInstance()
                .getReference()
                .child("Users")
                .child(userDataModel.name)
                .setValue(userDataModel)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            FirebaseAuth.getInstance().signOut();
                            Intent intent= new Intent(UserRegistrationActivity.this,UserLoginActivity.class);
                            startActivity(intent);
                            //Toast.makeText(UserHelperActivity.this,"User Added in REALtimeDB",Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(UserRegistrationActivity.this,"Failed to Added in REALtimeDB",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }



    public void clearFields(){
        Log.i("Reset cliked", "RESET clicked");
        mUsername.setText("");
        mPassword.setText("");
        mPhone.setText("");
        mAddress.setText("");
        mLatitude.setText("");
        mLongitude.setText("");
        mUsername.requestFocus();
    }

    public void getLocation(View button){
        Intent userHelper= new Intent(UserRegistrationActivity.this,UserHelperActivity.class);
        startActivityForResult(userHelper,INTENT_LOCATION_REQUEST);
    }

    public void onActivityResult(int requestCode,int resultCode,Intent data){
        if(resultCode == RESULT_OK) {
            switch (requestCode) {
                case INTENT_LOCATION_REQUEST:
                    //Setting Selected Coordinates to the Textview in New User Activity
                    Log.i("LAT_LON", data.getStringExtra("Latitude"));
                    Log.i("LAT_LON", data.getStringExtra("Longitude"));
                    selectedLat = data.getStringExtra("Latitude");
                    selectedLon = data.getStringExtra("Longitude");
                    mLatitude.setText(selectedLat.substring(0,7));
                    mLongitude.setText(selectedLon.substring(0,7));
                    break;
            }
        }
    }

    boolean isUserExists(String name, Context context){

        return true;
    }


}
