package models;

import com.avaje.ebean.annotation.EnumValue;
import play.db.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * Copyright 2013 blastbeat syndicate gmbh
 * Author: Roger Jaggi <roger.jaggi@blastbeatsyndicate.com>
 * Date: 17.07.13
 * Time: 21:12
 */
@Entity
public class Unit extends Model {

    public enum UnitType {
        @EnumValue("time")
        TIME,

        @EnumValue("length")
        LENGTH,

        @EnumValue("digital")
        DIGITALSIZE,
    }

    @Id
    @Column(columnDefinition = "CHAR(4)")
    public String code;  //iso_????;

    public String abbreviation;

    @Column(columnDefinition = "CHAR(10)")
    public UnitType type;

    public String i18nShortName;
    public String i18nLongName;

    @ManyToOne
    public BackendBasicModel.RowStatus rowStatus;

    @ManyToOne
    public Unit rootUnit;

    public Float factorToRootUnit;
}
