package models;

import actions.UserAddress;
import com.avaje.ebean.annotation.CreatedTimestamp;
import com.avaje.ebean.annotation.EnumValue;
import com.avaje.ebean.annotation.UpdatedTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import controllers.FrontendBasicController;
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

    public static class ViewPublic { }
    public static class ViewPublicExtended extends ViewPublic { }
    public static class ViewInternal extends ViewPublic { }

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

    @JsonView(ViewPublic.class)
    @Id
    @GeneratedValue
    public Long id;

    @JsonView(ViewPublic.class)
    public RowStatus rowStatus;

    @CreatedTimestamp
    @Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date createdOn;

    @UpdatedTimestamp
    @Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date updatedOn;

    @JsonIgnore
    @ManyToOne
    public Address createdBy;

    @JsonIgnore
    @ManyToOne
    public Address updatedBy;

    @Override
    public void save() {
        if (this.id == null) {
            this.createdBy = (FrontendBasicController.getLocalUser() != null ? FrontendBasicController.getLocalUser() : Address.getSystemAddress());
        }
        this.updatedBy = (FrontendBasicController.getLocalUser() != null ? FrontendBasicController.getLocalUser() : Address.getSystemAddress());

        super.save();
    }

    @Override
    public void update() {
        if (this.id == null) {
            this.createdBy = (FrontendBasicController.getLocalUser() != null ? FrontendBasicController.getLocalUser() : Address.getSystemAddress());
        }
        this.updatedBy = (FrontendBasicController.getLocalUser() != null ? FrontendBasicController.getLocalUser() : Address.getSystemAddress());

        super.update();
    }

    @Override
    public void update(Object o) {
        if (this.id == null) {
            this.createdBy = (FrontendBasicController.getLocalUser() != null ? FrontendBasicController.getLocalUser() : Address.getSystemAddress());
        }
        this.updatedBy = (FrontendBasicController.getLocalUser() != null ? FrontendBasicController.getLocalUser() : Address.getSystemAddress());

        super.update(o);
    }
}
