package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.Date;

/**
 * Copyright 2013 blastbeat syndicate gmbh
 * Author: Roger Jaggi <roger.jaggi@blastbeatsyndicate.com>
 * Date: 18.07.13
 * Time: 00:13
 */
//@Table(uniqueConstraints = { @UniqueConstraint( columnNames = { "trunkNumber", "documentNumber" } ) }  )
@Entity
public class Document extends BackendBasicModel {

    public Long trunkNumber;
    public Long documentNumber;

    @ManyToOne
    public Address clientAddress;

    @ManyToOne
    public Address invoiceAddress;

    @ManyToOne
    public Address deliveryAddress;

    public Date invoiceDate;

    public Date deliveryDate;


}
