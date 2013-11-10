package models.api;

import com.avaje.ebean.annotation.EnumValue;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.Date;

/**
 * Copyright 2013 blastbeat syndicate gmbh
 * Author: Roger Jaggi <roger.jaggi@blastbeatsyndicate.com>
 * Date: 17.07.13
 * Time: 22:21
 */
@Entity
public class Url extends BackendBasicModel {

    public enum Protocols {
        @EnumValue("http")
        HTTP,

        @EnumValue("https")
        HTTPS,

        @EnumValue("ftp")
        FTP,

        @EnumValue("smb")
        SMB,

        @EnumValue("ssh")
        SSH,

        @EnumValue("other")
        OTHER,
    }

    public Protocols protocol;

    @Column(columnDefinition = "varchar(500)")
    public String link;

    @ManyToOne
    public Address linkowner;

    public Date lastcheck;
}
