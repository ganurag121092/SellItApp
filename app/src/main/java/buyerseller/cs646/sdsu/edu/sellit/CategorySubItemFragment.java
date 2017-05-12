package buyerseller.cs646.sdsu.edu.sellit;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
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
    private List<ItemModel> mItemList;
    private ArrayAdapter<String> mArrayAdapter;
    private ArrayAdapter<CharSequence> mSpinnerAdapter;

    private View mRootView;
    private ListView mListView;
    private TextView mNoItem;
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

    @Override
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

        mPriceList= new ArrayList<String>(){
            {
                add("Select(None)");
            }
        };

        mRootView = mInflater.inflate(R.layout.categotysubitem_fragmentlayout, mContainer, false);
        mListView = (ListView) mRootView.findViewById(R.id.SubItemListView);
        mApply=(Button) mRootView.findViewById(R.id.Apply);
        mClear=(Button) mRootView.findViewById(R.id.clear);
        mPriceSpinner =(Spinner)mRootView.findViewById(R.id.PriceSpinner);
        mNoItem=(TextView) mRootView.findViewById(R.id.NoItemFound);
        mNoItem.setVisibility(View.INVISIBLE);
        mItemList=new ArrayList<ItemModel>();
        mCategoryItemList= new ArrayList<>();
        return mRootView;
    }

    //calling on item click/Select listeners in onActivity as these will be called once fragment is ready.
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // popluating the price range with respect to the selected item
        populatePrice();

       // to populate the item of items which are in stock( firebase DB).
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getActivity(), "SubItem  selected " + position,Toast.LENGTH_SHORT).show();
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
                //Toast.makeText(getActivity(), "Price   selected " + position,Toast.LENGTH_SHORT).show();
                mPriceSelected=adapterView.getItemAtPosition(position).toString();
                if(!mPriceSelected.equals("none(selected)"))
                {
                    String[] split = mPriceSelected.split("-");
                    PriceMinimum = Integer.valueOf(split[0]);
                    PriceMaximum = Integer.valueOf(split[1]);
                    Log.d(TAG, "Price   selected " + " " + PriceMinimum + " " + PriceMaximum);
                }
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
                mNoItem.setVisibility(View.INVISIBLE);
                populatePrice();
                getItems(mDatabaseReference);
                PriceMinimum =0;
                PriceMaximum =0;
            }
        });

        // on clicking the apply button it will apply the  selected filter selection
        mApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"onClick Apply");
                getPricesList();
            }
        });
    }

   public void  populatePrice()
   {
       mSpinnerAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.PriceRange, android.R.layout.simple_spinner_item);
       mSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
       mPriceSpinner.setAdapter(mSpinnerAdapter);
   }

    public void getPricesList()
    {
        Log.d(TAG, "getPricesList  "+ PriceMinimum + PriceMaximum);
        mCategoryItemList= new ArrayList<>();
        mItemList=new ArrayList<ItemModel>();
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> dataSnapshots = dataSnapshot.getChildren().iterator();
             //                Log.d(TAG, dataSnapshot.child("itemName").getChildren().iterator().toString());
                while (dataSnapshots.hasNext() )
                {
                    DataSnapshot dataSnapshotChild = dataSnapshots.next();
                    if (!TextUtils.equals(dataSnapshotChild.getKey().toString().split("_")[0],currentUser))
                    {
                        Log.d(TAG, dataSnapshotChild.child("itemName").getValue().toString());
                        ItemModel mItemModel = dataSnapshotChild.getValue(ItemModel.class);
                        if((mItemModel.getBuyerId().equals(""))&&(mItemModel.getBuyerName().equals("")))
                        {
                            mItemList.add(mItemModel);
                        }

                    }
                }
                getFilteresPriceItems(mItemList);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), "failed to bring the data" , Toast.LENGTH_LONG).show();
            }
        });
    }

    public void getFilteresPriceItems(List<ItemModel> itemList)
    {
        Log.d(TAG, "getPrices  "+ PriceMinimum + PriceMaximum);
        ArrayList<String> mCategoryItemList=new ArrayList<>();
        for (ItemModel each :itemList )
        {
            if(Integer.valueOf(each.getSellingCost())>=PriceMinimum && Integer.valueOf(each.getSellingCost())<=PriceMaximum) {
                mCategoryItemList.add(each.getItemName());
            }
        }
        if(mCategoryItemList.size()==0)
        {
            mNoItem.setVisibility(View.VISIBLE);
            //Toast toast = Toast.makeText(getActivity(), "Sorry! No item item found", Toast.LENGTH_LONG);
            //toast.setGravity(Gravity.CENTER, 0, 0);
            //toast.show();
        }


        mArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, mCategoryItemList);
        mListView.setAdapter(mArrayAdapter);
        mArrayAdapter.notifyDataSetChanged();
    }


    // fetch subitems names as soon as activity is launched
    public void getItems(final DatabaseReference mDatabaseReference)
    {
        Log.d(TAG,"getItems");
        mCategoryItemList= new ArrayList<>();
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
          public void onDataChange(DataSnapshot dataSnapshot) {
          Iterator<DataSnapshot> dataSnapshots = dataSnapshot.getChildren().iterator();
            Log.d(TAG, "children are " + dataSnapshot.getChildrenCount());
            if (dataSnapshot.getChildrenCount()==0)
            {
                mNoItem.setVisibility(View.VISIBLE);
            }
          if (dataSnapshot.getValue() != null) {
          while (dataSnapshots.hasNext() )
          {
             DataSnapshot dataSnapshotChild = dataSnapshots.next();
             if (!TextUtils.equals(dataSnapshotChild.getKey().toString().split("_")[0],currentUser))
             {
               Log.d(TAG, dataSnapshotChild.child("itemName").getValue().toString());
                 ItemModel mItemModel = dataSnapshotChild.getValue(ItemModel.class);
                 if((mItemModel.getBuyerId().equals(""))&&(mItemModel.getBuyerName().equals("")))
                 {
                     String mCategoryName = mItemModel.getItemName();
                     mCategoryItemList.add(mCategoryName);
                 }
             }
          }
              if (mCategoryItemList.size()==0)
              {
                  mNoItem.setVisibility(View.VISIBLE);
              }

              mArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, mCategoryItemList);
              mListView.setAdapter(mArrayAdapter);
        }
            else
            mDatabaseReference.removeEventListener(this);
        }
          @Override
          public void onCancelled(DatabaseError databaseError) {
          Toast.makeText(getActivity(), "failed to bring the data" , Toast.LENGTH_LONG).show();
          }
        });
    }
}
