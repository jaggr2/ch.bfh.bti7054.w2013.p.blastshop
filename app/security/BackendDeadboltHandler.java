package security;

import be.objectify.deadbolt.core.models.Subject;
import be.objectify.deadbolt.java.AbstractDeadboltHandler;
import be.objectify.deadbolt.java.DynamicResourceHandler;
import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.providers.AuthProvider;
import com.feth.play.module.pa.user.AuthUser;
import com.feth.play.module.pa.user.AuthUserIdentity;
import models.Address;
import models.AddressLinkedAccount;
import play.libs.F;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.SimpleResult;
import views.html.error;

import java.util.Date;

public class BackendDeadboltHandler extends AbstractDeadboltHandler {

    private static final String ORIGINAL_URL = "pa.url.orig";
    private static final String USER_KEY = "pa.u.id";
    private static final String PROVIDER_KEY = "pa.p.id";
    private static final String EXPIRES_KEY = "pa.u.exp";
    private static final String SESSION_ID_KEY = "pa.s.id";

    @Override
    public F.Promise<SimpleResult> beforeAuthCheck(final Http.Context context) {

        /**
         * Invoked immediately before controller or view restrictions are checked. This forms the integration with any
         * authentication actions that may need to occur.
         *
         * @param context the HTTP context
         * @return the action result if an action other than the delegate must be taken, otherwise null. For a case where
         *         the user is authenticated (or whatever your test condition is), this will be null otherwise the restriction
         *         won't be applied.
         */
		if (PlayAuthenticate.isLoggedIn(context.session())) {
			// user is logged in

			//return null;
            return F.Promise.promise(new F.Function0<SimpleResult>() {
                @Override
                public SimpleResult apply() throws Throwable {
                    return null;
                }
            });
		} else {
			// user is not logged in

			// call this if you want to redirect your visitor to the page that
			// was requested before sending him to the login page
			// if you don't call this, the user will get redirected to the page
			// defined by your resolver
			final String originalUrl = PlayAuthenticate.storeOriginalUrl(context);

			context.flash().put("error", "You need to log in first, to view '" + originalUrl + "'");
            return F.Promise.promise(new F.Function0<SimpleResult>() {
                @Override
                public SimpleResult apply() throws Throwable {
                    return redirect(PlayAuthenticate.getResolver().login());
                }
            });
		}
	}

	@Override
	public Subject getSubject(final Http.Context context) {
		final AuthUserIdentity u = PlayAuthenticate.getUser(context);

		// Caching might be a good idea here
        AddressLinkedAccount account = AddressLinkedAccount.findByAuthUserIdentity(u);
        if(account == null) {
            return null;
        }
		return account.address;
	}

	@Override
	public DynamicResourceHandler getDynamicResourceHandler(final Http.Context context) {
		return null;
	}

	@Override
    public F.Promise<SimpleResult> onAuthFailure(final Http.Context context, final String content) {

        // if the user has a cookie with a valid user and the local user has
        // been deactivated/deleted in between, it is possible that this gets
        // shown. You might want to consider to sign the user out in this case.
        return F.Promise.promise(new F.Function0<SimpleResult>() {
            @Override
            public SimpleResult apply() throws Throwable {
                if (PlayAuthenticate.isLoggedIn(context.session())) {
                    return ok(error.render("Zugriff verweigert", "Zum Aufrufen dieser Seite fehlen die nötigen Berechtigungen!","danger"));
                }
                else {
                    final String originalUrl = PlayAuthenticate.storeOriginalUrl(context);
                    context.flash().put("error", "You need to log in first, to view '" + originalUrl + "'");
                    return redirect(PlayAuthenticate.getResolver().login());
                }
            }
        });
	}
}
