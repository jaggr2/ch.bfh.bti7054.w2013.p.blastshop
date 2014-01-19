package controllers.backend;

import actions.GlobalContextHelper;
import actions.UserAddress;
import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;
import models.SecurityRole;
import play.mvc.*;
import views.html.backend.*;

/**
 * Created with IntelliJ IDEA.
 * User: Roger
 * Date: 10.11.13
 * Time: 03:40
 */

public class BackendController extends Controller {

    @With(GlobalContextHelper.class)
    @Restrict(@Group(SecurityRole.ADMINISTRATOR))
    public static Result index() {
        return ok(mainBackend.render("Blastshop Backend"));
    }

}
