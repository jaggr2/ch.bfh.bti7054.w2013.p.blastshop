package models.api;

import com.avaje.ebean.annotation.EnumValue;

import javax.persistence.*;

@Entity
public class DomainZoneServer extends BackendBasicModel {
    public enum ServerType {
        @EnumValue("m")
        MASTER_DNS_SERVER,

        @EnumValue("s")
        SECONDARY_DNS_SERVER,
    }

    @ManyToOne
    public DomainZone domainZone;

    @ManyToOne
    public Server targetServer;

    @Column(columnDefinition = "CHAR(1)")
    public ServerType serverType;
}
