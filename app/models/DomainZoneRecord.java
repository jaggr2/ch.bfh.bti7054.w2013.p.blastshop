package models;

import com.avaje.ebean.annotation.CreatedTimestamp;
import com.avaje.ebean.annotation.EnumValue;
import com.avaje.ebean.annotation.UpdatedTimestamp;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.Date;

@Entity
public class DomainZoneRecord extends BackendBasicModel {
    public enum RecordType {
        @EnumValue("a")
        A,

        @EnumValue("aaaa")
        AAAA,

        @EnumValue("mx")
        MX,

        @EnumValue("ns")
        NS,

        @EnumValue("srv")
        SRV,

        @EnumValue("txt")
        TXT,
    }

    @ManyToOne
    public DomainZone domainZone;

    public RecordType recordType;

    public String name;

    public Integer priority;

    public String value;
}
