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

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage.Registry;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.ocl.OCL;
import org.eclipse.ocl.ParserException;
import org.eclipse.ocl.ecore.Constraint;
import org.eclipse.ocl.ecore.EcoreEnvironmentFactory;
import org.eclipse.ocl.expressions.OCLExpression;
import org.eclipse.ocl.helper.OCLHelper;
import org.eclipse.uml2.uml.internal.impl.ModelImpl;


/**
 * Simple Ocl tooling initializer
 * @author perelengo
 *
 */
public class OCLTool {
	//initialize the OCL tooling
	OCL<?, EClassifier, EOperation, EStructuralFeature, ?, ?, ?, ?, ?, Constraint, ?, ?> ocl = null;
	OCLHelper<EClassifier, EOperation, EStructuralFeature, Constraint> helper = null;
	ModelImpl modelEClassifier = null;
			
	/**
	 * Initializes the OCL tooling
	 * @param context
	 * @param packageRegistry
	 */
	public OCLTool(EClassifier context, Registry packageRegistry) {
			//Initialize OCL system as explained at https://help.eclipse.org/2020-06/index.jsp?topic=%2Forg.eclipse.ocl.doc%2Fhelp%2FParsingDocuments.html
			EcoreEnvironmentFactory environmentFactory = new EcoreEnvironmentFactory(packageRegistry);
			ocl = OCL.newInstance(environmentFactory);
			// create an OCL helper object
			helper = ocl.createOCLHelper();
			//set the helper context
			helper.setContext(context);
	}

	/**
	 * Evaluates an OCL query string
	 * 
	 * @param oclQueryString
	 * @param context
	 * @return
	 * @throws ParserException
	 */
	public Object evaluateQuery(String oclQueryString, Object context) throws ParserException {
		OCLExpression<EClassifier> query;
		query = helper.createQuery(oclQueryString);
		return ocl.evaluate(context, query);
	}


}
