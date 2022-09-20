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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.m2m.qvt.oml.ModelExtent;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.samsarasoftware.scripting.ScriptingEngine;
import net.samsarasoftware.scripting.qvto.In;
import net.samsarasoftware.scripting.qvto.InOut;
import net.samsarasoftware.scripting.qvto.ModelExtentAdapter;
import net.samsarasoftware.scripting.qvto.Param;



public class ScriptingEngineTemplateProcessor implements TemplateProcessor{

	ScriptingEngine scriptingEngine;
	
	public ScriptingEngineTemplateProcessor() {
		this.scriptingEngine= new  ScriptingEngine();
	}
	
	@Override
	public File process(String templateUMLPath, ResourceSet resourceSet,Resource targetUML, File templateResultFilelPath, String params, TransactionalEditingDomain editingDomain) throws Exception {

		if(templateResultFilelPath==null) {
			templateResultFilelPath=File.createTempFile(templateUMLPath.substring(0,templateUMLPath.lastIndexOf("."))+"_transform",".uml");
			templateResultFilelPath.deleteOnExit();

			//inicializamos el modelo destino necesario para iniciar la transformaci�n
			FileOutputStream fos = new FileOutputStream(templateResultFilelPath);
			fos.write(new String(
					"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
					+ "<xmi:XMI xmi:version=\"20131001\" "
					+ " xmlns:xmi=\"http://www.omg.org/spec/XMI/20131001\""
					+ " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
					+ " xmlns:ecore=\"http://www.eclipse.org/emf/2002/Ecore\""
					+ " xmlns:uml=\"http://www.eclipse.org/uml2/5.0.0/UML\""
					+ ">\r\n" 
					+ "	<uml:Model xmi:id=\"viewmodel__temp\" name=\"viewmodel__temp\">"
					+ " </uml:Model>"
					+ "</xmi:XMI>\r\n"
					).getBytes("UTF-8")
				);
			fos.flush();
			fos.close();
			
		}

		
		List INPUT=new ArrayList();
		INPUT.add( new InOut(URI.createFileURI(templateResultFilelPath.getPath())));
		INPUT.add( new In(URI.createURI("pathmap://UML_LIBRARIES/UMLPrimitiveTypes.library.uml")));
		
		//Los profiles pueden obtenerse mediante el xpath //appliedProfile[@xmi:type='uml:Profile' and substring-before(@href,'uml2qvto.profile.uml')=@href]/@href
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = builderFactory.newDocumentBuilder();
		Document xmlDocument = builder.parse(templateUMLPath);
		XPath xPath = XPathFactory.newInstance().newXPath();
		String expression = "//appliedProfile[@type='uml:Profile']/@href";
		NodeList profileNodeList = (NodeList) xPath.compile(expression).evaluate(xmlDocument, XPathConstants.NODESET);
		for (int i=0; i< profileNodeList.getLength();i++) {
			Node profileNode=profileNodeList.item(i);
			if(!profileNode.getTextContent().contains("uml2qvto.profile.uml")) {
				String f=profileNode.getTextContent().substring(0,profileNode.getTextContent().indexOf("#"));
				File f3=new File(f);
				File f2=new File(new File(templateUMLPath).getParentFile().getAbsolutePath()+File.separator+f);
				if(f3.exists()) {
					INPUT.add(new In(URI.createFileURI(f3.getAbsolutePath())));
				}else if(f2.exists()) {
					INPUT.add(new In(URI.createFileURI(f2.getAbsolutePath())));
					
				}else {
					INPUT.add(new In(URI.createURI(profileNode.getTextContent())));
				}
			}
			
		}
		
		//Siempre hay un único fichero inout que es el fichero de salida
		ModelExtent viewModelExtent=new ModelExtentAdapter(targetUML);
		INPUT.add(viewModelExtent);
		

		//Para que BSF funcione en eclipse hay que modificar los classloaders
		ClassLoader ccld = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
		
		File qvto=null;
		
		//compilamos
		try {
			qvto=runCompile(templateUMLPath, params);
		}finally {
			//restauramos el classloader
			Thread.currentThread().setContextClassLoader(ccld);
		}

		runTransform(qvto, INPUT, resourceSet, editingDomain);
		

		

		return templateResultFilelPath;
		
	}

	
	/**
	 * Runs the umlScriptingEngine transform
	 * 
	 * @param scriptingEngine
	 * @param templateUML
	 * @param params 
	 * @param targetUML
	 * @param outputModelPath
	 * @return 
	 * @throws Exception 
	 */
	protected File runCompile( String templateUML, String params) throws Exception {
		FileInputStream fis=null;
		try{
			fis=new FileInputStream(templateUML);
			ArrayList<String> IN = new ArrayList<String>();
			IN.add("aaa"); //Just let know the transform needs an inoutFile (the source model we want to generate the viewmodel for)
			ArrayList<String> empty = new ArrayList<String>();
			return scriptingEngine.runCompile(fis, IN, empty,empty,params);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			if(fis!=null) try { fis.close(); } catch(Exception e) {}
		}
	}

	protected void runTransform(File qvto, List<? extends ModelExtent> INPUT, ResourceSet resourceSet,TransactionalEditingDomain editingDomain) throws Exception {
		try {
			ModelExtent editedResource = ((ModelExtent)INPUT.get(INPUT.size()-1));
			
			//Para obtener la transacci�n, debemos tener cargado el elemento
			if( (editedResource.getContents()==null || editedResource.getContents().isEmpty() )
				&& editedResource instanceof Param )
				((Param)editedResource).initialize(resourceSet);
			
			editingDomain.getCommandStack().execute(new RecordingCommand(editingDomain) {

		        @Override
		        protected void doExecute() {
		        	try {
		        		for (ModelExtent inputURI : INPUT) {
		        			resourceSet.getURIConverter().getURIMap();
		        			if(inputURI instanceof Param) {
		        				((Param) inputURI).initialize(resourceSet);
		        			}
		        			
		        		}
						scriptingEngine.runTransform(qvto,INPUT,resourceSet);
					} catch (Exception e) {
						e.printStackTrace();
					}
		        }
		    });
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}	
	}

}
