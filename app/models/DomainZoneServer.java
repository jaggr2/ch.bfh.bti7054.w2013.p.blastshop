package models;

import com.avaje.ebean.annotation.CreatedTimestamp;
import com.avaje.ebean.annotation.EnumValue;
import com.avaje.ebean.annotation.UpdatedTimestamp;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.Date;

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
