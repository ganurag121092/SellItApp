package buyerseller.cs646.sdsu.edu.sellit;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by vk on 4/28/17.
 */

public class CategoryItemSubItemActivity extends AppCompatActivity implements CategoryItemFragment.CategoryItemListener {

    private String mSelectedItem;
    private static final String TAG ="CategoryItemSubItem";
    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_item_subitem_layout);
        initalizeItems();

    }

    private void initalizeItems() {
        Bundle args=getIntent().getExtras();
        mSelectedItem=args.getString("SelectedItem");
        Log.d(TAG,mSelectedItem);
        mFragmentManager= getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        CategoryItemFragment mCategoryItemFragmentt = new CategoryItemFragment().newInstance(mSelectedItem);
        mFragmentTransaction.add(R.id.fragment_holder,mCategoryItemFragmentt);
        mFragmentTransaction.commit();
    }

    @Override
    public void onCategoryItemSelect(String onSubItemSelected) {


        Log.d(TAG, "onSelectPosition "+ onSubItemSelected);
        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        CategorySubItemFragment mCategorySubItemFragment = new CategorySubItemFragment().newInstance(onSubItemSelected);
       //replacing subItems fragment at runtime
        mFragmentTransaction.replace(R.id.fragment_holder, mCategorySubItemFragment);
        mFragmentTransaction.commit();

    }
}
