package pl.johnny.gwtQuiz.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class AddQuestionsViewImpl extends Composite implements AddQuestionsView {

	private static AddQuestionViewImplUiBinder uiBinder = GWT.create(AddQuestionViewImplUiBinder.class);

	interface AddQuestionViewImplUiBinder extends UiBinder<Widget, AddQuestionsViewImpl> {
	}

	private Presenter listener;
	
//	@UiField PanelCollapse panelCollapse;


	public AddQuestionsViewImpl() {
		initWidget(uiBinder.createAndBindUi(this));
		
//		panelCollapse.addShowHandler(new ShowHandler() {
//			@Override
//			public void onShow(ShowEvent showEvent) {
//				GWT.log("panelHeader clicked ");
//			}
//		});
	}

	@Override
	public void setPresenter(Presenter listener) {
		this.listener = listener;
	}
}