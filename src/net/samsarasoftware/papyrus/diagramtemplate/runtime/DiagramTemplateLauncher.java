package net.samsarasoftware.papyrus.diagramtemplate.runtime;

/*-
 * #%L
 * net.samsarasoftware.scripting.ScriptingEngine
 * %%
 * Copyright (C) 2014 - 2020 Pere Joseph Rodriguez
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 * #L%
 */

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.core.service.DiagramLayoutEngine;
import org.eclipse.elk.core.service.DiagramLayoutEngine.Parameters;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.commands.Command;
import org.eclipse.gmf.runtime.diagram.ui.editparts.DiagramEditPart;
import org.eclipse.gmf.runtime.diagram.ui.parts.DiagramEditor;
import org.eclipse.gmf.runtime.diagram.ui.requests.DropObjectsRequest;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.papyrus.commands.CreationCommandDescriptor;
import org.eclipse.papyrus.commands.CreationCommandRegistry;
import org.eclipse.papyrus.editor.PapyrusMultiDiagramEditor;
import org.eclipse.papyrus.infra.architecture.ArchitectureDomainManager;
import org.eclipse.papyrus.infra.architecture.representation.PapyrusRepresentationKind;
import org.eclipse.papyrus.infra.core.architecture.RepresentationKind;
import org.eclipse.papyrus.infra.core.architecture.merged.MergedArchitectureContext;
import org.eclipse.papyrus.infra.core.architecture.merged.MergedArchitectureViewpoint;
import org.eclipse.papyrus.infra.core.resource.ModelMultiException;
import org.eclipse.papyrus.infra.core.resource.ModelSet;
import org.eclipse.papyrus.infra.core.sashwindows.di.service.IPageManager;
import org.eclipse.papyrus.infra.core.services.ExtensionServicesRegistry;
import org.eclipse.papyrus.infra.core.services.ServiceException;
import org.eclipse.papyrus.infra.core.services.ServicesRegistry;
import org.eclipse.papyrus.infra.core.utils.DiResourceSet;
import org.eclipse.papyrus.infra.gmfdiag.common.model.NotationUtils;
import org.eclipse.papyrus.infra.ui.editor.IMultiDiagramEditor;
import org.eclipse.papyrus.infra.viewpoints.policy.ViewPrototype;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.Stereotype;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.resource.UMLResource;

import net.samsarasoftware.papyrus.diagramtemplate.runtime.OCLTool;
import net.samsarasoftware.papyrus.diagramtemplate.runtime.ScriptingEngineTemplateProcessor;

public class DiagramTemplateLauncher extends AbstractHandler {

	

	/**
	 * The instance used for the singleton pattern
	 */
	private static DiagramTemplateLauncher instance = null;

	/**
	 * The view of the elements added
	 */
	protected List<View> elementProcessed = new ArrayList<View>();

	/** List of Available diagrams defined by the model architecture framework **/
	private ArrayList<ViewPrototype>  representationsKinds;

	/** All new diagrams created   **/
	protected HashMap<String, EObject> diagramsCreated;
	
	/** Diagrams that already existed in the model before the update **/
	private HashMap<String, EObject> diagramsToUpdate;
	
	/** Quick access to diagrams that participate in the process**/
	private HashMap<String, Object> diagramsMapping;

	
	ServicesRegistry templateRegistry ;
	
	/**
	 * Constructor.
	 * Private constructor for the singleton pattern
	 */
	public DiagramTemplateLauncher() {
	}

	/**
	 * Get the singleton
	 * @return	the DiagramTemplateLauncher singleton
	 */
	public final synchronized static DiagramTemplateLauncher getInstance() {
		if (instance == null) {
			instance = new DiagramTemplateLauncher();
		}
		return instance;
	}

	/**
	 * Diagram categories available per architecture framework definition
	 * 
	 * @param modelSet
	 * @return
	 */
	protected ArrayList<ViewPrototype> initializeDiagramCategories(ModelSet modelSet) {
		ArrayList<ViewPrototype> representationsKinds = new ArrayList<ViewPrototype>();

		ArchitectureDomainManager manager = ArchitectureDomainManager.getInstance();
		Collection<MergedArchitectureContext> contexts = manager.getVisibleArchitectureContexts();

		for (MergedArchitectureContext mergedArchitectureContext : contexts) {
			Collection<MergedArchitectureViewpoint> viewpoints = mergedArchitectureContext.getViewpoints();

			for (MergedArchitectureViewpoint mergedArchitectureViewpoint : viewpoints) {
				Collection<RepresentationKind> representations = mergedArchitectureViewpoint.getRepresentationKinds();
				for (RepresentationKind representationKind : representations) {
					if (representationKind instanceof PapyrusRepresentationKind) {
						representationsKinds.add(ViewPrototype.get((PapyrusRepresentationKind) representationKind));
					}
				}
			}
		}
		return representationsKinds;
	}

	/**
	 * Get creation commands for the input list of available diagrams defined by the architecture framework for the diagram
	 * @param categories
	 * @param diagram
	 * @return
	 */
	public CreationCommandDescriptor getCommands(List<ViewPrototype> categories, Diagram diagram) {
		for (CreationCommandDescriptor desc : CreationCommandRegistry.getInstance(org.eclipse.papyrus.infra.ui.Activator.PLUGIN_ID).getCommandDescriptors()) {
			for (ViewPrototype category : categories) {
				if (category.getLabel().equalsIgnoreCase(desc.getLabel())) {
					if(category.getRepresentationKind().getImplementationID().contentEquals(diagram.getType())) {
						return desc;
					}
				}
			}
		}
		
		return null;
	}


	
	/**
	 * This is the main method for the template launcher. Executes the template
	 * @param editor 
	 *
	 */
	public void execute(URI templateDiURI, ModelSet modelSet, IMultiDiagramEditor editor) {
		File templateResultFilelPath=null;

		ArrayList<String> diagramsInResource = new ArrayList<String>();
		ArrayList<String> templateDiagramsInResource = new ArrayList<String>();
		Diagram	templateDiagram=null;
		
		diagramsCreated = new HashMap<String, EObject>();
		diagramsToUpdate = new HashMap<String, EObject>();
		diagramsMapping=new HashMap<String, Object>();
		
		ModelSet templateModelSet 		= new DiResourceSet();
		try {
			
			templateDiURI		= getTemplateURI();
			
			//Load target DI
			representationsKinds=initializeDiagramCategories(modelSet); //[org.eclipse.papyrus.infra.gmfdiag.common.helper.DiagramPrototype@7228b434, org.eclipse.papyrus.infra.gmfdiag.common.helper.DiagramPrototype@3ce5f36a, org.eclipse.papyrus.infra.gmfdiag.common.helper.DiagramPrototype@7c777ab2, org.eclipse.papyrus.infra.nattable.common.helper.TableViewPrototype@563ef05c, org.eclipse.papyrus.infra.nattable.common.helper.TableViewPrototype@610077bb, org.eclipse.papyrus.infra.nattable.common.helper.TableViewPrototype@726da652, org.eclipse.papyrus.infra.nattable.common.helper.TableViewPrototype@3508e047, org.eclipse.papyrus.infra.nattable.common.helper.TableViewPrototype@14955aaa, org.eclipse.papyrus.infra.nattable.common.helper.TableViewPrototype@764a5a2c, org.eclipse.papyrus.infra.gmfdiag.common.helper.DiagramPrototype@690a2d81, org.eclipse.papyrus.infra.gmfdiag.common.helper.DiagramPrototype@3ce5f36a, org.eclipse.papyrus.infra.gmfdiag.common.helper.DiagramPrototype@1bf2e23f, org.eclipse.papyrus.infra.gmfdiag.common.helper.DiagramPrototype@258cabe1, org.eclipse.papyrus.infra.gmfdiag.common.helper.DiagramPrototype@1b30e46, org.eclipse.papyrus.infra.gmfdiag.common.helper.DiagramPrototype@396d5dd4, org.eclipse.papyrus.infra.gmfdiag.common.helper.DiagramPrototype@3502a3dc, org.eclipse.papyrus.infra.gmfdiag.common.helper.DiagramPrototype@7591c53e, org.eclipse.papyrus.infra.gmfdiag.common.helper.DiagramPrototype@6c4924f4, org.eclipse.papyrus.infra.gmfdiag.common.helper.DiagramPrototype@3f994754, org.eclipse.papyrus.infra.gmfdiag.common.helper.DiagramPrototype@7c777ab2, org.eclipse.papyrus.infra.gmfdiag.common.helper.DiagramPrototype@1f250f38, org.eclipse.papyrus.infra.gmfdiag.common.helper.DiagramPrototype@64e681d7, org.eclipse.papyrus.infra.gmfdiag.common.helper.DiagramPrototype@412d673b, org.eclipse.papyrus.infra.nattable.common.helper.TableViewPrototype@563ef05c, org.eclipse.papyrus.infra.nattable.common.helper.TableViewPrototype@610077bb, org.eclipse.papyrus.infra.nattable.common.helper.TableViewPrototype@726da652, org.eclipse.papyrus.infra.nattable.common.helper.TableViewPrototype@3508e047, org.eclipse.papyrus.infra.nattable.common.helper.TableViewPrototype@14955aaa, org.eclipse.papyrus.infra.nattable.common.helper.TableViewPrototype@764a5a2c, org.eclipse.papyrus.infra.gmfdiag.common.helper.DiagramPrototype@34fb3bad, org.eclipse.papyrus.infra.gmfdiag.common.helper.DiagramPrototype@7c777ab2, org.eclipse.papyrus.infra.nattable.common.helper.TableViewPrototype@726da652]
		
				/** Available diagrams and its related classes
				for (ViewPrototype viewPrototype : representationsKinds) {
					System.out.println(viewPrototype.getFullLabel()+":"+viewPrototype.getImplementation());
						Use Case Diagram for PackageClass:UseCase
						Class Diagram for Package:PapyrusUMLClassDiagram
						Package Diagram for Package:PapyrusUMLClassDiagram
						Class Tree Table for Package:PapyrusClassTreeTable
						Generic Table for NamedElement:PapyrusGenericTable
						Generic Tree Table for Element:PapyrusUMLGenericTreeTable
						Stereotype Display Tree Table for View:PapyrusStereotypeDisplayTreeTable
						View Table for NamedElement:PapyrusViewsTable
						Relationship Generic Matrix for Package:UMLGenericMatrixOfRelationships
						Activity Diagram for Activity:PapyrusUMLActivityDiagram
						Class Diagram for Package:PapyrusUMLClassDiagram
						Communication Diagram for Interaction:PapyrusUMLCommunicationDiagram
						Component Diagram for Package:PapyrusUMLComponentDiagram
						Component Diagram for Component:PapyrusUMLComponentDiagram
						Composite Structure Diagram for Package:CompositeStructure
						Composite Structure Diagram for StructuredClassifier:CompositeStructure
						Deployment Diagram for Package:PapyrusUMLDeploymentDiagram
						Inner Class Diagram for Class:PapyrusUMLClassDiagram
						Interaction Overview Diagram for Activity:PapyrusUMLInteractionOverviewDiagram
						Package Diagram for Package:PapyrusUMLClassDiagram
						Sequence Diagram for Interaction:PapyrusUMLSequenceDiagram
						State Machine Diagram for StateMachineState:PapyrusUMLStateMachineDiagram
						Timing Diagram for Package:PapyrusUMLTimingDiagram
						Class Tree Table for Package:PapyrusClassTreeTable
						Generic Table for NamedElement:PapyrusGenericTable
						Generic Tree Table for Element:PapyrusUMLGenericTreeTable
						Stereotype Display Tree Table for View:PapyrusStereotypeDisplayTreeTable
						View Table for NamedElement:PapyrusViewsTable
						Relationship Generic Matrix for Package:UMLGenericMatrixOfRelationships
						Profile Diagram for Package:PapyrusUMLProfileDiagram
						Package Diagram for Package:PapyrusUMLClassDiagram
						Generic Tree Table for Element:PapyrusUMLGenericTreeTable
				}
			 */
			
			//Load template DI
			templateModelSet.loadModels(templateDiURI);
			//ArrayList<ViewPrototype> templateRepresentationKinds=initializeDiagramCategories(templateModelSet);
			
			
		} catch (ModelMultiException ex) {
			ex.printStackTrace(System.out);
		}

		try {
//			modelRegistry = new ExtensionServicesRegistry(org.eclipse.papyrus.infra.core.Activator.PLUGIN_ID);
//			modelRegistry.add(ModelSet.class, Integer.MAX_VALUE, modelSet);
//			try {
//				//we need to start the service
//				//more info at https://www.eclipse.org/forums/index.php/t/840760/
//				modelRegistry.startRegistry();
//			} catch (ServiceException ex) {
//				// Ignore
//			}

			templateRegistry = new ExtensionServicesRegistry(org.eclipse.papyrus.infra.core.Activator.PLUGIN_ID);
			templateRegistry.add(ModelSet.class, Integer.MAX_VALUE, templateModelSet);
			try {
				templateRegistry.startRegistry();
			} catch (ServiceException ex) {
				// Ignore
			}
			

			// Identify already available diagrams
			Resource modelSetNotation = NotationUtils.getNotationResource(modelSet);
			diagramsInResource.addAll(getAllDiagramsInNotation(modelSetNotation));
			
			// Identify already available template diagrams
			Resource templateModelSetNotation = NotationUtils.getNotationResource(templateModelSet);
			TreeIterator<EObject> it2 = templateModelSetNotation.getAllContents();
			while (it2.hasNext()) {
				EObject diagram = it2.next();
				if (diagram instanceof Diagram) {
					templateDiagramsInResource.add(((Diagram) diagram).getName());
					templateDiagram=(Diagram) diagram;
				}
			}

			//FASE 1 Create diagrams
			//Pasos:
			//1- Invocar el UmlScriptingEngine sobre el modelo template para generar la qvto
			//Ejecutar la qvto sobre el modelo target
			//Se generarán N paquetes estereotipados con ViewContainer.
			//Cada paquete <<ViewContainer>> debería contener un solo diagrama
			//Por cada diagrama del paquete
			//mirar si el modelo destino tiene un diagrama con el mismo nombre
			//Si no existe, crearlo vacío
			
			//FASE 2 : update diagrams
			//Por cada elemento que aparece en el nuevo diagrama
			//Mirar si existe el elemento uml relacionado al elemento gráfico del antiguo diagrama o se ha eliminado
			//si se ha eliminado, eliminar del diagrama
			//Mirar si ya está añadido al antiguo diagrama
			//Si no está añadido, añadirlo
			
			try {
				/** 1- Invocar el UmlScriptingEngine sobre el modelo template para generar la qvto **/
				ScriptingEngineTemplateProcessor adapter=new ScriptingEngineTemplateProcessor();
				
				String workspacePath=ResourcesPlugin.getWorkspace().getRoot().getLocation().toString();
				
				String templateUML 	= templateDiURI.toString().replace("platform:/resource",workspacePath);
				templateUML 	= templateUML.substring(0,templateUML.length()-2)+ "uml";
				templateResultFilelPath=File.createTempFile(templateUML.substring(0,templateUML.lastIndexOf("."))+"_transform",".uml");
				templateResultFilelPath.deleteOnExit();
				
				Resource targetUMLResource=null;
				String modelResourceName = editor.getEditorInput().getName().substring(0,editor.getEditorInput().getName().lastIndexOf("."));
				for (Resource modelSetResource: modelSet.getResources()) {
					if(modelSetResource instanceof UMLResource && modelSetResource.getURI().toString().endsWith(modelResourceName+".uml"))
						targetUMLResource=modelSetResource;
				}
				
				adapter.process(templateUML,modelSet,targetUMLResource, templateResultFilelPath);
								
				/** 2-Cada paquete <<ViewContainer>> debería contener un solo diagrama **/
				//TEST 1- ResourceSet transformedResourceSet = refreshResourceSet(scriptingEngine);
				
				//get the transformed resource
				//TEST 1- Resource resource = transformedResourceSet.getResource(URI.createFileURI(templateResultFilelPath.getPath()), true);
				Resource resource = modelSet.getResource(URI.createFileURI(templateResultFilelPath.getPath()), true);
				//get the context classifier
				Model templateResultFilelPathModel = (Model) EcoreUtil.getObjectByType(resource.getContents(),UMLPackage.Literals.MODEL);

				// create an OCLTool
				//TEST 1 -OCLTool oclTool = new OCLTool((EClass) templateResultFilelPathModel.eClass(), transformedResourceSet.getPackageRegistry());
				OCLTool oclTool = new OCLTool((EClass) templateResultFilelPathModel.eClass(), modelSet.getPackageRegistry());

				String viewContainersQuery="self.oclAsType(Model).allOwnedElements()->select(e | not e.oclAsType(Element).getAppliedStereotype('modelview::ViewContainer').oclIsUndefined())";
				//String viewElementsQuery="self.oclAsType(Element).allOwnedElements()->select(e | not e.oclAsType(Element).getAppliedStereotype('modelview::ViewOf').oclIsUndefined())";
				HashSet viewContainers = (HashSet) oclTool.evaluateQuery(viewContainersQuery,templateResultFilelPathModel);
				//foreach viewContainer
				for (Object containerObject: viewContainers) {
					org.eclipse.uml2.uml.Package viewContainer=(Package) containerObject;
					//HashSet views=(HashSet) oclTool.evaluateQuery(viewElementsQuery,viewContainer);
					
					if(!diagramsInResource.contains(viewContainer.getName())) {
						//If not already created, create the diagram
						CreationCommandDescriptor creationCommandDescriptor = getCommands(representationsKinds,templateDiagram);
						if (creationCommandDescriptor != null) {
	
	
							EObject root=(EObject) viewContainer.getValue(viewContainer.getAppliedStereotype("modelview::ViewContainer"),"targetElement");
							creationCommandDescriptor.getCommand().createDiagram(modelSet, root, viewContainer.getName());
							
							Diagram newDiagram = getDiagramByName(modelSetNotation, viewContainer.getName());
							diagramsCreated.put(viewContainer.getName(),newDiagram);
							diagramsMapping.put(((Diagram) newDiagram).getName(),containerObject);
							
							
						}else {
							MessageDialog.openError(PlatformUI.getWorkbench().getDisplay().getActiveShell(), "Error while generating diagrams","Unknown diagram type "+templateDiagram.getName());
						}
						
					}else {
						//diagram to update
						Diagram diagram = getDiagramByName(modelSetNotation, viewContainer.getName());
						diagramsToUpdate.put(viewContainer.getName(), diagram);
						diagramsMapping.put(((Diagram) diagram).getName(),containerObject);
						
					}
					
				}
				
				// Save the resource
				//modelSet.save(new NullProgressMonitor());
				
			} catch (Exception e1) {
				e1.printStackTrace(System.out);
			}



		} catch (ServiceException ex) {
			ex.printStackTrace(System.out);
		}
		//FASE 1 completada
		
		try {
			fillDiagram(editor, modelSet,templateResultFilelPath);
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
		}
		try {
			modelSet.getResource(URI.createFileURI(templateResultFilelPath.getPath()),true).unload();
			templateRegistry.disposeRegistry();
		} catch (ServiceException ex) {
			// Ignore
		}
	}



	private Diagram getDiagramByName(Resource modelSetNotation, String name) {
		TreeIterator<EObject> it = modelSetNotation.getAllContents();
		while (it.hasNext()) {
			EObject diagram = it.next();
			if (diagram instanceof Diagram && ((Diagram) diagram).getName().equals(name)) {
				return (Diagram)diagram;
			}
		}
		return null;
	}

	private Collection<? extends String> getAllDiagramsInNotation(Resource modelSetNotation) {
		ArrayList<String> diagramsInResource = new ArrayList<String>();
		TreeIterator<EObject> it = modelSetNotation.getAllContents();
		while (it.hasNext()) {
			EObject diagram = it.next();
			if (diagram instanceof Diagram) {
				diagramsInResource.add(((Diagram) diagram).getName());
			}
		}
		return diagramsInResource;
	}

	public void fillDiagram(IMultiDiagramEditor editor, ModelSet modelSet, File templateResultFilelPath) throws PartInitException, ServiceException {

		final ServicesRegistry services = editor.getServicesRegistry();
		TransactionalEditingDomain editingDomain = services.getService(TransactionalEditingDomain.class);
		org.eclipse.emf.common.command.Command openPagesCommand = new RecordingCommand(editingDomain, "Open created pages") {

			@Override
			protected void doExecute() {
				try {
					System.out.println("Executing");
					IPageManager pageManager = services.getService(IPageManager.class);


					System.out.println("Close all pages");
					pageManager.closeAllOpenedPages();

					// Go through the diagrams available in the resource
					for (Object pageDiagram : pageManager.allPages()) {

						if (pageDiagram instanceof Diagram) {
							//String pageID = ((Diagram) pageDiagram).eResource().getURIFragment((Diagram) pageDiagram);
							
							String pageID = ((Diagram) pageDiagram).getName();
							
							if (diagramsCreated.containsKey(pageID) || diagramsToUpdate.containsKey(pageID)) {
								System.out.println("Open page "+pageID);
								pageManager.openPage(pageDiagram);
								IEditorPart activeEditor = ((PapyrusMultiDiagramEditor) editor).getActiveEditor();

								if (activeEditor instanceof DiagramEditor) {
									// Get the GraphicalViewer for this diagram
									Object result = activeEditor.getAdapter(GraphicalViewer.class);
									if (result != null && result instanceof GraphicalViewer) {
										DiagramEditPart diagramEditPart = (DiagramEditPart) ((GraphicalViewer) result).getEditPartRegistry().get(pageDiagram);

										// Retrieve the selection to show for this diagram
										Object selection = diagramsMapping.get(pageID);
										addElementsFor((EObject) selection, ((Diagram) pageDiagram).getElement(), (DiagramEditor) activeEditor, diagramEditPart);

										// Arrange all recursively
										arrangeRecursively(activeEditor,diagramEditPart);
									}

									// This page is processed now (may be not necessary)
									diagramsCreated.remove(pageID);
								}
							}
						}
					}
				} catch (ServiceException ex) {
					ex.printStackTrace(System.out);
				}
			}
		};

		editingDomain.getCommandStack().execute(openPagesCommand);
	}

	/**
	 * Find the element to show depending on a list and try to add them to a specific editPart
	 *
	 * @param ((EObject)selection
	 *            The selection list of elements to add to the editPart
	 * @param root
	 *            The root to search the elements from
	 * @param activeEditor
	 *            the editor corresponding to the editPart
	 * @param editPartToShowIn
	 *            the editPart to show elements in
	 */
	protected void addElementsFor(EObject selection, EObject root, DiagramEditor activeEditor, EditPart editPartToShowIn) {
		// Go through the SelectionRef
		TreeIterator<EObject> it = ((EObject)selection).eAllContents();
		
		while (it.hasNext()) {
			EObject eObject = it.next();
			EditPart actualEditPart = showElementIn(eObject, activeEditor, editPartToShowIn, 0);
			//processRecursively(actualEditPart, eObject, selectionRef, activeEditor);
		}
	}

	/**
	 * Util method used to find all the children of a certain editpart
	 *
	 * @param list
	 *            the children found recursively
	 * @param root
	 *            the root editpart to start the search from
	 */
	protected void findAllChildren(List<EditPart> list, EditPart root) {
		list.addAll(root.getChildren());
		for (Object editPart : root.getChildren()) {
			if (editPart instanceof EditPart) {
				findAllChildren(list, (EditPart) editPart);
			}
		}
	}

	/**
	 * Try to show an element in an editPart (or its children)
	 *
	 * @param elementToShow
	 *            the element to show
	 * @param activeEditor
	 *            the editor corresponding to the editPart
	 * @param editPart
	 *            the editPart to show the element in
	 * @param position
	 *            position is used to try to distribute the drop
	 * @return
	 * 		the editPart in which the element has been actually added
	 */
	protected EditPart showElementIn(EObject elementToShow, DiagramEditor activeEditor, EditPart editPart, int position) {


		EditPart returnEditPart = null;

		if (elementToShow instanceof Element) {

			DropObjectsRequest dropObjectsRequest = new DropObjectsRequest();
			ArrayList<Element> list = new ArrayList<Element>();
			Stereotype st=((Element) elementToShow).getAppliedStereotype("modelview::ViewOf");
			Element resolved=(Element) ((Element) elementToShow).getValue(st, "relatedElement");
			list.add(resolved);
			dropObjectsRequest.setObjects(list);
			dropObjectsRequest.setLocation(new Point(20, 100 * position));
			Command commandDrop = editPart.getCommand(dropObjectsRequest);

			boolean processChildren = false;
			if (commandDrop == null) {
				processChildren = true;
			} else {
				if (commandDrop.canExecute()) {
					activeEditor.getDiagramEditDomain().getDiagramCommandStack().execute(commandDrop);
					returnEditPart = editPart;
				} else {
					processChildren = true;
				}
			}

			if (processChildren) {
				// try to add to one of its children
				boolean found = false;

				ArrayList<EditPart> childrenList = new ArrayList<EditPart>();
				findAllChildren(childrenList, editPart);
				for (Object child : childrenList) {
					if (child instanceof EditPart) {
						Command commandDropChild = ((EditPart) child).getCommand(dropObjectsRequest);
						if (commandDropChild != null) {
							if (commandDropChild.canExecute()) {
								activeEditor.getDiagramEditDomain().getDiagramCommandStack().execute(commandDropChild);
								found = true;
								returnEditPart = (EditPart) child;
								break;
							}
						}
					}
				}
				if (!found) {
					returnEditPart = editPart;
				}
			}
		}

		return returnEditPart;
	}

	/**
	 * Utils method that determine whether an element is stereotypedBy a certain stereotype qualiedName
	 *
	 * @param element
	 *            the element to test
	 * @param stereotypedBy
	 *            the qulifiedName of the stereotype to match
	 * @return
	 * 		true if matches false else.
	 */
	protected boolean matchStereotypedBy(EObject element, String stereotypedBy) {
		if (element instanceof Element) {
			// Read stereotypedBy
			stereotypedBy = stereotypedBy.replaceAll(" ", ""); //$NON-NLS-1$ //$NON-NLS-2$
			String[] stereotypes = stereotypedBy.split(","); //$NON-NLS-1$

			boolean matchStereotypes = true;
			for (String stereo : stereotypes) {
				if (stereo != null && stereo.length() > 0) {
					if (((Element) element).getAppliedStereotype(stereo) != null) {
						matchStereotypes = true;
					} else {
						matchStereotypes = false;
						break;
					}
				}
			}

			return matchStereotypes;
		}

		return false;
	}

	/**
	 * Helper method used to arrange recursively editparts
	 * @param activeEditor 
	 *
	 * @param editpart
	 *            the editpart to process
	 */
	protected void arrangeRecursively(IEditorPart activeEditor, EditPart editPart) {

		//configure ELK
		Parameters params = new Parameters();
        params.getGlobalSettings()
            .setProperty(CoreOptions.ANIMATE, false)
            .setProperty(CoreOptions.PROGRESS_BAR, false)
            .setProperty(CoreOptions.LAYOUT_ANCESTORS, false)
            .setProperty(CoreOptions.ZOOM_TO_FIT, false);
        
        //invoke ELK on all elements
        for (Object element : editPart.getChildren()) {
			if (element instanceof EditPart) {
				DiagramLayoutEngine.invokeLayout((DiagramEditor) activeEditor, (DiagramEditPart)editPart,  params);
				arrangeRecursively(activeEditor,(EditPart) element);
			}
		}
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IEditorPart editor=page.getActiveEditor();
		
		if (editor instanceof IMultiDiagramEditor) {
			PapyrusMultiDiagramEditor papyrusEditor = (PapyrusMultiDiagramEditor)editor;
			try {
				ModelSet modelSet = papyrusEditor.getServicesRegistry().getService(ModelSet.class);
				execute(null,modelSet, (IMultiDiagramEditor) editor);
			} catch (ServiceException e) {
				throw new ExecutionException(e.getMessage(), e);
			}
		}
		return null; 
	}

	protected  URI getTemplateURI() {
		return URI.createPlatformResourceURI("test/resources/script.di");
	}
	
}
