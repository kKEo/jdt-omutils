package org.maziarz.sqlipse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class SqlipsePlugin extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "sqlipse";

	private static SqlipsePlugin plugin;

	public SqlipsePlugin() {
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public static SqlipsePlugin getDefault() {
		return plugin;
	}

	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
	
	private File getConfigFile() {
		File file = SqlipsePlugin.getDefault().getStateLocation().append("config.xml").toFile();
		getLog().log(new Status(Status.INFO, PLUGIN_ID, "Config file: "+file.getAbsolutePath()));
		return file;
	}

	public Configuration readConfig() {
		File configuration = getConfigFile();
		try (FileInputStream fis = new FileInputStream(configuration)){
			return Configuration.read(fis);
		} catch (IOException e) {
			return new Configuration();
		} catch (JAXBException e) {
			throw new RuntimeException(e); 
		}
	}
	
	public void writeConfig(Configuration c) {
		File configurationFile = getConfigFile();
		try(FileOutputStream fos = new FileOutputStream(configurationFile)) {
			Configuration.write(c, fos);
		} catch (JAXBException | IOException e) {
			throw new RuntimeException(e);
		}
	}


}
