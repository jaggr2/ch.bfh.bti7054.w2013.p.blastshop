package actions;

import controllers.FrontendBasicController;
import models.Address;
import models.ArticleCollection;
import models.Language;
import models.Website;
import play.libs.F;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.SimpleResult;

import java.util.List;

/**
 * Copyright 2013 blastbeat syndicate gmbh
 * Author: Roger Jaggi <roger.jaggi@blastbeatsyndicate.com>
 * Date: 24.10.13
 * Time: 21:15
 */
public class GlobalContextHelper extends Action.Simple {

    public static final String USER_LANGUAGE = "userLanguage";

    public F.Promise<SimpleResult> call(Http.Context ctx) throws Throwable {
        ctx.args.put("rootMenus", Website.find.where().isNotNull("rootMenuId").findList());
        ctx.args.put("rootArticleCollection", ArticleCollection.find.where().isNull("parentCollection").findList());

        if(ctx.session().containsKey(USER_LANGUAGE)) {
            Language lang = Language.find.byId(ctx.session().get(USER_LANGUAGE));
            ctx.args.put(USER_LANGUAGE, lang);
            ctx.changeLang(lang.getLocale().toString().substring(0,2).toLowerCase());
        }
        else {
            ctx.args.put(USER_LANGUAGE, Language.findByLang(ctx.lang()));
        }
        return delegate.call(ctx);
    }
    public static List<ArticleCollection> getRootCollection() {
        return (List<ArticleCollection>) Http.Context.current().args.get("rootArticleCollection");
    }

    public static Language getUserLanguage() {
        return (Language) Http.Context.current().args.get("userLanguage");
    }

    public static Website getRootMenu(Integer rootMenuId) {
        List<Website> rootSites = (List<Website>)Http.Context.current().args.get("rootMenus");

        for(Website site : rootSites) {
            if(site.rootMenuId != null && site.rootMenuId.equals(rootMenuId)) {
                return site;
            }
        }

        throw new ArrayIndexOutOfBoundsException("RootMenuID " + rootMenuId.toString() + " was not found!");
    }
}