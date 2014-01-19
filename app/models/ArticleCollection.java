package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import play.i18n.Lang;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Copyright 2013 blastbeat syndicate gmbh
 * Author: Roger Jaggi <roger.jaggi@blastbeatsyndicate.com>
 * Date: 15.07.13
 * Time: 00:04
 */
@Entity
public class ArticleCollection extends BackendBasicModel {

    @ManyToOne
    public ArticleCollection parentCollection;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "articleCollection")
    public List<ArticleCollectionLanguage> languages;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parentCollection")
    public List<ArticleCollection> childCollections;


    public Article getHeadingArticle() {
        return null;
    }

    @JsonIgnore
    public List<Article> getFeaturedArticles() {
        return Article.find.fetch("articleCollections").where().eq("articleCollections", this).findList();
    }


    public void setName(Language language, String name) {

        if(language == null) {
            throw new IllegalArgumentException("Parameter language is null!");
        }

        if(this.languages == null) {
            this.languages = new ArrayList<>();
        }

        for(ArticleCollectionLanguage item : this.languages) {
            if(item.language.equals(language)) {
                item.name = name;
                return;
            }
        }

        ArticleCollectionLanguage newItem = new ArticleCollectionLanguage();
        newItem.language = language;
        newItem.name = name;
        newItem.articleCollection = this;

        this.languages.add(newItem);
    }

    public String getName(Language language) {
        for(ArticleCollectionLanguage item : this.languages) {
            if(item.language.equals(language)) {
                return item.name;
            }
        }

        return null;
    }

    public static Finder<Long, ArticleCollection> find = new Finder<Long, ArticleCollection>(Long.class, ArticleCollection.class);

    public static ArticleCollection create(ArticleCollection parentCollection, Language language, String name) {
        ArticleCollection newItem = new ArticleCollection();
        newItem.rowStatus = RowStatus.ACTIVE;
        newItem.setName(language, name);
        newItem.parentCollection = parentCollection;

        return newItem;
    }
}
