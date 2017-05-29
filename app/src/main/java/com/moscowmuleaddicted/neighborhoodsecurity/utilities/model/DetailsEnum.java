package com.moscowmuleaddicted.neighborhoodsecurity.utilities.model;

import com.moscowmuleaddicted.neighborhoodsecurity.R;

import xdroid.enumformat.EnumFormat;
import xdroid.enumformat.EnumString;

/**
 * Created by Simone Ripamonti on 25/04/2017.
 */

public enum DetailsEnum {
    @EnumString(R.string.details_date)
    DATE,
    @EnumString(R.string.details_event_type)
    EVENT_TYPE,
    @EnumString(R.string.details_description)
    DESCRIPTION,
    @EnumString(R.string.details_address)
    ADDRESS,
    @EnumString(R.string.details_coordinates)
    COORDINATES,
    @EnumString(R.string.details_votes)
    VOTES;

    @Override
    public String toString(){
        EnumFormat ef = EnumFormat.getInstance();
        return ef.format(this);
    }
}
