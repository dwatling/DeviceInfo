package com.synaptik.deviceinfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import android.content.Context;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.StatFs;
import android.support.v4.app.ListFragment;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class InfoFragment extends ListFragment {
	ArrayList<Row> mData;
	
	String mUserAgent;
	ListView mList;
	SimpleAdapter mAdapter;

    static InfoFragment newInstance() {
    	InfoFragment f = new InfoFragment();
        return f;
    }

    /**
     * When creating, retrieve this instance's number from its arguments.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	mData = generateDeviceInfoList();
    }
    
    protected List<Map<String, ?>> generateListMap() {
    	List<Map<String, ?>> result = new ArrayList<Map<String,?>>();
    	for (Row r : mData) {
    		HashMap<String, String> map = new HashMap<String,String>();
    		map.put("header", r.mHeader);
    		map.put("data", r.mData);
    		result.add(map);
    	}
    	return result;
    }

    /**
     * The Fragment's UI is just a simple text view showing its
     * instance number.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_list, container, false);
        mList = (ListView)result.findViewById(android.R.id.list);
        
        mAdapter = new SimpleAdapter(getActivity(), generateListMap(), android.R.layout.simple_list_item_2,
        		new String[]{"header", "data"}, new int[]{android.R.id.text1, android.R.id.text2});
        return result;
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setListAdapter(mAdapter);
    }
    
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
    }
    
	private ArrayList<Row> generateDeviceInfoList() {
		ArrayList<Row> result = new ArrayList<Row>();
		DisplayMetrics metrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
		
		result.add(buildLineItem("SDK", VERSION.SDK));
		result.add(buildLineItem("Density", getString(R.string.density), " (", String.valueOf(metrics.density), ")"));
		result.add(buildLineItem("DPI X/Y", String.valueOf(metrics.xdpi), " / ", String.valueOf(metrics.ydpi)));
		result.add(buildLineItem("Screen size", getString(R.string.screen_size)));
		result.add(buildLineItem("Screen resolution", String.valueOf(metrics.widthPixels), "x", String.valueOf(metrics.heightPixels)));
		result.add(buildLineItem("Orientation", getString(R.string.orientation)));
		result.add(buildLineItem("Locale", getResources().getConfiguration().locale.toString()));
		result.add(buildLineItem("Mobile County/Network code", String.valueOf(getResources().getConfiguration().mcc), "/", String.valueOf(getResources().getConfiguration().mnc)));
		result.add(buildLineItem("UserAgent", getUserAgent()));
		result.add(buildLineItem("OpenGL Version", getOpenGLVersion()));
		result.add(buildLineItem("Manufacturer", Build.MANUFACTURER));
		result.add(buildLineItem("Model", Build.MODEL));
		result.add(buildLineItem("Device", Build.DEVICE));
		result.add(buildLineItem("Product", Build.PRODUCT));
		result.add(buildLineItem("Brand", Build.BRAND));
		result.add(buildLineItem("CPU+ABI", Build.CPU_ABI));
		result.add(buildLineItem("Build (Tags)", Build.DISPLAY, " (", Build.TAGS, ")"));
		
		PackageManager pm = getActivity().getPackageManager();
		FeatureInfo[] features = pm.getSystemAvailableFeatures();
		TreeSet<String> l = new TreeSet<String>();
		for (FeatureInfo f : features) {
			if (f.name != null) {
				l.add(f.name);
			}
		}
		result.add(buildLineItem("Features", l));
		
		String[] libraries = pm.getSystemSharedLibraryNames();
		l = new TreeSet<String>();
		for (String lib : libraries) {
			if (lib != null) {
				l.add(lib);
			}
		}
		result.add(buildLineItem("Shared Libraries", l));
		
		StatFs fs = new StatFs("/data");
		Log.d("StatFS", Formatter.formatFileSize(getActivity(), fs.getAvailableBlocks() * fs.getBlockSize()));
		return result;
	}
	
	private String getOpenGLVersion() {
		Context context = getActivity();
        PackageManager packageManager = context.getPackageManager();
        FeatureInfo[] featureInfos = packageManager.getSystemAvailableFeatures();
        if (featureInfos != null && featureInfos.length > 0) {
            for (FeatureInfo featureInfo : featureInfos) {
                // Null feature name means this feature is the open gl es version feature.
                if (featureInfo.name == null) {
                    if (featureInfo.reqGlEsVersion != FeatureInfo.GL_ES_VERSION_UNDEFINED) {
                        return String.valueOf((featureInfo.reqGlEsVersion & 0xFFFF0000) >> 16) + "." + String.valueOf((featureInfo.reqGlEsVersion & 0x0000FFFF));
                    } else {
                        return "1.0"; // Lack of property means OpenGL ES version 1
                    }
                }
            }
        }
        return "1.0";
	}
	
	private String getUserAgent() {
		String result = "Unknown";
		WebView wv = new WebView(getActivity());
		result = wv.getSettings().getUserAgentString();
		return result;
	}
	
	protected Row buildLineItem(String header, String... data) {
		return buildLineItem(header, false, data);
	}
	protected Row buildLineItem(String header, boolean newLine, String... data) {
		StringBuilder sb = new StringBuilder();
		for (String s : data) {
			sb.append(s);
			if (newLine) {
				sb.append("\n");
			}
		}
		Row result = new Row(header, sb.toString());
		
		return result;
	}
	
	protected Row buildLineItem(String header, Set<String> data) {
		return buildLineItem(header, true, data.toArray(new String[]{}));
	}
}
