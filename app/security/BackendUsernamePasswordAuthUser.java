package security;

import com.feth.play.module.pa.providers.password.UsernamePasswordAuthUser;
import com.feth.play.module.pa.user.NameIdentity;
import com.feth.play.module.pa.user.ExtendedIdentity;
import com.feth.play.module.pa.user.LocaleIdentity;
import security.BackendUsernamePasswordAuthProvider.MySignup;

import java.util.Locale;

public class BackendUsernamePasswordAuthUser extends UsernamePasswordAuthUser implements
        NameIdentity, ExtendedIdentity, LocaleIdentity  {
	//

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    private final String gender;
    private final String firstName;
    private final String lastName;
    private final Locale locale;

	public BackendUsernamePasswordAuthUser(final MySignup signup) {
		super(signup.password, signup.email);
        this.gender = signup.gender;
        this.firstName = signup.firstName;
        this.lastName = signup.lastName;
        this.locale = signup.locale;
	}

	/**
	 * Used for password reset only - do not use this to signup a user!
	 * @param password
	 */
	public BackendUsernamePasswordAuthUser(final String password) {
		super(password, null);
        this.gender = null;
        this.firstName = null;
        this.lastName = null;
        this.locale = null;
	}

	@Override
	public String getName() {
		return lastName;
	}

    @Override
    public String getGender() {
        return gender;
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    @Override
    public Locale getLocale() {
        return locale;
    }

}
