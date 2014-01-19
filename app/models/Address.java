package models;

import be.objectify.deadbolt.core.models.Permission;
import be.objectify.deadbolt.core.models.Role;
import be.objectify.deadbolt.core.models.Subject;
import com.avaje.ebean.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.JsonNode;
import com.feth.play.module.pa.providers.password.UsernamePasswordAuthUser;
import com.feth.play.module.pa.user.*;
import security.BackendUsernamePasswordAuthProvider;

import javax.persistence.*;
import java.util.*;
import com.avaje.ebean.Ebean;
import security.BackendUsernamePasswordAuthUser;

/**
 * Copyright 2013 blastbeat syndicate gmbh
 * Author: Roger Jaggi <roger.jaggi@blastbeatsyndicate.com>
 * Date: 15.07.13
 * Time: 00:33
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class Address extends BackendBasicModel implements Subject {

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

    @JsonView(ViewPublic.class)
    public Long number;
    public Date lastLogin;

    @JsonView(ViewPublic.class)
    public String name;
    public String mailSalutation;
    public String nickName;
    //public String middleName;
    @JsonView(ViewPublic.class)
    public String preName;

    @JsonView(ViewPublic.class)
    public String addressline1;

    @JsonView(ViewPublic.class)
    public String addressline2;

    @JsonView(ViewPublic.class)
    public Date birthdate;

    @JsonView(ViewPublic.class)
    @ManyToOne
    public Region city;

    @JsonView(ViewPublic.class)
    @ManyToOne
    public Language preferredLanguage;

    @JsonView(ViewPublic.class)
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "address")
    public List<AddressContact> contacts;

    public String logo;

    public Float latitude;
    public Float longitude;

    public Boolean informally;

    public String localDescription;

    @JsonView(ViewPublic.class)
    @Column(columnDefinition = "CHAR(1)")
    public Gender gender;

    @JsonView(ViewPublic.class)
    @Column(columnDefinition = "TEXT")
    public String note;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "address")
    public List<AddressLinkedAccount> linkedAccounts;

    @JsonView(ViewPublic.class)
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parentAddress")
    public List<AddressRelation> relations;

    @ManyToMany
    public List<SecurityRole> roles;

    public static Finder<Long, Address> find = new Finder<Long, Address>(Long.class, Address.class);

    /***
     * Subject implementation
     * @return
    */
    @Override
    public String getIdentifier()
    {
        return Long.toString(id);
    }

    @Override
    public List<? extends Role> getRoles() {
        return roles;
    }

    @Override
    public List<? extends Permission> getPermissions() {
        return null;
    }


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

        if (authUser instanceof BackendUsernamePasswordAuthUser) {
            final BackendUsernamePasswordAuthUser identity = (BackendUsernamePasswordAuthUser) authUser;

            if (identity.getName() != null) {
                user.gender = Address.Gender.valueOf(identity.getGender());
                user.name = identity.getLastName();
                user.preName = identity.getFirstName();
            }
        }

        user.roles = Collections.singletonList(SecurityRole.findByRoleName(SecurityRole.USER));

        // user.permissions = new ArrayList<UserPermission>();
        // user.permissions.add(UserPermission.findByValue("printers.edit"));
        user.rowStatus = RowStatus.ACTIVE;
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
                if(identity.getGender().equals("male")) {
                    user.gender = Gender.MALE_PERSON;
                }
                if(identity.getGender().equals("female")) {
                    user.gender = Gender.FEMALE_PERSON;
                }
            }

        }

        if (authUser instanceof LocaleIdentity ) {
            final LocaleIdentity  identity = (LocaleIdentity ) authUser;

            if (identity.getLocale() != null) {
                Language lang = Language.findByLocale(identity.getLocale());
                if(lang == null) {
                    lang = Language.getOrCreate(identity.getLocale());
                }

                user.preferredLanguage = lang;
            }
        }

        user.save();
        user.saveManyToManyAssociations("roles");
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
            if( acc.useThisAsPrimaryPhoto != null && acc.useThisAsPrimaryPhoto == true ) {
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
                throw new RuntimeException("Account not enabled for password usage");
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

    public static Address getSystemAddress() {
        return Address.find.byId(1L);
    }

    public static Address getClientAddress() {
        return Address.find.byId(2L);
    }

    public static void initSystemAccounts() {
        // id: 1 - SYSTEM ACCOUNT
        Address system = new Address();
        system.rowStatus = RowStatus.SYSTEM;
        system.name = "System";
        system.gender = Gender.GROUP;
        system.save();

        // id: 2 - MANDANTEN ADRESSE
        Address clientAddress = new Address();
        clientAddress.rowStatus = RowStatus.SYSTEM;
        clientAddress.name = "Nicole Berger Spezialitäten";
        clientAddress.gender = Gender.COMPANY;
        clientAddress.save();
    }
}
