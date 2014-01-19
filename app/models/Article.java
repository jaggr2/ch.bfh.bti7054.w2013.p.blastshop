package models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import java.util.List;

/**
 * Copyright 2013 blastbeat syndicate gmbh
 * Author: Roger Jaggi <roger.jaggi@blastbeatsyndicate.com>
 * Date: 17.07.13
 * Time: 20:55
 */
@Entity
public class Article extends BackendBasicModel {

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "article")
    public List<ArticleOption> options;

    @ManyToMany
    public List<ArticleCollection> articleCollections;

    public static Finder<Long, Article> find = new Finder<Long, Article>(Long.class, Article.class);

    public ArticleOption getPrimaryOption() {
        return (options.size() > 0 ? options.get(0) : null);
    }

    /**
     * get or creates an new ArticleOption AND add it to the article
     * @param optionId NULL to create one or a valid optionId
     * @return NULL if not found or the object
     */
    public ArticleOption getOrCreateOption(Long optionId) {
        if(optionId != null && optionId > 0 && this.id != null && this.id > 0) { // in new articles allways add options (copy feature)
            for(ArticleOption item : this.options) {
                if(item.id.equals(optionId)) {
                    return item;
                }
            }
        }
        else {
            ArticleOption newItem = new ArticleOption();
            this.options.add(newItem);
            return newItem;
        }

        return null;
    }

    /**
     * get or creates an new Article
     * @param id NULL to create one or a valid id
     * @return NULL if not found or the object
     */
    public static Article getOrCreate(Long id) {
        if(id != null && id > 0) {
            return Article.find.byId(id);
        }
        else {
            return new Article();
        }
    }
}
