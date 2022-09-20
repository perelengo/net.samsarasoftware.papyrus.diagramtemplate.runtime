package net.samsarasoftware.papyrus.diagramtemplate.packageclassesdiagram;

import java.net.URL;
import java.util.EnumSet;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.elk.core.LayoutConfigurator;
import org.eclipse.elk.core.math.ElkPadding;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.core.options.EdgeRouting;
import org.eclipse.elk.core.options.SizeConstraint;
import org.eclipse.elk.core.options.SizeOptions;
import org.eclipse.elk.core.service.DiagramLayoutEngine;
import org.eclipse.elk.core.service.ILayoutListener;
import org.eclipse.elk.core.service.LayoutConnectorsService;
import org.eclipse.elk.core.service.LayoutMapping;
import org.eclipse.elk.core.service.DiagramLayoutEngine.Parameters;
import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.graph.ElkGraphElement;
import org.eclipse.elk.graph.ElkNode;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.DiagramEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.GraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.parts.DiagramEditor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.uml2.uml.Classifier;
import org.osgi.framework.Bundle;

import com.google.common.collect.BiMap;

import net.samsarasoftware.papyrus.diagramtemplate.runtime.DiagramTemplateLauncher;

public class PackageClassesDiagramLauncher extends DiagramTemplateLauncher {

	protected  URI getTemplateURI() throws Exception {
		Bundle bundle = Platform.getBundle("net.samsarasoftware.papyrus.diagramtemplate.packageclassesdiagram");
		URL url = FileLocator.find(bundle, new Path("resources/script.di"), null);
		url = FileLocator.toFileURL(url);
		URI uri= URI.createURI(url.toString());
		return uri;	
	}
	
	/**
	 * Helper method used to arrange recursively editparts
	 * @param activeEditor 
	 * @param editingDomain 
	 * @param diagramEditPart 
	 *
	 * @param editpart
	 *            the editpart to process
	 */
	protected void arrangeRecursively(IEditorPart activeEditor, EditPart editPart, TransactionalEditingDomain editingDomain, DiagramEditPart diagramEditPart) {
			//configure ELK https://www.eclipse.org/elk/documentation/tooldevelopers/usingeclipselayout/advancedconfiguration.html
			Parameters params = new Parameters();
	        LayoutConfigurator config=new LayoutConfigurator();
	        	params.getGlobalSettings()
	        	.setProperty(CoreOptions.ANIMATE, false)
	            .setProperty(CoreOptions.PROGRESS_BAR, false)
	            .setProperty(CoreOptions.LAYOUT_ANCESTORS, false)
	        	;
	            
	        	config.configure(ElkNode.class)
	        	.setProperty(CoreOptions.EDGE_ROUTING,EdgeRouting.POLYLINE)
	        	.setProperty(CoreOptions.NODE_SIZE_CONSTRAINTS, EnumSet.of(SizeConstraint.MINIMUM_SIZE,SizeConstraint.NODE_LABELS,SizeConstraint.PORT_LABELS,SizeConstraint.PORTS))
	            .setProperty(CoreOptions.NODE_SIZE_MINIMUM,new KVector(100,100))
	        	.setProperty(CoreOptions.NODE_SIZE_OPTIONS, EnumSet.of(SizeOptions.COMPUTE_PADDING,SizeOptions.MINIMUM_SIZE_ACCOUNTS_FOR_PADDING,SizeOptions.UNIFORM_PORT_SPACING,SizeOptions.ASYMMETRICAL))
	            .setProperty(CoreOptions.PADDING, new ElkPadding(100d, 100d, 100d, 100d))
	            .setProperty(CoreOptions.SPACING_COMMENT_COMMENT, 100d)
	            .setProperty(CoreOptions.SPACING_COMMENT_NODE, 100d)
	            .setProperty(CoreOptions.SPACING_COMPONENT_COMPONENT, 100d)
	            .setProperty(CoreOptions.SPACING_EDGE_EDGE, 100d)
	            .setProperty(CoreOptions.SPACING_EDGE_LABEL, 100d)
	            .setProperty(CoreOptions.SPACING_EDGE_NODE, 100d)
	            .setProperty(CoreOptions.SPACING_LABEL_LABEL, 100d)
	            .setProperty(CoreOptions.SPACING_LABEL_NODE, 100d)
	            .setProperty(CoreOptions.SPACING_LABEL_PORT, 100d)
	            .setProperty(CoreOptions.SPACING_NODE_NODE, 100d)
	            .setProperty(CoreOptions.SPACING_NODE_SELF_LOOP, 100d)
	            .setProperty(CoreOptions.SPACING_PORT_PORT, 100d);
	            
	        	params.addLayoutRun(config);
	        	
	        	DiagramLayoutEngine.invokeLayout((DiagramEditor) activeEditor, editPart,  params);
	        	
	}
	
}
