package models;

import com.fasterxml.jackson.annotation.JsonIgnore;

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

    @JsonIgnore
    @ManyToOne
    public Address parentAddress;

    @ManyToOne
    public Address childAddress;

    @ManyToOne
    public AddressFunction addressFunction;


    public static AddressRelation create(Address parentAddress, Address childAddress, AddressFunction function) {
        final AddressRelation ret = new AddressRelation();
        ret.parentAddress = parentAddress;
        ret.childAddress = childAddress;
        ret.addressFunction = function;
        ret.save();

        return ret;
    }

    public static AddressRelation create(Address parentAddress, AddressFunction function) {
        final AddressRelation ret = new AddressRelation();
        ret.parentAddress = parentAddress;
        ret.addressFunction = function;

        return ret;
    }

}
