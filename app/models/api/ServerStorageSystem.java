package models.api;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Copyright 2013 blastbeat syndicate gmbh
 * Author: Roger Jaggi <roger.jaggi@blastbeatsyndicate.com>
 * Date: 18.07.13
 * Time: 00:50
 */
@Entity
public class ServerStorageSystem extends BackendBasicModel {

    public String name;

    @ManyToOne
    public Url baseUrl;

}
