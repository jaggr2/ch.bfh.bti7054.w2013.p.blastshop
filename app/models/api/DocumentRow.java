package models.api;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Copyright 2013 blastbeat syndicate gmbh
 * Author: Roger Jaggi <roger.jaggi@blastbeatsyndicate.com>
 * Date: 18.07.13
 * Time: 00:14
 */
@Entity
public class DocumentRow extends BackendBasicModel {

    @ManyToOne
    public Document document;

    @ManyToOne
    public Article article;

    @Column(columnDefinition = "TEXT")
    public String text;

    public Float quantity;
    public Float price;

}
