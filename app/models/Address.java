package models;

//import be.objectify.deadbolt.core.models.Permission;
//import be.objectify.deadbolt.core.models.Role;
//import be.objectify.deadbolt.core.models.Subject;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.annotation.EnumValue;
//import com.feth.play.module.pa.providers.password.UsernamePasswordAuthUser;
//import com.feth.play.module.pa.user.*;
//import security.BackendUsernamePasswordAuthProvider;
//import security.BackendUsernamePasswordAuthUser;

import javax.persistence.*;
import java.util.*;

/**
 * Copyright 2013 blastbeat syndicate gmbh
 * Author: Roger Jaggi <roger.jaggi@blastbeatsyndicate.com>
 * Date: 15.07.13
 * Time: 00:33
 */
@Entity
public class Address extends BackendBasicModel { //implements Subject {
    public enum Gender {
        @EnumValue("m")
        MALE_PERSON,

        @EnumValue("f")
        FEMALE_PERSON,

        @EnumValue("c")
        COMPANY, // registered Company in Handelsregister

        @EnumValue("g")
        GROUP, // Band, Verein, etc.

    }

    public Long number;
    public Date lastLogin;

    public String name;
    public String mailSalutation;
    public String nickName;
    public String middleName;
    public String preName;
    public String street;
    public String addressline2;

    @ManyToOne
    public Region city;

    @ManyToOne
    public Language preferredLanguage;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "address")
    public List<AddressContact> contacts;

    public String logo;

    public String facebookId;
    public String googleId;

    public Float latitude;
    public Float longitude;
    public Boolean informally;

    @Column(columnDefinition = "CHAR(1)")
    public Gender gender;

    @Column(columnDefinition = "TEXT")
    public String note;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "address")
    public List<AddressLinkedAccount> linkedAccounts;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "childAddress")
    public List<AddressRelation> functions;

    /***
     * Subject implementation
     * @return
     
    @Override
    public String getIdentifier()
    {
        return Long.toString(id);
    }

    @Override
    public List<? extends Role> getRoles() {
        final List<AddressFunction> roles = new ArrayList<AddressFunction>(functions.size());

        for (final AddressRelation acc : functions) {
            roles.add(acc.function);
        }

        return roles;
    }

    @Override
    public List<? extends Permission> getPermissions() {
        return null;
    }

    public static Finder<Long, Address> find = new Finder<Long, Address>(Long.class, Address.class);


    public static com.avaje.ebean.Page<Address> page(int page, int pageSize, String sortBy, String order, String filter) {
        return find.where()
                .ilike("name", "%" + filter + "%")
                .orderBy(sortBy + " " + order)
                        //.fetch("company")
                .findPagingList(pageSize)
                .getPage(page);
    }

    public void merge(final Address otherUser) {
        for (final AddressLinkedAccount acc : otherUser.linkedAccounts) {
            this.linkedAccounts.add(AddressLinkedAccount.create(acc));
        }
        // do all other merging stuff here - like resources, etc.

        // deactivate the merged user that got added to this one
        otherUser.rowStatus = RowStatus.DELETED;

        Ebean.save(Arrays.asList(new Address[]{otherUser, this}));
    }

    public static Address create(final AuthUser authUser) {
        final Address user = new Address();

        Address group = null;
        if (authUser instanceof BackendUsernamePasswordAuthUser) {
            final BackendUsernamePasswordAuthUser identity = (BackendUsernamePasswordAuthUser) authUser;

            if (identity.getName() != null) {
                group = new Address();
                group.gender = Gender.GROUP;
                group.name = identity.getName();
                group.rowStatus = RowStatus.SYSTEM;
                group.save();
            }
        }

        user.functions = new ArrayList<AddressRelation>();
        user.functions.add(AddressRelation.create(group, AddressFunction.createOrGet(AddressFunction.USER)));
        user.functions.add(AddressRelation.create(group, AddressFunction.createOrGet(AddressFunction.ADMINISTRATOR)));

        //        .findByRoleName(controllers.Application.USER_ROLE));
        // user.permissions = new ArrayList<UserPermission>();
        // user.permissions.add(UserPermission.findByValue("printers.edit"));
        user.rowStatus = RowStatus.SYSTEM;
        user.lastLogin = new Date();

        user.linkedAccounts = Collections.singletonList(AddressLinkedAccount.create(authUser, true));

        if (authUser instanceof FirstLastNameIdentity) {
            final FirstLastNameIdentity identity = (FirstLastNameIdentity) authUser;

            if (identity.getFirstName() != null) {
                user.preName = identity.getFirstName();
            }
            if (identity.getLastName() != null) {
                user.name = identity.getLastName();
            }
        }

        if (authUser instanceof ExtendedIdentity ) {
            final ExtendedIdentity  identity = (ExtendedIdentity ) authUser;

            if (identity.getFirstName() != null) {
                user.preName = identity.getFirstName();
            }
            if (identity.getLastName() != null) {
                user.name = identity.getLastName();
            }
            if (identity.getGender() != null) {

            }

        }

        if (authUser instanceof LocaleIdentity ) {
            final LocaleIdentity  identity = (LocaleIdentity ) authUser;

            if (identity.getLocale() != null) {
                Language lang = Language.findByLocale(identity.getLocale());
                if(lang == null) {
                    lang = Language.create(identity.getLocale());
                }

                user.preferredLanguage = lang;
            }
        }


        user.save();
        //user.saveManyToManyAssociations("roles");
        // user.saveManyToManyAssociations("permissions");
        return user;
    }

    public static void merge(final AuthUser oldUser, final AuthUser newUser) {
        AddressLinkedAccount.findByAuthUserIdentity(oldUser).address.merge(AddressLinkedAccount.findByAuthUserIdentity(newUser).address);
    }

    public Set<String> getProviders() {
        final Set<String> providerKeys = new HashSet<String>(linkedAccounts.size());

        for (final AddressLinkedAccount acc : linkedAccounts) {
            providerKeys.add(acc.providerKey);
        }
        return providerKeys;
    }

    public String getProfilePictureUrl() {
        for (final AddressLinkedAccount acc : linkedAccounts) {
            if( acc.useThisAsPrimaryPhoto ) {
                return acc.photoUrl;
            }
        }
        return "";
    }

    public static void addLinkedAccount(final AuthUser oldUser,final AuthUser newUser) {
        final Address u = AddressLinkedAccount.findByAuthUserIdentity(oldUser).address;
        u.linkedAccounts.add(AddressLinkedAccount.create(newUser, false));
        u.save();
    }

    public static void setLastLoginDate(final AuthUser knownUser) {
        final Address u = AddressLinkedAccount.findByAuthUserIdentity(knownUser).address;
        u.lastLogin = new Date();
        u.save();
    }

    public AddressLinkedAccount getAccountByProvider(final String providerKey) {
        return AddressLinkedAccount.findByAddress(this, providerKey);
    }

    public static AddressLinkedAccount verify(final Address unverified) {

        final AddressLinkedAccount acc = unverified.getAccountByProvider(BackendUsernamePasswordAuthProvider.getProvider().getKey());

        // You might want to wrap this into a transaction
        acc.emailValidated = true;
        acc.save();

        AddressToken.deleteByAddress(unverified, AddressToken.Type.EMAIL_VERIFICATION);

        return acc;
    }

    public void changePassword(final UsernamePasswordAuthUser authUser, final boolean create) {

        AddressLinkedAccount a = this.getAccountByProvider(authUser.getProvider());
        if (a == null) {
            if (create) {
                a = AddressLinkedAccount.create(authUser, false);
                a.address = this;
            } else {
                throw new RuntimeException(
                        "Account not enabled for password usage");
            }
        }

        a.providerUserId = authUser.getHashedPassword();
        a.save();
    }

    public void resetPassword(final UsernamePasswordAuthUser authUser, final boolean create) {
        // You might want to wrap this into a transaction
        this.changePassword(authUser, create);
        AddressToken.deleteByAddress(this, AddressToken.Type.PASSWORD_RESET);
    }

    public String getLoginEmail() {
        final AddressLinkedAccount acc = this.getAccountByProvider(BackendUsernamePasswordAuthProvider.getProvider().getKey());
        if(acc == null ) {
            return null;
        }
        return acc.email;
    }
	*/
}
