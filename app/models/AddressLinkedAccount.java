package models;

import com.avaje.ebean.annotation.EnumValue;
import play.data.validation.Constraints;
//import com.feth.play.module.pa.providers.password.UsernamePasswordAuthUser;
//import com.feth.play.module.pa.user.*;
//import security.BackendUsernamePasswordAuthProvider;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Copyright 2013 blastbeat syndicate gmbh
 * Author: Roger Jaggi <roger.jaggi@blastbeatsyndicate.com>
 * Date: 17.07.13
 * Time: 20:23
 */
@Entity
public class AddressLinkedAccount extends BackendBasicModel {

    public String providerUserId;
    public String providerKey;

    public String email;
    public Boolean emailValidated;

    public String photoUrl;

    @Constraints.Required
    public Boolean useThisAsPrimaryPhoto;

    public String profileUrl;

    @Constraints.Required
    @ManyToOne
    public Address address;

    public static final Finder<Long, AddressLinkedAccount> find = new Finder<Long, AddressLinkedAccount>(Long.class, AddressLinkedAccount.class);

    public static AddressLinkedAccount findByAddress(final Address user, String key) {
        return find.where().eq("address", user).eq("providerKey", key).findUnique();
    }

    public static AddressLinkedAccount findByProviderKey(String userId, String key) {
        return find.where().eq("providerUserId", userId).eq("providerKey", key).eq("address.rowStatus", RowStatus.SYSTEM).findUnique();
    }

    public static AddressLinkedAccount findByEmail(final String email, String key) {
        return find.where().eq("email", email).eq("providerKey", key).eq("address.rowStatus", RowStatus.SYSTEM).findUnique();
    }

	/*
    public static AddressLinkedAccount findByAuthUserIdentity(final AuthUserIdentity identity) {
        if (identity == null) {
            return null;
        }
        if (identity instanceof UsernamePasswordAuthUser) {

            final UsernamePasswordAuthUser authUser = (UsernamePasswordAuthUser)identity;
            return findByEmail(authUser.getEmail(), authUser.getProvider());

        } else {
            return findByProviderKey(identity.getId(), identity.getProvider());
        }
    }

    public static AddressLinkedAccount create(final AuthUser authUser, boolean isFirstLink) {
        final AddressLinkedAccount ret = new AddressLinkedAccount();

        ret.providerUserId = authUser.getId();
        ret.providerKey = authUser.getProvider();
        ret.useThisAsPrimaryPhoto = false;

        if (authUser instanceof UsernamePasswordAuthUser) {

            final UsernamePasswordAuthUser passwordAuthUser = (UsernamePasswordAuthUser)authUser;

            ret.email = passwordAuthUser.getEmail();
            ret.emailValidated = false;
        }

        if (authUser instanceof EmailIdentity) {
            final EmailIdentity identity = (EmailIdentity) authUser;
            // Remember, even when getting them from FB & Co., emails should be
            // verified within the application as a security breach there might
            // break your security as well!
            ret.email = identity.getEmail();
            ret.emailValidated = false;
        }

        if (authUser instanceof PicturedIdentity) {
            final PicturedIdentity  identity = (PicturedIdentity ) authUser;

            if (identity.getPicture() != null) {
                ret.photoUrl = identity.getPicture();
                ret.useThisAsPrimaryPhoto = isFirstLink;
            }
        }

        if (authUser instanceof ProfiledIdentity) {
            final ProfiledIdentity  identity = (ProfiledIdentity ) authUser;

            if (identity.getProfileLink() != null) {
                ret.profileUrl = identity.getProfileLink();
            }
        }

        return ret;
    }

    public static AddressLinkedAccount create(final AddressLinkedAccount acc) {
        final AddressLinkedAccount ret = new AddressLinkedAccount();
        ret.providerKey = acc.providerKey;
        ret.providerUserId = acc.providerUserId;

        ret.email = acc.email;
        ret.emailValidated = acc.emailValidated;
        ret.photoUrl = acc.photoUrl;
        ret.useThisAsPrimaryPhoto = acc.useThisAsPrimaryPhoto;
        ret.profileUrl = acc.profileUrl;

        return ret;
    }
	*/
}
