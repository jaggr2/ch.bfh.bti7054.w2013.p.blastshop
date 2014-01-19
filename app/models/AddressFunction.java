package models;

import be.objectify.deadbolt.core.models.Role;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

/**
 * Copyright 2013 blastbeat syndicate gmbh
 * Author: Roger Jaggi <roger.jaggi@blastbeatsyndicate.com>
 * Date: 15.07.13
 * Time: 01:23
 */
@Entity
public class AddressFunction extends BackendBasicModel {

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "addressFunction")
    public List<AddressFunctionLanguage> languages;

    public void setName(Language language, String name) {

        if(language == null) {
            throw new IllegalArgumentException("Parameter language is null!");
        }

        if(this.languages == null) {
            this.languages = new ArrayList<>();
        }

        for(AddressFunctionLanguage item : this.languages) {
            if(item.language.equals(language)) {
                item.name = name;
                return;
            }
        }

        AddressFunctionLanguage newItem = new AddressFunctionLanguage();
        newItem.language = language;
        newItem.name = name;
        newItem.addressFunction = this;

        this.languages.add(newItem);
    }

    public String getName(Language language) {
        for(AddressFunctionLanguage item : this.languages) {
            if(item.language.equals(language)) {
                return item.name;
            }
        }

        return null;
    }

    public static final Finder<Long, AddressFunction> find = new Finder<Long, AddressFunction>(Long.class, AddressFunction.class);

    public static AddressFunction get(Language language, String englishDescription) {
        return AddressFunction.find.fetch("languages").where().eq("languages.name", englishDescription).findUnique();
    }

    protected static void create(String englishDescription, RowStatus status) {
        final AddressFunction ret = new AddressFunction();
        ret.setName(Language.getEnglish(),englishDescription);
        ret.rowStatus = status;
        ret.save();
    }

    public static void initData() {
        create("works at", RowStatus.ACTIVE);
        create("is member of", RowStatus.ACTIVE);
        create("is child of", RowStatus.ACTIVE);
    }
}
