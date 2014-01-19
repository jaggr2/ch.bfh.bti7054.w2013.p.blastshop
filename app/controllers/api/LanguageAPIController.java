package controllers.api;

import com.avaje.ebean.ExpressionList;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
public class LanguageAPIController extends Controller {

    public static Result list() throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();

        return ok(objectMapper.writeValueAsString(Language.find.all()));
    }

    public static Result lookup() throws JsonProcessingException {


        String querystring = request().getQueryString("q");
        String limit = request().getQueryString("limit");

        Integer rowcount = (limit != null && !limit.trim().isEmpty() ? Integer.parseInt(limit) : 100);

        ExpressionList<Language> searchexpression = Language.find.where().ilike("englishName", "" + querystring + "%").ne("rowStatus", "d").ne("rowStatus", "s");

        List<Language> LanguageList = ( rowcount != null && rowcount > 0 ? searchexpression.setMaxRows(rowcount).findList() : searchexpression.findList() );

        List<TypeAheadDatum> nodeList = new ArrayList<TypeAheadDatum>();

        for(Language LanguageEntry : LanguageList) {

            TypeAheadDatum datum = new TypeAheadDatum();
            datum.value = LanguageEntry.englishName;
            datum.tokens.add(LanguageEntry.englishName);
            datum.dataobject = LanguageEntry;

            nodeList.add(datum);
        }

        ObjectMapper objectMapper = new ObjectMapper();

        return ok(objectMapper.writeValueAsString(nodeList));
    }

    public static Result get(String id) throws JsonProcessingException {

        if(id == null || id.isEmpty()) {
            return badRequest();
        }

        Language addressFunctionToLoad = Language.find.byId(id);
        if(addressFunctionToLoad == null) {
            return notFound("Language with ID " + id.toString() + " was not found!");
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


            String id = ( body.get("code") == null ? null : body.get("code").asText());
            Language languageToSave = null;

            if(id != null && !id.isEmpty()) {
                languageToSave = Language.find.byId(id);
                if(languageToSave == null) {
                    return notFound("Language with ID " + id.toString() + " was not found!");
                }
            }
            else {
                languageToSave = new Language();
                languageToSave.rowStatus = BackendBasicModel.RowStatus.ACTIVE;
            }

            languageToSave.englishName = body.get("englishName").asText();

            languageToSave.save();

            ObjectMapper objectMapper = new ObjectMapper();

            return ok(objectMapper.writeValueAsString(languageToSave));
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


            String code = ( body.get("code") == null ? null : body.get("code").asText());

            if(code == null || !code.isEmpty()) {
                // not saved address dont have to be deleted
                return ok();
            }

            Language languageToDelete = Language.find.byId(code);
            if(languageToDelete == null) {
                return notFound("Address with ID " + code + " was not found!");
            }

            languageToDelete.delete();

            return ok("{ \"message\":\"Language gelöscht!\"}");
        }

        return badRequest("body JSON is not an object!");
    }
}
