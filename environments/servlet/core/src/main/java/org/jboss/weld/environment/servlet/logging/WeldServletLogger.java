/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.weld.environment.servlet.logging;

import org.jboss.logging.Logger;
import org.jboss.logging.Logger.Level;
import org.jboss.logging.annotations.Cause;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.Message.Format;
import org.jboss.logging.annotations.MessageLogger;
import org.jboss.weld.environment.logging.WeldEnvironmentLogger;

/**
 *
 * Message IDs: 001000 - 001099
 *
 * @author Kirill Gaevskii
 *
 */
@MessageLogger(projectCode = WeldEnvironmentLogger.WELD_ENV_PROJECT_CODE)
public interface WeldServletLogger extends WeldEnvironmentLogger {
    WeldServletLogger LOG = Logger.getMessageLogger(WeldServletLogger.class, Category.WELDSERVLET.getName());

    @LogMessage(level = Level.WARN)
    @Message(id = 1000, value = "@Resource injection not available in simple beans")
    void resourceInjectionNotAvailable();

    @LogMessage(level = Level.INFO)
    @Message(id = 1001, value = "No supported servlet container detected, CDI injection will NOT be available in Servlets, Filters or Listeners")
    void noSupportedServletContainerDetected();

    @LogMessage(level = Level.INFO)
    @Message(id = 1002, value = "Container detection skipped - custom container class loaded: {0}.", format = Format.MESSAGE_FORMAT)
    void containerDetectionSkipped(Object param1);

    @LogMessage(level = Level.WARN)
    @Message(id = 1003, value = "Unable to instantiate custom container class: {0}.", format = Format.MESSAGE_FORMAT)
    void unableToInstantiateCustomContainerClass(Object param1);

    @LogMessage(level = Level.WARN)
    @Message(id = 1004, value = "Found both WEB-INF/beans.xml and WEB-INF/classes/META-INF/beans.xml. It''s not portable to use both locations at the same time. Weld is going to use {0}.", format = Format.MESSAGE_FORMAT)
    void foundBothConfiguration(Object param1);

    @LogMessage(level = Level.TRACE)
    @Message(id = 1005, value = "Exactly one constructor ({0}) annotated with @Inject defined, using it as the bean constructor for {1}", format = Format.MESSAGE_FORMAT)
    void foundOneInjectableConstructor(Object param1, Object param2);

    @LogMessage(level = Level.INFO)
    @Message(id = 1006, value = "org.jboss.weld.environment.servlet.EnhancedListener used for ServletContext notifications")
    void enhancedListenerUsedForNotifications();

    @LogMessage(level = Level.INFO)
    @Message(id = 1007, value = "Initialize Weld using ServletContextListener")
    void initializeWeldUsingServletContextListener();

    @LogMessage(level = Level.INFO)
    @Message(id = 1008, value = "Initialize Weld using ServletContainerInitializer")
    void initializeWeldUsingServletContainerInitializer();

    @LogMessage(level = Level.INFO)
    @Message(id = 1009, value = "org.jboss.weld.environment.servlet.Listener used for ServletRequest and HttpSession notifications")
    void listenerUserForServletRequestAndHttpSessionNotifications();

    @LogMessage(level = Level.INFO)
    @Message(id = 1010, value = "Problem when iterating through {0}", format = Format.MESSAGE_FORMAT)
    void problemWhenInterating(Object param1, @Cause Throwable cause);

    @LogMessage(level = Level.INFO)
    @Message(id = 1011, value = "Could not read context {0}: Trying to create it!", format = Format.MESSAGE_FORMAT)
    void couldNotReadContext(Object param1);

    @LogMessage(level = Level.INFO)
    @Message(id = 1012, value = "BeanManager reference bound to {0}.", format = Format.MESSAGE_FORMAT)
    void beanManagerReferenceBoundTo(Object param1);

    @LogMessage(level = Level.INFO)
    @Message(id = 1013, value = "Successfully unbound BeanManager reference.")
    void successfullyUnboundBeanManagerReference();

    @LogMessage(level = Level.WARN)
    @Message(id = 1014, value = "Failed to unbind BeanManager reference!")
    void failedToUnbindBeanManagerReference();

    @LogMessage(level = Level.ERROR)
    @Message(id = 1015, value = "Could not create context: {0}.", format = Format.MESSAGE_FORMAT)
    void couldntCreateContext(Object param1);

    @Message(id = 1016, value = "Error loading resources from servlet context.")
    IllegalStateException errorLoadingResources(@Cause Throwable cause);

    @Message(id = 1017, value = "Exception fetching BeanManager instance!")
    IllegalStateException exceptionFetchingBeanManager(@Cause Throwable cause);

    @Message(id = 1018, value = "Not in a servlet or portlet environment!")
    IllegalStateException notInAServletOrPortlet();

    @Message(id = 1019, value = "Error creating JNDI context")
    IllegalStateException errorCreatingJNDIContext(@Cause Throwable cause);

    @Message(id = 1020, value = "Cannot instantiate instance of {0} with no-argument constructor.", format = Format.MESSAGE_FORMAT)
    IllegalStateException cannotInstantiateInstance(Object param1, @Cause Throwable cause);

    @Message(id = 1021, value = "Cannot load class for {0}.", format = Format.MESSAGE_FORMAT)
    IllegalStateException cannotLoadClass(Object param1, @Cause Throwable cause);

    @Message(id = 1022, value = "No ServiceLoader class available!")
    IllegalStateException noServiceLoaderClassAvailable();

    @Message(id = 1023, value = "No load method available on ServiceLoader - {0}.", format = Format.MESSAGE_FORMAT)
    IllegalStateException noLoadMethodAvailableOnServiceLoader(Object param1, @Cause Throwable cause);

    @Message(id = 1024, value = "Could not bind BeanManager reference to JNDI: {0}\nIf the naming context is read-only, you may need to use a configuration to bind the BeanManager instead, such as Tomcat's context.xml or Jetty's jetty-web.xml.", format = Format.MESSAGE_FORMAT)
    RuntimeException couldNotBindBeanManagerReferenceToJNDI(Object param1);

    @Message(id = 1025, value = "Could not create InitialContext to bind BeanManager reference in JNDI: {0}.", format = Format.MESSAGE_FORMAT)
    RuntimeException couldNotCreateInitialContext(Object param1);

    @Message(id = 1026, value = "Error loading Weld bootstrap, check that Weld is on the classpath.")
    IllegalStateException errorLoadingWeldBootstrap(@Cause Throwable cause);

    @Message(id = 1027, value = "Error loading Weld listener, check that Weld is on the classpath.")
    IllegalStateException errorLoadingWeldListener(@Cause Throwable cause);

    @Message(id = 1028, value = "Error loading Weld ELContext Listener, check that Weld is on the classpath.")
    IllegalStateException errorLoadingWeldELContextListener(@Cause Throwable cause);
}
