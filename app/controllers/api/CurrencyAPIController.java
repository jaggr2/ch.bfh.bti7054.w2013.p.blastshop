package controllers.api;

import com.avaje.ebean.Expr;
import com.avaje.ebean.ExpressionList;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.BackendBasicModel;
import models.Language;
import models.Currency;
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
public class CurrencyAPIController extends Controller {

    public static Result list() throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();

        return ok(objectMapper.writeValueAsString(Currency.find.all()));
    }

    public static Result lookup() throws JsonProcessingException {


        String querystring = request().getQueryString("q");
        String limit = request().getQueryString("limit");

        Integer rowcount = (limit != null && !limit.trim().isEmpty() ? Integer.parseInt(limit) : 100);

        ExpressionList<Currency> searchexpression = Currency.find.fetch("languages").where().or(Expr.ilike("languages.longName", "" + querystring + "%"), Expr.ilike("languages.abbrevation","" + querystring + "%")).ne("rowStatus", "d").ne("rowStatus", "s");

        List<Currency> CurrencyList = ( rowcount != null && rowcount > 0 ? searchexpression.setMaxRows(rowcount).findList() : searchexpression.findList() );

        List<TypeAheadDatum> nodeList = new ArrayList<TypeAheadDatum>();

        for(Currency CurrencyEntry : CurrencyList) {

            TypeAheadDatum datum = new TypeAheadDatum();
            datum.value = CurrencyEntry.getLongName(Language.getEnglish()) + "("+ CurrencyEntry.symbol + ")";
            datum.tokens.add(CurrencyEntry.getLongName(Language.getEnglish()));
            datum.tokens.add(CurrencyEntry.getShortName(Language.getEnglish()));
            datum.dataobject = CurrencyEntry;

            nodeList.add(datum);
        }

        ObjectMapper objectMapper = new ObjectMapper();

        return ok(objectMapper.writeValueAsString(nodeList));
    }

    public static Result get(String id) throws JsonProcessingException {

        if(id == null || id.isEmpty()) {
            return badRequest();
        }

        Currency CurrencyToLoad = Currency.find.byId(id);
        if(CurrencyToLoad == null) {
            return notFound("Currency with ID " + id.toString() + " was not found!");
        }

        ObjectMapper objectMapper = new ObjectMapper();

        return ok(objectMapper.writeValueAsString(CurrencyToLoad));

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


            String id = ( body.get("id") == null ? null : body.get("id").asText());
            Currency CurrencyToSave = null;

            if(id != null && !id.isEmpty()) {
                CurrencyToSave = Currency.find.byId(id);
                if(CurrencyToSave == null) {
                    return notFound("Currency with ID " + id.toString() + " was not found!");
                }
            }
            else {
                CurrencyToSave = new Currency();
                CurrencyToSave.rowStatus = BackendBasicModel.RowStatus.ACTIVE;
            }

            CurrencyToSave.setLongName(Language.getEnglish(), body.get("longName").asText());
            CurrencyToSave.setShortName(Language.getEnglish(), body.get("shortName").asText());

            CurrencyToSave.save();

            ObjectMapper objectMapper = new ObjectMapper();

            return ok(objectMapper.writeValueAsString(CurrencyToSave));
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


            String id = ( body.get("id") == null ? null : body.get("id").asText());

            if(id == null || id.isEmpty()) {
                // not saved address dont have to be deleted
                return ok();
            }

            Currency CurrencyToDelete = Currency.find.byId(id);
            if(CurrencyToDelete == null) {
                return notFound("Currency with ID " + id + " was not found!");
            }

            CurrencyToDelete.delete();

            return ok("{ \"message\":\"Currency gelöscht!\"}");
        }

        return badRequest("body JSON is not an object!");
    }
}
