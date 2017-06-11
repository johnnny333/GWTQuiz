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
import org.gwtbootstrap3.client.ui.NavTabs;
import org.gwtbootstrap3.client.ui.TabListItem;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.InputType;
import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.gwtbootstrap3.extras.toggleswitch.client.ui.ToggleSwitch;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
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

	@UiField
	TabListItem loginTab;

	@UiField
	TabListItem signUpTab;

	// Login fields

	@UiField
	Button loginButton;

	@UiField
	Input password;

	@UiField
	TextBox email;

	@UiField
	Form formLogin;

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

	@UiField
	ToggleSwitch passwordToogleSwitch;

	public LoginViewImp() {
		initWidget(uiBinder.createAndBindUi(this));

		// Show/Hide password input values on toggle.
		passwordToogleSwitch.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				if (event.getValue()) {
					passwordRegister.setType(InputType.TEXT);
					passwordRetypeRegister.setType(InputType.TEXT);
				} else {
					passwordRegister.setType(InputType.PASSWORD);
					passwordRetypeRegister.setType(InputType.PASSWORD);
				}
			}
		});
	}

	@Override
	public void setPresenter(Presenter listener) {
		this.listener = listener;
	}

	// Login logic
	@UiHandler("loginButton")
	void onLoginButtonClicked(ClickEvent e) {

		if (formLogin.validate() && listener != null) {

			isLogging(true);

			listener.loginUser(new User(SafeHtmlUtils.htmlEscape(email.getValue().trim()), password.getValue().trim()));
		}
	}

	// Register Validation
	@UiHandler("registerButton")
	void onRegisterButtonClicked(ClickEvent e) {

		// Check if fields are non-empty.
		if (formRegister.validate() && listener != null) {
			// Form is non-empty. Now Check if typed passwords are the same.
			if (passwordRegister.getText().equals(passwordRetypeRegister.getText())) {
				// Passwords are the same,now commence Hibernate client
				// Validation.

				GWT.log("email " + emailRegister.getValue() + " password " + passwordRegister.getValue());

				Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

				Set<ConstraintViolation<User>> violations = validator.validate(
						new User(emailRegister.getValue().trim(), passwordRegister.getValue()), Default.class);

				if (!violations.isEmpty()) {
					StringBuffer errorMessage = new StringBuffer();
					for (ConstraintViolation<User> constraintViolation : violations) {

						switch (constraintViolation.getPropertyPath().toString()) {
						case "password":

							passwordRegisterFormGroup.setValidationState(ValidationState.ERROR);
							// passwordRetypeRegisterFormGroup.setValidationState(ValidationState.ERROR);
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
					isSigningUp(true);
					listener.registerUser(new User(emailRegister.getValue(), passwordRegister.getValue()));
				}
			} else {
				// Display error message in a appropriate fields.
				passwordRegisterFormGroup.setValidationState(ValidationState.ERROR);
				passwordRetypeRegisterFormGroup.setValidationState(ValidationState.ERROR);
				passwordRetypeRegisterInlineHelpBlock.setText("Passwords are not the same.");
			}
		}
	}

	@Override
	public void setLoginServerErrorMessage(String errorMessage) {

		switch (errorMessage) {

		// User login.
		case "No such user":
			userMailFormGroup.setValidationState(ValidationState.ERROR);
			userEmailInlineHelpBlock.setText(errorMessage);
			break;

		// Password login.
		case "Bad password":
			passwordFormGroup.setValidationState(ValidationState.ERROR);
			passwordInlineHelpBlock.setText(errorMessage);
			break;

		// Sign up
		case "User already exist":
			userRegisterMailFormGroup.setValidationState(ValidationState.ERROR);
			userRegisterEmailInlineHelpBlock.setText(errorMessage + "! Use different email to register.");

			break;

		default:
			GWT.log("LoginViewImpl.setServerErrorMessage error " + errorMessage);
			break;
		}
	}

	@Override
	public void selectTab(String tabToSelect) {

		switch (tabToSelect) {
		case "SignUp":
			signUpTab.showTab();
			break;

		case "Login":
			loginTab.showTab();
			break;

		default:
			break;
		}
	}

	@Override
	public void resetLoginForms(LoginForm formToReset) {
		switch (formToReset) {
		case FORM_LOGIN:
			formLogin.reset();
			break;

		case FORM_REGISTER:
			formRegister.reset();
			break;

		default:
			break;
		}
	}

	@Override
	public void isLogging(boolean isLogging) {

		if (isLogging) {
			loginButton.setIcon(IconType.SPINNER);
			loginButton.setIconSpin(true);
		} else {
			loginButton.setIcon(IconType.USER);
			loginButton.setIconSpin(false);
		}
	}

	@Override
	public void isSigningUp(boolean isLogging) {
		
		if (isLogging) {
			registerButton.setIcon(IconType.SPINNER);
			registerButton.setIconSpin(true);
		} else {
			registerButton.setIcon(IconType.SIGN_IN);
			registerButton.setIconSpin(false);
		}
	}
}