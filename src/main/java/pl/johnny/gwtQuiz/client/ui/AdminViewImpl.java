/**
 * @author jzarewicz
 */
package pl.johnny.gwtQuiz.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class AdminViewImpl extends Composite implements AdminView {

	private static AdminViewImplUiBinder uiBinder = GWT.create(AdminViewImplUiBinder.class);

	interface AdminViewImplUiBinder extends UiBinder<Widget, AdminViewImpl> {
	}

	public AdminViewImpl() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	private Presenter listener;

	public AdminViewImpl(String firstName) {
		initWidget(uiBinder.createAndBindUi(this));
	}


	@Override
	public void setPresenter(Presenter listener) {
		this.listener = listener;
	}
}