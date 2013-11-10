package models.api;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Copyright 2013 blastbeat syndicate gmbh
 * Author: Roger Jaggi <roger.jaggi@blastbeatsyndicate.com>
 * Date: 17.07.13
 * Time: 20:55
 */
@Entity
public class Article extends BackendBasicModel {

    public String number;

    public String i18nDescription;

    @ManyToOne
    public Unit unit;

    @ManyToOne
    public Unit weight;

}
