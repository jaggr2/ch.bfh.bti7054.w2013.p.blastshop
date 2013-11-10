package models;

import com.avaje.ebean.annotation.EnumValue;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class Server extends BackendBasicModel {

    public String fullQualifiedDomainName;

}
