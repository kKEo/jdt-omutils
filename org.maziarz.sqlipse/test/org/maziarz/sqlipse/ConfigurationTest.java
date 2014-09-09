package org.maziarz.sqlipse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.xml.bind.JAXBException;

import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;

public class ConfigurationTest {

	@Test
	public void configuration() throws JAXBException {

		Configuration c = new Configuration();
		c.addDriver(new JdbcDriver("d1", "jars", "org.driver.Driver"));
		JdbcDriver driver = new JdbcDriver("d2", "jars2", "org.driver.Driver2");
		c.addDriver(driver);

		c.addConnection(new JdbcConnection(driver, "jdbc:h2:mem", "", ""));

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		Configuration.write(c, os);

		System.out.println(new String(os.toByteArray()));

		ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());

		Configuration c1 = Configuration.read(is);

		Assert.assertThat(c1.getConnections().size(), Is.is(1));
		Assert.assertThat(c1.getDrivers().size(), Is.is(2));
	}

}
