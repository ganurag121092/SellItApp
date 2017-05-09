package buyerseller.cs646.sdsu.edu.sellit;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import android.widget.ImageButton;
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

    String chatRoomAB, chatRoomBA, currentUser, seller, currentUid, sellerUid, message,sellerPhone;
    private Double buyerLat, buyerLon, sellerLat, sellerLon;
    SimpleDateFormat dataFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.ENGLISH);
    private ImageButton mPhoneCall;
    private RecyclerView chatRecyclerView;
    private ArrayList<ChatModel> chatArrayList;
    private static ChatListAdapter chatListAdapter;
    LinearLayoutManager mLayoutManager;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Intent intent = getIntent();
        currentUser = intent.getStringExtra("CurrentUser");
        seller = intent.getStringExtra("Seller");
        currentUid = intent.getStringExtra("CurrentUserUID");
        sellerUid = intent.getStringExtra("SellerUID");

        mMessage = (EditText) findViewById(R.id.newMsgText);
        Button sendBtn = (Button) findViewById(R.id.sendButton);
        Button locateBtn = (Button) findViewById(R.id.mapLocate);
        Log.d(TAG, "current User: " + currentUser);
        Log.d(TAG, "seller: " + seller);
        Log.d(TAG, "currentUid: "+currentUid);
        Log.d(TAG, "sellerUid" + sellerUid);
        getBuyerDetails(currentUser);
        getSellerDetails(seller);
        chatRoomAB = currentUser + "_" + seller;
        chatRoomBA = seller + "_" + currentUser;
        chatArrayList = new ArrayList<>();
        chatListAdapter = new ChatListAdapter(chatArrayList);
        this.setTitle("Recipient: "+ seller);

        chatRecyclerView = (RecyclerView) this.findViewById(R.id.chats_recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setStackFromEnd(true);
        getMessageFromFirebase();
        mPhoneCall=(ImageButton) findViewById(R.id.ButtonCall);
        databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Chats");
        locateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent =new Intent(ChatActivity.this,LocateUserActivity.class);
                mIntent.putExtra("Buyer", currentUser);
                mIntent.putExtra("Seller", seller);
                mIntent.putExtra("BuyerLat", buyerLat);
                mIntent.putExtra("BuyerLon", buyerLon);
                mIntent.putExtra("SellerLat", sellerLat);
                mIntent.putExtra("SellerLon", sellerLon);
                startActivity(mIntent);
            }
        });

        mPhoneCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0)
            {
                if(!TextUtils.isEmpty(sellerPhone)){
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:" + sellerPhone));
                    startActivity(callIntent);
                }
                else{
                    Toast.makeText(ChatActivity.this, "Phone Doesn't Exist" , Toast.LENGTH_LONG).show();
                }
            }
        });

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

    public void getSellerDetails(String name)
    {
        Log.d(TAG,"getSellerDetails" + name);
        FirebaseDatabase.getInstance().getReference().child("Users").child(name).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                UserModel mUsers = dataSnapshot.getValue(UserModel.class);
                Log.d(TAG, mUsers.phone);
                sellerPhone=mUsers.phone;
                sellerLat = mUsers.latitude;
                sellerLon = mUsers.longitude;
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ChatActivity.this, "failed to bring the data" , Toast.LENGTH_LONG).show();
            }
        });
    }

    public void getBuyerDetails(String name)
    {
        FirebaseDatabase.getInstance().getReference().child("Users").child(name).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {

                //Log.d(TAG,dataSnapshot.getValue().toString() );
                UserModel buyer = dataSnapshot.getValue(UserModel.class);
                buyerLat = buyer.latitude;
                buyerLon = buyer.longitude;
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ChatActivity.this, "failed to bring the data" , Toast.LENGTH_LONG).show();
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
