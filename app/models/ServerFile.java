package models;

import com.avaje.ebean.annotation.EnumValue;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;


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
