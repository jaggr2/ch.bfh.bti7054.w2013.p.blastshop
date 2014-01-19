package controllers;

import actions.UserAddress;
import models.Address;
import models.AddressLinkedAccount;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.With;
import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.user.AuthUser;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Roger on 03.01.14.
 */
@With(UserAddress.class)
public class FrontendBasicController extends Controller {
    public static final String FLASH_SUCCESS_KEY = "success";
    public static final String FLASH_INFO_KEY = "info";
    public static final String FLASH_WARNING_KEY = "warning";
    public static final String FLASH_ERROR_KEY = "error";

    public static Address getLocalUser(final Http.Session session) {
        if(session == null) {
            return null;
        }

        final AuthUser currentAuthUser = PlayAuthenticate.getUser(session);
        if(currentAuthUser == null) {
            return null;
        }

        final AddressLinkedAccount account = AddressLinkedAccount.findByAuthUserIdentity(currentAuthUser);
        if( account == null ) {
            PlayAuthenticate.logout(session);
            return null;
        }

        return account.address;
    }

    public static Address getLocalUser() {

        if(Http.Context.current.get() == null) {
            // we have no context, for example when we are in the startup/shutdown phase
            return null;
        }
        return getLocalUser(session());
    }

    public static Result homepage() {

        if (FrontendBasicController.getLocalUser(session()) != null) {
            return redirect(PlayAuthenticate.getResolver().afterAuth());
        } else {
            return redirect(PlayAuthenticate.getResolver().login());
        }
    }

    public static String formatTimestamp(final long t) {
        return formatDate(new Date(t));
    }

    public static String formatDate(final Date date) {
        return new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(date);
    }
    public static String formatDecimal(Double decimal, String format) {
        return (new DecimalFormat(format != null ? format : "#.##")).format(decimal);
    }
}
