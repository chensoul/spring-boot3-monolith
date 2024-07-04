package com.chensoul.sharedlib;

import com.chensoul.sharedlib.errorhandler.ErrorConfiguration;
import com.chensoul.sharedlib.jpa.AuditingConfiguration;
import com.chensoul.sharedlib.profiling.PyroscopeConfiguration;
import com.chensoul.sharedlib.resttemplate.RestTemplateConfiguration;
import com.chensoul.sharedlib.springdoc.SpringdocConfig;
import com.chensoul.sharedlib.validation.ValidatorConfiguration;
import com.chensoul.sharedlib.webmvc.AsyncConfiguration;
import com.chensoul.sharedlib.webmvc.CorsConfiguration;
import com.chensoul.sharedlib.webmvc.SchedulingConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@Import({
	CommonLibraryBeanFactory.class,
	AsyncConfiguration.class,
	AuditingConfiguration.class,
	AuditingConfiguration.AuditorAwareImpl.class,
	SchedulingConfiguration.class,
	CorsConfiguration.class,
	ValidatorConfiguration.class,
	SpringdocConfig.class,
	RestTemplateConfiguration.class,
	PyroscopeConfiguration.class,
	ErrorConfiguration.class
})
public class SharedLibConfiguration {
}
