package io.budgetapp;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.glassfish.jersey.servlet.ServletContainer;
import org.hdiv.filter.ValidatorFilter;
import org.hdiv.init.ServletContextInitializer;
import org.hdiv.listener.InitListener;
import org.hdiv.util.HDIVUtil;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.context.request.RequestContextListener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hdivsecurity.services.config.SerializerConfig;
import com.hdivsecurity.services.pb.technology.jaxrs.ServerWriterInterceptor;
import com.hdivsecurity.services.pb.technology.jaxrs.jersey.EndPointCapturer;

import io.dropwizard.setup.Environment;

public class DropwizardContextInitializer implements ServletContextListener {

	AnnotationConfigApplicationContext context;

	ServletContextInitializer initializer;

	InitListener listener = new InitListener();

	RequestContextListener contextListener = new RequestContextListener();

	ObjectMapper mapper;

	public static void initialize(final Environment environment) {
		/**
		 * Hdiv
		 */
		environment.jersey().register(ServerWriterInterceptor.class);
		environment.jersey().register(new EndPointCapturer("/"));
		environment.servlets().addFilter("ValidatorFilter", ValidatorFilter.class)
				.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), false, "/*");
		environment.servlets().addServletListeners(new DropwizardContextInitializer(environment.getObjectMapper()),
				new RequestContextListener());

		EndPointCapturer.setServletContainer((ServletContainer) environment.getJerseyServletContainer());
	}

	public DropwizardContextInitializer(final ObjectMapper mapper) {
		this.mapper = mapper;
	}

	/**
	 * Initialize {@link ServletContext} scoped objects.
	 * 
	 * @param servletContextEvent ServletContext creation event
	 * @since HDIV 2.1.0
	 */
	@Override
	public void contextInitialized(final ServletContextEvent servletContextEvent) {
		context = new AnnotationConfigApplicationContext();
		context.register(DelegateConfig.class);
		context.refresh();
		SerializerConfig.configureObjectMapper(mapper);
		HDIVUtil.registerApplicationContext(context, servletContextEvent.getServletContext());
		initializer = context.getBean(ServletContextInitializer.class);
		initializer.initializeServletContext(servletContextEvent.getServletContext());
		listener.contextInitialized(servletContextEvent);
	}

	/**
	 * Executed at {@link ServletContext} destroy.
	 * 
	 * @param servletContextEvent ServletContext destroy event
	 * @since HDIV 2.1.0
	 */
	@Override
	public void contextDestroyed(final ServletContextEvent servletContextEvent) {
		initializer.destroyServletContext(servletContextEvent.getServletContext());
		listener.contextDestroyed(servletContextEvent);
	}
}
