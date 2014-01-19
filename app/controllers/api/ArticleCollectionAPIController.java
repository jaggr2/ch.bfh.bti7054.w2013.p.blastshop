package controllers.api;

import com.avaje.ebean.ExpressionList;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.ArticleCollection;
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
public class ArticleCollectionAPIController extends Controller {

    public static Result list() throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();

        return ok(objectMapper.writeValueAsString(ArticleCollection.find.all()));
    }

    public static Result lookup() throws JsonProcessingException {

        String querystring = request().getQueryString("q");
        String limit = request().getQueryString("limit");

        Integer rowcount = (limit != null && !limit.trim().isEmpty() ? Integer.parseInt(limit) : 100);

        ExpressionList<ArticleCollection> searchexpression = ArticleCollection.find.fetch("languages").where().ilike("languages.name", "%" + querystring + "%").ne("rowStatus", "d").ne("rowStatus", "s");

        List<ArticleCollection> addressList = ( rowcount != null && rowcount > 0 ? searchexpression.setMaxRows(rowcount).findList() : searchexpression.findList() );

        List<TypeAheadDatum> nodeList = new ArrayList<TypeAheadDatum>();

        for(ArticleCollection ArticleCollection : addressList) {

            TypeAheadDatum datum = new TypeAheadDatum();
            datum.value = ArticleCollection.getName(Language.getEnglish());
            datum.tokens.add(ArticleCollection.getName(Language.getEnglish()));
            datum.id = ArticleCollection.id;

            nodeList.add(datum);
        }

        ObjectMapper objectMapper = new ObjectMapper();

        return ok(objectMapper.writeValueAsString(nodeList));
    }

    public static Result get(Long id) throws JsonProcessingException {

        if(id == null || id <= 0) {
            return badRequest();
        }

        ArticleCollection addressFunctionToLoad = ArticleCollection.find.byId(id);
        if(addressFunctionToLoad == null) {
            return notFound("ArticleCollection with ID " + id.toString() + " was not found!");
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
            ArticleCollection addressFunctionToSave = null;

            if(id != null && id > 0) {
                addressFunctionToSave = ArticleCollection.find.byId(id);
                if(addressFunctionToSave == null) {
                    return notFound("ArticleCollection with ID " + id.toString() + " was not found!");
                }
            }
            else {
                addressFunctionToSave = new ArticleCollection();
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

            ArticleCollection addressToDelete = ArticleCollection.find.byId(id);
            if(addressToDelete == null) {
                return notFound("Address with ID " + id.toString() + " was not found!");
            }

            addressToDelete.delete();

            return ok("{ \"message\":\"ArticleCollection gelöscht!\"}");
        }

        return badRequest("body JSON is not an object!");
    }
}
