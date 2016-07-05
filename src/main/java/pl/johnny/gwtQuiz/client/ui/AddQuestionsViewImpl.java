package pl.johnny.gwtQuiz.client.ui;

import org.gwtbootstrap3.client.shared.event.ShowEvent;
import org.gwtbootstrap3.client.shared.event.ShowHandler;
import org.gwtbootstrap3.client.ui.PanelCollapse;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class AddQuestionsViewImpl extends Composite implements AddQuestionsView {

	private static AddQuestionViewImplUiBinder uiBinder = GWT.create(AddQuestionViewImplUiBinder.class);

	interface AddQuestionViewImplUiBinder extends UiBinder<Widget, AddQuestionsViewImpl> {
	}

	private Presenter listener;
	
	@UiField PanelCollapse panelCollapse;


	public AddQuestionsViewImpl() {
		initWidget(uiBinder.createAndBindUi(this));
		
		panelCollapse.addShowHandler(new ShowHandler() {
			@Override
			public void onShow(ShowEvent showEvent) {
				GWT.log("panelHeader clicked ");
			}
		});
	}

	@Override
	public void setPresenter(Presenter listener) {
		this.listener = listener;
	}
}