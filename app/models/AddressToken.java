package models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.QueryIterator;
import com.avaje.ebean.annotation.EnumValue;
import play.data.format.Formats;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.Date;
import java.util.UUID;

/**
 * Copyright 2013 blastbeat syndicate gmbh
 * Author: Roger Jaggi <roger.jaggi@blastbeatsyndicate.com>
 * Date: 14.07.13
 * Time: 22:50
 */
@Entity
public class AddressToken extends BackendBasicModel {

    public enum Type {
        @EnumValue("EV")
        EMAIL_VERIFICATION,

        @EnumValue("PR")
        PASSWORD_RESET
    }

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * Verification time frame (until the user clicks on the link in the email)
     * in seconds
     * Defaults to one week
     */
    private final static long VERIFICATION_TIME = 7 * 24 * 3600;

    @Column(unique = true)
    public String token;

    @ManyToOne
    public Address address;

    public Type type;

    @Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date expires;

    public static final Finder<Long, AddressToken> find = new Finder<Long, AddressToken>(
            Long.class, AddressToken.class);

    public static AddressToken findByToken(final String token, final Type type) {
        return find.where().eq("token", token).eq("type", type).findUnique();
    }

    public static void deleteByAddress(final Address s, final Type type) {
        QueryIterator<AddressToken> iterator = find.where().eq("address.id", s.id).eq("type", type).findIterate();
        Ebean.delete(iterator);
        iterator.close();
    }

    public boolean isValid() {
        return this.expires.after(new Date());
    }

    private static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public static AddressToken create(final Type type, final Address address) {
        if( type == null || address == null) {
            return null;
        }

        final AddressToken ua = new AddressToken();
        ua.address = address;
        ua.token = generateToken();
        ua.type = type;
        ua.expires = new Date((new Date()).getTime() + VERIFICATION_TIME * 1000);
        ua.save();
        return ua;
    }
}
