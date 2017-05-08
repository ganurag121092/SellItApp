package buyerseller.cs646.sdsu.edu.sellit;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static android.content.ContentValues.TAG;

public class SellerActivity extends BaseActivity {
    Button mUploadImages, mReset, mSave;
    private static final String TAG = "SellerActivity" ;
    private static final int INTENT_UPLOAD_IMAGE_REQUEST = 431;
    private static final int PICK_IMAGE_REQUEST = 478;
    private EditText mItemName, mItemTitle, mItemDesc, mItemPrice;
    private Spinner mCategory, mSubcategory;
    public List<String> categories,subcategories;
    ArrayAdapter<String> categoryAdapter,subcategoryAdapter;
    private TextView mUploadCount;
    String itemName, itemTitle, itemDesc, itemPrice, selectedCategory, selectedSubcategory;
    private Uri filePath;
    ItemModel itemModel;
    StorageReference storageReference;
    DatabaseReference mDatabaseRef;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseUser user;
    String userName,UID;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller);
        firebaseAuth = FirebaseAuth.getInstance();
        storageReference= FirebaseStorage.getInstance().getReference();


        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        if (user != null) {
            // User is signed in
            Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
            String username = user.getEmail().substring(0,user.getEmail().length()-10);
            userName = username.substring(0,1).toUpperCase()+username.substring(1);
            UID = user.getUid();
        }



        mItemName = (EditText) this.findViewById(R.id.itemNametext);
        mItemTitle = (EditText) this.findViewById(R.id.itemTitleText);
        mItemDesc = (EditText) this.findViewById(R.id.itemDescText);
        mItemPrice = (EditText) this.findViewById(R.id.itemPriceText);
        mUploadCount = (TextView) this.findViewById(R.id.imageCount);
        mItemName.setSelection(mItemName.getText().length());

        categories = new ArrayList<>();
        subcategories = new ArrayList<>();
        subcategories.add("Select Subcategory");

        categories = getCategories();
        categories.add(0, "Select Category");

        mCategory = (Spinner) this.findViewById(R.id.categorySpinner);
        mSubcategory = (Spinner) this.findViewById(R.id.subCategorySpinner);

// Create an ArrayAdapter using the string array and a default spinner layout
        categoryAdapter = new ArrayAdapter<String>(SellerActivity.this, android.R.layout.simple_spinner_item, categories);
        subcategoryAdapter = new ArrayAdapter<String>(SellerActivity.this, android.R.layout.simple_spinner_item, subcategories);

// Specify the layout to use when the list of choices appears
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subcategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

// Apply the adapter to the spinner
        mCategory.setAdapter(categoryAdapter);
        mSubcategory.setAdapter(subcategoryAdapter);


        mCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                selectedCategory = categories.get(position);
                if(position!=0) {
                    Log.i("selected Category", selectedCategory);
                    subcategories = getSubcategories(selectedCategory);
                    subcategories.add(0,"Select Subcategory");
                }
                else{
                    subcategories = new ArrayList<>();
                    subcategories.add("Select Subcategory");
                }
                    subcategoryAdapter = new ArrayAdapter<String>(SellerActivity.this,android.R.layout.simple_spinner_item,subcategories);
                    subcategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mSubcategory.setAdapter(subcategoryAdapter);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedCategory = null;
            }
        });

        if(selectedCategory == "Select Category"){
            selectedCategory= null;
        }
        if(selectedSubcategory == "Select Subcategory"){
            selectedSubcategory = null;
        }

        mSubcategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                selectedSubcategory = subcategories.get(position);
                if(position!=0) {
                    Log.i("selected Subcategory", selectedSubcategory);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedSubcategory = null;
            }
        });


        mUploadImages = (Button) this.findViewById(R.id.imageUploadBtn);
        mUploadImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent uploadHelper= new Intent(SellerActivity.this,ImageUploadActivity.class);
                startActivityForResult(uploadHelper,INTENT_UPLOAD_IMAGE_REQUEST);*/
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });
        mSave = (Button) this.findViewById(R.id.saveBtn);

        mReset = (Button) this.findViewById(R.id.resetBtn);
        mReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearFields();
            }
        });
    }

    private List<String> getSubcategories(String categoryName){
        subcategories = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child(categoryName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> dataSnapshots = dataSnapshot.getChildren().iterator();
                while (dataSnapshots.hasNext()) {
                    DataSnapshot dataSnapshotChild = dataSnapshots.next();
                    Log.d(TAG, dataSnapshotChild.getValue().toString());
                    String subcategoryName = dataSnapshotChild.getValue().toString();
                    subcategories.add(subcategoryName);
                    }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(SellerActivity.this, "failed to bring the data" , Toast.LENGTH_LONG).show();
            }
        });
        return subcategories;
    }

    private List<String> getCategories(){
        categories = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child("Categories").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> dataSnapshots = dataSnapshot.getChildren().iterator();
                while (dataSnapshots.hasNext()) {
                    DataSnapshot dataSnapshotChild = dataSnapshots.next();
                    Log.d(TAG, dataSnapshotChild.getValue().toString());
                    String mCategoryName = dataSnapshotChild.getValue().toString();
                    categories.add(mCategoryName);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(SellerActivity.this, "failed to bring the data" , Toast.LENGTH_LONG).show();

            }
        });
        return categories;
    }

    public void onActivityResult(int requestCode,int resultCode,Intent data){
        if(resultCode == RESULT_OK) {
            switch (requestCode) {
                case PICK_IMAGE_REQUEST:
                    filePath = data.getData();
                    if (filePath != null){
                        String count = "1";
                        mUploadCount.setText(count+" "+getResources().getString(R.string.imageUploadStatus));
                    }
                        try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                        // imageView.setImageBitmap(bitmap);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    public void postItemData(View button){
        boolean isValid = true;
        itemName = mItemName.getText().toString();
        itemTitle = mItemTitle.getText().toString();
        itemDesc = mItemDesc.getText().toString();
        itemPrice = mItemPrice.getText().toString();

        if(TextUtils.isEmpty(itemName)){
            mItemName.setError("Item name Required");
        }

        if(TextUtils.isEmpty(itemTitle)){
            //Toast.makeText(getBaseContext(), "Please Enter Password", Toast.LENGTH_LONG).show();
            mItemTitle.setError("Item Title Required");
            isValid = false;
        }
        if(TextUtils.isEmpty(itemPrice)){
            //Toast.makeText(getBaseContext(), "Please Enter Password", Toast.LENGTH_LONG).show();
            mItemPrice.setError("Selling Price Required");
            isValid = false;
        }

        //limiting  itemDesc range
        if(itemDesc.length()>120)
        {
            mItemDesc.setError("Description should be within 120character");
            isValid = false;
        }


        //checking for price range
        int price=Integer.valueOf(itemPrice);
        if((price<=0)||(price>=10000))
        {
            mItemPrice.setError("Price ranges between 0-10000");
            isValid = false;
        }

        if(selectedCategory=="Select Category"){
            Toast.makeText(getBaseContext(), "Please Select Category", Toast.LENGTH_LONG).show();
            isValid = false;
        }

        if(selectedSubcategory=="Select Subcategory"){
            Toast.makeText(getBaseContext(), "Please Select Subcategory", Toast.LENGTH_LONG).show();
            isValid = false;
        }
        if(TextUtils.equals(mUploadCount.getText().toString(),"0 Images Upload")){
            Toast.makeText(getBaseContext(), "Please Upload Image", Toast.LENGTH_LONG).show();
            isValid = false;
        }


        if (isValid) {
            Log.i("Data Validity","All enter data Valid");
            itemModel = new ItemModel();
            itemModel.setItemName(itemName);
            itemModel.setItemTitle(itemTitle);
            itemModel.setItemDescription(itemDesc);
            itemModel.setSellingCost(itemPrice);
            itemModel.setSellerName(userName);
            itemModel.setSellerId(UID);
            itemModel.setBuyerId("");
            itemModel.setBuyerName("");
            mDatabaseRef = FirebaseDatabase.getInstance().getReference(selectedSubcategory);
            String itemKey = mDatabaseRef.push().getKey();
            itemModel.setItemId(itemKey);
            addItemToDB(itemModel);

        }
    }

    private void addItemToDB(final ItemModel item) {
        //if there is a file to upload
        if (filePath != null) {
            //displaying a progress dialog while upload is going on
            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading");
            progressDialog.show();
            StorageReference imageStorage = storageReference.child(("images/"+ System.currentTimeMillis() + "."+ getImageExt(filePath)));
            imageStorage.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //if the upload is successfull
                            //hiding the progress dialog
                            //progressDialog.dismiss();
                            //and displaying a success toast
                            Toast.makeText(getApplicationContext(), "File Uploaded ", Toast.LENGTH_LONG).show();
                            if(taskSnapshot!=null) {
                                item.setImageUrl(taskSnapshot.getDownloadUrl().toString());

                                mDatabaseRef.child(item.getSellerName() + "_" + item.getItemId()).setValue(item);
                                progressDialog.dismiss();
                                clearFields();
                            }}
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            //if the upload is not successfull
                            //hiding the progress dialog
                            progressDialog.dismiss();
                            //and displaying error message
                            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //calculating progress percentage
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            //displaying percentage in progress dialog
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                        }
                    });

            FragmentManager fm = getSupportFragmentManager();
            SellFragment dialogFragment = new SellFragment();
            dialogFragment.show(fm, " Thanks for Posting ");
        }
        //if there is not any file
        else {
            //you can display an error toast
            Toast.makeText(getApplicationContext(), "File Does not Exist", Toast.LENGTH_LONG).show();
        }
    }

    public String getImageExt(Uri uri)
    {
        ContentResolver mContentResolver = getContentResolver();
        MimeTypeMap mMimeTypeMap = MimeTypeMap.getSingleton();
        return mMimeTypeMap.getExtensionFromMimeType(mContentResolver.getType(uri));
    }

    public void clearFields(){
        Log.i("Reset cliked", "RESET clicked");
        mItemName.setText("");
        mItemTitle.setText("");
        mItemDesc.setText("");
        mItemPrice.setText("");
        mCategory.setSelection(0);
        mSubcategory.setSelection(0);
        mItemName.requestFocus();
        selectedCategory = "Select Category";
        selectedCategory = "Select Subcategory";
        mUploadCount.setText(getResources().getString(R.string.imageUploadCount));
        mItemName.requestFocus();
    }
}
