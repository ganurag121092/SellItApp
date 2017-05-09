package buyerseller.cs646.sdsu.edu.sellit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ChatListActivity extends BaseActivity {
    private static final String TAG = "ChatListActy";
    String currentUser, currentUid;
    static List<String> usernames = new ArrayList<>();
    ArrayAdapter<String> usersAdapter;
    ListView mlistView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);
        Intent intent = getIntent();
        currentUser = intent.getStringExtra("CurrentUser");
        currentUid = intent.getStringExtra("CurrentUserUid");
        mlistView = (ListView) this.findViewById(R.id.listview);

        mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                int position, long id) {
                final String selectedUser = (String) parent.getItemAtPosition(position);
                FirebaseDatabase.getInstance()
                        .getReference()
                        .child("Users").child(selectedUser).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        UserModel user = dataSnapshot.getValue(UserModel.class);
                        String selectedUserId = user.uid;
                        Intent chatIntent = new Intent(ChatListActivity.this, ChatActivity.class);
                        chatIntent.putExtra("CurrentUser", currentUser);
                        chatIntent.putExtra("Seller", selectedUser);
                        chatIntent.putExtra("CurrentUserUID", currentUid);
                        chatIntent.putExtra("SellerUID", selectedUserId);
                        startActivity(chatIntent);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(ChatListActivity.this, "failed to bring User data" , Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        FirebaseDatabase.getInstance()
                .getReference()
                .child("Chats")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterator<DataSnapshot> dataSnapshots = dataSnapshot.getChildren()
                                .iterator();
                        usernames = new ArrayList<String>();
                        while (dataSnapshots.hasNext()) {
                            DataSnapshot dataSnapshotChild = dataSnapshots.next();
                            String key_first = dataSnapshotChild.getKey().split("_")[0];
                            String key_second = dataSnapshotChild.getKey().split("_")[1];
                            final String otherUser;
                            if(TextUtils.equals(currentUser,key_first)){
                                otherUser = key_second;
                                usernames.add(otherUser);
                            }
                            else if(TextUtils.equals(currentUser,key_second)){
                                otherUser = key_first;
                                usernames.add(otherUser);
                            }
                        }
                        setChatList(usernames);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Unable to retrieve the users.
                    }
                });
    }

    public void setChatList(List<String> names){
        if(names.size()!=0) {
            usersAdapter = new ArrayAdapter<String>(ChatListActivity.this,
                    android.R.layout.simple_list_item_1, names);
            mlistView.setAdapter(usersAdapter);
        }
        else {
            Toast.makeText(this, "No Chat Users Available", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this,CategoryActivity.class);
            startActivity(intent);
        }
    }





}
