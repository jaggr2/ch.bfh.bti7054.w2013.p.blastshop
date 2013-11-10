package models;

import com.avaje.ebean.annotation.EnumValue;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class AddressSettingRelation extends BackendBasicModel {

    public enum AccessType {
        @EnumValue("a")
        ACCESS,

        @EnumValue("d")
        DENY,
    }

    @ManyToOne
    public AddressSetting addressSetting;

    @ManyToOne
    public Address address;

    public Integer integerParameter;
    public String stringParameter;

    @Column(columnDefinition = "CHAR(1)")
    public AccessType accessOrDeny;
}
