package com.synaptik.deviceinfo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

/**
 * TODO - Find other things that can be displayed WITHOUT new permissions (hardware features? [dpad, touch, multitouch,etc.])
 *   
 * @author Edward
 */
public class MainActivity extends FragmentActivity {
	private static final String TAG = "DeviceInfo";
	ViewPager mPager;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.main);
        
        mPager = (ViewPager)findViewById(R.id.pager);
        mPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));
    }
    
    public class PagerAdapter extends FragmentStatePagerAdapter {
        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 1;
        }
        
        @Override
        public Fragment getItem(int position) {
            return InfoFragment.newInstance();
        }
    }    

}