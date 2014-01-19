package models;

import com.avaje.ebean.Ebean;
import org.apache.xerces.dom.DocumentImpl;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Author: Roger Jaggi <roger.jaggi@gmail.com>
 * Date: 18.07.13
 * Time: 00:13
 */
@Entity
public class DocumentType extends BackendBasicModel {


    @OneToMany(cascade = CascadeType.ALL, mappedBy = "documentType")
    public List<DocumentTypeLanguage> languages;

    public static final Finder<Long, DocumentType> find = new Finder<Long, DocumentType>(Long.class, DocumentType.class);

    public static DocumentType create(RowStatus rowStatus) {
        DocumentType ret = new DocumentType();
        ret.rowStatus = rowStatus;
        return ret;
    };

    public void setName(Language language, String name) {

        if(language == null) {
            throw new IllegalArgumentException("Parameter language is null!");
        }

        if(this.languages == null) {
            this.languages = new ArrayList<>();
        }

        for(DocumentTypeLanguage item : this.languages) {
            if(item.language.equals(language)) {
                item.name = name;
                return;
            }
        }

        DocumentTypeLanguage newItem = new DocumentTypeLanguage();
        newItem.language = language;
        newItem.name = name;
        newItem.documentType = this;

        this.languages.add(newItem);
    }

    public String getName(Language language) {
        for(DocumentTypeLanguage item : this.languages) {
            if(item.language.equals(language)) {
                return item.name;
            }
        }

        return null;
    }

    public static void initData() {
        List<DocumentType> collectionList = new ArrayList<>();

        DocumentType warenkorb = DocumentType.create(RowStatus.SYSTEM);
        warenkorb.setName(Language.getGerman(), "Warenkorb");
        warenkorb.setName(Language.getEnglish(), "Shopping cart");
        collectionList.add(warenkorb);

        DocumentType bestellung = DocumentType.create(RowStatus.SYSTEM);
        bestellung.setName(Language.getGerman(), "Kundenbestellung");
        bestellung.setName(Language.getEnglish(), "Client order");
        collectionList.add(bestellung);

        Ebean.save(collectionList);
    }

    private static DocumentType shoppingCartCache = null;

    public static DocumentType getShoppingCart() {
        if(shoppingCartCache == null) {
            shoppingCartCache = DocumentType.find.byId(1L);
        }
        return shoppingCartCache;
    }

    private static DocumentType clientOrderCache = null;

    public static DocumentType getClientOrder() {
        if(clientOrderCache == null) {
            clientOrderCache = DocumentType.find.byId(2L);
        }
        return clientOrderCache;
    }
}
