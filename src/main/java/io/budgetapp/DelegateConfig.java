package io.budgetapp;

import java.util.Arrays;

import javax.sql.DataSource;

import org.hdiv.config.annotation.ExclusionRegistry;
import org.hdiv.config.annotation.RuleRegistry;
import org.hdiv.config.annotation.ValidationConfigurer;
import org.hdiv.ee.config.SessionType;
import org.hdiv.ee.config.SingleCacheConfig;
import org.hdiv.ee.config.annotation.ExternalStateStorageConfigurer;
import org.hdiv.ee.session.cache.CacheType;
import org.springframework.context.annotation.Configuration;

import com.hdivsecurity.services.config.EnableHdiv4ServicesSecurityConfiguration;
import com.hdivsecurity.services.config.HdivServicesSecurityConfigurerAdapter;
import com.hdivsecurity.services.config.ServicesSecurityConfigBuilder;

import io.budgetapp.configuration.AppConfiguration;
import io.dropwizard.setup.Environment;

@Configuration
@EnableHdiv4ServicesSecurityConfiguration
public class DelegateConfig extends HdivServicesSecurityConfigurerAdapter {

	private static DataSource dataSource = null;

	public static void setup(final AppConfiguration conf, final Environment env) {
		DelegateConfig.dataSource = conf.getDataSourceFactory().build(env.metrics(), "hdiv");
	}

	@Override
	public void configure(final ServicesSecurityConfigBuilder builder) {
		builder.confidentiality(false);
		builder.sessionType(SessionType.COOKIE);
		builder.showErrorPageOnEditableValidation(true);
		builder.reuseExistingPageInAjaxRequest(false);
		builder.hypermediaSupport(false).csrfHeader(false);
		builder.allowPartialSubEntities(true);
	}

	@Override
	public void addExclusions(final ExclusionRegistry registry) {
		registry.addUrlExclusions("/", "/favicon.ico", "/app/.*");
	}

	@Override
	public void addRules(final RuleRegistry registry) {
		registry.addRule("safeText").acceptedPattern("^[a-zA-Z0-9 @.\\-_+#]*$").rejectedPattern("(\\s|\\S)*(--)(\\s|\\S)*]");
		registry.addRule("numbers").acceptedPattern("^[1-9]\\d*$");
	}

	@Override
	public void configureEditableValidation(final ValidationConfigurer validationConfigurer) {
		validationConfigurer.addValidation("/.*");
	}

	public DataSource externalStorageDataSource() {
		return dataSource;
	}

	@Override
	public void configureExternalStateStorage(final ExternalStateStorageConfigurer externalStateStorageConfigurer) {

		SingleCacheConfig config = new SingleCacheConfig(CacheType.EXT_DB);
		externalStateStorageConfigurer.databaseExternalStateStore().dataSource(externalStorageDataSource()).numberOfTables(4)
				.tablesSubjectName("Hdiv_Pages_");
		externalStateStorageConfigurer.cacheConfig(Arrays.asList(config));

		super.configureExternalStateStorage(externalStateStorageConfigurer);
	}

}
