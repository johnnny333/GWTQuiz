/**
 * @author jzarewicz
 */
package pl.johnny.gwtQuiz.client.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.gwtbootstrap3.client.ui.Modal;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.HTML;

import pl.johnny.gwtQuiz.client.ClientFactory;
import pl.johnny.gwtQuiz.client.ClientFactory.CookieType;
import pl.johnny.gwtQuiz.client.place.AddQuestionsPlace;
import pl.johnny.gwtQuiz.client.place.AdminPlace;
import pl.johnny.gwtQuiz.client.place.LoginPlace;
import pl.johnny.gwtQuiz.client.ui.AdminView;
import pl.johnny.gwtQuiz.client.ui.widgets.PanelWidget;
import pl.johnny.gwtQuiz.shared.Question;
import pl.johnny.gwtQuiz.shared.SQLConstraintException;

public class AdminActivity extends AbstractActivity implements AdminView.Presenter {
	// Used to obtain views, eventBus, placeController
	// Alternatively, could be injected via GIN
	private ClientFactory clientFactory;
	private AdminView adminView;
	private AdminView.Presenter adminViewPresenter = this;

	public AdminActivity(AdminPlace place, final ClientFactory clientFactory) {
		this.adminView = clientFactory.getAdminView();
		this.clientFactory = clientFactory;
	}

	/**
	 * Invoked by the ActivityManager to start a new Activity
	 */
	@Override
	public void start(final AcceptsOneWidget containerWidget, EventBus eventBus) {
		
		/**
		 * Check for session cookie and if exist, validate it on server. If
		 * validation passed, let user stay into AdmininPlace. Otherwise,
		 * redirect him into LoginPlace.
		 */
		String cookieSessionID = clientFactory.getCookie(CookieType.SESSION_ID);
		if (cookieSessionID == null) {
			Cookies.removeCookie("gwtQuiz");
			goTo(new LoginPlace(""));
			return;
		} else {
			clientFactory.getQuestionsService().validateSession(cookieSessionID,clientFactory.getCookie(CookieType.UUID),
					new AsyncCallback<String[][]>() {

				@Override
				public void onFailure(Throwable caught) {
					GWT.log("AdminActivity.validateSession() failed", caught);
					return;
				}

				@Override
				public void onSuccess(String[][] result) {
					
					/** 
					 * If user is not logged (IOW don't have his user cookie but other user cookie exist
					 * in browser e.g user spoofed cookie) restrict access to AdminActicity.
					 * Else if user is not a super user(0) but a normal user(1)
					 * also restrict access to AdminActivity.
					 */
					if (result == null) {
						Cookies.removeCookie("gwtQuiz");
						goTo(new LoginPlace(""));
					} else if(Integer.parseInt(result[0][1]) == 1) {
						goTo(new AddQuestionsPlace(""));
					}else {
						adminView = clientFactory.getAdminView();
						adminView.setPresenter(adminViewPresenter);
						containerWidget.setWidget(adminView.asWidget());

						fetchAndBuildPanelWithTmpQuestions();		
					}
				}
			});
		}
	}

	/**
	 * Ask user before stopping this activity
	 */
	@Override
	public String mayStop() {
		return null;
	}

	/**
	 * Navigate to a new Place in the browser
	 */
	@Override
	public void goTo(Place place) {
		clientFactory.getPlaceController().goTo(place);
	}

	/**
	 * Set categories from database into field in AdminView. After successful
	 * upload of categories, call database for temporary questions and hand them
	 * to AdminView.
	 */
	@Override
	public void fetchAndBuildPanelWithTmpQuestions() {

		clientFactory.getQuestionsService().getCategories(new AsyncCallback<String[][]>() {

			@Override
			public void onSuccess(String[][] result) {
				
				adminView.setCategories(result);
				adminView.buildCategoriesCellList(result);

				// Get tmp questions
				clientFactory.getQuestionsService().getTmpQuestions(new AsyncCallback<ArrayList<Question>>() {

					@Override
					public void onSuccess(ArrayList<Question> result) {
						adminView.buildAndFillPanelsWithTmpQuestions(result);
					}

					@Override
					public void onFailure(Throwable caught) {
						GWT.log("Failed AdminActivity.getTmpQuestions() RPC! ", caught);
					}
				});
			}

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Failed AdminActivity.getCategories() RPC! ", caught);
			}
		});
	}

	@Override
	public void acceptUserTmpQuestion(Question acceptedQuestion, String tmpQuestionID) {
		clientFactory.getQuestionsService().acceptUserTmpQuestion(acceptedQuestion, tmpQuestionID,
				new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {

						// Catch Hibernate validation exception message.
						if (caught instanceof ConstraintViolationException) {

							ConstraintViolationException violationException = (ConstraintViolationException) caught;
							Set<ConstraintViolation<?>> violations = violationException.getConstraintViolations();

							StringBuffer sb = new StringBuffer();
							for (ConstraintViolation<?> constraintViolation : violations) {
								sb.append(constraintViolation.getPropertyPath().toString()).append(":")
										.append(constraintViolation.getMessage()).append("\n")
										.append(constraintViolation.getRootBean());

								GWT.log("acceptUserTmpQuestion hibernate " + sb);

								PanelWidget[] panelWidgets = adminView.getPanelWidgets();

								for (int i = 0; i < panelWidgets.length; i++) {
									panelWidgets[i].setServerErrorMessage(
											constraintViolation.getPropertyPath().toString(),
											constraintViolation.getMessage());
								}

							}
							GWT.log("insertUserQuestion On Failure hibernate " + sb);
							return;
						}

						GWT.log("acceptUserTmpQuestion failed", caught);
					}

					@Override
					public void onSuccess(Void result) {
						GWT.log("acceptUserTmpQuestion succeded,");
						adminView.refreshPanel();
					}
				});
	}

	@Override
	public void deleteUserTmpQuestion(String questionID) {
		clientFactory.getQuestionsService().deleteUserTmpQuestion(questionID, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("deleteUserTmpQuestion failed", caught);
			}

			@Override
			public void onSuccess(Void result) {
				GWT.log("deleteUserTmpQuestion succeded");
				adminView.refreshPanel();
			}
		});
	}

	@Override
	public void addCategory(final String newCategory, final List<String> list) {
		clientFactory.getQuestionsService().insertNewCategory(newCategory, new AsyncCallback<Void>() {

			@Override
			public void onSuccess(Void result) {
				GWT.log("Category added successfully in AdminActivity.addCategory()");
				/*On success, update categoriesTable list to reflect change in data source.
				The dataProvider will update the cellList. */
				list.add(newCategory);
			}

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("AdminActivity.addCategory() failed", caught);
			}
		});
	}

	@Override
	public void removeCategory(final String categoryToDelete, final List<String> list, final Modal constraintModal) {
		clientFactory.getQuestionsService().deleteCategory(categoryToDelete, new AsyncCallback<Void>() {

			@Override
			public void onSuccess(Void result) {
				GWT.log("Category deleted successfully in AdminActivity.removeCategory()");
				/*On success, update categoriesTable list to reflect change in data source.
				The dataProvider will update the cellList. */
				list.remove(categoryToDelete);
			}

			@Override
			public void onFailure(Throwable caught) {

				if (caught instanceof SQLConstraintException && caught.getMessage().equals("[SQL_CONSTRAINT]")) {
					GWT.log("[SQL_CONSTRAINT] catched in AdminActivity || " + caught.getMessage());

					constraintModal.show();
				}
				GWT.log("AdminActivity.deleteCategory() failed", caught);
			}
		});
	}
	
	@Override
	public void updateCategory(final String updatedCategory, final String oldCategoryValue, 
			final int indexOnListOfUpdatedCategory, final List<String> categoriesDatabProviderList){
		
		clientFactory.getQuestionsService().getCategory(oldCategoryValue, new AsyncCallback<String[][]>() {
			
			@Override
			public void onSuccess(String[][] result) {
								
				clientFactory.getQuestionsService().updateCategory(updatedCategory, Integer.parseInt(result[0][0]), new AsyncCallback<Void>() {
					
					@Override
					public void onSuccess(Void result) {
						GWT.log("Category updated successfully in AdminActivity.updateCategory()");
						categoriesDatabProviderList.set(indexOnListOfUpdatedCategory, updatedCategory);
					}
					
					@Override
					public void onFailure(Throwable caught) {
						GWT.log("AdminActivity.updateCategory() failed", caught);
					}
				});
			}
			
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("AdminActivity.getCategory() failed", caught);
			}
		});
	}
}