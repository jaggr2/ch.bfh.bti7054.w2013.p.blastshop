package actions;

import controllers.FrontendBasicController;
import models.Address;
import play.libs.F;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.SimpleResult;

/**
 * Copyright 2013 blastbeat syndicate gmbh
 * Author: Roger Jaggi <roger.jaggi@blastbeatsyndicate.com>
 * Date: 24.10.13
 * Time: 21:15
 */
public class UserAddress extends Action.Simple {

    public F.Promise<SimpleResult> call(Http.Context ctx) throws Throwable {
        ctx.args.put("currentUserAddress", FrontendBasicController.getLocalUser(ctx.session()));
        return delegate.call(ctx);
    }
    public static Address current() {
        return (Address) Http.Context.current().args.get("currentUserAddress");
    }
}
