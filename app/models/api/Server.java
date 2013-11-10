package models.api;

import javax.persistence.Entity;

@Entity
public class Server extends BackendBasicModel {

    public String fullQualifiedDomainName;

}
