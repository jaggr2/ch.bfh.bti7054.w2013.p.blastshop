package models;

import play.db.ebean.Model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Copyright 2013 blastbeat syndicate gmbh
 * Author: Roger Jaggi
 * Date: 13.07.13
 * Time: 18:29
 */
@Entity
public class Currency extends Model {
    @Id
    @Column(columnDefinition = "CHAR(4)")
    public String code;  //iso_???? for example CHF, USD, CAD, EUR, etc.;

    public String symbol; // for example Fr. / $

    public Float globalExchangeRateToCHF;

    @ManyToOne
    public BackendBasicModel.RowStatus rowStatus;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "currency")
    public List<CurrencyLanguage> languages;

    public void setShortName(Language language, String name) {

        if(language == null) {
            throw new IllegalArgumentException("Parameter language is null!");
        }

        if(this.languages == null) {
            this.languages = new ArrayList<>();
        }

        for(CurrencyLanguage lang : this.languages) {
            if(lang.language.equals(language)) {
                lang.shortName = name;
                return;
            }
        }

        CurrencyLanguage newItem = new CurrencyLanguage();
        newItem.language = language;
        newItem.shortName = name;
        newItem.currency = this;

        this.languages.add(newItem);
    }

    public void setLongName(Language language, String name) {

        if(language == null) {
            throw new IllegalArgumentException("Parameter language is null!");
        }

        if(this.languages == null) {
            this.languages = new ArrayList<>();
        }

        for(CurrencyLanguage lang : this.languages) {
            if(lang.language.equals(language)) {
                lang.longName = name;
                return;
            }
        }

        CurrencyLanguage newItem = new CurrencyLanguage();
        newItem.language = language;
        newItem.longName = name;
        newItem.currency = this;

        this.languages.add(newItem);
    }

    public String getShortName(Language language) {
        for(CurrencyLanguage lang : this.languages) {
            if(lang.language.equals(language)) {
                return lang.shortName;
            }
        }

        return null;
    }

    public String getLongName(Language language) {
        for(CurrencyLanguage lang : this.languages) {
            if(lang.language.equals(language)) {
                return lang.longName;
            }
        }

        return null;
    }

    public static final Finder<String, Currency> find = new Finder<String, Currency>(String.class, Currency.class);

    public static Currency findByCode(String code) {
        return find.where().eq("code", code).findUnique();
    }

    public static Currency getOrCreate(String code, String symbol, String englishShortName, String englishLongName) {
        if(code == null || symbol == null || englishShortName == null || englishLongName == null) {
            return null;
        }

        final Currency find = Currency.findByCode(code);
        if(find != null) {
            return find;
        }

        final Currency ret = new Currency();
        ret.code = code;
        ret.symbol = symbol;
        ret.setShortName(Language.getEnglish(),englishShortName);
        ret.setLongName(Language.getEnglish(), englishLongName);
        ret.rowStatus = BackendBasicModel.RowStatus.ACTIVE;
        ret.save();

        return ret;
    };
}
