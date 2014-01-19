package models;

import play.db.ebean.Model;
import play.i18n.Lang;

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

    public String englishName;
    public String localName;

    @ManyToOne
    public BackendBasicModel.RowStatus rowStatus;

    public static final Finder<String, Language> find = new Finder<String, Language>(String.class, Language.class);

    public Locale getLocale() {
        return Locale.forLanguageTag(code);
    }

    public static Language findByLocale(Locale language) {
        return find.where().eq("code", language.getISO3Language()).findUnique();
    }

    public static Language findByLang(Lang lang) {
        return findByLocale(lang.toLocale());
    }

    public static Language getOrCreate(Locale language) {
        if(language == null) {
            return null;
        }

        final Language find = Language.findByLocale(language);
        if(find != null) {
            return find;
        }

        final Language ret = new Language();
        ret.code = language.getISO3Language();
        ret.englishName = language.getDisplayLanguage(Locale.ENGLISH);
        ret.localName = language.getDisplayLanguage();
        ret.rowStatus = BackendBasicModel.RowStatus.ACTIVE;
        ret.save();

        return ret;
    };

    public static Language englishLanguageCache = null;

    public static Language getEnglish() {
        if(englishLanguageCache == null) {
            englishLanguageCache = getOrCreate(Locale.ENGLISH);
        }

        return englishLanguageCache;
    }

    public static Language germanLanguageCache = null;

    public static Language getGerman() {
        if(germanLanguageCache == null) {
            germanLanguageCache = getOrCreate(Locale.GERMAN);
        }

        return germanLanguageCache;
    }


    /**
     * get an Language object of the desired language and creates it if it does not exist on database
     *
     * As information for the string to locale conversion:
     * ----------------------------------------------------------------------------------
     * Returns a locale for the specified IETF BCP 47 language tag string.
     * If the specified language tag contains any ill-formed subtags, the first such subtag and all following subtags are ignored. Compare to Locale.Builder.setLanguageTag(java.lang.String) which throws an exception in this case.
     *
     *  The following conversions are performed:
     *
     * The language code "und" is mapped to language "".
     * The language codes "he", "yi", and "id" are mapped to "iw", "ji", and "in" respectively. (This is the same canonicalization that's done in Locale's constructors.)
     * The portion of a private use subtag prefixed by "lvariant", if any, is removed and appended to the variant field in the result locale (without case normalization). If it is then empty, the private use subtag is discarded:
     * Locale loc;
     * loc = Locale.forLanguageTag("en-US-x-lvariant-POSIX");
     * loc.getVariant(); // returns "POSIX"
     * loc.getExtension('x'); // returns null
     *
     * loc = Locale.forLanguageTag("de-POSIX-x-URP-lvariant-Abc-Def");
     * loc.getVariant(); // returns "POSIX_Abc_Def"
     * loc.getExtension('x'); // returns "urp"
     *
     * When the languageTag argument contains an extlang subtag, the first such subtag is used as the language, and the primary language subtag and other extlang subtags are ignored:
     * Locale.forLanguageTag("ar-aao").getLanguage(); // returns "aao"
     * Locale.forLanguageTag("en-abc-def-us").toString(); // returns "abc_US"
     *
     * Case is normalized except for variant tags, which are left unchanged. Language is normalized to lower case, script to title case, country to upper case, and extensions to lower case.
     * If, after processing, the locale would exactly match either ja_JP_JP or th_TH_TH with no extensions, the appropriate extensions are added as though the constructor had been called:
     * Locale.forLanguageTag("ja-JP-x-lvariant-JP").toLanguageTag();
     * // returns "ja-JP-u-ca-japanese-x-lvariant-JP"
     * Locale.forLanguageTag("th-TH-x-lvariant-TH").toLanguageTag();
     * // returns "th-TH-u-nu-thai-x-lvariant-TH"
     *
     * This implements the 'Language-Tag' production of BCP47, and so supports grandfathered (regular and irregular) as well as private use language tags. Stand alone private use tags are represented as empty language and extension 'x-whatever', and grandfathered tags are converted to their canonical replacements where they exist.
     * ----------------------------------------------------------------------------------
     *
     * @param languageCode must be in http://tools.ietf.org/html/bcp47 format
     * @return
     */
    public static Language getOrCreate(String languageCode) {

        return getOrCreate(Locale.forLanguageTag(languageCode));

    };

    /* Old String to Locale function
    public static Locale localeFromString(String locale) {
        String parts[] = locale.split("_", -1);
        if (parts.length == 1) {
            return new Locale(parts[0]);
        }
        else if (parts.length == 2 || (parts.length == 3 && parts[2].startsWith("#"))) {
            return new Locale(parts[0], parts[1]);
        }
        else {
            return new Locale(parts[0], parts[1], parts[2]);
        }
    }
    */

    public static void initData() {

        Language.getOrCreate(Locale.GERMAN);
        Language.getOrCreate(Locale.FRENCH);
        Language.getOrCreate(Locale.ENGLISH);

    }

}
