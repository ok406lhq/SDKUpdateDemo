package com.momo.sdk.update.common;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class GeneralUtils {

    public static int getVersionCode(Context context)
    {
        try {
            PackageInfo pi=context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return 0;
        }
    }

    public static int getIdByName(Context context, String className, String name) {
        return context.getResources().getIdentifier(name,className,context.getPackageName());
    }
}
