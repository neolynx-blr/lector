package com.neolynks.vendor.auth;

import java.security.Principal;

import lombok.Data;

@Data
public class User implements Principal {
	
    private final Account accountDetail;
    
	/* (non-Javadoc)
	 * @see java.security.Principal#getName()
	 */
	public String getName() {
		return this.getAccountDetail().getUserName();
	}

	public User(Account accountDetail) {
		super();
		this.accountDetail = accountDetail;
	}



}
