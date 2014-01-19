package controllers;

import actions.GlobalContextHelper;
import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;
import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.user.AuthUser;
import models.Address;
import models.AddressLinkedAccount;
import models.SecurityRole;
import play.data.Form;
import play.data.format.Formats.NonEmpty;
import play.data.validation.Constraints.MinLength;
import play.data.validation.Constraints.Required;
import play.i18n.Messages;
import play.mvc.Result;
import play.mvc.With;
import security.BackendUsernamePasswordAuthProvider;
import security.BackendUsernamePasswordAuthUser;
import views.html.account.*;
import views.html.profile;

import static play.data.Form.form;

@With(GlobalContextHelper.class)
public class AccountController extends FrontendBasicController {

    public static final Form<Address> PROFILE_FORM = form(Address.class);

	public static class Accept {

		@Required
		@NonEmpty
		public Boolean accept;

		public Boolean getAccept() {
			return accept;
		}

		public void setAccept(Boolean accept) {
			this.accept = accept;
		}

    }

	public static class PasswordChange {
		@MinLength(5)
		@Required
		public String password;

		@MinLength(5)
		@Required
		public String repeatPassword;

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public String getRepeatPassword() {
			return repeatPassword;
		}

		public void setRepeatPassword(String repeatPassword) {
			this.repeatPassword = repeatPassword;
		}

		public String validate() {
			if (password == null || !password.equals(repeatPassword)) {
				return Messages
						.get("playauthenticate.change_password.error.passwords_not_same");
			}
			return null;
		}
	}

	private static final Form<Accept> ACCEPT_FORM = form(Accept.class);
	private static final Form<PasswordChange> PASSWORD_CHANGE_FORM = form(PasswordChange.class);

	public static Result link() {
		com.feth.play.module.pa.controllers.Authenticate.noCache(response());
		return ok(link.render());
	}

    @Restrict(@Group(SecurityRole.USER))
	public static Result verifyEmail() {
		com.feth.play.module.pa.controllers.Authenticate.noCache(response());
		final Address user = FrontendBasicController.getLocalUser(session());

        final AddressLinkedAccount account = user.getAccountByProvider(BackendUsernamePasswordAuthProvider.getProvider().getKey());

		if (account.emailValidated) {
			// E-Mail has been validated already
			flash(FrontendBasicController.FLASH_INFO_KEY,
					Messages.get("playauthenticate.verify_email.error.already_validated"));
		} else if (account.providerUserId != null && !account.providerUserId.trim().isEmpty()) {
			flash(FrontendBasicController.FLASH_INFO_KEY, Messages.get(
					"playauthenticate.verify_email.message.instructions_sent",
                    account.providerUserId));

			BackendUsernamePasswordAuthProvider.getProvider().sendVerifyEmailMailingAfterSignup(user, ctx());
		} else {
			flash(FrontendBasicController.FLASH_INFO_KEY, Messages.get(
					"playauthenticate.verify_email.error.set_email_first",
                    account.providerUserId));
		}
		return redirect(routes.AccountController.profile());
	}

	public static Result changePassword() {
		com.feth.play.module.pa.controllers.Authenticate.noCache(response());
		final Address u = FrontendBasicController.getLocalUser(session());

        final AddressLinkedAccount account = u.getAccountByProvider(BackendUsernamePasswordAuthProvider.getProvider().getKey());

		if (!account.emailValidated) {
			return ok(views.html.account.unverified.render());
		} else {
			return ok(views.html.account.password_change.render(PASSWORD_CHANGE_FORM));
		}
	}

	public static Result doChangePassword() {
		com.feth.play.module.pa.controllers.Authenticate.noCache(response());
		final Form<PasswordChange> filledForm = PASSWORD_CHANGE_FORM
				.bindFromRequest();
		if (filledForm.hasErrors()) {
			// User did not select whether to link or not link
			return badRequest(views.html.account.password_change.render(filledForm));
		} else {
			final Address user = FrontendBasicController.getLocalUser(session());
			final String newPassword = filledForm.get().password;
			user.changePassword(new BackendUsernamePasswordAuthUser(newPassword), true);
			flash(FrontendBasicController.FLASH_INFO_KEY, Messages.get("playauthenticate.change_password.success"));
			return redirect(routes.AccountController.profile());
		}
	}

	public static Result askLink() {
		com.feth.play.module.pa.controllers.Authenticate.noCache(response());
		final AuthUser u = PlayAuthenticate.getLinkUser(session());
		if (u == null) {
			// account to link could not be found, silently redirect to login
            return redirect(PlayAuthenticate.getResolver().login());
		}
		return ok(ask_link.render(ACCEPT_FORM, u));
	}

	public static Result doLink() {
		com.feth.play.module.pa.controllers.Authenticate.noCache(response());
		final AuthUser u = PlayAuthenticate.getLinkUser(session());
		if (u == null) {
			// account to link could not be found, silently redirect to login
            return redirect(PlayAuthenticate.getResolver().login());
		}

		final Form<Accept> filledForm = ACCEPT_FORM.bindFromRequest();
		if (filledForm.hasErrors()) {
			// User did not select whether to link or not link
			return badRequest(ask_link.render(filledForm, u));
		} else {
			// User made a choice :)
			final boolean link = filledForm.get().accept;
			if (link) {
				flash(FrontendBasicController.FLASH_INFO_KEY, Messages.get("playauthenticate.accounts.link.success"));
			}
			return PlayAuthenticate.link(ctx(), link);
		}
	}

	public static Result askMerge() {
		com.feth.play.module.pa.controllers.Authenticate.noCache(response());
		// this is the currently logged in user
		final AuthUser aUser = PlayAuthenticate.getUser(session());

		// this is the user that was selected for a login
		final AuthUser bUser = PlayAuthenticate.getMergeUser(session());
		if (bUser == null) {
			// user to merge with could not be found, silently redirect to login
			return redirect(PlayAuthenticate.getResolver().login());

		}

		// You could also get the local user object here via
		// User.findByAuthUserIdentity(newUser)
		return ok(ask_merge.render(ACCEPT_FORM, aUser, bUser));
	}

	public static Result doMerge() {
		com.feth.play.module.pa.controllers.Authenticate.noCache(response());
		// this is the currently logged in user
		final AuthUser aUser = PlayAuthenticate.getUser(session());

		// this is the user that was selected for a login
		final AuthUser bUser = PlayAuthenticate.getMergeUser(session());
		if (bUser == null) {
			// user to merge with could not be found, silently redirect to login
            return redirect(PlayAuthenticate.getResolver().login());
		}

		final Form<Accept> filledForm = ACCEPT_FORM.bindFromRequest();
		if (filledForm.hasErrors()) {
			// User did not select whether to merge or not merge
			return badRequest(ask_merge.render(filledForm, aUser, bUser));
		} else {
			// User made a choice :)
			final boolean merge = filledForm.get().accept;
			if (merge) {
				flash(FrontendBasicController.FLASH_INFO_KEY,
						Messages.get("playauthenticate.accounts.merge.success"));
			}
			return PlayAuthenticate.merge(ctx(), merge);
		}
	}

    @Restrict(@Group(SecurityRole.USER))
    public static Result profile() {
        return ok(profile.render(PROFILE_FORM));
    }

    @Restrict(@Group(SecurityRole.USER))
    public static Result doProfile() {
        return ok(profile.render(PROFILE_FORM));
    }
}
