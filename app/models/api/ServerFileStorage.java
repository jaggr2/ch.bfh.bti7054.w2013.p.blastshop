package models.api;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Copyright 2013 blastbeat syndicate gmbh
 * Author: Roger Jaggi <roger.jaggi@blastbeatsyndicate.com>
 * Date: 18.07.13
 * Time: 00:46
 */
@Entity
public class ServerFileStorage extends BackendBasicModel {

    @ManyToOne
    public ServerFilePhysical serverFilePhysical;

    @ManyToOne
    public ServerStorageSystem serverFileStorageSystem;

    @ManyToOne
    public Url fileUrl;
}
