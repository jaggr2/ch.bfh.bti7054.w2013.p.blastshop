package models;

import be.objectify.deadbolt.core.models.Role;

import javax.persistence.Entity;

/**
 * Copyright 2013 blastbeat syndicate gmbh
 * Author: Roger Jaggi <roger.jaggi@blastbeatsyndicate.com>
 * Date: 15.07.13
 * Time: 00:04
 */
@Entity
public class SecurityRole extends BackendBasicModel implements Role {

    public final static String USER = "backenduser";
    public final static String ADMINISTRATOR = "backendadministrator";


    public String roleName;

    public static final Finder<Long, SecurityRole> find = new Finder<Long, SecurityRole>(
            Long.class, SecurityRole.class);

    @Override
    public String getName() {
        return roleName;
    }

    public static SecurityRole findByRoleName(String roleName) {
        return find.where().eq("roleName", roleName).findUnique();
    }

    protected static void create(String roleName, RowStatus status) {
        final SecurityRole ret = new SecurityRole();
        ret.roleName = roleName;
        ret.rowStatus = status;
        ret.save();
    }

    public static void initData() {
        create(USER, RowStatus.SYSTEM);
        create(ADMINISTRATOR, RowStatus.SYSTEM);
    }
}
