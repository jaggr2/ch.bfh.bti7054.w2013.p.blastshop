package models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Copyright 2013 blastbeat syndicate gmbh
 * Author: Roger Jaggi <roger.jaggi@blastbeatsyndicate.com>
 * Date: 17.07.13
 * Time: 21:49
 */
@Entity
public class AddressFunctionLanguage extends BackendBasicModel {

    @JsonIgnore
    @ManyToOne
    public AddressFunction addressFunction;

    @ManyToOne
    public Language language;

    public String name;
}
