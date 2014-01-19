package controllers;

import actions.GlobalContextHelper;
import com.feth.play.module.pa.PlayAuthenticate;
import models.Address;
import models.AddressLinkedAccount;
import models.AddressToken;
import play.Routes;
import play.data.Form;
import play.i18n.Messages;
import play.mvc.Result;
import play.mvc.With;
import security.BackendLoginUsernamePasswordAuthUser;
import security.BackendUsernamePasswordAuthProvider;
import security.BackendUsernamePasswordAuthProvider.MyIdentity;
import security.BackendUsernamePasswordAuthUser;
import views.html.account.signup.*;
import views.html.login;
import views.html.signup;

import static play.data.Form.form;

@With(GlobalContextHelper.class)
public class Signup extends FrontendBasicController {

	public static class PasswordReset extends AccountController.PasswordChange {

		public PasswordReset() {
		}

		public PasswordReset(final String token) {
			this.token = token;
		}

		public String token;

		public String getToken() {
			return token;
		}

		public void setToken(String token) {
			this.token = token;
		}
	}

	private static final Form<PasswordReset> PASSWORD_RESET_FORM = form(PasswordReset.class);

    public static Result login() {
        return ok(login.render(BackendUsernamePasswordAuthProvider.LOGIN_FORM));
    }

    public static Result doLogin() {
        com.feth.play.module.pa.controllers.Authenticate.noCache(response());
        final Form<BackendUsernamePasswordAuthProvider.MyLogin> filledForm = BackendUsernamePasswordAuthProvider.LOGIN_FORM.bindFromRequest();
        if (filledForm.hasErrors()) {
            // User did not fill everything properly
            return badRequest(login.render(filledForm));
        } else {
            // Everything was filled
            session().remove("basketID");
            return BackendUsernamePasswordAuthProvider.handleLogin(ctx());
        }
    }

    public static Result signup() {
        return ok(signup.render(BackendUsernamePasswordAuthProvider.SIGNUP_FORM));
    }

    public static Result jsRoutes() {
        return ok(
                Routes.javascriptRouter("jsRoutes", controllers.routes.javascript.Signup.forgotPassword())).as("text/javascript");
    }

    public static Result doSignup() {
        com.feth.play.module.pa.controllers.Authenticate.noCache(response());
        final Form<BackendUsernamePasswordAuthProvider.MySignup> filledForm = BackendUsernamePasswordAuthProvider.SIGNUP_FORM.bindFromRequest();
        if (filledForm.hasErrors()) {
            // User did not fill everything properly
            return badRequest(signup.render(filledForm));
        } else {
            // Everything was filled
            // do something with your part of the form before handling the user
            // signup
            session().remove("basketID");
            return BackendUsernamePasswordAuthProvider.handleSignup(ctx());
        }
    }

	public static Result unverified() {
		com.feth.play.module.pa.controllers.Authenticate.noCache(response());
		return ok(unverified.render());
	}

	private static final Form<MyIdentity> FORGOT_PASSWORD_FORM = form(MyIdentity.class);

	public static Result forgotPassword(final String email) {
		com.feth.play.module.pa.controllers.Authenticate.noCache(response());
		Form<MyIdentity> form = FORGOT_PASSWORD_FORM;
		if (email != null && !email.trim().isEmpty()) {
			form = FORGOT_PASSWORD_FORM.fill(new MyIdentity(email));
		}
		return ok(password_forgot.render(form));
	}

	public static Result doForgotPassword() {
		com.feth.play.module.pa.controllers.Authenticate.noCache(response());
		final Form<MyIdentity> filledForm = FORGOT_PASSWORD_FORM
				.bindFromRequest();
		if (filledForm.hasErrors()) {
			// User did not fill in his/her email
			return badRequest(password_forgot.render(filledForm));
		} else {
			// The email address given *BY AN UNKNWON PERSON* to the form - we
			// should find out if we actually have a user with this email
			// address and whether password login is enabled for him/her. Also
			// only send if the email address of the user has been verified.
			final String email = filledForm.get().email;

			// We don't want to expose whether a given email address is signed
			// up, so just say an email has been sent, even though it might not
			// be true - that's protecting our user privacy.
			flash(FrontendBasicController.FLASH_INFO_KEY,
					Messages.get(
							"playauthenticate.reset_password.message.instructions_sent",
							email));

            final BackendUsernamePasswordAuthProvider provider = BackendUsernamePasswordAuthProvider.getProvider();

            final AddressLinkedAccount account = AddressLinkedAccount.findByEmail(email, provider.getKey());

			if (account != null) {
				// yep, we have a user with this email that is active - we do
				// not know if the user owning that account has requested this
				// reset, though.


				// User exists
				if (account.emailValidated) {
					provider.sendPasswordResetMailing(account.address, ctx());
					// In case you actually want to let (the unknown person)
					// know whether a user was found/an email was sent, use,
					// change the flash message
				} else {
					// We need to change the message here, otherwise the user
					// does not understand whats going on - we should not verify
					// with the password reset, as a "bad" user could then sign
					// up with a fake email via OAuth and get it verified by an
					// a unsuspecting user that clicks the link.
					flash(FrontendBasicController.FLASH_INFO_KEY, Messages.get("playauthenticate.reset_password.message.email_not_verified"));

					// You might want to re-send the verification email here...
					provider.sendVerifyEmailMailingAfterSignup(account.address, ctx());
				}
			}

			return redirect(routes.Signup.login());
		}
	}

	/**
	 * Returns a token object if valid, null if not
	 *
	 * @param token
	 * @param type
	 * @return
	 */
	private static AddressToken tokenIsValid(final String token, final AddressToken.Type type) {
        AddressToken ret = null;
		if (token != null && !token.trim().isEmpty()) {
			final AddressToken ta = AddressToken.findByToken(token, type);
			if (ta != null && ta.isValid()) {
				ret = ta;
			}
		}

		return ret;
	}

	public static Result resetPassword(final String token) {
		com.feth.play.module.pa.controllers.Authenticate.noCache(response());
		final AddressToken ta = tokenIsValid(token, AddressToken.Type.PASSWORD_RESET);
		if (ta == null) {
			return badRequest(no_token_or_invalid.render());
		}

		return ok(password_reset.render(PASSWORD_RESET_FORM.fill(new PasswordReset(token))));
	}

	public static Result doResetPassword() {
		com.feth.play.module.pa.controllers.Authenticate.noCache(response());
		final Form<PasswordReset> filledForm = PASSWORD_RESET_FORM
				.bindFromRequest();
		if (filledForm.hasErrors()) {
			return badRequest(password_reset.render(filledForm));
		} else {
			final String token = filledForm.get().token;
			final String newPassword = filledForm.get().password;

			final AddressToken ta = tokenIsValid(token, AddressToken.Type.PASSWORD_RESET);
			if (ta == null) {
				return badRequest(no_token_or_invalid.render());
			}
			final Address u = ta.address;
			try {
				// Pass true for the second parameter if you want to
				// automatically create a password and the exception never to
				// happen
				u.resetPassword(new BackendUsernamePasswordAuthUser(newPassword), false);
			} catch (final RuntimeException re) {
				flash(FrontendBasicController.FLASH_INFO_KEY,
						Messages.get("playauthenticate.reset_password.message.no_password_account"));
			}
			final boolean login = BackendUsernamePasswordAuthProvider.getProvider().isLoginAfterPasswordReset();

			if (login) {
				// automatically log in
				flash(FrontendBasicController.FLASH_INFO_KEY, Messages.get("playauthenticate.reset_password.message.success.auto_login"));

				return PlayAuthenticate.loginAndRedirect(ctx(), new BackendLoginUsernamePasswordAuthUser(u.getLoginEmail()));
			} else {
				// send the user to the login page
				flash(FrontendBasicController.FLASH_INFO_KEY, Messages.get("playauthenticate.reset_password.message.success.manual_login"));
			}
			return redirect(routes.Signup.login());
		}
	}

	public static Result oAuthDenied(final String getProviderKey) {
		com.feth.play.module.pa.controllers.Authenticate.noCache(response());
		return ok(oAuthDenied.render(getProviderKey));
	}

	public static Result exists() {
		com.feth.play.module.pa.controllers.Authenticate.noCache(response());
		return ok(exists.render());
	}

	public static Result verify(final String token) {
		com.feth.play.module.pa.controllers.Authenticate.noCache(response());
		final AddressToken ta = tokenIsValid(token, AddressToken.Type.EMAIL_VERIFICATION);
		if (ta == null) {
			return badRequest(no_token_or_invalid.render());
		}

        AddressLinkedAccount account = Address.verify(ta.address);

		flash(FrontendBasicController.FLASH_INFO_KEY, Messages.get("playauthenticate.verify_email.success", account.email));

		if (FrontendBasicController.getLocalUser(session()) != null) {
			return redirect(PlayAuthenticate.getResolver().afterAuth());
		} else {
			return redirect(PlayAuthenticate.getResolver().login());
		}
	}
}
