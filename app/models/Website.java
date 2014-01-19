package models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

/**
 * Copyright 2013 blastbeat syndicate gmbh
 * Author: Roger Jaggi <roger.jaggi@blastbeatsyndicate.com>
 * Date: 14.07.13
 * Time: 22:03
 */
@Entity
public class Website extends BackendBasicModel {

    @Column(unique=true)
    public Integer rootMenuId;

    @ManyToOne
    public Website parentWebsite;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parentWebsite")
    public List<Website> childWebsites;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "website")
    public List<WebsiteLanguage> languages;

    public void setTitle(Language language, String title) {

        if(language == null) {
            throw new IllegalArgumentException("Parameter language is null!");
        }

        if(this.languages == null) {
            this.languages = new ArrayList<>();
        }

        for(WebsiteLanguage item : this.languages) {
            if(item.language.equals(language)) {
                item.title = title;
                return;
            }
        }

        WebsiteLanguage newItem = new WebsiteLanguage();
        newItem.language = language;
        newItem.title = title;
        newItem.website = this;

        this.languages.add(newItem);
    }

    public String getTitle(Language language) {
        if(this.languages != null && this.languages.size() > 0) {
            for(WebsiteLanguage item : this.languages) {
                if(item.language.equals(language)) {
                    return item.title;
                }
            }

            return this.languages.get(0).title;
        }

        return null;
    }

    public String getURLfriendlyTitle(Language language) {
        String string = getTitle(language);

        return (string != null ? Normalizer.normalize(string.toLowerCase(), Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .replaceAll("[^\\p{Alnum}]+", "-") : null);
    }

    public void setContent(Language language, String content) {

        if(language == null) {
            throw new IllegalArgumentException("Parameter language is null!");
        }

        if(this.languages == null) {
            this.languages = new ArrayList<>();
        }

        for(WebsiteLanguage item : this.languages) {
            if(item.language.equals(language)) {
                item.content = content;
                return;
            }
        }

        WebsiteLanguage newItem = new WebsiteLanguage();
        newItem.language = language;
        newItem.content = content;
        newItem.website = this;

        this.languages.add(newItem);
    }

    public String getContent(Language language) {
        for(WebsiteLanguage item : this.languages) {
            if(item.language.equals(language)) {
                return item.content;
            }
        }

        return null;
    }


    ////////////////////// STATIC FUNCTIONS //////////////////////////////////////
    public static final Finder<Long, Website> find = new Finder<Long, Website>(Long.class, Website.class);

    public static Website create() {

        final Website ret = new Website();
        ret.rowStatus = RowStatus.ACTIVE;

        return ret;
    }

    public static void initTestData() {
        List<Website> collectionList = new ArrayList<>();

        Website infos = Website.create();
        infos.setTitle(Language.getEnglish(),"Informations");
        infos.setTitle(Language.getGerman(),"Informationen");
        infos.rootMenuId = 1;
        collectionList.add(infos);

        Website impressum = Website.create();
        impressum.setTitle(Language.getEnglish(),"Legal info");
        impressum.setContent(Language.getEnglish(), "The legal info in english");
        impressum.setTitle(Language.getGerman(), "Impressum");
        impressum.setContent(Language.getGerman(), "Das Impressum in Deutsch");
        impressum.parentWebsite = infos;
        collectionList.add(impressum);

        Website lieferbedingungen = Website.create();
        lieferbedingungen.setTitle(Language.getEnglish(),"Delivery conditions");
        lieferbedingungen.setContent(Language.getEnglish(), "The delivery conditions in english");
        lieferbedingungen.setTitle(Language.getGerman(), "Lieferbedingungen");
        lieferbedingungen.setContent(Language.getGerman(), "Die Lieferbedingungen in Deutsch");
        lieferbedingungen.parentWebsite = infos;
        collectionList.add(lieferbedingungen);

        Website datenschutz = Website.create();
        datenschutz.setTitle(Language.getEnglish(),"Privacy protection");
        datenschutz.setContent(Language.getEnglish(), "The Privacy protection in english");
        datenschutz.setTitle(Language.getGerman(), "Datenschutz");
        datenschutz.setContent(Language.getGerman(), "Der Datenschutz in Deutsch");
        datenschutz.parentWebsite = infos;
        collectionList.add(datenschutz);

        Website agb = Website.create();
        agb.setTitle(Language.getEnglish(),"Terms and conditions");
        agb.setContent(Language.getEnglish(), "The terms and conditions in english");
        agb.setTitle(Language.getGerman(), "Allgemeine Geschäftsbedingungen");
        agb.setContent(Language.getGerman(), "Die AGBs in Deutsch");
        agb.parentWebsite = infos;
        collectionList.add(agb);

        Website homepage = Website.create();
        homepage.rowStatus = RowStatus.SYSTEM;
        homepage.setTitle(Language.getEnglish(),"Our Easter deal 2014");
        homepage.setContent(Language.getEnglish(), "<img src=\"/file/2/ei.jpg\" height=\"300\" />\n" +
                "        <p class=\"lead\">Easter-Egg filled with home made pralines!</p>\n" +
                "        <p><a class=\"btn btn-primary\" href=\"/collection/1/article/1\" role=\"button\">Show article &raquo;</a>\n" +
                "               <a class=\"btn btn-default\" href=\"#\" onclick=\"addArticleToShoppingCart(1)\" role=\"button\">Only 9.90 CHF - Order now &raquo;</a>\n" +
                "            </p>");
        homepage.setTitle(Language.getGerman(),"Unser Oster Hit 2014");
        homepage.setContent(Language.getGerman(), "<img src=\"/file/2/ei.jpg\" height=\"300\" />\n" +
                "        <p class=\"lead\">Schokaladen-Ei gefüllt mit hausgemachten Pralinen!</p>\n" +
                "        <p><a class=\"btn btn-primary\" href=\"/collection/1/article/1\" role=\"button\">Zum Artikel &raquo;</a>\n" +
                "               <a class=\"btn btn-default\" href=\"#\" onclick=\"addArticleToShoppingCart(1)\" role=\"button\">Nur 9.90 CHF - Jetzt bestellen &raquo;</a>\n" +
                "            </p>");;
        homepage.rootMenuId = 2;
        collectionList.add(homepage);

        Website kundenservice = Website.create();
        kundenservice.setTitle(Language.getEnglish(),"Customer service");
        kundenservice.setTitle(Language.getGerman(),"Kundenservice");
        kundenservice.rootMenuId = 3;
        collectionList.add(kundenservice);

        Website contact = Website.create();
        contact.setTitle(Language.getEnglish(),"Contact");
        contact.setContent(Language.getEnglish(), "contact in english");
        contact.setTitle(Language.getGerman(), "Kontakt");
        contact.setContent(Language.getGerman(), "Kontakt in Deutsch");
        contact.parentWebsite = kundenservice;
        collectionList.add(contact);

        Website retouren = Website.create();
        retouren.setTitle(Language.getEnglish(),"Returns");
        retouren.setContent(Language.getEnglish(), "Returns in english");
        retouren.setTitle(Language.getGerman(), "Retouren");
        retouren.setContent(Language.getGerman(), "Die Retour-Bedingungen in Deutsch");
        retouren.parentWebsite = kundenservice;
        collectionList.add(retouren);

        Website summary = Website.create();
        summary.setTitle(Language.getEnglish(),"Summary");
        summary.setContent(Language.getEnglish(), "The summary in english");
        summary.setTitle(Language.getGerman(), "Übersicht");
        summary.setContent(Language.getGerman(), "Die Übersicht in Deutsch");
        summary.parentWebsite = kundenservice;
        collectionList.add(summary);

        Website extras = Website.create();
        extras.setTitle(Language.getEnglish(), "Extras");
        extras.setTitle(Language.getGerman(), "Extras");
        extras.rootMenuId = 4;
        collectionList.add(extras);

        Website brands = Website.create();
        brands.setTitle(Language.getEnglish(),"Brands");
        brands.setContent(Language.getEnglish(), "Brands in english");
        brands.setTitle(Language.getGerman(), "Marken");
        brands.setContent(Language.getGerman(), "Marken in Deutsch");
        brands.parentWebsite = extras;
        collectionList.add(brands);

        Website coupon = Website.create();
        coupon.setTitle(Language.getEnglish(),"Gift coupons");
        coupon.setContent(Language.getEnglish(), "Gift coupons in english");
        coupon.setTitle(Language.getGerman(), "Geschenkgutscheine");
        coupon.setContent(Language.getGerman(), "Die Geschenkgutscheine in Deutsch");
        coupon.parentWebsite = extras;
        collectionList.add(coupon);

        Website offerings = Website.create();
        offerings.setTitle(Language.getEnglish(),"Offerings");
        offerings.setContent(Language.getEnglish(), "The offerings in english");
        offerings.setTitle(Language.getGerman(), "Angebote");
        offerings.setContent(Language.getGerman(), "Die Angebote in Deutsch");
        offerings.parentWebsite = extras;
        collectionList.add(offerings);

        Ebean.save(collectionList);
    }
}
