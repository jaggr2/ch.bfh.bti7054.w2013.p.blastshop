package security;

import com.feth.play.module.pa.service.UserServicePlugin;
import com.feth.play.module.pa.user.AuthUser;
import com.feth.play.module.pa.user.AuthUserIdentity;
import models.Address;
import models.AddressLinkedAccount;
import play.Application;

public class BackendUserServicePlugin extends UserServicePlugin {

	public BackendUserServicePlugin(final Application app) {
		super(app);
	}

	@Override
	public Object save(final AuthUser authUser) {
        final AddressLinkedAccount account = AddressLinkedAccount.findByAuthUserIdentity(authUser);

		if (account == null) {
			return Address.create(authUser).id;
		} else {
			// we have this user already, so return null
			return null;
		}
	}

	@Override
	public Object getLocalIdentity(final AuthUserIdentity identity) {
		// For production: Caching might be a good idea here...
		// ...and dont forget to sync the cache when users get deactivated/deleted
		final AddressLinkedAccount u = AddressLinkedAccount.findByAuthUserIdentity(identity);
		if(u != null) {
			return u.address.id;
		} else {
			return null;
		}
	}

	@Override
	public AuthUser merge(final AuthUser newUser, final AuthUser oldUser) {
		if (!oldUser.equals(newUser)) {
            Address.merge(oldUser, newUser);
		}
		return oldUser;
	}

	@Override
	public AuthUser link(final AuthUser oldUser, final AuthUser newUser) {
        Address.addLinkedAccount(oldUser, newUser);
		return newUser;
	}
	
	@Override
	public AuthUser update(final AuthUser knownUser) {
		// User logged in again, bump last login date
        Address.setLastLoginDate(knownUser);
		return knownUser;
	}

}
