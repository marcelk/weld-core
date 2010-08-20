package org.jboss.weld.environment.servlet.test.examples;

import static org.jboss.weld.environment.servlet.test.util.TomcatDeployments.CONTEXT_XML;

import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class MockExampleTest extends MockExampleTestBase
{
   
   @Deployment
   public static WebArchive deployment()
   {
      return MockExampleTestBase.deployment().add(CONTEXT_XML, "META-INF/context.xml");
   }
   
}