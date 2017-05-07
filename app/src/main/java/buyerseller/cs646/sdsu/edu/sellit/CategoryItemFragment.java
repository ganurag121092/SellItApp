package buyerseller.cs646.sdsu.edu.sellit;


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

public class CategoryItemFragment extends ListFragment {

    private static final String TAG = "CategoryItemFragment";
    private String mItemSelected;
    private DatabaseReference mDatabaseReference;
    private List<String> mCategoryItemList ;
    private ArrayAdapter<String> mArrayAdapter;
    public CategoryItemListener  getListener;

    public void CategoryItemFragment (){
        // mandatory constructor
    }

   // passing Category Selected to Fragment
    public static CategoryItemFragment newInstance(String mItemSelected) {
        Log.d(TAG, "CategoryItemFragment newInstance ");
        CategoryItemFragment fragment = new CategoryItemFragment();
        Bundle args = new Bundle();
        args.putString("SelectedItem", mItemSelected);
        fragment.setArguments(args);
        return fragment;
    }


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mItemSelected = getArguments().getString("SelectedItem");
         }
        Log.d(TAG,mItemSelected);
        //passing the received seected category to find corresponding item from firebase Database
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child(mItemSelected);
        getItems(mDatabaseReference);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // on selecting Category's list of Items
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String onItemSelected =  parent.getItemAtPosition(position).toString();

                getListener= (CategoryItemListener) getActivity();
                if (getListener!= null)
                {
                    getListener.onCategoryItemSelect(onItemSelected);
                }
            }
        });
    }



    // interface to pass selected item value to parent Activity
    public interface CategoryItemListener {
        public void onCategoryItemSelect(String onItemSelected);
    }

   // method to fetch data from firebase and displaying in List format
    public void getItems(DatabaseReference mDatabaseReference)
    {
        Log.d(TAG,"getItems");
        mCategoryItemList= new ArrayList<>();
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> dataSnapshots = dataSnapshot.getChildren().iterator();
                while (dataSnapshots.hasNext()) {
                    DataSnapshot dataSnapshotChild = dataSnapshots.next();
                    Log.d(TAG, dataSnapshotChild.getValue().toString());
                    String mCategoryName = dataSnapshotChild.getValue().toString();
                    mCategoryItemList.add(mCategoryName);
                    mArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, mCategoryItemList);
                    setListAdapter(mArrayAdapter);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), "failed to bring the data" , Toast.LENGTH_LONG).show();
            }
        });
    }

}
