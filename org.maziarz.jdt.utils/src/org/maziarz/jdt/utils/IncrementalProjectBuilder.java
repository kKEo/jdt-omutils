package org.maziarz.jdt.utils;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.maziarz.utils.ui.model.AccessedResource;
import org.maziarz.utils.ui.model.AccessedResource.AccessedResourceType;

public class IncrementalProjectBuilder extends org.eclipse.core.resources.IncrementalProjectBuilder {

	private final Map<String, AccessedResource> lastModified;

	public static final String BUILDER_ID = "org.maziarz.utils.ui.resourceAssistentBuilder";

	public IncrementalProjectBuilder() {
		System.out.println("IncrementalProjectBuilder on");
		lastModified = new HashMap<String, AccessedResource>();
	}

	public void addModified(String resource) {
		if (!lastModified.containsKey(resource)) {
			AccessedResource md = new AccessedResource(resource, AccessedResourceType.Modified);
			lastModified.put(resource, md);
		} else {
			lastModified.get(resource).updMTime();
		}
	}

	class CustomDeltaVisitor implements IResourceDeltaVisitor {

		@Override
		public boolean visit(IResourceDelta delta) throws CoreException {

			IResource resource = delta.getResource();

//			int flags = delta.getFlags();

			int resourceDeltaKind = delta.getKind();
			switch (resourceDeltaKind) {
			case IResourceDelta.ADDED:
			case IResourceDelta.REMOVED:
			case IResourceDelta.CHANGED:
				reportResource(resource);
				break;
			case IResourceDelta.OPEN:
				System.out.println("Opened");
				break;
			}

			return delta.getAffectedChildren().length > 0;
		}
	}

	@Override
	protected IProject[] build(int kind, Map<String, String> args, IProgressMonitor monitor) throws CoreException {

		if (kind == FULL_BUILD) {
			return null;
		}

		IResourceDelta delta = getDelta(getProject());

		if (delta != null) {
			incrementalBuild(delta, monitor);
			System.out.println(lastModified);
		}

		return null;
	}

	public void reportResource(IResource resource) {
		if (resource instanceof IFile && resource.getName().endsWith(".java")) {
			String name = resource.getName();
			addModified(name);
		}
	}

	private void incrementalBuild(IResourceDelta delta, IProgressMonitor monitor) throws CoreException {
		delta.accept(new CustomDeltaVisitor());
	}

}
