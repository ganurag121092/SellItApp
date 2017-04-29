package buyerseller.cs646.sdsu.edu.sellit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by vk on 4/29/17.
 */

public class CategorySubItemFragment  extends ListFragment {

    private static final String TAG = "CategorySubItemFragment";
    private String mSubItemSelected;
    private DatabaseReference mDatabaseReference;
    private List<String> mCategoryItemList ;
    private ArrayAdapter<String> mArrayAdapter;
    public CategoryItemFragment.CategoryItemListener getListener;

    public void CategorySubItemFragment (){
        // mandatory constructor
    }

    // passing Category Selected to Fragment
    public static CategorySubItemFragment newInstance(String mSubItemSelected) {
        Log.d(TAG, "CategorySubItemFragment newInstance ");
        CategorySubItemFragment mCategorySubItemFragment = new CategorySubItemFragment();
        Bundle args = new Bundle();
        args.putString("SelectedSubItem", mSubItemSelected);
        mCategorySubItemFragment.setArguments(args);
        return mCategorySubItemFragment;
    }


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mSubItemSelected = getArguments().getString("SelectedSubItem");
        }
        Log.d(TAG,mSubItemSelected);
        //passing the received seected category to find corresponding item from firebase Database
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child(mSubItemSelected);
        getItems(mDatabaseReference);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // on selecting Category's list of Items
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(), "SubItem  selected " + position,Toast.LENGTH_SHORT).show();
                String mItemSelected =  parent.getItemAtPosition(position).toString();
                Intent mIntent = new Intent(getActivity(),ItemActivity.class);
                mIntent.putExtra("Item", mItemSelected);
                startActivity(mIntent);
            }
        });
    }

    // fetch subitems names
    public void getItems(final DatabaseReference mDatabaseReference)
    {
        Log.d(TAG,"getItems");
        mCategoryItemList= new ArrayList<>();
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> dataSnapshots = dataSnapshot.getChildren().iterator();
                Log.d(TAG, dataSnapshot.child("ItemName").getChildren().iterator().toString());
                if (dataSnapshot.getValue() != null) {
                   while (dataSnapshots.hasNext() ) {
                        DataSnapshot dataSnapshotChild = dataSnapshots.next();
                        Log.d(TAG, dataSnapshotChild.child("ItemName").getValue().toString());
                        String mCategoryName = dataSnapshotChild.child("ItemName").getValue().toString();
                        mCategoryItemList.add(mCategoryName);
                        mArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, mCategoryItemList);
                        setListAdapter(mArrayAdapter);
                    }
                }
                else
                {
                    mDatabaseReference.removeEventListener(this);

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), "failed to bring the data" , Toast.LENGTH_LONG).show();
            }
        });
    }

}
