package buyerseller.cs646.sdsu.edu.sellit;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by vk on 4/28/17.
 */

public class ItemActivity extends BaseActivity {

    private String mSelectedItem,mSelectedParentItem;
    private DatabaseReference mDatabaseReference;
    private TextView mItemDetails,mItemName,mItemNameFirebaseValue;
    private TextView mItemPrice,mItemPriceFirebaseValue;
    private TextView mItemDescFirebaseValue;
    private TextView mSellerName,mSellerNameFirebaseValue;
    private static final String TAG ="ItemActivity";
    private Button mBuy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_detail_layout);
        initalizeItems();
        mBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    private void initalizeItems() {
        Bundle args=getIntent().getExtras();
        mSelectedItem=args.getString("Item");
        mSelectedParentItem=args.getString("SelectedSubItem");
        Log.d(TAG,mSelectedItem + " " +mSelectedParentItem);
        mItemDetails =(TextView)findViewById(R.id.itemDetails);
        mItemName= (TextView)findViewById(R.id.itemName);

        mItemNameFirebaseValue=(TextView)findViewById(R.id.itemNameFirebaseValue);
        mItemPrice=(TextView)findViewById(R.id.itemPrice);
        mItemPriceFirebaseValue=(TextView)findViewById(R.id.itemPriceFirebaseValue);
        //mItemDescName=(TextView)findViewById(R.id.itemDescName);
        mItemDescFirebaseValue=(TextView)findViewById(R.id.itemDescFirebaseValue);

        mSellerName=(TextView)findViewById(R.id.SellerName);
        mSellerNameFirebaseValue=(TextView)findViewById(R.id.SellerNameFirebaseValue);
        mBuy=(Button) findViewById(R.id.buy_item);
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
                        mSellerNameFirebaseValue.setText(mItemModel.getSellerName());
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ItemActivity.this, "failed to bring the data" , Toast.LENGTH_LONG).show();
            }
        });
    }
}
