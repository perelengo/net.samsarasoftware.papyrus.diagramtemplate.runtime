/**
 */
package modelview;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see modelview.modelviewPackage
 * @generated
 */
public interface modelviewFactory extends EFactory {
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	modelviewFactory eINSTANCE = modelview.impl.modelviewFactoryImpl.init();

	/**
	 * Returns a new object of class '<em>View Of</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>View Of</em>'.
	 * @generated
	 */
	ViewOf createViewOf();

	/**
	 * Returns a new object of class '<em>View Container</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>View Container</em>'.
	 * @generated
	 */
	ViewContainer createViewContainer();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	modelviewPackage getmodelviewPackage();

} //modelviewFactory
