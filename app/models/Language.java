package models;

import play.db.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Locale;

/**
 * Copyright 2013 blastbeat syndicate gmbh
 * Author: Roger Jaggi
 * Date: 13.07.13
 * Time: 18:29
 */
@Entity
public class Language extends Model {
    @Id
    @Column(columnDefinition = "CHAR(4)")
    public String code;  //iso_639_2;

    public String i18nName;
    public String localName;

    @ManyToOne
    public BackendBasicModel.RowStatus rowStatus;

    public static final Finder<String, Language> find = new Finder<String, Language>(String.class, Language.class);

    public static Language findByLocale(Locale language) {
        return find.where().eq("code", language.getISO3Language()).findUnique();
    }

    public static Language create(Locale language) {
        final Language ret = new Language();
        ret.code = language.getISO3Language();
        ret.i18nName = language.getDisplayLanguage(Locale.ENGLISH);
        ret.localName = language.getDisplayLanguage();
        ret.rowStatus = BackendBasicModel.RowStatus.ACTIVE;
        ret.save();

        return ret;
    };


}
