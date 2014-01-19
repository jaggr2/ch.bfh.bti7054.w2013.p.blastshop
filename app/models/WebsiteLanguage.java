package models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Copyright 2013 blastbeat syndicate gmbh
 * Author: Roger Jaggi <roger.jaggi@blastbeatsyndicate.com>
 * Date: 17.07.13
 * Time: 21:49
 */
@Entity
public class WebsiteLanguage extends BackendBasicModel {

    @JsonIgnore
    @ManyToOne
    public Website website;

    @ManyToOne
    public Language language;

    public String title;

    @Column(columnDefinition = "TEXT")
    public String content;
}
