/**
 */
package modelview.impl;

import modelview.ViewOf;
import modelview.modelviewPackage;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.uml2.uml.Element;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>View Of</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link modelview.impl.ViewOfImpl#getBase_Element <em>Base Element</em>}</li>
 *   <li>{@link modelview.impl.ViewOfImpl#getRelatedElement <em>Related Element</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ViewOfImpl extends EObjectImpl implements ViewOf {
	/**
	 * The cached value of the '{@link #getBase_Element() <em>Base Element</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getBase_Element()
	 * @generated
	 * @ordered
	 */
	protected Element base_Element;

	/**
	 * The cached value of the '{@link #getRelatedElement() <em>Related Element</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRelatedElement()
	 * @generated
	 * @ordered
	 */
	protected Element relatedElement;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ViewOfImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return modelviewPackage.Literals.VIEW_OF;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Element getBase_Element() {
		if (base_Element != null && base_Element.eIsProxy()) {
			InternalEObject oldBase_Element = (InternalEObject)base_Element;
			base_Element = (Element)eResolveProxy(oldBase_Element);
			if (base_Element != oldBase_Element) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, modelviewPackage.VIEW_OF__BASE_ELEMENT, oldBase_Element, base_Element));
			}
		}
		return base_Element;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Element basicGetBase_Element() {
		return base_Element;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setBase_Element(Element newBase_Element) {
		Element oldBase_Element = base_Element;
		base_Element = newBase_Element;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, modelviewPackage.VIEW_OF__BASE_ELEMENT, oldBase_Element, base_Element));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Element getRelatedElement() {
		if (relatedElement != null && relatedElement.eIsProxy()) {
			InternalEObject oldRelatedElement = (InternalEObject)relatedElement;
			relatedElement = (Element)eResolveProxy(oldRelatedElement);
			if (relatedElement != oldRelatedElement) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, modelviewPackage.VIEW_OF__RELATED_ELEMENT, oldRelatedElement, relatedElement));
			}
		}
		return relatedElement;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Element basicGetRelatedElement() {
		return relatedElement;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setRelatedElement(Element newRelatedElement) {
		Element oldRelatedElement = relatedElement;
		relatedElement = newRelatedElement;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, modelviewPackage.VIEW_OF__RELATED_ELEMENT, oldRelatedElement, relatedElement));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case modelviewPackage.VIEW_OF__BASE_ELEMENT:
				if (resolve) return getBase_Element();
				return basicGetBase_Element();
			case modelviewPackage.VIEW_OF__RELATED_ELEMENT:
				if (resolve) return getRelatedElement();
				return basicGetRelatedElement();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case modelviewPackage.VIEW_OF__BASE_ELEMENT:
				setBase_Element((Element)newValue);
				return;
			case modelviewPackage.VIEW_OF__RELATED_ELEMENT:
				setRelatedElement((Element)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case modelviewPackage.VIEW_OF__BASE_ELEMENT:
				setBase_Element((Element)null);
				return;
			case modelviewPackage.VIEW_OF__RELATED_ELEMENT:
				setRelatedElement((Element)null);
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case modelviewPackage.VIEW_OF__BASE_ELEMENT:
				return base_Element != null;
			case modelviewPackage.VIEW_OF__RELATED_ELEMENT:
				return relatedElement != null;
		}
		return super.eIsSet(featureID);
	}

} //ViewOfImpl
