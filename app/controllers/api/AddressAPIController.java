package controllers.api;

import com.avaje.ebean.Expr;
import com.avaje.ebean.ExpressionList;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import models.*;
import play.mvc.Result;
import play.mvc.Controller;
import utils.ISO8601DateParser;
import utils.TypeAheadDatum;

import java.text.ParseException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Roger
 * Date: 10.11.13
 * Time: 03:22
 */
public class AddressAPIController extends Controller {

    public static Result list() throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();

        //objectMapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false);

        //return ok(objectMapper.writerWithView(BackendBasicModel.ViewPublic.class).writeValueAsString(Address.find.where().ne("rowStatus", "d").ne("rowStatus", "s").findList()));
        return ok(objectMapper.writeValueAsString(Address.find.where().ne("rowStatus", "d").ne("rowStatus", "s").findList()));
    }

    public static Result newest() throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();

        return ok(objectMapper.writeValueAsString(Address.find.where().ne("rowStatus", "d").ne("rowStatus", "s").setMaxRows(5).orderBy("createdOn DESC").findList()));
    }

    public static Result lookup() throws JsonProcessingException {

        String querystring = request().getQueryString("q");
        String limit = request().getQueryString("limit");

        Integer rowcount = (limit != null && !limit.trim().isEmpty() ? Integer.parseInt(limit) : 100);

        ExpressionList<Address> searchexpression = Address.find.where().or(Expr.ilike("name", "%" + querystring + "%"), Expr.ilike("preName", "%" + querystring + "%")).ne("rowStatus", "s").ne("rowStatus","d");

        List<Address> addressList = ( rowcount != null && rowcount > 0 ? searchexpression.setMaxRows(rowcount).findList() : searchexpression.findList() );

        List<TypeAheadDatum> nodeList = new ArrayList<TypeAheadDatum>();

        for(Address address : addressList) {

            TypeAheadDatum datum = new TypeAheadDatum();
            datum.value = (address.preName != null ? address.preName + " " : "") + address.name;
            datum.tokens.add(address.preName);
            datum.tokens.add(address.name);
            datum.id = address.id;
            datum.dataobject = address;

            nodeList.add(datum);

        }

        ObjectMapper objectMapper = new ObjectMapper();

        return ok(objectMapper.writeValueAsString(nodeList));
    }

    public static Result get(Long id) throws JsonProcessingException {

        if(id == null || id <= 0) {
            return badRequest();
        }

        Address addressToLoad = Address.find.byId(id);
        if(addressToLoad == null) {
            return notFound("Address with ID " + id.toString() + " was not found!");
        }

        ObjectMapper objectMapper = new ObjectMapper();

        return ok(objectMapper.writeValueAsString(addressToLoad));

    }

    public static Result save() throws JsonProcessingException, ParseException {

        if(request().body() == null) {
            return badRequest("body ist null");
        }

        if( request().body().asJson() == null) {
            return badRequest("body json ist null");
        }

        JsonNode body = request().body().asJson();

        if(body.isObject()) {


            Long id = ( body.get("id") == null ? null : body.get("id").asLong());
            Address addressToSave = null;

            if(id != null && id > 0) {
                addressToSave = Address.find.byId(id);
                if(addressToSave == null) {
                    return notFound("Address with ID " + id.toString() + " was not found!");
                }
            }
            else {
                addressToSave = new Address();
                addressToSave.rowStatus = BackendBasicModel.RowStatus.ACTIVE;
            }


            addressToSave.name = body.get("name").asText();

            if( body.hasNonNull("gender") ) { addressToSave.gender = Address.Gender.valueOf( body.get("gender").asText() ); }

            if( body.hasNonNull("preName") ) { addressToSave.preName = body.get("preName").asText(); }

            if( body.hasNonNull("addressline1") ) { addressToSave.addressline1 = body.get("addressline1").asText(); }
            if( body.hasNonNull("addressline2") ) { addressToSave.addressline2 = body.get("addressline2").asText(); }


            if( body.hasNonNull("birthdate")) {
                ISO8601DateParser df = new ISO8601DateParser();

                addressToSave.birthdate = df.parse(body.get("birthdate").asText());
            }

            if( body.hasNonNull("cityId")) {

                Region city = Region.find.byId(body.get("cityId").asLong());

                addressToSave.city = city;
            }


            for(AddressContact contact : addressToSave.contacts) {
                contact.delete();
            }

            if(body.get("contacts").size() > 0) {


                addressToSave.contacts.clear();

                for(JsonNode contact : body.get("contacts")) {


                    AddressContact addressContact = new AddressContact();

                    //EnumFormatter<AddressContact.ContactType> formatter = new EnumFormatter<>(AddressContact.ContactType.class);
                    //formatter.parse(contact.get("contactType").asText(), null);

                    addressContact.contactType = AddressContact.ContactType.valueOf( contact.get("contactType").asText() );


                    addressContact.value = contact.get("value").asText();

                    addressToSave.contacts.add(addressContact);


                }

            }

            for(AddressRelation relation : addressToSave.relations) {
                relation.delete();
            }

            if(body.get("relations").size() > 0) {


                addressToSave.relations.clear();

                for(JsonNode relation : body.get("relations")) {


                    AddressRelation addressRelation = new AddressRelation();

                    AddressFunction function = AddressFunction.find.byId(relation.get("addressFunctionId").asLong());
                    Address otherAddress = Address.find.byId(relation.get("childAddressId").asLong());

                    if(function == null || otherAddress == null) {
                        return badRequest("function or childAddress in AddressRelation is null");
                    }

                    addressRelation.addressFunction = function;
                    addressRelation.childAddress = otherAddress;

                    if(addressToSave.id != null && addressToSave.id.equals(otherAddress.id)){
                        return badRequest("eine Adresse kann nicht sich selber zugeordnet werden!");
                    }

                    addressToSave.relations.add(addressRelation);


                }

            }


            addressToSave.save();

            ObjectMapper objectMapper = new ObjectMapper();

            return ok(objectMapper.writeValueAsString(addressToSave));
        }

        return badRequest("body JSON is not an object!");
    }

    public static Result delete() {
        if(request().body() == null) {
            return badRequest("body ist null");
        }

        if( request().body().asJson() == null) {
            return badRequest("body json ist null");
        }

        JsonNode body = request().body().asJson();

        if(body.isObject()) {


            Long id = ( body.get("id") == null ? null : body.get("id").asLong());

            if(id == null || id <= 0) {
                // not saved address dont have to be deleted
                return ok();
            }

            Address addressToDelete = Address.find.byId(id);
            if(addressToDelete == null) {
                return notFound("Address with ID " + id.toString() + " was not found!");
            }

            addressToDelete.delete();

            return ok("{ \"message\":\"Adresse gelöscht!\"}");
        }

        return badRequest("body JSON is not an object!");
    }

}
