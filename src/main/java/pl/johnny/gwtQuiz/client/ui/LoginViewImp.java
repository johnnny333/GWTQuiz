package pl.johnny.gwtQuiz.client.ui;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Form;
import org.gwtbootstrap3.client.ui.Input;
import org.gwtbootstrap3.client.ui.TextBox;

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

	@UiField
	Button loginButton;

	@UiField
	Input password;

	@UiField
	TextBox email;

	@UiField
	Form form;

	public LoginViewImp() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public void setPresenter(Presenter listener) {
		this.listener = listener;
	}

	@UiHandler("loginButton")
	void onLoginButtonClicked(ClickEvent e) {

		if(form.validate()) {

			GWT.log("LoginViewImpl validated");

			if(listener != null) {
				listener.loginUser(new User(email.getValue(), password.getValue()));
			}
		}
	}
}