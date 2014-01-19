package models;

import com.fasterxml.jackson.annotation.JsonIgnore;

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

    @JsonIgnore
    @ManyToOne
    public Document document;

    @ManyToOne
    public ArticleOption articleOption;

    @Column(columnDefinition = "TEXT")
    public String text;

    public Integer rowOrder;

    public Double quantity;
    public Double price;

}
