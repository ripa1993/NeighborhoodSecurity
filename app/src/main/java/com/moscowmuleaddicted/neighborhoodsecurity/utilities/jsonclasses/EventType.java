package com.moscowmuleaddicted.neighborhoodsecurity.utilities.jsonclasses;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

import com.moscowmuleaddicted.neighborhoodsecurity.R;

import xdroid.enumformat.EnumFormat;
import xdroid.enumformat.EnumString;

/**
 * Created by Simone Ripamonti on 21/04/2017.
 */

public enum EventType {
    @EnumString(R.string.eventtype_carjacking)
    CARJACKING, // furto d'auto
    @EnumString(R.string.eventtype_burglary)
    BURGLARY, // effrazione
    @EnumString(R.string.eventtype_robbery)
    ROBBERY, // rapina
    @EnumString(R.string.eventtype_theft)
    THEFT,	// furto generico
    @EnumString(R.string.eventtype_shady_people)
    SHADY_PEOPLE, // persone losche
    @EnumString(R.string.eventtype_scammers)
    SCAMMERS; // truffatori


    @Override
    public String toString(){
        EnumFormat ef = EnumFormat.getInstance();
        return ef.format(this);
    }

    public String toStringNotLocalized(){
        return super.toString();
    }
}
