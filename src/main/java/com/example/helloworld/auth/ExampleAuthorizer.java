package com.example.helloworld.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.neolynks.vendor.auth.User;

import io.dropwizard.auth.Authorizer;

public class ExampleAuthorizer implements Authorizer<User> {

	static Logger LOGGER = LoggerFactory.getLogger(ExampleAuthorizer.class);

	@Override
	public boolean authorize(User user, String role) {

		LOGGER.debug("Checking correct role access for user [{}] against required roles [{}]:", user.getAccountDetail()
				.getUserName(), role);

		if (role.toLowerCase().contains(user.getAccountDetail().getRole().toLowerCase())) {
			return true;
		}

		return false;
	}
}
