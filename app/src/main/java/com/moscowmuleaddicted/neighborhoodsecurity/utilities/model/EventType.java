package com.moscowmuleaddicted.neighborhoodsecurity.utilities.model;

import com.moscowmuleaddicted.neighborhoodsecurity.R;

import xdroid.enumformat.EnumFormat;
import xdroid.enumformat.EnumString;

/**
 * All the possible types for the field eventType in {@link Event}
 * Annotations are used to provide localization of the enum values
 *
 * @author Simone Ripamonti
 * @version 1
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

    /**
     * @return non localized enum value text
     */
    public String toStringNotLocalized(){
        return super.toString();
    }
}
