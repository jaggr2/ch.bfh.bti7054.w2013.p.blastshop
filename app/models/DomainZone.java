package models;

import com.avaje.ebean.annotation.CreatedTimestamp;
import com.avaje.ebean.annotation.EnumValue;
import com.avaje.ebean.annotation.UpdatedTimestamp;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.Date;

@Entity
public class DomainZone extends BackendBasicModel {

    public String fullQualifiedDomainName;

    public String localDescription;

    public Integer dnsSerial;

    @Column(columnDefinition = "CHAR(1)")
    public String type;  // Inhalt?

    public Date lastAvailabilityCheck;


    //@Formats.DateTime(pattern="dd/MM/yyyy")
    //public Date dueDate = new Date();

    public static Finder<Long, DomainZone> find = new Finder<Long, DomainZone>(
            Long.class, DomainZone.class
    );

    /**
     * Return a page of News
     *
     * @param page     Page to display
     * @param pageSize Number of news per page
     * @param sortBy   News property used for sorting
     * @param order    Sort order (either or asc or desc)
     * @param filter   Filter applied on the message column
     */
    public static com.avaje.ebean.Page<DomainZone> page(int page, int pageSize, String sortBy, String order, String filter) {
        return find.where()
                .ilike("fullQualifiedDomainName", "%" + filter + "%")
                .orderBy(sortBy + " " + order)
                        //.fetch("company")
                .findPagingList(pageSize)
                .getPage(page);
    }
}
