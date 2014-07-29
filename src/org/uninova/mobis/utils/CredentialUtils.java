package org.uninova.mobis.utils;

import org.uninova.mobis.pojos.MobisResponse;
import org.uninova.mobis.pojos.MobisUser;

public interface CredentialUtils {

	public MobisResponse<Integer> handleRegister(String firstName, String lastName, String username, String password, String email, String country, String gId, String gToken, String gRefreshToken) ;
	
	public MobisResponse<MobisUser> handleLogin(String username, String password) ; 
}
