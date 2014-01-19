package models;

import com.avaje.ebean.annotation.EnumValue;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.*;

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

    public String localName;

    // Fields for RegionType country
    public String postalCodeFormat;
    public String postalCodeDenomination;
    public String localStateName;
    public String localDistrictName;
    public String localSubdistrictName;
    public String localCommuneName;
    public String telephonePrefix;

    // Fields for RegionType commune
    public String postalCode;
    public Float latitude;
    public Float longitude;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "region")
    public List<RegionLanguage> languages;

    public void setName(Language language, String name) {

        if(language == null) {
            throw new IllegalArgumentException("Parameter language is null!");
        }

        if(this.languages == null) {
            this.languages = new ArrayList<>();
        }

        for(RegionLanguage item : this.languages) {
            if(item.language.equals(language)) {
                item.name = name;
                return;
            }
        }

        RegionLanguage newItem = new RegionLanguage();
        newItem.language = language;
        newItem.name = name;
        newItem.region = this;

        this.languages.add(newItem);
    }

    public String getName(Language language) {
        for(RegionLanguage item : this.languages) {
            if(item.language.equals(language)) {
                return item.name;
            }
        }

        return null;
    }


    ////////////////////// STATIC FUNCTIONS //////////////////////////////////////
    public static final Finder<Long, Region> find = new Finder<Long, Region>(Long.class, Region.class);

    public static Region findByRegionCode(Region parentRegion, String regionCode) {

        if(parentRegion == null) {
            return find.where().isNull("parentRegion").eq("regionCode", regionCode).findUnique();
        }
        else {
            return find.where().eq("parentRegion",parentRegion).eq("regionCode", regionCode).findUnique();
        }
    }

    public static Region getOrCreate(RegionType regionLevel, Region parentRegion, String regionCode, String localName, String englishName) {
        if(regionLevel == null || regionCode == null) {
            return null;
        }

        final Region find = Region.findByRegionCode(parentRegion, regionCode);
        if(find != null) {
            return find;
        }

        final Region ret = new Region();
        ret.regionCode = regionCode;
        ret.regionLevel = regionLevel;
        ret.localName = localName;
        ret.setName(Language.getEnglish(), englishName);

        if(parentRegion != null) { ret.parentRegion = parentRegion; }

        ret.rowStatus = BackendBasicModel.RowStatus.ACTIVE;
        ret.save();

        return ret;
    };

    public static Region getOrCreateCommune(Region parentRegion, String postalCode, String localName, String englishName) {
        if(parentRegion == null || localName == null) {
            return null;
        }

        final Region find = Region.find.fetch("languages").where().eq("parentRegion", parentRegion).eq("languages.name",englishName).eq("languages.language",Language.getEnglish()).findUnique();
        if(find != null) {
            return find;
        }

        final Region ret = new Region();
        ret.regionCode = null;
        ret.regionLevel = RegionType.COMMUNE;
        ret.setName(Language.getEnglish(), englishName);
        ret.localName = localName;
        ret.parentRegion = parentRegion;

        ret.postalCode = postalCode;

        ret.rowStatus = BackendBasicModel.RowStatus.ACTIVE;
        ret.save();

        return ret;
    };
}
