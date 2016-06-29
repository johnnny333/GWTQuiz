package pl.johnny.gwtQuiz.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class HighScoresViewImpl extends Composite implements HighScoresView {

	private static HighScoresViewImplUiBinder uiBinder = GWT.create(HighScoresViewImplUiBinder.class);

	interface HighScoresViewImplUiBinder extends UiBinder<Widget, HighScoresViewImpl> {
	}

	public HighScoresViewImpl() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	private Presenter listener;

	@UiField HTMLPanel cellTableContainer;

	@Override
	public void setPresenter(Presenter listener) {
		this.listener = listener;
	}
	
	@Override
	public void buildAndFillHighScoreCellTableView(HighScoreCellTableView highScoreCellTableView) {
		cellTableContainer.add(highScoreCellTableView);
		listener.getUserScores();
	}
}
