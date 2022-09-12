package net.samsarasoftware.papyrus.diagramtemplate.qvtopackageclassesdiagram;

import java.io.File;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.uml2.uml.resource.UMLResource;

import net.samsarasoftware.papyrus.diagramtemplate.runtime.DiagramTemplateLauncher;
import net.samsarasoftware.papyrus.diagramtemplate.runtime.ScriptingEngineTemplateProcessor;
import net.samsarasoftware.papyrus.diagramtemplate.runtime.TemplateProcessor;

public class QvtoPackageClassesDiagramLauncher extends DiagramTemplateLauncher {

	protected  URI getTemplateURI() {
		return URI.createPlatformPluginURI("net.samsarasoftware.papyrus.diagramtemplate.qvtopackageclassesdiagram/resources/script.di",true);
	}
	
	/**
	 * Execute the qvto template to generate the uml with viewModel stereotypes applied
	 * @param pluginDiagramTemplateDiURI
	 * @param templateResultFilelPath
	 * @param editor
	 * @param modelSet
	 * @return
	 * @throws Exception
	 */
	public File createViewModel(Object pluginDiagramTemplateDiURI, File templateResultFilelPath, EditorPart editor, ResourceSetImpl modelSet) throws Exception {
		TemplateProcessor adapter=new ScriptingEngineTemplateProcessor();
		
		String workspacePath=ResourcesPlugin.getWorkspace().getRoot().getLocation().toString();
		
		String templateUML 	= pluginDiagramTemplateDiURI.toString().replace("platform:/resource",workspacePath);
		templateUML 	= templateUML.substring(0,templateUML.length()-2)+ "uml";

		Resource targetUMLResource=null;
		String modelResourceName = editor.getEditorInput().getName().substring(0,editor.getEditorInput().getName().lastIndexOf("."));
		for (Resource modelSetResource: modelSet.getResources()) {
			if(modelSetResource instanceof UMLResource && modelSetResource.getURI().toString().endsWith(modelResourceName+".uml"))
				targetUMLResource=modelSetResource;
		}
		
		templateResultFilelPath=adapter.process(templateUML,modelSet,targetUMLResource, templateResultFilelPath);

		return templateResultFilelPath;
		
	}
}
