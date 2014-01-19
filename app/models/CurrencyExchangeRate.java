package models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.Date;

/**
 * Copyright 2013 blastbeat syndicate gmbh
 * Author: Roger Jaggi <roger.jaggi@blastbeatsyndicate.com>
 * Date: 17.07.13
 * Time: 21:32
 */
@Entity
public class CurrencyExchangeRate extends BackendBasicModel {

    public Date validFrom;

    @JsonIgnore
    @ManyToOne
    public Currency from;

    @ManyToOne
    public Currency to;

    public Float exchangerate;
}
