package com.example.weather;

import android.app.Activity;

public class ResourceUtils {

    static String getLocalizedStringResource(Activity activity, String city) {
        int resourceId = activity.getResources().getIdentifier(city, "string", activity.getPackageName());
        if(resourceId == 0) return city;
        return activity.getResources().getString(resourceId);
    }
}
