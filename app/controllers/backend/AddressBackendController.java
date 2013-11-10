package controllers.backend;

import play.mvc.*;
import views.html.backend.*;

/**
 * Created with IntelliJ IDEA.
 * User: Roger
 * Date: 10.11.13
 * Time: 03:40
 */
public class AddressBackendController extends Controller {

    public static Result dashboard() {
        return ok(dashboard.render(""));
    }

    public static Result list() {
        return ok(addressList.render(""));
    }

}
