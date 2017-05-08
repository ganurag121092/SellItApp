package buyerseller.cs646.sdsu.edu.sellit;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ChatActivity extends BaseActivity {
    private static final String TAG = "Chat Activity";
    EditText mMessage;

    String chatRoomAB, chatRoomBA, currentUser, seller, currentUid, sellerUid, message;

    SimpleDateFormat dataFormat = new SimpleDateFormat("yyyyMMddmmss", Locale.ENGLISH);

    private RecyclerView chatRecyclerView;
    private ArrayList<ChatModel> chatArrayList;
    private static ChatListAdapter chatListAdapter;
    LinearLayoutManager mLayoutManager;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setIcon(R.drawable.common_google_signin_btn_text_light_normal);
        Intent intent = getIntent();
        currentUser = intent.getStringExtra("CurrentUser");
        seller = intent.getStringExtra("Seller");
        currentUid = intent.getStringExtra("CurrentUserUID");
        sellerUid = intent.getStringExtra("SellerUID");

        mMessage = (EditText) findViewById(R.id.newMsgText);
        Button sendBtn = (Button) findViewById(R.id.sendButton);

        Log.d(TAG, "current User: " + currentUser);
        Log.d(TAG, "seller: " + seller);
        Log.d(TAG, "currentUid: "+currentUid);
        Log.d(TAG, "sellerUid" + sellerUid);
        chatRoomAB = currentUid + "_" + sellerUid;
        chatRoomBA = sellerUid + "_" + currentUid;
        chatArrayList = new ArrayList<>();
        chatListAdapter = new ChatListAdapter(chatArrayList);
        this.setTitle("Seller Name: "+ seller);

        chatRecyclerView = (RecyclerView) this.findViewById(R.id.chats_recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setStackFromEnd(true);
        getMessageFromFirebase();

        databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Chats");

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message = mMessage.getText().toString();

                final ChatModel chat = new ChatModel();
                chat.sender = currentUser.toLowerCase();
                chat.receiver = seller.toLowerCase();
                chat.senderUid = currentUid;
                chat.receiverUid = sellerUid;
                chat.message = message;
                chat.timestamp = new Date();
                if(!TextUtils.isEmpty(chat.getMessage())){
                    Log.d(TAG, "typed message: " + message);
                    databaseReference.getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(chatRoomAB)) {
                                Log.e(TAG, "sendMessageToFirebaseUser: " + chatRoomAB + " exists");
                                databaseReference
                                        .child(chatRoomAB)
                                        .child(String.valueOf(dataFormat.format(chat.timestamp)))
                                        .setValue(chat);
                            } else if (dataSnapshot.hasChild(chatRoomBA)) {
                                Log.e(TAG, "sendMessageToFirebaseUser: " + chatRoomBA + " exists");
                                databaseReference
                                        .child(chatRoomBA)
                                        .child(String.valueOf(dataFormat.format(chat.timestamp)))
                                        .setValue(chat);
                                //inserFirstMsg(chat);
                            } else {
                                Log.e(TAG, "sendMessageToFirebaseUser: success");
                                databaseReference
                                        .child(chatRoomAB)
                                        .child(String.valueOf(dataFormat.format(chat.timestamp)))
                                        .setValue(chat);
                                getMessageFromFirebase();
                                }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    mMessage.setText("");
                }
                else {
                    Toast.makeText(ChatActivity.this, "Send Button pressed, Message is Empty", Toast.LENGTH_SHORT).show();
                }
                    }
                });
    }

    public void getMessageFromFirebase(){
        databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Chats");
        databaseReference
                .getRef()
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(chatRoomAB)) {
                            Log.e(TAG, "getMessageFromFirebaseUser: " + chatRoomAB + " exists");
                            databaseReference.child(chatRoomAB).addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                    onGetChild(dataSnapshot);
                                }

                                @Override
                                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                                    onGetChild(dataSnapshot);
                                }

                                @Override
                                public void onChildRemoved(DataSnapshot dataSnapshot) {

                                }

                                @Override
                                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                        else if(dataSnapshot.hasChild(chatRoomBA)) {
                            Log.e(TAG, "getMessageFromFirebaseUser: " + chatRoomBA + " exists");
                            databaseReference.child(chatRoomBA).addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                    onGetChild(dataSnapshot);
                                }

                                @Override
                                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                                    onGetChild(dataSnapshot);
                                }

                                @Override
                                public void onChildRemoved(DataSnapshot dataSnapshot) {

                                }

                                @Override
                                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                        else {
                            Log.e(TAG, "getMessageFromFirebaseUser: no such room available");
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Unable to get message
                    }
                });
    }

    private void onGetChild(DataSnapshot dataSnapshot){
        ChatModel chat = dataSnapshot.getValue(ChatModel.class);
        Log.i("Snapshot now",chat.getMessage());
        chatArrayList.add(chat);
        Log.d("User List SIZE", String.valueOf(chatArrayList.size()));
        chatListAdapter.notifyDataSetChanged();
        chatRecyclerView.setLayoutManager(mLayoutManager);
        chatRecyclerView.setItemAnimator(new DefaultItemAnimator());
        chatRecyclerView.setAdapter(chatListAdapter);
    }
}
