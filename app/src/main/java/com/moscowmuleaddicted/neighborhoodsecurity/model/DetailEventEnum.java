package com.moscowmuleaddicted.neighborhoodsecurity.model;

import com.moscowmuleaddicted.neighborhoodsecurity.R;

import xdroid.enumformat.EnumFormat;
import xdroid.enumformat.EnumString;

/**
 * Auxiliary class to represent the {@link Detail} names for {@link Event} class
 *
 * @author Simone Ripamonti
 * @version 1
 */
public enum DetailEventEnum {
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
