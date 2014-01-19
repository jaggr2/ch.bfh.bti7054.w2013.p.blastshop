package models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Copyright 2013 blastbeat syndicate gmbh
 * Author: Roger Jaggi <roger.jaggi@blastbeatsyndicate.com>
 * Date: 17.07.13
 * Time: 20:55
 */
@Entity
public class ArticleOption extends BackendBasicModel {

    public String number;

    @JsonIgnore
    @ManyToOne
    public Article article;

    public Integer optionOrder;

    @ManyToOne
    public Unit unit;

    @ManyToOne
    public Unit weight;

    @ManyToOne
    public Double sellPrice;

    @ManyToOne
    public Currency sellPriceCurrency;

    @ManyToOne
    public LocalFile primaryPicture;

    @ManyToMany
    public List<LocalFile> localFiles;

    public static Finder<Long, ArticleOption> find = new Finder<Long, ArticleOption>(Long.class, ArticleOption.class);

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "articleOption")
    public List<ArticleOptionLanguage> languages;

    public void setTitle(Language language, String name) {

        if(language == null) {
            throw new IllegalArgumentException("Parameter language is null!");
        }

        if(this.languages == null) {
            this.languages = new ArrayList<>();
        }

        for(ArticleOptionLanguage item : this.languages) {
            if(item.language.equals(language)) {
                item.title = name;
                return;
            }
        }

        ArticleOptionLanguage newItem = new ArticleOptionLanguage();
        newItem.language = language;
        newItem.title = name;
        newItem.articleOption = this;

        this.languages.add(newItem);
    }

    public String getTitle(Language language) {
        for(ArticleOptionLanguage item : this.languages) {
            if(item.language.equals(language)) {
                return item.title;
            }
        }

        return null;
    }

    public void setDescription(Language language, String name) {

        if(language == null) {
            throw new IllegalArgumentException("Parameter language is null!");
        }

        if(this.languages == null) {
            this.languages = new ArrayList<>();
        }

        for(ArticleOptionLanguage item : this.languages) {
            if(item.language.equals(language)) {
                item.description = name;
                return;
            }
        }

        ArticleOptionLanguage newItem = new ArticleOptionLanguage();
        newItem.language = language;
        newItem.description = name;
        newItem.articleOption = this;

        this.languages.add(newItem);
    }


    public String getDescription(Language language) {
        for(ArticleOptionLanguage item : this.languages) {
            if(item.language.equals(language)) {
                return item.description;
            }
        }

        return null;
    }


}
