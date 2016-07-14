/**
 * 
 */
package com.example.helloworld.auth;

import com.example.helloworld.core.User;
import io.dropwizard.auth.Authorizer;

/**
 * Created by nitesh.garg on Jul 13, 2016
 *
 */
public class ExampleAuthorizer2 implements Authorizer<User> {

    @Override
    public boolean authorize(User user, String role) {
        return user.getRoles() != null && user.getRoles().contains(role);
    }

}
