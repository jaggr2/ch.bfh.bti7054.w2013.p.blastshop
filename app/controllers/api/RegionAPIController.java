package controllers.api;

import com.avaje.ebean.Expr;
import com.avaje.ebean.ExpressionList;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.BackendBasicModel;
import models.Language;
import models.Region;
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
public class RegionAPIController extends Controller {

    public static Result list() throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();

        return ok(objectMapper.writeValueAsString(Region.find.all()));
    }

    public static Result lookup() throws JsonProcessingException {

        Region.RegionType regionType = Region.RegionType.valueOf(request().getQueryString("t"));
        if(regionType == null) {
            return badRequest();
        }

        String querystring = request().getQueryString("q");
        String limit = request().getQueryString("limit");

        Integer rowcount = (limit != null && !limit.trim().isEmpty() ? Integer.parseInt(limit) : 100);

        ExpressionList<Region> searchexpression = Region.find.fetch("languages").where().eq("regionLevel", regionType).or(Expr.ilike("languages.name", "%" + querystring + "%"), Expr.ilike("postalCode", "" + querystring + "%")).ne("rowStatus", "d").ne("rowStatus", "s");

        List<Region> regionList = ( rowcount != null && rowcount > 0 ? searchexpression.setMaxRows(rowcount).findList() : searchexpression.findList() );

        List<TypeAheadDatum> nodeList = new ArrayList<TypeAheadDatum>();

        for(Region regionEntry : regionList) {

            TypeAheadDatum datum = new TypeAheadDatum();
            datum.value = regionEntry.postalCode + " " + regionEntry.getName(Language.getEnglish());

            datum.tokens.add(regionEntry.postalCode);
            datum.tokens.add(regionEntry.getName(Language.getEnglish()));
            datum.id = regionEntry.id;
            datum.dataobject = regionEntry;

            nodeList.add(datum);
        }

        ObjectMapper objectMapper = new ObjectMapper();

        return ok(objectMapper.writeValueAsString(nodeList));
    }

    public static Result get(Long id) throws JsonProcessingException {

        if(id == null || id <= 0) {
            return badRequest();
        }

        Region addressFunctionToLoad = Region.find.byId(id);
        if(addressFunctionToLoad == null) {
            return notFound("Region with ID " + id.toString() + " was not found!");
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
            Region addressFunctionToSave = null;

            if(id != null && id > 0) {
                addressFunctionToSave = Region.find.byId(id);
                if(addressFunctionToSave == null) {
                    return notFound("Region with ID " + id.toString() + " was not found!");
                }
            }
            else {
                addressFunctionToSave = new Region();
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

            Region addressToDelete = Region.find.byId(id);
            if(addressToDelete == null) {
                return notFound("Address with ID " + id.toString() + " was not found!");
            }

            addressToDelete.delete();

            return ok("{ \"message\":\"AddressFunction gelöscht!\"}");
        }

        return badRequest("body JSON is not an object!");
    }
}
