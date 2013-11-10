package models;

//import be.objectify.deadbolt.core.models.Role;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * Copyright 2013 blastbeat syndicate gmbh
 * Author: Roger Jaggi <roger.jaggi@blastbeatsyndicate.com>
 * Date: 15.07.13
 * Time: 01:23
 */
@Entity
public class AddressFunction extends BackendBasicModel { //  implements Role {

    public final static String USER = "backenduser";
    public final static String ADMINISTRATOR = "backendadministrator";

    @Column(unique = true)
    public String i18nDescription;

    public static final Finder<Long, AddressFunction> find = new Finder<Long, AddressFunction>(Long.class, AddressFunction.class);

    //@Override
    public String getName() {
        return i18nDescription;
    }

    public static AddressFunction findByName(String roleName) {
        return find.where().eq("i18nDescription", roleName).findUnique();
    }

    public static AddressFunction createOrGet(String i18nDescription) {
        final AddressFunction find = AddressFunction.findByName(i18nDescription);
        if(find != null) {
            return find;
        }

        final AddressFunction ret = new AddressFunction();
        ret.i18nDescription = i18nDescription;
        ret.rowStatus = RowStatus.ACTIVE;
        ret.save();
        return ret;
    }

    protected static void createSystem(String i18nDescription) {
        final AddressFunction ret = new AddressFunction();
        ret.i18nDescription = i18nDescription;
        ret.rowStatus = RowStatus.SYSTEM;
        ret.save();
    }

    public static void initData() {
        createSystem(USER);
        createSystem(ADMINISTRATOR);
    }
}
