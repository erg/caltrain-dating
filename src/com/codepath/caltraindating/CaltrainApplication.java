package com.codepath.caltraindating;
import android.app.Application;
import android.content.Context;

import com.codepath.caltraindating.models.ChatInParse;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;


public class CaltrainApplication extends Application {
	private static Context context;
	
    @Override
    public void onCreate() {
        super.onCreate();
        // Parse doc says we have to register before initialize
	    ParseObject.registerSubclass(ChatInParse.class);
		Parse.initialize(this, getResources().getString(R.string.parseId), getResources().getString(R.string.parseKey));
		ParseFacebookUtils.initialize(getResources().getString(R.string.app_id));
        
        // Create global configuration and initialize ImageLoader with this configuration
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().
        		cacheInMemory().cacheOnDisc().displayer(new RoundedBitmapDisplayer(8)).build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
            .defaultDisplayImageOptions(defaultOptions)
            .build();
        ImageLoader.getInstance().init(config);
        
    }
    
}
