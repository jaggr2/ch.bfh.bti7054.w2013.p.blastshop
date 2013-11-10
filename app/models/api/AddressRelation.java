package models.api;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Copyright 2013 blastbeat syndicate gmbh
 * Author: Roger Jaggi <roger.jaggi@blastbeatsyndicate.com>
 * Date: 15.07.13
 * Time: 01:22
 */
@Entity
public class AddressRelation extends BackendBasicModel {

    @ManyToOne
    public Address parentAddress;

    @ManyToOne
    public Address childAddress;

    @ManyToOne
    public AddressFunction function;

    public static AddressRelation create(Address parentAddress, Address childAddress, AddressFunction function) {
        final AddressRelation ret = new AddressRelation();
        ret.parentAddress = parentAddress;
        ret.childAddress = childAddress;
        ret.function = function;
        ret.save();

        return ret;
    }

    public static AddressRelation create(Address parentAddress, AddressFunction function) {
        final AddressRelation ret = new AddressRelation();
        ret.parentAddress = parentAddress;
        ret.function = function;

        return ret;
    }
}
