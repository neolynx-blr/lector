package com.neolynks.vendor.manager;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.CollectionUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.neolynks.vendor.auth.Account;

/**
 * Created by nitesh.garg on Oct 5, 2015
 */
public class AccountService {

	private final SessionFactory sessionFactory;
	static Logger LOGGER = LoggerFactory.getLogger(AccountService.class);

	public AccountService(SessionFactory sessionFactory) {
		super();
		this.sessionFactory = sessionFactory;
	}

	@SuppressWarnings("unchecked")
	public Optional<Account> getAccountDetails(String userName) {

		Session session = this.sessionFactory.openSession();

		Query query = session.createSQLQuery("select acc.* from account acc where acc.user_name = :userName")
				.addEntity("account", Account.class).setParameter("userName", userName);

		List<Account> accountDetails = query.list();
		
		session.close();
		
		if (CollectionUtils.isNotEmpty(accountDetails)) {
			return Optional.of(accountDetails.get(0));
		}
		
		return Optional.empty();

	}

}
