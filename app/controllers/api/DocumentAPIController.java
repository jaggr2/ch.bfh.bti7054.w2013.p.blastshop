package controllers.api;

import com.avaje.ebean.Expr;
import com.avaje.ebean.ExpressionList;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.*;
import play.mvc.Controller;
import play.mvc.Result;
import utils.ISO8601DateParser;
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
public class DocumentAPIController extends Controller {

    public static Result list() throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();

        //objectMapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false);

        //return ok(objectMapper.writerWithView(BackendBasicModel.ViewPublic.class).writeValueAsString(Document.find.where().ne("rowStatus", "d").ne("rowStatus", "s").findList()));
        return ok(objectMapper.writeValueAsString(Document.find.where().ne("rowStatus", "d").ne("rowStatus", "s").findList()));
    }

    public static Result newestClientOrders() throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();

        return ok(objectMapper.writeValueAsString(Document.find.where().ne("rowStatus", "d").ne("rowStatus", "s").eq("documentType", DocumentType.getClientOrder()).setMaxRows(5).orderBy("createdOn DESC").findList()));
    }

}
