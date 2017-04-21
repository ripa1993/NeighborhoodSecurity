package com.moscowmuleaddicted.neighborhoodsecurity.utilities.jsonclasses;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

/**
 * Created by Simone Ripamonti on 21/04/2017.
 */

public enum EventType {
    CARJACKING, // furto d'auto
    BURGLARY, // effrazione
    ROBBERY, // rapina
    THEFT,	// furto generico
    SHADY_PEOPLE, // persone losche
    SCAMMERS; // truffatori

    private static Context context;

    public static void setContext(Context context){
        EventType.context = context;
    }

    public String getLabel(Context context) {
        Resources res = context.getResources();
        int resId = res.getIdentifier(this.name(), "string", context.getPackageName());
        if (0 != resId) {
            return (res.getString(resId));
        }
        return (name());
    }

    @Override
    public String toString(){
        return getLabel(context);
    }
}
