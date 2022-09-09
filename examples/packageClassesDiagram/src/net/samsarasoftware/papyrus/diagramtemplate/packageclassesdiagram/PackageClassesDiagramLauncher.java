package net.samsarasoftware.papyrus.diagramtemplate.packageclassesdiagram;

import org.eclipse.emf.common.util.URI;

import net.samsarasoftware.papyrus.diagramtemplate.runtime.DiagramTemplateLauncher;

public class PackageClassesDiagramLauncher extends DiagramTemplateLauncher {

	protected  URI getTemplateURI() {
		return URI.createPlatformPluginURI("net.samsarasoftware.papyrus.diagramtemplate.packageclassesdiagram/resources/script.di",true);
	}
	
}
