/**
 */
package modelview;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see modelview.modelviewFactory
 * @model kind="package"
 *        annotation="PapyrusVersion Version='1.0.0' Comment='' Copyright='' Date='2020-11-29' Author=''"
 * @generated
 */
public interface modelviewPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "modelview"; //$NON-NLS-1$

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://www.samsarasoftware.net/modelview.ecore"; //$NON-NLS-1$

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "modelview"; //$NON-NLS-1$

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	modelviewPackage eINSTANCE = modelview.impl.modelviewPackageImpl.init();

	/**
	 * The meta object id for the '{@link modelview.impl.ViewOfImpl <em>View Of</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see modelview.impl.ViewOfImpl
	 * @see modelview.impl.modelviewPackageImpl#getViewOf()
	 * @generated
	 */
	int VIEW_OF = 0;

	/**
	 * The feature id for the '<em><b>Base Element</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VIEW_OF__BASE_ELEMENT = 0;

	/**
	 * The feature id for the '<em><b>Related Element</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VIEW_OF__RELATED_ELEMENT = 1;

	/**
	 * The number of structural features of the '<em>View Of</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VIEW_OF_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '{@link modelview.impl.ViewContainerImpl <em>View Container</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see modelview.impl.ViewContainerImpl
	 * @see modelview.impl.modelviewPackageImpl#getViewContainer()
	 * @generated
	 */
	int VIEW_CONTAINER = 1;

	/**
	 * The feature id for the '<em><b>Base Element</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VIEW_CONTAINER__BASE_ELEMENT = 0;

	/**
	 * The feature id for the '<em><b>Target Element</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VIEW_CONTAINER__TARGET_ELEMENT = 1;

	/**
	 * The number of structural features of the '<em>View Container</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VIEW_CONTAINER_FEATURE_COUNT = 2;


	/**
	 * Returns the meta object for class '{@link modelview.ViewOf <em>View Of</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>View Of</em>'.
	 * @see modelview.ViewOf
	 * @generated
	 */
	EClass getViewOf();

	/**
	 * Returns the meta object for the reference '{@link modelview.ViewOf#getBase_Element <em>Base Element</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Base Element</em>'.
	 * @see modelview.ViewOf#getBase_Element()
	 * @see #getViewOf()
	 * @generated
	 */
	EReference getViewOf_Base_Element();

	/**
	 * Returns the meta object for the reference '{@link modelview.ViewOf#getRelatedElement <em>Related Element</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Related Element</em>'.
	 * @see modelview.ViewOf#getRelatedElement()
	 * @see #getViewOf()
	 * @generated
	 */
	EReference getViewOf_RelatedElement();

	/**
	 * Returns the meta object for class '{@link modelview.ViewContainer <em>View Container</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>View Container</em>'.
	 * @see modelview.ViewContainer
	 * @generated
	 */
	EClass getViewContainer();

	/**
	 * Returns the meta object for the reference '{@link modelview.ViewContainer#getBase_Element <em>Base Element</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Base Element</em>'.
	 * @see modelview.ViewContainer#getBase_Element()
	 * @see #getViewContainer()
	 * @generated
	 */
	EReference getViewContainer_Base_Element();

	/**
	 * Returns the meta object for the reference '{@link modelview.ViewContainer#getTargetElement <em>Target Element</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Target Element</em>'.
	 * @see modelview.ViewContainer#getTargetElement()
	 * @see #getViewContainer()
	 * @generated
	 */
	EReference getViewContainer_TargetElement();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	modelviewFactory getmodelviewFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '{@link modelview.impl.ViewOfImpl <em>View Of</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see modelview.impl.ViewOfImpl
		 * @see modelview.impl.modelviewPackageImpl#getViewOf()
		 * @generated
		 */
		EClass VIEW_OF = eINSTANCE.getViewOf();

		/**
		 * The meta object literal for the '<em><b>Base Element</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference VIEW_OF__BASE_ELEMENT = eINSTANCE.getViewOf_Base_Element();

		/**
		 * The meta object literal for the '<em><b>Related Element</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference VIEW_OF__RELATED_ELEMENT = eINSTANCE.getViewOf_RelatedElement();

		/**
		 * The meta object literal for the '{@link modelview.impl.ViewContainerImpl <em>View Container</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see modelview.impl.ViewContainerImpl
		 * @see modelview.impl.modelviewPackageImpl#getViewContainer()
		 * @generated
		 */
		EClass VIEW_CONTAINER = eINSTANCE.getViewContainer();

		/**
		 * The meta object literal for the '<em><b>Base Element</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference VIEW_CONTAINER__BASE_ELEMENT = eINSTANCE.getViewContainer_Base_Element();

		/**
		 * The meta object literal for the '<em><b>Target Element</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference VIEW_CONTAINER__TARGET_ELEMENT = eINSTANCE.getViewContainer_TargetElement();

	}

} //modelviewPackage
