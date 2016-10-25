package pl.johnny.gwtQuiz.client.ui;

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

	public LoginViewImp() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public void setPresenter(Presenter listener) {
		this.listener = listener;
	}

	@UiHandler("loginButton")
	void onLoginButtonClicked(ClickEvent e) {

		if (form.validate() && listener != null) {
			listener.loginUser(new User(email.getValue(), password.getValue()));
		}
	}
	
	//Register Validation
	@UiHandler("registerButton")
	void onRegisterButtonClicked(ClickEvent e) {

		// Check if fields are not empty.
		if (formRegister.validate() && listener != null) {
			
//			if(HibernateValidation){
//				
//			}else{
//				
//			}
			
			// Check if "Password" and "Retype Password" are the same
			if (passwordRegister.getText().equals(passwordRetypeRegister.getText())) {
				GWT.log("Ok nygga,that look neat");
			} else {
				GWT.log("Passwords are not the same nigga");
				// TODO display error to user
			}
		}
	}

	@Override
	public void setLoginServerErrorMessage(String errorMessage) {
		switch (errorMessage) {
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