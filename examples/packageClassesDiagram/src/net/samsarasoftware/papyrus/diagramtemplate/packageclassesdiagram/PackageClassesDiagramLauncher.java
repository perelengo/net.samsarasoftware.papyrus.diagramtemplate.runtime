package net.samsarasoftware.papyrus.diagramtemplate.packageclassesdiagram;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.EnumSet;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.runtime.Platform;
import org.eclipse.elk.core.LayoutConfigurator;
import org.eclipse.elk.core.math.ElkPadding;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.core.options.EdgeRouting;
import org.eclipse.elk.core.options.SizeConstraint;
import org.eclipse.elk.core.options.SizeOptions;
import org.eclipse.elk.core.service.DiagramLayoutEngine;
import org.eclipse.elk.core.service.DiagramLayoutEngine.Parameters;
import org.eclipse.elk.core.service.ILayoutListener;
import org.eclipse.elk.core.service.LayoutConnectorsService;
import org.eclipse.elk.core.service.LayoutMapping;
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
		Bundle bundle = Platform.getBundle(this.getClass().getPackageName());
		InputStream diIs=null;
		FileOutputStream diOs=null;
		InputStream notIs=null;
		FileOutputStream  notOs=null;
		InputStream umlIs=null;
		FileOutputStream  umlOs=null;
		
		diIs=bundle.getEntry("resources/script.di").openStream();
		notIs=bundle.getEntry("resources/script.notation").openStream();
		umlIs=bundle.getEntry("resources/script.uml").openStream();

		File diFile=new File(System.getProperty("java.io.tmpdir")+((System.getProperty("java.io.tmpdir").endsWith(File.separator))?"":File.separator)+"script.di");
		File notFile=new File(diFile.getAbsolutePath().substring(0,diFile.getAbsolutePath().length()-2)+"notation");
		File umlFile=new File(diFile.getAbsolutePath().substring(0,diFile.getAbsolutePath().length()-2)+"uml");

		
		diOs=new FileOutputStream(diFile);
		notOs=new FileOutputStream(notFile);
		umlOs=new FileOutputStream(umlFile);
		
		diFile.deleteOnExit();
		notFile.deleteOnExit();
		umlFile.deleteOnExit();
		
		IOUtils.copy(diIs, diOs);
		IOUtils.copy(notIs, notOs);
		IOUtils.copy(umlIs, umlOs);

		return URI.createFileURI(diFile.getAbsolutePath());	
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
	protected void arrangeRecursively(IEditorPart activeEditor, EditPart editPart, TransactionalEditingDomain editingDomain, EditPart diagramEditPart) {
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
	        	
	        	/**
	        	 * We can't find a way to make layered algorithm layout classes and packages as different sizes, so we have to configure each ELKElement whose
	        	 * UML related element is instance of classifier.
	        	 *
	        	 * To be able to configure each node separately, we have to add a layout listener to be able to have access to the LayoutMapping
	        	 * Then add payout runs to the params foreach of the classifier nodes
	        	 **/
	        	 LayoutConnectorsService.getInstance().addLayoutListener(new ILayoutListener() {
					
					@Override
					public void layoutDone(LayoutMapping mapping, IElkProgressMonitor progressMonitor) {
						
					}
					
					@Override
					public void layoutAboutToStart(LayoutMapping mapping, IElkProgressMonitor progressMonitor) {
						BiMap<ElkGraphElement, Object> map = (mapping.getGraphMap());
						for (ElkGraphElement elkElement : map.keySet()) {
							EObject semantic = ((GraphicalEditPart)map.get(elkElement)).resolveSemanticElement();
							if(semantic instanceof Classifier) {
								LayoutConfigurator config2=new LayoutConfigurator();
								config2.configure(elkElement)
								.setProperty(CoreOptions.NODE_SIZE_CONSTRAINTS, EnumSet.of(SizeConstraint.MINIMUM_SIZE,SizeConstraint.NODE_LABELS,SizeConstraint.PORT_LABELS,SizeConstraint.PORTS))
					            .setProperty(CoreOptions.NODE_SIZE_MINIMUM,new KVector(100,100));
								params.addLayoutRun(config2);
							}
						}
					}
				});
	        	 
	        	DiagramLayoutEngine.invokeLayout((DiagramEditor) activeEditor, editPart,  params);
	        	
	}
	
}
