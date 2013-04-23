package com.example.userlockout;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class HomeSelActivity extends Activity implements OnClickListener 
{

	private Button buttonSelHome;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_sel);
		
		buttonSelHome = (Button)findViewById(R.id.buttonSelHome);
		buttonSelHome.setOnClickListener(this);			
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		
		if (isHomeActivity()) 
		{			
			MainActivity.enableMainActivity(this);

			Intent selector = new Intent("android.intent.action.MAIN");
			selector.addCategory("android.intent.category.HOME");
			startActivity(selector);			
			finish();
		}
	}

	@Override
	public void onClick(View arg0) 
	{
		MainActivity.enableMainActivity(this);

		// Make the user pick the preferred HOME activity, this is not possible to do via API
        Intent localIntent = new Intent(Intent.ACTION_MAIN);
        localIntent.addCategory(Intent.CATEGORY_HOME);
        localIntent.setComponent(new ComponentName("android", "com.android.internal.app.ResolverActivity"));
        startActivity(localIntent);
        finish();
	}

	private boolean isHomeActivity() {
        final IntentFilter filter = new IntentFilter(Intent.ACTION_MAIN);
        filter.addCategory(Intent.CATEGORY_HOME);

        List<IntentFilter> filters = new ArrayList<IntentFilter>();
        filters.add(filter);

        final String myPackageName = getPackageName();
        List<ComponentName> activities = new ArrayList<ComponentName>();
        final PackageManager packageManager = (PackageManager) getPackageManager();

        packageManager.getPreferredActivities(filters, activities, null);

        for (ComponentName activity : activities) {
            if (myPackageName.equals(activity.getPackageName())) {
                return true;
            }
        }
        return false;
    }
}
