package pl.johnny.gwtQuiz.client;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

import pl.johnny.gwtQuiz.client.mvp.AppActivityMapper;
import pl.johnny.gwtQuiz.client.mvp.AppPlaceHistoryMapper;
import pl.johnny.gwtQuiz.client.mvp.NavActivityMapper;
import pl.johnny.gwtQuiz.client.place.MainMenuPlace;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class GWTQuiz implements EntryPoint {
	private Place defaultPlace = new MainMenuPlace("World!!!");

	private final DockPanel dockLayoutPanel = new DockPanel();
	private SimplePanel navBarWidget = new SimplePanel();
	private SimplePanel appWidget = new SimplePanel();
	
	AcceptsOneWidget northDisplay = new AcceptsOneWidget() {
		@Override
		public void setWidget(IsWidget activityWidget) {
			Widget widget = Widget.asWidgetOrNull(activityWidget);
			navBarWidget.setVisible(widget != null);
			navBarWidget.setWidget(widget);
		}
	};
	
	AcceptsOneWidget southDisplay = new AcceptsOneWidget() {
		@Override
		public void setWidget(IsWidget activityWidget) {
			Widget widget = Widget.asWidgetOrNull(activityWidget);
			appWidget.setVisible(widget != null);
			appWidget.setWidget(widget);
		}
	};

	/**
	 * This is the entry point method.
	 */
	@Override
	public void onModuleLoad() {

		dockLayoutPanel.setWidth("100%");

		navBarWidget.getElement().getStyle()
				.setProperty("border", "3px solid red");

		dockLayoutPanel.getElement().getStyle()
				.setProperty("border", "3px solid green");

		dockLayoutPanel.add(navBarWidget, DockPanel.NORTH);
		dockLayoutPanel.add(appWidget, DockPanel.SOUTH);

		// Create ClientFactory using deferred binding so we can replace with different
		// impls in gwt.xml
		ClientFactory clientFactory = GWT.create(ClientFactory.class);
		EventBus eventBus = clientFactory.getEventBus();
		PlaceController placeController = clientFactory.getPlaceController();

		// Start ActivityManager for the main widget with our ActivityMapper
		ActivityMapper navActivityMapper = new NavActivityMapper(clientFactory);
		ActivityManager navActivityManager = new ActivityManager(navActivityMapper, eventBus);
		navActivityManager.setDisplay(northDisplay);

		// Start ActivityManager for the main widget with our ActivityMapper
		ActivityMapper mainActivityMapper = new AppActivityMapper(clientFactory);
		ActivityManager mainActivityManager = new ActivityManager(mainActivityMapper, eventBus);
		mainActivityManager.setDisplay(southDisplay);

		// Start PlaceHistoryHandler with our PlaceHistoryMapper
		AppPlaceHistoryMapper historyMapper = GWT.create(AppPlaceHistoryMapper.class);
		PlaceHistoryHandler historyHandler = new PlaceHistoryHandler(historyMapper);
		historyHandler.register(placeController, eventBus, defaultPlace);

		RootPanel.get().add(dockLayoutPanel);
		// Goes to place represented on URL or default place
		historyHandler.handleCurrentHistory();
	}
}