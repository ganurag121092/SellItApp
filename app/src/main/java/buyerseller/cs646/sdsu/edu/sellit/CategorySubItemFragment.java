package buyerseller.cs646.sdsu.edu.sellit;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
public class CategorySubItemFragment  extends Fragment {

    private static final String TAG = "CategorySubItemFragment";
    private String mSubItemSelected;
    private DatabaseReference mDatabaseReference;
    private List<String> mCategoryItemList ;
    private ArrayAdapter<String> mArrayAdapter;
    private View mRootView;
    private ListView mListView;
    private Button mApply, mClear;
    private Spinner mPriceSpinner;
    private List<String>  mPriceList;
    private String mPriceSelected;
    private int PriceMinimum, PriceMaximum;
    private FirebaseAuth firebaseAuth;
    FirebaseUser user;
    private String currentUser;

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
        PriceMinimum =0;
        PriceMaximum =0;

        //passing the received seected category to find corresponding item from firebase Database
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child(mSubItemSelected);
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        if (user != null) {
            // User is signed in
            Log.d(TAG, "onAuthStateChanged:signed_in:" + FirebaseAuth.getInstance().getCurrentUser().toString());
            String username = user.getEmail().substring(0,user.getEmail().length()-10);
            currentUser = username.substring(0,1).toUpperCase()+username.substring(1);

            //Toast.makeText(getActivity(), user.getEmail() +" is signed-in!!",Toast.LENGTH_LONG).show();
        } else {
            // User is signed out
            currentUser = "Guest";
            Log.d(TAG, "onAuthStateChanged:signed_out");
        }


        getItems(mDatabaseReference);
    }

    //create view and initializing all the fragment elements
    @Override
    public View onCreateView(LayoutInflater mInflater, ViewGroup mContainer, Bundle savedInstanceState) {
        super.onCreateView(mInflater, mContainer, savedInstanceState);
        mPriceList= new ArrayList<String>(){{ add("Select(None)");}};
        mRootView = mInflater.inflate(R.layout.categotysubitem_fragmentlayout, mContainer, false);
        mListView = (ListView) mRootView.findViewById(R.id.SubItemListView);
        mApply=(Button) mRootView.findViewById(R.id.Apply);
        mClear=(Button) mRootView.findViewById(R.id.clear);
        mPriceSpinner =(Spinner)mRootView.findViewById(R.id.PriceSpinner);
        return mRootView;
    }

    //calling on item click/Select listeners in onActivity as these will be called once fragment is ready.
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // popluating the price range with respect to the selected item
        populatePrice(mSubItemSelected);

       // to populate the item of items which are in stock( firebase DB).
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(), "SubItem  selected " + position,Toast.LENGTH_SHORT).show();
                String mItemSelected =  parent.getItemAtPosition(position).toString();
                Intent mIntent = new Intent(getActivity(),ItemActivity.class);
                mIntent.putExtra("Item", mItemSelected);
                mIntent.putExtra("SelectedSubItem", mSubItemSelected);
                startActivity(mIntent);
            }
        });

        // on selecting the particular price choice , getting the price value which will be passed as
        //parameter in getPrice list , so only item in those range will be feteched.
        mPriceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                Toast.makeText(getActivity(), "Price   selected " + position,Toast.LENGTH_SHORT).show();
                mPriceSelected=adapterView.getItemAtPosition(position).toString();
                String[] split = mPriceSelected.split("-");
                PriceMinimum = Integer.valueOf(split[0]);
                PriceMaximum = Integer.valueOf(split[1]);
                Log.d(TAG, "Price   selected " + " " + PriceMinimum + " " + PriceMaximum);
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });

       // on clicking the clear button it will clear all the filter selection
        mClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PriceMinimum =0;
                PriceMaximum =0;

            }
        });

        mApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPricesList();
            }
        });

    }

   public void  populatePrice(String mItemSelected)
   {
       ArrayAdapter<CharSequence> adapter=null;
       if(mItemSelected.equals(mItemSelected)){
           adapter = ArrayAdapter.createFromResource(getActivity(), R.array.PriceRange, android.R.layout.simple_spinner_item);
       }
       adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
       mPriceSpinner.setAdapter(adapter);
   }

    public void getPricesList()
    {
        Log.d(TAG, "getPricesList  ");
        //final int Value =15000;
        mCategoryItemList= new ArrayList<>();
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> dataSnapshots = dataSnapshot.getChildren().iterator();
                Log.d(TAG, dataSnapshot.child("itemName").getChildren().iterator().toString());

                    while (dataSnapshots.hasNext() ) {
                        DataSnapshot dataSnapshotChild = dataSnapshots.next();
                        //Log.d(TAG, dataSnapshotChild.child("itemName").getValue().toString());
                        String Price =dataSnapshotChild.child("sellingCost").getValue().toString();
                        Log.d(TAG, "Price "+ Price);
                        if(PriceMinimum ==0 && PriceMaximum==0)
                        {
                            Log.d(TAG, "Price when 0 "+ PriceMinimum + " " + PriceMaximum + " ");
                            String mCategoryName = dataSnapshotChild.child("itemName").getValue().toString();
                            mCategoryItemList.add(mCategoryName);
                            mArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, mCategoryItemList);
                            mListView.setAdapter(mArrayAdapter);
                            mArrayAdapter.notifyDataSetChanged();
                        }
                        else
                        if(Integer.valueOf(Price)>=PriceMinimum && Integer.valueOf(Price)<=PriceMaximum)
                        {
                            Log.d(TAG, "Price when > and < "+ PriceMinimum + " " + PriceMaximum + " ");
                            String mCategoryName = dataSnapshotChild.child("itemName").getValue().toString();
                            mCategoryItemList.add(mCategoryName);
                            mArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, mCategoryItemList);
                            mListView.setAdapter(mArrayAdapter);
                            mArrayAdapter.notifyDataSetChanged();
                        }
                        else
                        {
                            Log.d(TAG, " what is this"+ PriceMinimum + " " + PriceMaximum + " ");
                            mArrayAdapter.clear();
                        }
                    }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), "failed to bring the data" , Toast.LENGTH_LONG).show();
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
               // Log.d(TAG, dataSnapshot.child("itemName").getChildren().iterator().toString());
                if (dataSnapshot.getValue() != null) {
                   while (dataSnapshots.hasNext() ) {
                        DataSnapshot dataSnapshotChild = dataSnapshots.next();

                        // ItemModel mItemModel = dataSnapshotChild.getValue(ItemModel.class);
                        if (!TextUtils.equals(dataSnapshotChild.getKey().toString().split("_")[0],currentUser))
                        {
                            Log.d(TAG, dataSnapshotChild.child("itemName").getValue().toString());
                            String mCategoryName = dataSnapshotChild.child("itemName").getValue().toString();
                            mCategoryItemList.add(mCategoryName);
                            mArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, mCategoryItemList);
                            mListView.setAdapter(mArrayAdapter);
                       }
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
