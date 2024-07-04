package com.chensoul.sharedlib;

import com.chensoul.sharedlib.actuator.PyroscopeConfiguration;
import com.chensoul.sharedlib.errorhandler.ErrorConfiguration;
import com.chensoul.sharedlib.jpa.AuditingConfiguration;
import com.chensoul.sharedlib.springdoc.SpringdocConfig;
import com.chensoul.sharedlib.validation.ValidatorConfiguration;
import com.chensoul.sharedlib.webmvc.AsyncConfiguration;
import com.chensoul.sharedlib.webmvc.CorsConfiguration;
import com.chensoul.sharedlib.webmvc.RestTemplateConfiguration;
import com.chensoul.sharedlib.webmvc.SchedulingConfiguration;
import com.chensoul.sharedlib.webmvc.SpringContextHolder;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@Import({
	CommonBeanFactory.class,
	AsyncConfiguration.class,
	SchedulingConfiguration.class,
	SpringContextHolder.class,
	CorsConfiguration.class,
	RestTemplateConfiguration.class,
	ValidatorConfiguration.class,
	SpringdocConfig.class,
	AuditingConfiguration.class,
	PyroscopeConfiguration.class,
	ErrorConfiguration.class
})
public class SharedLibConfiguration {
}
