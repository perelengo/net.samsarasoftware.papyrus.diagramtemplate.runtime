package net.samsarasoftware.papyrus.diagramtemplate.packageclassesdiagram;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.util.URI;
import org.osgi.framework.Bundle;

import net.samsarasoftware.papyrus.diagramtemplate.runtime.DiagramTemplateLauncher;

public class PackageClassesDiagramLauncher extends DiagramTemplateLauncher {

	protected  URI getTemplateURI() throws Exception {
		Bundle bundle = Platform.getBundle("net.samsarasoftware.papyrus.diagramtemplate.packageclassesdiagram");
		URL url = FileLocator.find(bundle, new Path("resources/script.di"), null);
		url = FileLocator.toFileURL(url);
		URI uri= URI.createURI(url.toString());
		return uri;	
	}
	
}
