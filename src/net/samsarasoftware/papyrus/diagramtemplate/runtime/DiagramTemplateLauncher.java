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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.core.service.DiagramLayoutEngine;
import org.eclipse.elk.core.service.DiagramLayoutEngine.Parameters;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.commands.Command;
import org.eclipse.gmf.runtime.diagram.ui.editparts.DiagramEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.GraphicalEditPart;
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
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.Stereotype;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.resource.UMLResource;

public class DiagramTemplateLauncher extends AbstractHandler {

	

	/**
	 * The instance used for the singleton pattern
	 */
	private static DiagramTemplateLauncher instance = null;

	
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


	protected  URI getTemplateURI() {
		return URI.createPlatformResourceURI("test/resources/script.di");
	}

	public File getViewModelFilePath() {
		return null;
	}

	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IEditorPart editor=page.getActiveEditor();
		
		if (editor instanceof IMultiDiagramEditor) {
			PapyrusMultiDiagramEditor papyrusEditor = (PapyrusMultiDiagramEditor)editor;
			try {
				ModelSet modelSet = papyrusEditor.getServicesRegistry().getService(ModelSet.class);
				execute(modelSet, (IMultiDiagramEditor) editor);
			} catch (Exception e) {
				throw new ExecutionException(e.getMessage(), e);
			}
		}
		return null; 
	}

	/**
	 * This is the main method for the template launcher. Executes the template
	 * @param editor 
	 * @throws Exception 
	 *
	 */
	public void execute(ModelSet targetModelSet, IMultiDiagramEditor editor) throws Exception {

		 
		// Identify already available template diagrams
		ArrayList<ViewPrototype>  	representationsKinds=initializeDiagramCategories(targetModelSet); // List of Available diagrams defined by the model architecture framework
		
		//Log list of available diagrams and its related classes
		for (ViewPrototype viewPrototype : representationsKinds) {
			System.out.println(viewPrototype.getFullLabel()+":"+viewPrototype.getImplementation());
		}
		
		

		//Load template DI
		URI 						pluginDiagramTemplateDiURI 		= getTemplateURI();
		Diagram						pluginDiagramTemplateDiagram	= null;
		ModelSet 					pluginDiagramTemplateModelSet 	= new DiResourceSet();
		pluginDiagramTemplateModelSet.loadModels(pluginDiagramTemplateDiURI);
		Resource 					pluginDiagramTemplateModelSetNotation 	= NotationUtils.getNotationResource(pluginDiagramTemplateModelSet); 		//Get template model set notation resource

		//Load target DI
		File 						viewModeltFilelPath				= getViewModelFilePath();
		Resource 					targetModelSetNotation			= NotationUtils.getNotationResource(targetModelSet);
		ServicesRegistry 			targetModelSetRegistry 			= new ExtensionServicesRegistry(org.eclipse.papyrus.infra.core.Activator.PLUGIN_ID);
		try {
			targetModelSetRegistry.add(ModelSet.class, Integer.MAX_VALUE, pluginDiagramTemplateModelSet);
			targetModelSetRegistry.startRegistry();
		} catch (ServiceException ex) {
			// Ignore errors
		}

		
		ArrayList<String> 			diagramsInInputModelResource 			= new ArrayList<String>();
		diagramsInInputModelResource.addAll(getAllDiagramsInNotation(targetModelSetNotation)); // Identify already available diagrams
		
		ArrayList<String> 			templatediagramsInInputModelResource 	= new ArrayList<String>();
		HashMap<String, Object> 	diagramsMapping				= new HashMap<String, Object>();		// Quick access to diagrams that participate in the process
		HashMap<String, EObject> 	diagramsCreated 			= new HashMap<String, EObject>(); // All new diagrams created
		HashMap<String, EObject> 	diagramsToUpdate			= new HashMap<String, EObject>(); // All existing diagrams to update
		//List<View> 					elementProcessed 			= new ArrayList<View>(); 	//The view of the elements added
		
		
		/**This template plugin can only have one diagram, because it must just create one type of diagrams*/
		TreeIterator<EObject> it2 = pluginDiagramTemplateModelSetNotation.getAllContents();
		while (it2.hasNext()) {
			EObject diagram = it2.next();
			if (diagram instanceof Diagram) {
				templatediagramsInInputModelResource.add(((Diagram) diagram).getName());
				pluginDiagramTemplateDiagram=(Diagram) diagram;
				break;
			}
		}
		
		
		/** 1- Invoke UmlScriptingEngine to generate qvto **/
		viewModeltFilelPath=createViewModelAndHandleExceptions(pluginDiagramTemplateDiURI,  viewModeltFilelPath, (EditorPart)editor, targetModelSet);
			
		/** 2-Cada paquete <<ViewContainer>> debería contener un solo diagrama **/
		Resource resource = targetModelSet.getResource(URI.createFileURI(viewModeltFilelPath.getPath()), true);
		//get the context classifier
		Model templateResultFilelPathModel = (Model) EcoreUtil.getObjectByType(resource.getContents(),UMLPackage.Literals.MODEL);
		
		createDiagramsAndHandleExceptions(templateResultFilelPathModel, targetModelSet, targetModelSetNotation, representationsKinds, pluginDiagramTemplateDiagram, diagramsInInputModelResource, diagramsCreated, diagramsToUpdate, diagramsMapping);
		
		// Save the resource
		saveModelSetAndHandleExceptions(targetModelSet);
		
		/** Fill diagrams **/
		fillDiagramAndHandleExceptions(editor, pluginDiagramTemplateModelSet, viewModeltFilelPath, diagramsCreated,diagramsToUpdate,diagramsMapping);
		
		/** Finalize resouces **/
		try {
			targetModelSet.getResource(URI.createFileURI(viewModeltFilelPath.getPath()),true).unload();
			targetModelSetRegistry.disposeRegistry();
		} catch (ServiceException ex) {
			// Ignore
		}		
	}

	public void fillDiagramAndHandleExceptions(IMultiDiagramEditor editor, ModelSet modelSet, File templateResultFilelPath,HashMap<String, EObject> diagramsCreated, HashMap<String, EObject> diagramsToUpdate,HashMap<String, Object>  diagramsMapping)  {
		try {
			fillDiagram(editor, modelSet, templateResultFilelPath, diagramsCreated, diagramsToUpdate, diagramsMapping);
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}

	public void saveModelSetAndHandleExceptions(ModelSet modelSet) {
		try {
			saveModelSet(modelSet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void createDiagramsAndHandleExceptions(Model templateResultFilelPathModel, ModelSet modelSet,Resource modelSetNotation, List<ViewPrototype> representationsKinds, Diagram templateDiagram, ArrayList<String> diagramsInInputModelResource ,  HashMap<String, EObject> diagramsCreated, HashMap<String, EObject> diagramsToUpdate , HashMap<String, Object> diagramsMapping) throws Exception {
		try {
			createDiagrams(templateResultFilelPathModel, modelSet, modelSetNotation, representationsKinds, templateDiagram, diagramsInInputModelResource, diagramsCreated, diagramsToUpdate, diagramsMapping);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public File createViewModelAndHandleExceptions(URI pluginDiagramTemplateDiURI, File templateResultFilePath,
			EditorPart editor, ModelSet targetModelSet) {
		try {
			return createViewModel(pluginDiagramTemplateDiURI,  templateResultFilePath,  editor, targetModelSet);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


	@SuppressWarnings("rawtypes")
	public void createDiagrams(Model templateResultFilelPathModel, ModelSet modelSet,Resource modelSetNotation, List<ViewPrototype> representationsKinds, Diagram templateDiagram, ArrayList<String> diagramsInInputModelResource ,  HashMap<String, EObject> diagramsCreated, HashMap<String, EObject> diagramsToUpdate , HashMap<String, Object> diagramsMapping) throws Exception {

		// create an OCLTool
		OCLTool oclTool = new OCLTool((EClass) templateResultFilelPathModel.eClass(), modelSet.getPackageRegistry());

		String viewContainersQuery="self.oclAsType(Model).allOwnedElements()->select(e | not e.oclAsType(Element).getAppliedStereotype('modelview::ViewContainer').oclIsUndefined())";
		HashSet viewContainers = (HashSet) oclTool.evaluateQuery(viewContainersQuery,templateResultFilelPathModel);

		//foreach viewContainer
		for (Object containerObject: viewContainers) {
			org.eclipse.uml2.uml.Package viewContainer=(Package) containerObject;
			
			if(!diagramsInInputModelResource.contains(viewContainer.getName())) {

				//If not already created, create the diagram
				CreationCommandDescriptor creationCommandDescriptor = getCommands(representationsKinds,templateDiagram);
				if (creationCommandDescriptor != null) {


					EObject root=(EObject) viewContainer.getValue(viewContainer.getAppliedStereotype("modelview::ViewContainer"),"targetElement");
					creationCommandDescriptor.getCommand().createDiagram((ModelSet) modelSet, root, viewContainer.getName());
					
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
	}

	/**
	 * Execute the UML-Scripting-Engine template to generate the uml with viewModel stereotypes applied
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
		ArrayList<String> diagramsInInputModelResource = new ArrayList<String>();
		TreeIterator<EObject> it = modelSetNotation.getAllContents();
		while (it.hasNext()) {
			EObject diagram = it.next();
			if (diagram instanceof Diagram) {
				diagramsInInputModelResource.add(((Diagram) diagram).getName());
			}
		}
		return diagramsInInputModelResource;
	}

	public void fillDiagram(IMultiDiagramEditor editor, ModelSet modelSet, File templateResultFilelPath,HashMap<String, EObject> diagramsCreated, HashMap<String, EObject> diagramsToUpdate,HashMap<String, Object>  diagramsMapping) throws Exception {

		final ServicesRegistry services = editor.getServicesRegistry();
		TransactionalEditingDomain editingDomain = services.getService(TransactionalEditingDomain.class);
		org.eclipse.emf.common.command.Command openPagesCommand = new RecordingCommand(editingDomain, "Open created pages") {

			@Override
			protected void doExecute() {
				try {
					IPageManager pageManager = services.getService(IPageManager.class);
					pageManager.closeAllOpenedPages();

					// Go through the diagrams available in the resource
					for (Object pageDiagram : pageManager.allPages()) {
						if (pageDiagram instanceof Diagram) {

							String pageID = ((Diagram) pageDiagram).getName();
							
							if (diagramsCreated.containsKey(pageID) || diagramsToUpdate.containsKey(pageID)) {

								pageManager.openPage(pageDiagram);
								IEditorPart activeEditor = ((PapyrusMultiDiagramEditor) editor).getActiveEditor();

								if (activeEditor instanceof DiagramEditor) {
							
									// Get the GraphicalViewer for this diagram
									Object result = activeEditor.getAdapter(GraphicalViewer.class);
									
									if (result != null && result instanceof GraphicalViewer) {
									
										DiagramEditPart diagramEditPart = (DiagramEditPart) ((GraphicalViewer) result).getEditPartRegistry().get(pageDiagram);

										// Retrieve the selection to show for this diagram
										Object selection = diagramsMapping.get(pageID);
										addElementsFor((EObject) selection, (DiagramEditor) activeEditor, diagramEditPart);

										// Arrange all recursively
										arrangeRecursively(activeEditor,diagramEditPart,editingDomain);
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
	protected void addElementsFor(EObject selection, DiagramEditor activeEditor, EditPart editPartToShowIn) {
		// Go through the SelectionRef
		EList<EObject> it = ((EObject)selection).eContents();
		
		for (EObject eObject : it) {

			
			EditPart actualEditPart = showElementIn(eObject, activeEditor, editPartToShowIn, 0);
			
			//Esto funciona
			//Stereotype st=((Element) eObject).getAppliedStereotype("modelview::ViewOf");
			//Element resolved=(Element) ((Element) eObject).getValue(st, "relatedElement");
			//processRecursively(actualEditPart, eObject,  activeEditor,resolved);
			
			//Esto funciona igual y es más simple
			addElementsFor(eObject, activeEditor, actualEditPart);

		}
	}
	protected void processRecursively(EditPart actualEditPart, EObject elementToShow, DiagramEditor activeEditor, EObject container, List<View> elementProcessed) {

		// Guess which of the View is the new one
		EditPartViewer viewer = actualEditPart.getViewer();
		Map<?, ?> map = viewer.getEditPartRegistry();

		// We must have a copy since map may change during the loop
		Map<?, ?> mapCopy = new HashMap<Object, Object>(map);
		Iterator<?> it = mapCopy.keySet().iterator();
		boolean found = false;
		while (it.hasNext() && !found) {
			Object view = it.next();

			Object value = mapCopy.get(view);
			if (value instanceof GraphicalEditPart) {


				GraphicalEditPart editPart = (GraphicalEditPart) value;


				// The element of the editPart and the element we just added must match
				Stereotype st=((Element) elementToShow).getAppliedStereotype("modelview::ViewOf");
				Element resolved=(Element) ((Element) elementToShow).getValue(st, "relatedElement");
				
				String editPartSemanticElementID = editPart.resolveSemanticElement().eResource().getURIFragment(editPart.resolveSemanticElement());
				String elementToShowID = resolved.eResource().getURIFragment(resolved);
				if (editPartSemanticElementID.equals(elementToShowID)) {

					// The view should be the editpart whose parent's element is not the elementToShow
					boolean foundParentWithElementToShowAsElement = false;

					EditPart elementToProcess = editPart.getParent();
					while (elementToProcess != null && !foundParentWithElementToShowAsElement) {

						if (elementToProcess instanceof GraphicalEditPart) {
							String elementToProcessSemanticElementID = ((GraphicalEditPart) elementToProcess).resolveSemanticElement().eResource().getURIFragment(((GraphicalEditPart) elementToProcess).resolveSemanticElement());
							if (elementToProcessSemanticElementID.equals(elementToShowID)) {
								foundParentWithElementToShowAsElement = true;
							}
						}

						elementToProcess = elementToProcess.getParent();
					}

					if (!foundParentWithElementToShowAsElement) {
						// Last we must be sure that it is really new one
						if (!elementProcessed.contains(view)) { editPart.resolveSemanticElement();
							// We can process it
							addElementsFor(elementToShow, activeEditor, editPart);

							// FIXME we may need to add all new elements as processed
							// Record that it is processed
							elementProcessed.add((View) view);

							found = true;
						}
					}
				}
			}
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
	@SuppressWarnings("unchecked")
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
	 * @param container
	 *            the UML element that contains it
	 * @return
	 * 		the editPart in which the element has been actually added
	 */
	protected EditPart showElementIn(EObject elementToShow, DiagramEditor activeEditor, EditPart editPart, int position) {
		EditPart returnEditPart = null;

		if (
				elementToShow instanceof Element
			) {

			DropObjectsRequest dropObjectsRequest = new DropObjectsRequest();
			ArrayList<Element> list = new ArrayList<Element>();
			Stereotype st=((Element) elementToShow).getAppliedStereotype("modelview::ViewOf");
			Element resolved=(Element) ((Element) elementToShow).getValue(st, "relatedElement");
			list.add(resolved);
			dropObjectsRequest.setObjects(list);
			dropObjectsRequest.setLocation(new Point(40 , 40 ));
			
			Command commandDrop = editPart.getCommand(dropObjectsRequest);
			
			boolean processChildren = false;
			if (commandDrop == null) {
				processChildren = true;
			} else {
				if (commandDrop.canExecute()) {
					activeEditor.getDiagramEditDomain().getDiagramCommandStack().execute(commandDrop);
					
					
					((GraphicalEditPart)editPart).refresh();
					
					//Buscamos el editPart creado
					returnEditPart = editPart;
					ArrayList<EditPart> childrenList = new ArrayList<EditPart>();
					findAllChildren(childrenList, editPart);
					for (Object child : childrenList) {
						if (child instanceof GraphicalEditPart) {
							if(
									((GraphicalEditPart)child).resolveSemanticElement().eResource().getURIFragment(((GraphicalEditPart)child).resolveSemanticElement())
								.equals(
									resolved.eResource().getURIFragment(resolved)
								)
							)
								returnEditPart=(EditPart) child;
						}
					}
					
					//((GraphicalEditPart)editPart).resolveSemanticElement();
					//EList elementsInEditPart = ((DiagramEditPart)editPart).getNotationView().getChildren();

					//View createdView=((View)elementsInEditPart.get(elementsInEditPart.size()-1));
					//Element element = (Element) createdView.getElement();
					
//					for (Object curr : elementsInEditPart) {
//						if(((Element)((View)curr).getElement()).getOwner().equals(container)) {
//							((View)createdView.eContainer()).removeChild(createdView);
//							((View)curr).insertChild(createdView);
//						}
//					}
					/**
					
//					((editPart).getViewer()).getEditPartRegistry().get(
//							org.eclipse.papyrus.infra.gmfdiag.common.utils.DiagramEditPartsUtil.getEObjectViews(resolved).get(0)
//					);
//					
//					org.eclipse.papyrus.infra.gmfdiag.common.utils.DiagramEditPartsUtil.findEditParts(
//							(View)((Diagram)org.eclipse.papyrus.infra.gmfdiag.common.utils.DiagramEditPartsUtil.getEObjectViews(resolved).get(0).eContainer()).getChildren().get(1)
//							);
//					((GraphicalViewer)((GraphicalEditPart)editPart).getViewer()).findObjectAt(new Point(10 * (position +1), 10 * (position+1)))
//					((GraphicalEditPart)editPart.getTargetEditPart(dropObjectsRequest)).resolveSemanticElement();
					//((GraphicalViewer)((GraphicalEditPart)editPart).getViewer()).getEditPartRegistry();
					//((Diagram)org.eclipse.papyrus.infra.gmfdiag.common.utils.DiagramEditPartsUtil.getEObjectViews(resolved).get(0).eContainer()).getChildren();
					//org.eclipse.papyrus.infra.gmfdiag.common.utils.DiagramEditPartsUtil.getAllEditParts(editPart)
					 * 
					 * 
					 */
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
								((GraphicalEditPart)child).refresh();
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
	 * @param editingDomain 
	 *
	 * @param editpart
	 *            the editpart to process
	 */
	protected void arrangeRecursively(IEditorPart activeEditor, EditPart editPart, TransactionalEditingDomain editingDomain) {

		//configure ELK
		Parameters params = new Parameters();
        params.getGlobalSettings()
            .setProperty(CoreOptions.ANIMATE, false)
            .setProperty(CoreOptions.PROGRESS_BAR, false)
            .setProperty(CoreOptions.LAYOUT_ANCESTORS, false)
            .setProperty(CoreOptions.ZOOM_TO_FIT, false);
        

		//First round to layout
        DiagramLayoutEngine.invokeLayout((DiagramEditor) activeEditor, editPart,  params);
        		
        //second round to relayout is necessary because is a top to child layout so, inner content may have changed in the first layout
        //this is easier than doing a bottom-up layout
        DiagramLayoutEngine.invokeLayout((DiagramEditor) activeEditor, editPart,  params);
        
        
		for (Object element : editPart.getChildren()) {
			if (element instanceof EditPart) {
				DiagramLayoutEngine.invokeLayout((DiagramEditor) activeEditor, editPart,  params);
				arrangeRecursively(activeEditor,(EditPart) element, editingDomain);
			}
		}
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
	 * Handle exceptions on initialize
	 * @param pluginDiagramTemplateDiURI
	 * @param modelSet
	 * @param pluginDiagramTemplateModelSet
	 * @param diagramsInInputModelResource
	 * @param templatediagramsInInputModelResource
	 * @param templateDiagram
	 * @param modelSetNotation 
//	 * @param pluginDiagramTemplateModelSetNotation 
	 * @throws Exception
	 */

	public void saveModelSet(ModelSet modelSet) throws Exception {
		modelSet.save(new IProgressMonitor() {
			
			@Override
			public void worked(int arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void subTask(String arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setTaskName(String arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setCanceled(boolean arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public boolean isCanceled() {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public void internalWorked(double arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void done() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beginTask(String arg0, int arg1) {
				// TODO Auto-generated method stub
				
			}
		});

		
	}

	
}
