package controllers.api;

import com.avaje.ebean.ExpressionList;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.AddressFunction;
import models.BackendBasicModel;
import models.Language;
import play.mvc.Controller;
import play.mvc.Result;
import utils.TypeAheadDatum;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Roger
 * Date: 10.11.13
 * Time: 03:22
 */
public class AddressFunctionAPIController extends Controller {

    public static Result list() throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();

        return ok(objectMapper.writeValueAsString(AddressFunction.find.all()));
    }

    public static Result lookup() throws JsonProcessingException {

        String querystring = request().getQueryString("q");
        String limit = request().getQueryString("limit");

        Integer rowcount = (limit != null && !limit.trim().isEmpty() ? Integer.parseInt(limit) : 100);

        ExpressionList<AddressFunction> searchexpression = AddressFunction.find.where().ilike("i18nDescription", "%" + querystring + "%").ne("rowStatus", "d").ne("rowStatus", "s");

        List<AddressFunction> addressList = ( rowcount != null && rowcount > 0 ? searchexpression.setMaxRows(rowcount).findList() : searchexpression.findList() );

        List<TypeAheadDatum> nodeList = new ArrayList<TypeAheadDatum>();

        for(AddressFunction addressFunction : addressList) {

            TypeAheadDatum datum = new TypeAheadDatum();
            datum.value = addressFunction.getName(Language.getEnglish());
            datum.tokens.add(addressFunction.getName(Language.getEnglish()));
            datum.id = addressFunction.id;

            nodeList.add(datum);
        }

        ObjectMapper objectMapper = new ObjectMapper();

        return ok(objectMapper.writeValueAsString(nodeList));
    }

    public static Result get(Long id) throws JsonProcessingException {

        if(id == null || id <= 0) {
            return badRequest();
        }

        AddressFunction addressFunctionToLoad = AddressFunction.find.byId(id);
        if(addressFunctionToLoad == null) {
            return notFound("AddressFunction with ID " + id.toString() + " was not found!");
        }

        ObjectMapper objectMapper = new ObjectMapper();

        return ok(objectMapper.writeValueAsString(addressFunctionToLoad));

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
            AddressFunction addressFunctionToSave = null;

            if(id != null && id > 0) {
                addressFunctionToSave = AddressFunction.find.byId(id);
                if(addressFunctionToSave == null) {
                    return notFound("AddressFunction with ID " + id.toString() + " was not found!");
                }
            }
            else {
                addressFunctionToSave = new AddressFunction();
                addressFunctionToSave.rowStatus = BackendBasicModel.RowStatus.ACTIVE;
            }

            addressFunctionToSave.setName(Language.getEnglish(), body.get("name").asText());

            addressFunctionToSave.save();

            ObjectMapper objectMapper = new ObjectMapper();

            return ok(objectMapper.writeValueAsString(addressFunctionToSave));
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

            AddressFunction addressToDelete = AddressFunction.find.byId(id);
            if(addressToDelete == null) {
                return notFound("Address with ID " + id.toString() + " was not found!");
            }

            addressToDelete.delete();

            return ok("{ \"message\":\"AddressFunction gelöscht!\"}");
        }

        return badRequest("body JSON is not an object!");
    }
}
