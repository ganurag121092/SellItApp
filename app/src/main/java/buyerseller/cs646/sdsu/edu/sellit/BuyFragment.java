package buyerseller.cs646.sdsu.edu.sellit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by Pullies on 5/6/2017.
 */

public class BuyFragment extends DialogFragment {

    private static final String TAG = "BuyFragment";
    private Button mShopMore, mSignOut;
    private TextView mThankyou;
    private View mRootView;

    public void BuyFragment()
    {   }

    @Override
    public View onCreateView(LayoutInflater mInflater, ViewGroup mContainer, Bundle savedInstanceState) {
        super.onCreateView(mInflater, mContainer, savedInstanceState);
        Log.d(TAG,"setOnClickListener");
        mRootView = mInflater.inflate(R.layout.buy_fragment_layout, mContainer, false);
        getDialog().setTitle("Thank You!!!");
        mThankyou = (TextView) mRootView.findViewById(R.id.Thankyou);
        mShopMore=(Button) mRootView.findViewById(R.id.ShopMore);
        mSignOut=(Button) mRootView.findViewById(R.id.Dialogsignout);
        //BUTTON



        mShopMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chatIntent = new Intent(getActivity(),CategoryActivity.class);
                startActivity(chatIntent);
                dismiss();
            }
        });

        mSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(),MainActivity.class);
                startActivity(intent);
                dismiss();
            }
        });

        return mRootView;
    }

}

