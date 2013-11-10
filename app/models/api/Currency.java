package models.api;

import play.db.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * Copyright 2013 blastbeat syndicate gmbh
 * Author: Roger Jaggi
 * Date: 13.07.13
 * Time: 18:29
 */
@Entity
public class Currency extends Model {
    @Id
    @Column(columnDefinition = "CHAR(4)")
    public String code;  //iso_????;

    public String abbreviation; // for example CHF

    public String i18nShortName;
    public String i18nLongName;

    public Float globalExchangeRateToCHF;

    @ManyToOne
    public BackendBasicModel.RowStatus rowStatus;
}
