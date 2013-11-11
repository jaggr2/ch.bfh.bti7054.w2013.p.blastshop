package models.api;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Copyright 2013 blastbeat syndicate gmbh
 * Author: Roger Jaggi <roger.jaggi@blastbeatsyndicate.com>
 * Date: 17.07.13
 * Time: 21:49
 */
@Entity
public class AddressI18n extends BackendBasicModel {

    @ManyToOne
    public Address address;

    @ManyToOne
    public Language language;

    public String description;
}