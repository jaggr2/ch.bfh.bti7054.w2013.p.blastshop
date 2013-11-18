package controllers.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.api.Address;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Controller;

/**
 * Created with IntelliJ IDEA.
 * User: Roger
 * Date: 10.11.13
 * Time: 03:22
 */
public class AddressAPIController extends Controller {

    public static Result list() throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();

        return ok(objectMapper.writeValueAsString(Address.find.all()));
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

    public static Result save() throws JsonProcessingException {

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
            }

            addressToSave.name = body.get("name").asText();
            addressToSave.preName = body.get("preName").asText();

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
