package com.u8.sdk.update;

import android.content.Context;

public class UpdateTools {
    public static int getIdByName(Context context, String className, String name) {
        return context.getResources().getIdentifier(name,className,context.getPackageName());
    }

}
