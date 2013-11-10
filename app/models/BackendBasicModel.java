package models;

import com.avaje.ebean.annotation.CreatedTimestamp;
import com.avaje.ebean.annotation.EnumValue;
import com.avaje.ebean.annotation.UpdatedTimestamp;
import play.data.format.Formats;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.Date;

/**
 * Copyright 2013 blastbeat syndicate gmbh
 * Author: Roger Jaggi <roger.jaggi@blastbeatsyndicate.com>
 * Date: 15.07.13
 * Time: 01:03
 */
@MappedSuperclass
public abstract class BackendBasicModel extends Model {

    public enum RowStatus {
        @EnumValue("a")
        ACTIVE,

        @EnumValue("i")
        INACTIVE,

        @EnumValue("b")
        BLOCKED,

        @EnumValue("s")
        SYSTEM, // no modifies through users

        @EnumValue("d")
        DELETED, // trash bin

        @EnumValue("v")
        TO_BE_VALIDATED, // need user validation

        @EnumValue("u")
        DO_NOT_USE_ANYMORE, // will be deleted soon
    }

    @Id
    @GeneratedValue
    public Long id;

    public RowStatus rowStatus;

    // Auskommentiert, weil sonst der incremental-update nicht mehr funktioniert und jedes Feld einzeln rübergeschrieben werden muss
    // Wenn es eh gemacht werden muss, kann man es wieder aktivieren
    // Fehler macht sich bemerkbar, indem bei der update(id) prozedur eine NullPointerException kommt
    // http://play.lighthouseapp.com/projects/82401/tickets/207-ebean-saving-detached-entity-directly-from-form-causes-problems
    //@Version
    //public Long version;

    @ManyToOne
    public Address clientAddress;

    @CreatedTimestamp
    @Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date createdOn;

    @UpdatedTimestamp
    @Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date updatedOn;

    @ManyToOne
    public Address createdBy;

    @ManyToOne
    public Address updatedBy;

    @Transient
    public Integer rowPosition;

}
