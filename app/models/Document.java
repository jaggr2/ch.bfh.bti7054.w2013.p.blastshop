package models;

import com.avaje.ebean.annotation.EnumValue;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.Date;
import java.util.List;

/**
 * Copyright 2013 blastbeat syndicate gmbh
 * Author: Roger Jaggi <roger.jaggi@blastbeatsyndicate.com>
 * Date: 18.07.13
 * Time: 00:13
 */
//@Table(uniqueConstraints = { @UniqueConstraint( columnNames = { "trunkNumber", "documentNumber" } ) }  )
@Entity
public class Document extends BackendBasicModel {

    @ManyToOne
    public DocumentType documentType;

    public Long documentNumber;

    @ManyToOne
    public Address clientAddress;

    @ManyToOne
    public Address invoiceAddress;

    @ManyToOne
    public Address deliveryAddress;

    public Date invoiceDate;

    public Date deliveryDate;

    public String title;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "document")
    public List<DocumentRow> rows;

    public static Finder<Long, Document> find = new Finder<Long, Document>(Long.class, Document.class);

    public Double calculateTotal() {
        Double total = 0.0;
        for(DocumentRow row : this.rows) {
            total += row.price * row.quantity;
        }

        return total;
    }
}
