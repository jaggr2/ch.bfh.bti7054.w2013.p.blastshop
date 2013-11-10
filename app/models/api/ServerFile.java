package models.api;

import javax.persistence.Entity;


/**
 * Copyright 2013 blastbeat syndicate gmbh
 * Author: Roger Jaggi <roger.jaggi@blastbeatsyndicate.com>
 * Date: 18.07.13
 * Time: 00:27
 */
@Entity
public class ServerFile extends BackendBasicModel {

    public String i18nFilename;

    public String fileMetaType;

    public Integer fileAttributes;

}
