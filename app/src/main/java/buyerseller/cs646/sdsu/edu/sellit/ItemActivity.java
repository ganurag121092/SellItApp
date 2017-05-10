package buyerseller.cs646.sdsu.edu.sellit;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class ItemActivity extends BaseActivity {

    private String mSelectedItem,mSelectedParentItem;
    private DatabaseReference mDatabaseReference;
    private TextView mItemDetails,mItemName,mItemNameFirebaseValue,mItemPrice,mItemPriceFirebaseValue,mItemDescFirebaseValue,mSellerName,mSellerNameFirebaseValue;
    private ImageView mImageView;
    private static final String TAG ="ItemActivity";
    private Button mBuy,mChat, mLocate;
    private ImageButton mPhoneCall;
    private String sellerName, sellerUId, buyerName, buyerUId, sellerPhone;
    private Double buyerLat, buyerLon, sellerLat, sellerLon;
    private FirebaseAuth firebaseAuth;
    private Context mContext;
    private static String loginName = "Guest";
    private String mUserUID;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_detail_layout);
        mContext=getBaseContext();
        mItemName= (TextView)findViewById(R.id.itemName);
        mItemNameFirebaseValue=(TextView)findViewById(R.id.itemNameFirebaseValue);
        mItemPrice=(TextView)findViewById(R.id.itemPrice);
        mItemPriceFirebaseValue=(TextView)findViewById(R.id.itemPriceFirebaseValue);
        //mItemDescName=(TextView)findViewById(R.id.itemDescName);
        mItemDescFirebaseValue=(TextView)findViewById(R.id.itemDescFirebaseValue);
        mSellerName=(TextView)findViewById(R.id.SellerName);
        mImageView=(ImageView) findViewById(R.id.ImageFirebaseValue);
        mSellerNameFirebaseValue=(TextView)findViewById(R.id.SellerNameFirebaseValue);
        mBuy=(Button) findViewById(R.id.buy_item);
        mChat=(Button) findViewById(R.id.buttonchat);
        mLocate=(Button) findViewById(R.id.buttonLocate);
        mPhoneCall=(ImageButton) findViewById(R.id.ButtonCall);
        initalizeItems();

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        if (user != null) {
            // User is signed in
            Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

            String username = user.getEmail().substring(0,user.getEmail().length()-10);
            buyerUId = user.getUid();
            loginName = username.substring(0,1).toUpperCase()+username.substring(1);
            buyerName = loginName;
            getBuyerDetails(buyerName);
            mUserUID=user.getUid();
        } else {
            // User is signed out
            //loginName = "Guest";
            Log.d(TAG, "onAuthStateChanged:signed_out");
        }

        mBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,loginName);
                if (loginName.equals("Guest"))
                {
                    Toast.makeText(ItemActivity.this, "Please login !!!" , Toast.LENGTH_LONG).show();
                    Intent mIntent =new Intent(ItemActivity.this,MainActivity.class);
                    startActivity(mIntent);
                }
                else
                purchasedItem();

            }
        });

        mLocate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,loginName);
                if (loginName.equals("Guest"))
                {
                    Toast.makeText(ItemActivity.this, "Please login !!!" , Toast.LENGTH_LONG).show();
                    Intent loginIntent = new Intent(ItemActivity.this,MainActivity.class);
                    startActivity(loginIntent);
                }
                else {
                    Log.d(TAG, "BUYER" + buyerName);
                    Log.d(TAG, "BUYER" + buyerLat);
                    Log.d(TAG, "BUYER" + buyerLon);
                    Log.d(TAG, "SELLER" + sellerName);
                    Log.d(TAG, "SELLER" + sellerLat);
                    Log.d(TAG, "SELLER" + sellerLon);
                    Intent mIntent =new Intent(ItemActivity.this,LocateUserActivity.class);
                    mIntent.putExtra("Buyer", buyerName);
                    mIntent.putExtra("Seller", sellerName);
                    mIntent.putExtra("BuyerLat", buyerLat);
                    mIntent.putExtra("BuyerLon", buyerLon);
                    mIntent.putExtra("SellerLat", sellerLat);
                    mIntent.putExtra("SellerLon", sellerLon);
                    startActivity(mIntent);
                }
            }
        });

        mChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if(user!=null) {
                    Intent chatIntent = new Intent(ItemActivity.this, ChatActivity.class);
                    Bundle bundle = new Bundle();
                    chatIntent.putExtra("CurrentUser", buyerName);
                    chatIntent.putExtra("Seller", sellerName);
                    chatIntent.putExtra("CurrentUserUID", buyerUId);
                    chatIntent.putExtra("SellerUID", sellerUId);

                    startActivity(chatIntent);
                }
                else {
                    Toast.makeText(ItemActivity.this, "You Must Register First" , Toast.LENGTH_LONG).show();
                    Intent loginIntent = new Intent(ItemActivity.this,MainActivity.class);
                    startActivity(loginIntent);
                }
            }
        });

        mPhoneCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0)
            {
                if(user!=null) {
                    Log.d(TAG, sellerPhone);
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:" + sellerPhone));
                    startActivity(callIntent);
                }
                else{
                    Toast.makeText(ItemActivity.this, "You Must Register First" , Toast.LENGTH_LONG).show();
                    Intent loginIntent = new Intent(ItemActivity.this,MainActivity.class);
                    startActivity(loginIntent);
                }
            }
        });
    }

    private void initalizeItems() {
        Bundle args=getIntent().getExtras();
        mSelectedItem=args.getString("Item");
        mSelectedParentItem=args.getString("SelectedSubItem");
        Log.d(TAG,mSelectedItem + " " +mSelectedParentItem);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child(mSelectedParentItem);
        getItems(mDatabaseReference);
    }

    public void getItems(final DatabaseReference mDatabaseReference)
    {
        Log.d(TAG,"getItems");
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                Iterator<DataSnapshot> dataSnapshots = dataSnapshot.getChildren().iterator();
                while (dataSnapshots.hasNext() )
                {
                    DataSnapshot dataSnapshotChild = dataSnapshots.next();
                    Log.d(TAG,dataSnapshotChild.getValue().toString() );
                    String ItemName= dataSnapshotChild.child("itemName").getValue().toString();
                    if(ItemName.equals(mSelectedItem))
                    {
                        ItemModel mItemModel = dataSnapshotChild.getValue(ItemModel.class);
                        Log.d(TAG,mItemModel.getItemName() );
                        mItemNameFirebaseValue.setText(mItemModel.getItemName());
                        mItemPriceFirebaseValue.setText(mItemModel.getSellingCost());
                        mItemDescFirebaseValue.setText(mItemModel.getItemDescription());
                        //SellerName=mItemModel.getSellerName();
                        sellerName = mItemModel.getSellerName();
                        sellerUId = mItemModel.getSellerId();
                        getSellerDetails(mItemModel.getSellerName());
                        mSellerNameFirebaseValue.setText(mItemModel.getSellerName());
                        String url= mItemModel.getImageUrl();
                        Glide.with(mContext).load(url).into(mImageView);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ItemActivity.this, "failed to bring the data" , Toast.LENGTH_LONG).show();
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

                    //Log.d(TAG,dataSnapshot.getValue().toString() );
                    UserModel mUsers = dataSnapshot.getValue(UserModel.class);
                    Log.d(TAG, mUsers.phone);
                    sellerPhone=mUsers.phone;
                    sellerLat = mUsers.latitude;
                    sellerLon = mUsers.longitude;
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ItemActivity.this, "failed to bring the data" , Toast.LENGTH_LONG).show();
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
                Toast.makeText(ItemActivity.this, "failed to bring the data" , Toast.LENGTH_LONG).show();
            }
        });
    }

    public void purchasedItem()
    {
        Log.d(TAG,"purchasedItem" );
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterator<DataSnapshot> dataSnapshots = dataSnapshot.getChildren().iterator();
                        while (dataSnapshots.hasNext()) {
                            DataSnapshot dataSnapshotChild = dataSnapshots.next();
                            Log.d(TAG, dataSnapshotChild.getValue().toString());
                            String ItemName = dataSnapshotChild.child("itemName").getValue().toString();
                            if (ItemName.equals(mSelectedItem))
                            {
                                Log.d(TAG,loginName +  " " + dataSnapshotChild.getKey() +  dataSnapshotChild.getRef());
                                DatabaseReference mDatabaseReference= dataSnapshotChild.getRef();
                                Map<String, Object> childUpdates = new HashMap<>();
                                childUpdates.put("buyerId" , user.getUid());
                                childUpdates.put("buyerName" ,loginName);
                                mDatabaseReference.updateChildren(childUpdates);
                           }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ItemActivity.this, "failed to bring the data" , Toast.LENGTH_LONG).show();
            }
        });

       // Toast.makeText(ItemActivity.this, "thanks for purchasing" , Toast.LENGTH_LONG).show();
        FragmentManager fm = getSupportFragmentManager();
        BuyFragment dialogFragment = new BuyFragment();
        dialogFragment.show(fm, " Thanks ");
    }

}
