package pl.johnny.gwtQuiz.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

import pl.johnny.gwtQuiz.client.ui.widgets.UploadWidget;

public class AddQuestionsViewImpl extends Composite implements AddQuestionsView {

	private static AddQuestionViewImplUiBinder uiBinder = GWT.create(AddQuestionViewImplUiBinder.class);

	interface AddQuestionViewImplUiBinder extends UiBinder<Widget, AddQuestionsViewImpl> {
	}

	private Presenter listener;
	
//	@UiField PanelCollapse panelCollapse;
	@UiField HTMLPanel imageWidget; 


	public AddQuestionsViewImpl() {
		initWidget(uiBinder.createAndBindUi(this));
		
		imageWidget.add(new UploadWidget());
	}

	@Override
	public void setPresenter(Presenter listener) {
		this.listener = listener;
	}
}