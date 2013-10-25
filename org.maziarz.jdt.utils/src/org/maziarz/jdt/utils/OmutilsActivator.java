package org.maziarz.jdt.utils;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class OmutilsActivator extends AbstractUIPlugin {

	private static OmutilsActivator instance;

	public OmutilsActivator() {
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		instance = this;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		instance = null;
		super.stop(context);
	}

	public static OmutilsActivator getDefault() {
		return instance;
	}

}
