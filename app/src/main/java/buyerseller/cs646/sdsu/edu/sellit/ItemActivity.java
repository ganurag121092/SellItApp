package buyerseller.cs646.sdsu.edu.sellit;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by vk on 4/28/17.
 */

public class ItemActivity extends AppCompatActivity {

    private String mSelectedItem;
    private static final String TAG ="ItemActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_item_layout);
        initalizeItems();
    }

    private void initalizeItems() {
        Bundle args=getIntent().getExtras();
        mSelectedItem=args.getString("Item");
        Log.d(TAG,mSelectedItem);
    }
}
