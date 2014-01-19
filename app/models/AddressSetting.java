package models;

import com.avaje.ebean.annotation.EnumValue;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * Copyright 2013 blastbeat syndicate gmbh
 * Author: Roger Jaggi <roger.jaggi@blastbeatsyndicate.com>
 * Date: 15.07.13
 * Time: 00:04
 */
@Entity
public class AddressSetting extends BackendBasicModel {
    public enum ParameterType {
        @EnumValue("I")
        INTEGER,

        @EnumValue("S")
        STRING,
    }

    @Column(unique = true)
    public String uniqueName;

    public ParameterType parameterType;
}
