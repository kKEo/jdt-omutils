package org.maziarz.sqlipse;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;

@XmlAccessorType(XmlAccessType.FIELD)
public class JdbcDriver {
	
	@XmlID
	@XmlAttribute
	private String name;
	
	@XmlElement
	private String jars;
	
	@XmlAttribute
	private String driver;
	
	@SuppressWarnings("unused")
	private JdbcDriver() {
	}
	
	public JdbcDriver(String name, String jars, String driver) {
		this.name = name;
		this.jars = jars;
		this.driver = driver;
	}

	public String getName() {
		return name;
	}
	
	public String getDriverClass() {
		return driver;
	}
	
	public String getJars() {
		return jars;
	}
	
	@Override
	public String toString() {
		return name;
	}

	public void setName(String value) {
		this.name = value;
	}
	
	public void setJars(String jars) {
		this.jars = jars;
	}
	
	public void setDriverClass(String value) {
		this.driver = value; 
	}

	public static JdbcDriver cloneMe(JdbcDriver d) {
		return new JdbcDriver(d.getName(), d.getJars(), d.getDriverClass());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((driver == null) ? 0 : driver.hashCode());
		result = prime * result + ((jars == null) ? 0 : jars.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JdbcDriver other = (JdbcDriver) obj;
		if (driver == null) {
			if (other.driver != null)
				return false;
		} else if (!driver.equals(other.driver))
			return false;
		if (jars == null) {
			if (other.jars != null)
				return false;
		} else if (!jars.equals(other.jars))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
    
}
