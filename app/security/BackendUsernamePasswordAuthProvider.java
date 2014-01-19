package security;

import com.feth.play.module.mail.Mailer.Mail.Body;
import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.providers.password.UsernamePasswordAuthProvider;
import com.feth.play.module.pa.providers.password.UsernamePasswordAuthUser;
import controllers.FrontendBasicController;
import models.Address;
import models.AddressLinkedAccount;
import models.AddressToken;
import play.Application;
import play.Logger;
import play.data.Form;
import play.data.validation.Constraints.Email;
import play.data.validation.Constraints.MinLength;
import play.data.validation.Constraints.Required;
import play.i18n.Lang;
import play.i18n.Messages;
import play.mvc.Call;
import play.mvc.Http.Context;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static play.data.Form.form;

public class BackendUsernamePasswordAuthProvider extends UsernamePasswordAuthProvider<String, BackendLoginUsernamePasswordAuthUser, BackendUsernamePasswordAuthUser, BackendUsernamePasswordAuthProvider.MyLogin, BackendUsernamePasswordAuthProvider.MySignup> {

	private static final String SETTING_KEY_VERIFICATION_LINK_SECURE = SETTING_KEY_MAIL + "." + "verificationLink.secure";
	private static final String SETTING_KEY_PASSWORD_RESET_LINK_SECURE = SETTING_KEY_MAIL + "." + "passwordResetLink.secure";
	private static final String SETTING_KEY_LINK_LOGIN_AFTER_PASSWORD_RESET = "loginAfterPasswordReset";

	private static final String EMAIL_TEMPLATE_FALLBACK_LANGUAGE = "en";

	@Override
	protected List<String> neededSettingKeys() {
		final List<String> needed = new ArrayList<String>(super.neededSettingKeys());
		needed.add(SETTING_KEY_VERIFICATION_LINK_SECURE);
		needed.add(SETTING_KEY_PASSWORD_RESET_LINK_SECURE);
		needed.add(SETTING_KEY_LINK_LOGIN_AFTER_PASSWORD_RESET);
		return needed;
	}

	public static BackendUsernamePasswordAuthProvider getProvider() {
		return (BackendUsernamePasswordAuthProvider) PlayAuthenticate.getProvider(UsernamePasswordAuthProvider.PROVIDER_KEY);
	}

	public static class MyIdentity {

		public MyIdentity() {
		}

		public MyIdentity(final String email) {
			this.email = email;
		}

		@Required
		@Email
		public String email;

	}

	public static class MyLogin extends MyIdentity
			implements
			com.feth.play.module.pa.providers.password.UsernamePasswordAuthProvider.UsernamePassword {

		@Required
		@MinLength(5)
		public String password;

		@Override
		public String getEmail() {
			return email;
		}

		@Override
		public String getPassword() {
			return password;
		}


	}

	public static class MySignup extends MyLogin {

		@Required
		@MinLength(5)
		public String repeatPassword;

        @Required
        public String gender;

        @Required
        public String firstName;

        @Required
        public String lastName;

        public Locale locale;

		public String validate() {
			if (password == null || !password.equals(repeatPassword)) {
				return Messages
						.get("playauthenticate.password.signup.error.passwords_not_same");
			}
			return null;
		}
	}

	public static final Form<MySignup> SIGNUP_FORM = form(MySignup.class);
	public static final Form<MyLogin> LOGIN_FORM = form(MyLogin.class);

	public BackendUsernamePasswordAuthProvider(Application app) {
		super(app);
	}

	protected Form<MySignup> getSignupForm() {
		return SIGNUP_FORM;
	}

	protected Form<MyLogin> getLoginForm() {
		return LOGIN_FORM;
	}

	@Override
	protected com.feth.play.module.pa.providers.password.UsernamePasswordAuthProvider.SignupResult signupUser(final BackendUsernamePasswordAuthUser user) {
        final AddressLinkedAccount account = AddressLinkedAccount.findByAuthUserIdentity(user);

		if (account != null) {

			if (account.emailValidated) {
				// This user exists, has its email validated and is active
				return SignupResult.USER_EXISTS;
			} else {
				// this user exists, is active but has not yet validated its
				// email
				return SignupResult.USER_EXISTS_UNVERIFIED;
			}
		}

		// The user either does not exist or is inactive - create a new one
		@SuppressWarnings("unused")
		final Address newUser = Address.create(user);


		//Usually the email should be verified before allowing login, however
		//if you return
		//return SignupResult.USER_CREATED;
		//then the user gets logged in directly
		return SignupResult.USER_CREATED_UNVERIFIED;
	}

	@Override
	protected com.feth.play.module.pa.providers.password.UsernamePasswordAuthProvider.LoginResult loginUser(final BackendLoginUsernamePasswordAuthUser authUser) {
        AddressLinkedAccount account = AddressLinkedAccount.findByAuthUserIdentity(authUser);

		if (account == null) {
			return LoginResult.NOT_FOUND;
		} else {
            //for (final AddressLinkedAccount acc : u.linkedAccounts) {
                //if (getKey().equals(acc.providerKey)) {
            if(account.emailValidated) {
                if (authUser.checkPassword(account.providerUserId, authUser.getPassword())) {
                    // Password was correct
                    return LoginResult.USER_LOGGED_IN;

                } else {
                    // if you don't return here,
                    // you would allow the user to have
                    // multiple passwords defined
                    // usually we don't want this
                    return LoginResult.WRONG_PASSWORD;
                }
            }
            else {
                return LoginResult.USER_UNVERIFIED;
            }

                //}
            //}
            //return LoginResult.WRONG_PASSWORD;
		}
	}

	@Override
	protected Call userExists(final UsernamePasswordAuthUser authUser) {
		return controllers.routes.Signup.exists();
	}

	@Override
	protected Call userUnverified(final UsernamePasswordAuthUser authUser) {
		return controllers.routes.Signup.unverified();
	}

	@Override
	protected BackendUsernamePasswordAuthUser buildSignupAuthUser(final MySignup signup, final Context ctx) {
		return new BackendUsernamePasswordAuthUser(signup);
	}

	@Override
	protected BackendLoginUsernamePasswordAuthUser buildLoginAuthUser(final MyLogin login, final Context ctx) {
		return new BackendLoginUsernamePasswordAuthUser(login.getPassword(),login.getEmail());
	}
	

	@Override
	protected BackendLoginUsernamePasswordAuthUser transformAuthUser(final BackendUsernamePasswordAuthUser authUser, final Context context) {
		return new BackendLoginUsernamePasswordAuthUser(authUser.getEmail());
	}

	@Override
	protected String getVerifyEmailMailingSubject(final BackendUsernamePasswordAuthUser user, final Context ctx) {
		return Messages.get("playauthenticate.password.verify_signup.subject");
	}

	@Override
	protected String onLoginUserNotFound(final Context context) {
		context.flash()
				.put(FrontendBasicController.FLASH_ERROR_KEY,
						Messages.get("playauthenticate.password.login.unknown_user_or_pw"));
		return super.onLoginUserNotFound(context);
	}

	@Override
	protected Body getVerifyEmailMailingBody(final String token, final BackendUsernamePasswordAuthUser user, final Context ctx) {

		final boolean isSecure = getConfiguration().getBoolean(SETTING_KEY_VERIFICATION_LINK_SECURE);

		final String url = controllers.routes.Signup.verify(token).absoluteURL(ctx.request(), isSecure);

		final Lang lang = Lang.preferred(ctx.request().acceptLanguages());
		final String langCode = lang.code();

		final String html = getEmailTemplate(
				"views.html.account.signup.email.verify_email", langCode, url,
				token, user.getName(), user.getEmail());
		final String text = getEmailTemplate(
				"views.txt.account.signup.email.verify_email", langCode, url,
				token, user.getName(), user.getEmail());

		return new Body(text, html);
	}



	@Override
	protected String generateVerificationRecord(final BackendUsernamePasswordAuthUser user) {
        final AddressLinkedAccount account = AddressLinkedAccount.findByAuthUserIdentity(user);

        final AddressToken token = AddressToken.create(AddressToken.Type.EMAIL_VERIFICATION, account.address);

		return token.token;
	}

	protected String getPasswordResetMailingSubject(final Address user, final Context ctx) {
		return Messages.get("playauthenticate.password.reset_email.subject");
	}

	protected Body getPasswordResetMailingBody(final String token, final Address user, final Context ctx) {

		final boolean isSecure = getConfiguration().getBoolean(SETTING_KEY_PASSWORD_RESET_LINK_SECURE);

		final String url = controllers.routes.Signup.resetPassword(token).absoluteURL(ctx.request(), isSecure);

		final Lang lang = Lang.preferred(ctx.request().acceptLanguages());
		final String langCode = lang.code();

		final String html = getEmailTemplate(
				"views.html.account.email.password_reset", langCode, url,
				token, user.name, user.getLoginEmail());
		final String text = getEmailTemplate(
				"views.txt.account.email.password_reset", langCode, url, token,
				user.name, user.getLoginEmail());

		return new Body(text, html);
	}

	public void sendPasswordResetMailing(final Address user, final Context ctx) {

        final AddressToken token = AddressToken.create(AddressToken.Type.PASSWORD_RESET, user);

		final String subject = getPasswordResetMailingSubject(user, ctx);
		final Body body = getPasswordResetMailingBody(token.token, user, ctx);
		mailer.sendMail(subject, body, getEmailName(user));
	}

	public boolean isLoginAfterPasswordReset() {
		return getConfiguration().getBoolean(
				SETTING_KEY_LINK_LOGIN_AFTER_PASSWORD_RESET);
	}

	protected String getVerifyEmailMailingSubjectAfterSignup(final Address user, final Context ctx) {
		return Messages.get("playauthenticate.password.verify_email.subject");
	}

	protected String getEmailTemplate(final String template,
			final String langCode, final String url, final String token,
			final String name, final String email) {
		Class<?> cls = null;
		String ret = null;
		try {
			cls = Class.forName(template + "_" + langCode);
		} catch (ClassNotFoundException e) {
			Logger.warn("Template: '"
					+ template
					+ "_"
					+ langCode
					+ "' was not found! Trying to use English fallback template instead.");
		}
		if (cls == null) {
			try {
				cls = Class.forName(template + "_"
						+ EMAIL_TEMPLATE_FALLBACK_LANGUAGE);
			} catch (ClassNotFoundException e) {
				Logger.error("Fallback template: '" + template + "_"
						+ EMAIL_TEMPLATE_FALLBACK_LANGUAGE
						+ "' was not found either!");
			}
		}
		if (cls != null) {
			Method htmlRender = null;
			try {
				htmlRender = cls.getMethod("render", String.class,
						String.class, String.class, String.class);
				ret = htmlRender.invoke(null, url, token, name, email)
						.toString();

			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		return ret;
	}

	protected Body getVerifyEmailMailingBodyAfterSignup(final String token, final Address user, final Context ctx) {

		final boolean isSecure = getConfiguration().getBoolean(
				SETTING_KEY_VERIFICATION_LINK_SECURE);
		final String url = controllers.routes.Signup.verify(token).absoluteURL(ctx.request(), isSecure);

		final Lang lang = Lang.preferred(ctx.request().acceptLanguages());
		final String langCode = lang.code();

		final String html = getEmailTemplate(
				"views.html.account.email.verify_email", langCode, url, token,
				user.name, user.getLoginEmail());
		final String text = getEmailTemplate(
				"views.txt.account.email.verify_email", langCode, url, token,
				user.name, user.getLoginEmail());

		return new Body(text, html);
	}

	public void sendVerifyEmailMailingAfterSignup(final Address user, final Context ctx) {

		final String subject = getVerifyEmailMailingSubjectAfterSignup(user,
				ctx);

        final AddressToken token = AddressToken.create(AddressToken.Type.EMAIL_VERIFICATION, user);

		final Body body = getVerifyEmailMailingBodyAfterSignup(token.token, user, ctx);
		mailer.sendMail(subject, body, getEmailName(user));
	}

	private String getEmailName(final Address user) {
		return getEmailName(user.getLoginEmail(), user.name);
	}
}
