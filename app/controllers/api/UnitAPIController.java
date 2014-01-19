package controllers.api;

import com.avaje.ebean.Expr;
import com.avaje.ebean.ExpressionList;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.BackendBasicModel;
import models.Language;
import models.Unit;
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
public class UnitAPIController extends Controller {

    public static Result list() throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();

        return ok(objectMapper.writeValueAsString(Unit.find.all()));
    }

    public static Result lookup() throws JsonProcessingException {


        String querystring = request().getQueryString("q");
        String limit = request().getQueryString("limit");

        Integer rowcount = (limit != null && !limit.trim().isEmpty() ? Integer.parseInt(limit) : 100);

        ExpressionList<Unit> searchexpression = Unit.find.fetch("UnitLanguage").where().or(Expr.ilike("languages.name", "" + querystring + "%"), Expr.ilike("languages.abbrevation","" + querystring + "%")).ne("rowStatus", "d").ne("rowStatus", "s");

        List<Unit> UnitList = ( rowcount != null && rowcount > 0 ? searchexpression.setMaxRows(rowcount).findList() : searchexpression.findList() );

        List<TypeAheadDatum> nodeList = new ArrayList<TypeAheadDatum>();

        for(Unit UnitEntry : UnitList) {

            TypeAheadDatum datum = new TypeAheadDatum();
            datum.value = UnitEntry.getName(Language.getEnglish()) + "("+ UnitEntry.getAbbrevation(Language.getEnglish()) + ")";
            datum.tokens.add(UnitEntry.getName(Language.getEnglish()));
            datum.tokens.add(UnitEntry.getAbbrevation(Language.getEnglish()));
            datum.dataobject = UnitEntry;

            nodeList.add(datum);
        }

        ObjectMapper objectMapper = new ObjectMapper();

        return ok(objectMapper.writeValueAsString(nodeList));
    }

    public static Result get(Long id) throws JsonProcessingException {

        if(id == null || id <= 0) {
            return badRequest();
        }

        Unit unitToLoad = Unit.find.byId(id);
        if(unitToLoad == null) {
            return notFound("Unit with ID " + id.toString() + " was not found!");
        }

        ObjectMapper objectMapper = new ObjectMapper();

        return ok(objectMapper.writeValueAsString(unitToLoad));

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
            Unit UnitToSave = null;

            if(id != null && id > 0) {
                UnitToSave = Unit.find.byId(id);
                if(UnitToSave == null) {
                    return notFound("Unit with ID " + id.toString() + " was not found!");
                }
            }
            else {
                UnitToSave = new Unit();
                UnitToSave.rowStatus = BackendBasicModel.RowStatus.ACTIVE;
            }

            UnitToSave.setName(Language.getEnglish(), body.get("name").asText());
            UnitToSave.setAbbrevation(Language.getEnglish(), body.get("abbrevation").asText());

            UnitToSave.save();

            ObjectMapper objectMapper = new ObjectMapper();

            return ok(objectMapper.writeValueAsString(UnitToSave));
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

            Unit UnitToDelete = Unit.find.byId(id);
            if(UnitToDelete == null) {
                return notFound("Unit with ID " + id + " was not found!");
            }

            UnitToDelete.delete();

            return ok("{ \"message\":\"Unit gelöscht!\"}");
        }

        return badRequest("body JSON is not an object!");
    }
}
