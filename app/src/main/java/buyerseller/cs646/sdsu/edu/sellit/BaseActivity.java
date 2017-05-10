package buyerseller.cs646.sdsu.edu.sellit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class BaseActivity extends AppCompatActivity {
    private String TAG="BaseActivity";
    private FirebaseAuth firebaseAuth;
    private static String loginName = "Guest";
    static String userUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user != null) {
            // User is signed in
            Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
            String username = user.getEmail().substring(0,user.getEmail().length()-10);
            loginName = username.substring(0,1).toUpperCase()+username.substring(1);
            userUid = user.getUid();
            //Toast.makeText(getBaseContext(), user.getEmail() +" is signed-in!!",Toast.LENGTH_LONG).show();
        } else {
            // User is signed out
            loginName = "Guest";
            Log.d(TAG, "onAuthStateChanged:signed_out");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(TAG, "im inside the onCreateOptionsMenu");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.select_buy_sell, menu);
        MenuItem menuItem = menu.findItem(R.id.hello);
        menuItem.setTitle("Hello, "+loginName);
        MenuItem signoutMenu = menu.findItem(R.id.signout);
        MenuItem chatListMenu = menu.findItem(R.id.chatList);
        if(loginName == "Guest"){
            signoutMenu.setVisible(false);
            chatListMenu.setVisible(false);
        }
        else{
            signoutMenu.setVisible(true);
            chatListMenu.setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.hello:
                return true;
            case R.id.signout:
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(this,MainActivity.class);
                startActivity(intent);
                return true;
            case R.id.Sellmenu:
                //firebaseAuth = FirebaseAuth.getInstance();
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user!=null){
                    Intent sellerIntent = new Intent(this,SellerActivity.class);
                    startActivity(sellerIntent);
                }
                else {
                    // User is not existant
                    Log.d(TAG, "User must Register");
                    FirebaseAuth.getInstance().signOut();
                    Intent loginIntent = new Intent(this,UserLoginActivity.class);
                    startActivity(loginIntent);
                }
                return true;
            case R.id.Buymenu:
                FirebaseUser currentuser = firebaseAuth.getCurrentUser();
                if(currentuser!=null){
                    Intent sellerIntent = new Intent(this,CategoryActivity.class);
                    startActivity(sellerIntent);
                }
                else {
                    // User is not existant
                    Log.d(TAG, "User must Register");
                    FirebaseAuth.getInstance().signOut();
                    Intent loginIntent = new Intent(this,MainActivity.class);
                    startActivity(loginIntent);
                }
                return true;
            case R.id.chatList:
                Intent chatListIntent = new Intent(this,ChatListActivity.class);
                chatListIntent.putExtra("CurrentUser",loginName);
                chatListIntent.putExtra("CurrentUserUid",userUid);
                startActivity(chatListIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
