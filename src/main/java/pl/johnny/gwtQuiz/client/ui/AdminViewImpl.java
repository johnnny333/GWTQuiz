/**
 * @author jzarewicz
 */
package pl.johnny.gwtQuiz.client.ui;

import org.gwtbootstrap3.client.ui.PanelGroup;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import pl.johnny.gwtQuiz.client.ui.widgets.PanelWidget;

public class AdminViewImpl extends Composite implements AdminView {

	private static AdminViewImplUiBinder uiBinder = GWT.create(AdminViewImplUiBinder.class);

	interface AdminViewImplUiBinder extends UiBinder<Widget, AdminViewImpl> {
	}

	private Presenter listener;
	
	@UiField
	PanelGroup panelGroup;

	public AdminViewImpl() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public AdminViewImpl(String firstName) {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public void setPresenter(Presenter listener) {
		this.listener = listener;
	}

	@Override
	public void setCategories(String[] categories) {
		
		PanelWidget[] panelWidget = new PanelWidget[3];

		for (int i = 0; i < panelWidget.length; i++) {
			panelWidget[i] = new PanelWidget();
			panelWidget[i].setHeaderAndIDs(String.valueOf(i), "What time is it?");
			panelWidget[i].setUserCategoryListBox(categories, "Geografia");
			GWT.log("panel group count: " + panelGroup.getWidgetCount());
			
			// Avoid doubling widgets...
			if(panelGroup.getWidgetCount() < panelWidget.length)
			panelGroup.add(panelWidget[i]);
		}
	}
}