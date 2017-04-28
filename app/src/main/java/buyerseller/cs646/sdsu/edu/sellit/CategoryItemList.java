package buyerseller.cs646.sdsu.edu.sellit;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CategoryItemList extends AppCompatActivity {

    private String mSelectedItem;
    private DatabaseReference mDatabaseReference;
    private ListView mListView;
    private ArrayAdapter<String> mArrayAdapter;
    private static final String TAG ="CategoryItemList";
    private List<String> mCategoryItemList ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_item_layout);
        initalizeItems();
    }

    private void initalizeItems() {
        mListView=(ListView)findViewById(R.id.category_item_list) ;
        mCategoryItemList= new ArrayList<>();
        Bundle args=getIntent().getExtras();

        mSelectedItem=args.getString("SelectedItem");

        Log.d(TAG,mSelectedItem);
        // will go to the root according to the selected item and will display them to the
        // list/recycle view , then accrodingly to the selected item will fetech data from the sub item
        /*
            Electronics
               mobile
               laptopb
            Sports
               basketball
               Criccket
         */

        if(mSelectedItem.equals("electronics"))
        {
            Log.d(TAG,mSelectedItem);
            mDatabaseReference =FirebaseDatabase.getInstance().getReference().child("Electronics");
            getItems(mDatabaseReference);
        }
        else if (mSelectedItem.equals("furniture"))
        {
            mDatabaseReference =FirebaseDatabase.getInstance().getReference().child("Furniture");
            getItems(mDatabaseReference);
        }
    }



    public void getItems(DatabaseReference mDatabaseReference)
    {
        Log.d(TAG,"getItems");
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> dataSnapshots = dataSnapshot.getChildren().iterator();
                while (dataSnapshots.hasNext()) {
                    DataSnapshot dataSnapshotChild = dataSnapshots.next();
                    Log.d(TAG, dataSnapshotChild.getValue().toString());
                    String mCategoryName = dataSnapshotChild.getValue().toString();
                    mCategoryItemList.add(mCategoryName);
                    mArrayAdapter = new ArrayAdapter<String>(CategoryItemList.this, R.layout.grid_item_layout, mCategoryItemList);
                    mListView.setAdapter(mArrayAdapter);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(CategoryItemList.this, "failed to bring the data" , Toast.LENGTH_LONG).show();
            }
        });
    }



}
