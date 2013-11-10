package models;

import com.avaje.ebean.annotation.EnumValue;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * Copyright 2013 blastbeat syndicate gmbh
 * Author: Roger Jaggi <roger.jaggi@blastbeatsyndicate.com>
 * Date: 14.07.13
 * Time: 22:03
 */
@Entity
public class Region extends BackendBasicModel {
    public enum RegionType {
        @EnumValue("COU")
        COUNTRY,

        @EnumValue("STA")
        STATE,

        @EnumValue("DIS")
        DISTRICT,

        @EnumValue("SUB")
        SUBDISTRICT,

        @EnumValue("COM")
        COMMUNE,
    }

    @ManyToOne
    public Region parentRegion;

    public String regionCode;  //iso-3166-2;
    public RegionType regionLevel;

    public String i18nName;
    public String localName;

    // Fields for RegionType country
    public String postalCodeFormat;
    public String localStateName;
    public String localDistrictName;
    public String localSubdistrictName;
    public String localCommuneName;
    public String telephonePrefix;

    // Fields for RegionType commune
    public String postalCode;
    public Float latitude;
    public Float longitude;

}
