package controllers.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.*;
import play.mvc.Controller;
import play.mvc.Result;

/**
 * Created with IntelliJ IDEA.
 * User: Roger
 * Date: 10.11.13
 * Time: 03:22
 */
public class ArticleAPIController extends Controller {

    public static Result list() throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();

        return ok(objectMapper.writeValueAsString(Article.find.all()));
    }

    public static Result get(Long id) throws JsonProcessingException {

        if(id == null || id <= 0) {
            return badRequest();
        }

        Article articleToLoad = Article.find.byId(id);
        if(articleToLoad == null) {
            return notFound("Article with ID " + id.toString() + " was not found!");
        }

        ObjectMapper objectMapper = new ObjectMapper();

        return ok(objectMapper.writeValueAsString(articleToLoad));

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
            Article articleToSave = Article.getOrCreate(id);
            if(articleToSave == null) {
                return notFound("Article with ID " + (id != null ? id.toString() : "null") + " was not found!");
            }

            if(!body.hasNonNull("options")) {
                return badRequest("options are missing!");
            }

            // add and update options
            for(JsonNode option : body.get("options")) {

                Long optionId = ( option.get("id") == null ? null : option.get("id").asLong());
                ArticleOption articleOptionToSave = articleToSave.getOrCreateOption(optionId);

                if(articleOptionToSave == null) {
                    return notFound("ArticleOption with ID " + (id != null ? id.toString() : "null") + " was not found!");
                }

                articleOptionToSave.number = option.get("number").asText();

                for(ArticleOptionLanguage lang : articleOptionToSave.languages) {
                    lang.delete();
                }

                if(option.get("languages").size() > 0) {

                    articleOptionToSave.languages.clear();

                    for(JsonNode articleOptionLang : option.get("languages")) {

                        if(!articleOptionLang.hasNonNull("language")) {
                            return badRequest("language is missing!");
                        }

                        Language language = Language.getOrCreate(articleOptionLang.get("language").get("code").asText());

                        if(language == null) {
                            return badRequest("invalid language!");
                        }

                        ArticleOptionLanguage articleOptionLanguageToSave = new ArticleOptionLanguage();
                        articleOptionLanguageToSave.language = language;
                        articleOptionLanguageToSave.description = (articleOptionLang.hasNonNull("description") ? articleOptionLang.get("description").asText() : null);
                        articleOptionLanguageToSave.title = (articleOptionLang.hasNonNull("title") ? articleOptionLang.get("title").asText() : null);
                        articleOptionLanguageToSave.additionalDescription = (articleOptionLang.hasNonNull("additionalDescription") ? articleOptionLang.get("additionalDescription").asText() : null);
                        articleOptionToSave.languages.add(articleOptionLanguageToSave);
                    }

                }

                if(option.hasNonNull("unit")) {
                    Unit theUnit = Unit.find.byId(option.get("unit").get("id").asLong());
                    if(theUnit == null) {
                        return badRequest("invalid unit");
                    }

                    articleOptionToSave.unit = theUnit;
                }

                if(option.hasNonNull("sellPriceCurrency") && option.get("sellPriceCurrency").hasNonNull("code")) {
                    Currency theCurrency = Currency.findByCode(option.get("sellPriceCurrency").get("code").asText());
                    if(theCurrency == null) {
                        return badRequest("invalid currency");
                    }

                    articleOptionToSave.sellPriceCurrency = theCurrency;
                }

                if(option.hasNonNull("sellPrice")) {
                    articleOptionToSave.sellPrice = option.get("sellPrice").asDouble();
                }
            }

            // TODO
            /* remove deleted options
            Iterator<ArticleOption> iter = articleToSave.options.iterator();
            while(iter.hasNext()) {
                ArticleOption option = iter.next();
                Boolean isInList = false;
                for(JsonNode optionNode : body.get("options")) {
                    Long optionId = ( optionNode.get("id") == null ? null : optionNode.get("id").asLong());
                    if(option.id != null && optionId != null && option.id.equals(optionId)) {
                        isInList = true;
                    }
                }

                if(!isInList) option.delete();
            } */

            articleToSave.save();

            ObjectMapper objectMapper = new ObjectMapper();

            return ok(objectMapper.writeValueAsString(articleToSave));
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
                // not saved articles don't have to be deleted
                return ok();
            }

            Article articleToDelete = Article.find.byId(id);
            if(articleToDelete == null) {
                return notFound("Article with ID " + id.toString() + " was not found!");
            }

            articleToDelete.delete();

            return ok("{ \"message\":\"Article deleted!\"}");
        }

        return badRequest("body JSON is not an object!");
    }

}
