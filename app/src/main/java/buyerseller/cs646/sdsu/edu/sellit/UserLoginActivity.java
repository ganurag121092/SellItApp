package buyerseller.cs646.sdsu.edu.sellit;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserLoginActivity extends AppCompatActivity {
    Button mLoginButton;
    TextView mRegisterButton;
    private EditText mUsername, mPassword;
    String username, password;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static final String TAG = "User Auth";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);
        mUsername = (EditText) this.findViewById(R.id.loginTextId);
        mPassword = (EditText) this.findViewById(R.id.passwordTextId);

        mLoginButton = (Button)this.findViewById(R.id.loginBtn);
        mRegisterButton = (TextView)this.findViewById(R.id.registerBtn);
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(UserLoginActivity.this,UserRegistrationActivity.class);
                startActivity(intent);
            }
        });
        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]
        FirebaseAuth.getInstance().signOut();
        // [START auth_state_listener]
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    //Toast.makeText(getBaseContext(), user.getEmail() +" is signed-in!!",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(UserLoginActivity.this,CategoryActivity.class);
                    intent.putExtra("Username",user.getEmail());
                    startActivity(intent);

                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    public void loginUser(final View button){
        boolean isValid = true;
        username = mUsername.getText().toString().trim();
        password = mPassword.getText().toString();

        if(TextUtils.isEmpty(username)){
            mUsername.setError("Username Required");
        }
        if(TextUtils.isEmpty(password)){
            mPassword.setError("Password Required");
            isValid = false;
        }
        if(!TextUtils.isEmpty(password)){
            if(password.length()<6){
                mPassword.setError("Must be more than 5 characters");
                isValid = false;
            }
        }

        if(isValid){
            String email = username.toLowerCase() + "@gmail.com";
            // [START sign_in_with_email]
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this,new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                Log.w(TAG, "signInWithEmail:failed", task.getException());
                                Toast.makeText(button.getContext(), "Authentication Failed",
                                        Toast.LENGTH_SHORT).show();
                            }

                            // [START_EXCLUDE]
                            if (!task.isSuccessful()) {
                                //mStatusTextView.setText(R.string.auth_failed);
                            }
                        }
                    });
            // [END sign_in_with_email]

        }
        else{
            Toast.makeText(this, "Please Enter Valid Username & Password", Toast.LENGTH_LONG).show();
        }
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
}
