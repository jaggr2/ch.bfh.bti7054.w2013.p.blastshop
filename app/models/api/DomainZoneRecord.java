package models.api;

import com.avaje.ebean.annotation.EnumValue;

import javax.persistence.*;

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
