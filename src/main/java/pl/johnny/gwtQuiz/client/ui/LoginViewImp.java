/**
 * @author jzarewicz
 */
package pl.johnny.gwtQuiz.client.ui;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.groups.Default;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Form;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.InlineHelpBlock;
import org.gwtbootstrap3.client.ui.Input;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ValidationState;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import pl.johnny.gwtQuiz.shared.User;

public class LoginViewImp extends Composite implements LoginView {

	private static LoginViewImpUiBinder uiBinder = GWT.create(LoginViewImpUiBinder.class);

	interface LoginViewImpUiBinder extends UiBinder<Widget, LoginViewImp> {
	}

	private Presenter listener;

	// Login fields

	@UiField
	Button loginButton;

	@UiField
	Input password;

	@UiField
	TextBox email;

	@UiField
	Form form;

	@UiField
	FormGroup userMailFormGroup;

	@UiField
	FormGroup passwordFormGroup;

	@UiField
	InlineHelpBlock userEmailInlineHelpBlock;

	@UiField
	InlineHelpBlock passwordInlineHelpBlock;

	// Register fields

	@UiField
	Button registerButton;

	@UiField
	Form formRegister;

	@UiField
	Input emailRegister;

	@UiField
	Input passwordRegister;

	@UiField
	Input passwordRetypeRegister;
	
	@UiField
	InlineHelpBlock passwordRegisterInlineHelpBlock;
	
	@UiField 
	InlineHelpBlock passwordRetypeRegisterInlineHelpBlock;
	
	@UiField
	FormGroup passwordRegisterFormGroup;
	
	@UiField
	FormGroup passwordRetypeRegisterFormGroup;
	
	@UiField
	FormGroup userRegisterMailFormGroup;
	
	@UiField
	InlineHelpBlock userRegisterEmailInlineHelpBlock;

	public LoginViewImp() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public void setPresenter(Presenter listener) {
		this.listener = listener;
	}

	@UiHandler("loginButton")
	void onLoginButtonClicked(ClickEvent e) {

		if(form.validate() && listener != null) {
			listener.loginUser(new User(email.getValue(), password.getValue()));
		}
	}

	//Register Validation
	@UiHandler("registerButton")
	void onRegisterButtonClicked(ClickEvent e) {

		//Check if fields are non-empty.
		if(formRegister.validate() && listener != null) {
			//Form is non-empty. Now Check if typed passwords are the same.
			if(passwordRegister.getText().equals(passwordRetypeRegister.getText())) {
				//Passwords are the same,now commence Hibernate client Validation.

				GWT.log("email " + emailRegister.getValue() + " password " + passwordRegister.getValue());

				Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

				Set<ConstraintViolation<User>> violations = validator.
						validate(new User(emailRegister.getValue(), passwordRegister.getValue()), Default.class);

				if(!violations.isEmpty()) {
					StringBuffer errorMessage = new StringBuffer();
					for(ConstraintViolation<User> constraintViolation : violations) {
//						if(errorMessage.length() == 0) {
//							errorMessage.append('\n');
//						}
//						errorMessage.append(constraintViolation.getMessage() + "| source: " + constraintViolation.getPropertyPath());
						
						switch(constraintViolation.getPropertyPath().toString()) {
							case "password":
								
								passwordRegisterFormGroup.setValidationState(ValidationState.ERROR);
//								passwordRetypeRegisterFormGroup.setValidationState(ValidationState.ERROR);
								passwordRegisterInlineHelpBlock.setText(constraintViolation.getMessage());
								
								break;
								
							case "email":
								
								userRegisterMailFormGroup.setValidationState(ValidationState.ERROR);
								userRegisterEmailInlineHelpBlock.setText(constraintViolation.getMessage());
								
								break;

							default:
								
								GWT.log("Client side Hibernate Validation exception in LoginViewImpl.");
								
								break;
						}
					}
					GWT.log(errorMessage.toString());
					return;

				} else {
					GWT.log("Hibernate validation OK");
					//TODO send validated user to server over RPC.
				}
			} else {
				//Display error message in a appropriate fields.
				passwordRegisterFormGroup.setValidationState(ValidationState.ERROR);
				passwordRetypeRegisterFormGroup.setValidationState(ValidationState.ERROR);
				passwordRetypeRegisterInlineHelpBlock.setText("Passwords are not the same.");
			}
		}
	}

	@Override
	public void setLoginServerErrorMessage(String errorMessage) {
		switch(errorMessage) {
			case "No such user":
				userMailFormGroup.setValidationState(ValidationState.ERROR);
				userEmailInlineHelpBlock.setText(errorMessage);
				break;
			case "Bad password":
				passwordFormGroup.setValidationState(ValidationState.ERROR);
				passwordInlineHelpBlock.setText(errorMessage);
				break;

			default:
				GWT.log("LoginViewImpl.setServerErrorMessage error " + errorMessage);
				break;
		}
	}
}