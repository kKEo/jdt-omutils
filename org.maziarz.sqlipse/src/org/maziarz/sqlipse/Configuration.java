package org.maziarz.sqlipse;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "configuration")
public class Configuration {

	@XmlElementWrapper(name = "entries")
	@XmlElement(name = "entry")
	private List<ConfigurationEntry> enties = new ArrayList<ConfigurationEntry>();

	public void load() {
		Activator.getDefault().getStateLocation().append("config.xml").toFile();
	}

	public static <T> T read(InputStream is, Class<T> clazz) throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(clazz);
		@SuppressWarnings("unchecked")
		T result = (T) context.createUnmarshaller().unmarshal(is);
		return result;
	}

	public static <T> void write(T o, OutputStream os, Class<T> clazz) throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(clazz);
		Marshaller m = context.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		m.marshal(o, os);

	}

}
