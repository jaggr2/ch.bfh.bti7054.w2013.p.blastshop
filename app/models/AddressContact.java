package models;

import com.avaje.ebean.annotation.EnumValue;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
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
        HOME_DIRECT,

        @EnumValue("i")
        HOME_GENERAL,

        @EnumValue("o")
        OFFICE_DIRECT,

        @EnumValue("p")
        OFFICE_GENERAL,

        @EnumValue("m")
        MOBILE,
    }

    @ManyToOne
    public Address address;

    @Column(columnDefinition = "CHAR(1)")
    public ContactType type;

    @Column(columnDefinition = "CHAR(1)")
    public TypeDescription typedescription;

    public String value;
}
