package models;

import com.avaje.ebean.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Copyright 2013 blastbeat syndicate gmbh
 * Author: Roger Jaggi <roger.jaggi@blastbeatsyndicate.com>
 * Date: 15.07.13
 * Time: 01:16
 */
@Entity
public class AddressContact extends BackendBasicModel {
    public enum ContactType {
        @EnumValue("e")
        EMAIL,

        @EnumValue("f")
        FAX,

        @EnumValue("p")
        PHONE,

        @EnumValue("w")
        WEBSITE,
    }

    public enum TypeDescription {
        @EnumValue("h")
        HOME,

        @EnumValue("o")
        OFFICE,

        @EnumValue("m")
        MOBILE,
    }

    @JsonIgnore
    @ManyToOne
    public Address address;

    @Column(columnDefinition = "CHAR(1)")
    public ContactType contactType;

    /* @Column(columnDefinition = "CHAR(1)")
    public TypeDescription typedescription; */

    public String value;
}
