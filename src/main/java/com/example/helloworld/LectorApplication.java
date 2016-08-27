package com.example.helloworld;

import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.helloworld.auth.ExampleAuthenticator;
import com.example.helloworld.auth.ExampleAuthorizer;
import com.example.helloworld.cli.RenderCommand;
import com.example.helloworld.core.Person;
import com.example.helloworld.core.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neolynks.common.model.client.price.DiscountDetail;
import com.neolynks.common.model.client.price.DiscountInfo;
import com.neolynks.common.model.client.price.TaxDetail;
import com.neolynks.common.model.client.price.TaxInfo;
import com.neolynks.common.model.order.CartRequest;
import com.neolynks.common.model.order.DeliveryMode;
import com.neolynks.common.model.order.ItemRequest;
//import com.neolynks.common.util.PasswordHash;
import com.neolynks.vendor.ClientResource;
import com.neolynks.vendor.auth.Account;
import com.neolynks.vendor.job.InventorySync;
import com.neolynks.vendor.manager.AccountService;
import com.neolynks.vendor.manager.InventoryService;

public class LectorApplication extends Application<LectorConfiguration> {
	
	static Logger LOGGER = LoggerFactory.getLogger(LectorApplication.class);
	
    public static void main(String[] args) throws Exception {
        new LectorApplication().run(args);
    }

    private final HibernateBundle<LectorConfiguration> hibernateBundle =
            new HibernateBundle<LectorConfiguration>(Person.class, Account.class) {
                @Override
                public DataSourceFactory getDataSourceFactory(LectorConfiguration configuration) {
                    return configuration.getDataSourceFactory();
                }
            };

    @Override
    public String getName() {
        return "hello-world";
    }

    @Override
    public void initialize(Bootstrap<LectorConfiguration> bootstrap) {
        // Enable variable substitution with environment variables
        bootstrap.setConfigurationSourceProvider(
                new SubstitutingSourceProvider(
                        bootstrap.getConfigurationSourceProvider(),
                        new EnvironmentVariableSubstitutor(false)
                )
        );

        bootstrap.addCommand(new RenderCommand());
        bootstrap.addBundle(new AssetsBundle());
        bootstrap.addBundle(new MigrationsBundle<LectorConfiguration>() {
            @Override
            public DataSourceFactory getDataSourceFactory(LectorConfiguration configuration) {
                return configuration.getDataSourceFactory();
            }
        });
        bootstrap.addBundle(hibernateBundle);
        bootstrap.addBundle(new ViewBundle<LectorConfiguration>() {
            @Override
            public Map<String, Map<String, String>> getViewConfiguration(LectorConfiguration configuration) {
                return configuration.getViewRendererConfiguration();
            }
        });
    }

    @Override
    public void run(LectorConfiguration configuration, Environment environment) {
    	
    	
		LOGGER.info("Initialising Lector, starting with setting up DAO classes & service layer for authentication and authorization...");
		final AccountService accountService = new AccountService(hibernateBundle.getSessionFactory());
		
		LOGGER.info("Setting up the business-logic class followed by registering the inventory resource and it's lifecycle...");
		final InventoryService inventoryService = new InventoryService(configuration.getCurationConfig());
		
		environment.jersey().register(new ClientResource(inventoryService));
		environment.lifecycle().manage(new InventorySync(configuration.getCurationConfig()));

		//final Template template = configuration.buildTemplate();
		//environment.healthChecks().register("template", new TemplateHealthCheck(template));
		
		environment.jersey().register(
				new AuthDynamicFeature(new BasicCredentialAuthFilter.Builder<com.neolynks.vendor.auth.User>()
						.setAuthenticator(new ExampleAuthenticator(accountService))
						.setAuthorizer(new ExampleAuthorizer())
						.setRealm("SUPER SECRET STUFF").buildAuthFilter()));
		environment.jersey().register(new AuthValueFactoryProvider.Binder<>(User.class));


		temporaryCode();
		LOGGER.info("Initialisation complete.");
    	
    }
    
	public void temporaryCode() {
		/************************************ Temporary Code *******************************************/
		
		try {
//			String passwordHash = PasswordHash.createHash("passwd");
//			System.out.println("Hash for password:" + passwordHash);
//			System.out.println("Password Match:"+ PasswordHash.validatePassword("passwd", passwordHash));
//			passwordHash = PasswordHash.createHash("analyst");
//			System.out.println("Hash for password:" + passwordHash);
//			System.out.println("Password Match:"+ PasswordHash.validatePassword("analyst", passwordHash));
//			passwordHash = PasswordHash.createHash("vendor");
//			System.out.println("Hash for password:" + passwordHash);
//			System.out.println("Password Match:"+ PasswordHash.validatePassword("vendor", passwordHash));
//
			DiscountInfo discount1 = new DiscountInfo();
			//discount1.setDiscountedItemCode("ITEM201");
			//discount1.setDiscountType(6);
			//discount1.setDiscountValue(2.0);
			//discount1.setRequiredCountForDiscount(8);
			
			DiscountInfo discount2 = new DiscountInfo();
			//discount1.setDiscountType(1);
			//discount1.setDiscountValue(20.0);

			DiscountDetail discountDetail = new DiscountDetail();
			discountDetail.getDiscountInfo().add(discount1);
			discountDetail.getDiscountInfo().add(discount2);
			
			ObjectMapper mapper = new ObjectMapper();
			try {
				System.out.println(mapper.writeValueAsString(discountDetail));
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			
			TaxInfo tax1 = new TaxInfo();
			tax1.setTaxType(1);
			tax1.setTaxValue(20.0);
			
			TaxInfo tax2 = new TaxInfo();
			tax2.setTaxType(2);
			tax2.setTaxValue(10.0);
			
			TaxDetail taxDetail = new TaxDetail();
			taxDetail.getTaxInfo().add(tax1);
			taxDetail.getTaxInfo().add(tax2);
			
			try {
				System.out.println(mapper.writeValueAsString(taxDetail));
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			
			CartRequest cart = new CartRequest();
			cart.setDeliveryMode(DeliveryMode.IN_STORE_PICKUP);
			cart.setDeviceDataVersionId(1448552860765L);
			cart.setVendorId(281L);
			
			ItemRequest firstItem = new ItemRequest();
			firstItem.setBarcode(8906004864247L);
			firstItem.setItemCode("B00E3QW6P4");
			firstItem.setCountForInStorePickup(2);
			
			ItemRequest secondItem = new ItemRequest();
			secondItem.setBarcode(8901030320491L);
			secondItem.setItemCode("B00791DDUM");
			secondItem.setCountForInStorePickup(2);
			/*
			cart.setItemList(new ArrayList<ItemRequest>());
			cart.getItemList().add(firstItem);
			cart.getItemList().add(secondItem);*/
			
			cart.setNetAmount(132.34D);
			
			System.out.println(mapper.writeValueAsString(cart));
			System.out.println("Finished temporary code execution ...");
			
		}
//        catch (NoSuchAlgorithmException e) {
//			System.out.println("Failed with exception:"+ e.getClass().getName());
//			e.printStackTrace();
//		} catch (InvalidKeySpecException e) {
//			System.out.println("Failed with exception:"+ e.getClass().getName());
//			e.printStackTrace();
//		}
        catch (JsonProcessingException e) {
			System.out.println("Failed with exception:"+ e.getClass().getName());
			e.printStackTrace();
		}
		
		/************************************ Temporary Code *******************************************/

	}

}
