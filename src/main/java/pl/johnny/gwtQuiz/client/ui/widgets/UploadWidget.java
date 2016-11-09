package pl.johnny.gwtQuiz.client.ui.widgets;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Icon;
import org.gwtbootstrap3.client.ui.Image;
import org.gwtbootstrap3.client.ui.Modal;
import org.gwtbootstrap3.client.ui.ModalBody;
import org.gwtbootstrap3.client.ui.ModalFooter;
import org.gwtbootstrap3.client.ui.Tooltip;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.ContextualBackground;
import org.gwtbootstrap3.client.ui.constants.IconSize;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.ImageType;
import org.gwtbootstrap3.client.ui.html.Span;
import org.moxieapps.gwt.uploader.client.Uploader;
import org.moxieapps.gwt.uploader.client.events.FileDialogCompleteEvent;
import org.moxieapps.gwt.uploader.client.events.FileDialogCompleteHandler;
import org.moxieapps.gwt.uploader.client.events.FileQueueErrorEvent;
import org.moxieapps.gwt.uploader.client.events.FileQueueErrorHandler;
import org.moxieapps.gwt.uploader.client.events.UploadErrorEvent;
import org.moxieapps.gwt.uploader.client.events.UploadErrorHandler;
import org.moxieapps.gwt.uploader.client.events.UploadProgressEvent;
import org.moxieapps.gwt.uploader.client.events.UploadProgressHandler;
import org.moxieapps.gwt.uploader.client.events.UploadSuccessEvent;
import org.moxieapps.gwt.uploader.client.events.UploadSuccessHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.DragLeaveEvent;
import com.google.gwt.event.dom.client.DragLeaveHandler;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DragOverHandler;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

import pl.johnny.gwtQuiz.client.ui.AddQuestionsView.Presenter;

/** 
 * Simple Text Link and Progress text label example of GWT Uploader 
 */
public class UploadWidget extends Composite {

	private Label progressLabel = new Label();;
	private Modal modal;
	private Image recivedImg = new Image();
	private ModalBody modalBody;
	private final ModalFooter modalFooter;
	private Uploader uploader;
	private VerticalPanel verticalPanel = new VerticalPanel();
	private HTML modalLabel = new HTML();
	private Presenter listener;

	public UploadWidget(final Presenter listener) {
		this.listener = listener;
		//Modal settings
		modal = new Modal();
		modal.setClosable(false);
		modal.setFade(true);
		modal.setTitle("Upload Error");
		modalBody = new ModalBody();
		modal.add(modalBody);
		
		modalFooter = new ModalFooter();
		Button okBtn = new Button("OK");
		okBtn.setType(ButtonType.PRIMARY);
		okBtn.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				 modal.hide();
			}
		});
		modalFooter.getElement().getStyle().setTextAlign(TextAlign.CENTER);
        modalFooter.add(okBtn);
        modal.add(modalFooter);
        
		progressLabel.setStyleName("progressLabel");

		uploader = new Uploader();
		uploader.setUploadURL(GWT.getModuleBaseURL() + "upload")
				.setButtonText("<button class=\"btn btn-default\">Click to upload image</button>")
				.setButtonTextStyle(".buttonText {font-family: Arial, sans-serif; font-size: 14px; color: #BB4B44}")
				.setFileSizeLimit("1 MB")
				.setFileTypes("*.png;*.jpg;*.jpeg;*.img")
				//            .setButtonWidth(200)  
				//            .setButtonHeight(42)  
				.setButtonCursor(Uploader.Cursor.HAND)
				.setButtonAction(Uploader.ButtonAction.SELECT_FILE)
				.setUploadProgressHandler(new UploadProgressHandler() {
					@Override
					public boolean onUploadProgress(UploadProgressEvent uploadProgressEvent) {
						progressLabel.setText(NumberFormat.getPercentFormat().format(
								(double) uploadProgressEvent.getBytesComplete() / (double) uploadProgressEvent.getBytesTotal()));
						return true;
					}
				})
				.setUploadSuccessHandler(new UploadSuccessHandler() {

					@Override
					public boolean onUploadSuccess(UploadSuccessEvent uploadSuccessEvent) {
						resetText();
						StringBuilder sb = new StringBuilder();
						sb
								//                    	.append("File ")
								                    		.append(uploadSuccessEvent.getFile().getName())  
								//                        .append(" (")  
								//                        .append(NumberFormat.getDecimalFormat().format(uploadSuccessEvent.getFile().getSize() / 1024))  
								//                        .append(" KB)")  
								//                        .append(" uploaded successfully at ")  
								//                        .append(NumberFormat.getDecimalFormat().format(  
								//                            uploadSuccessEvent.getFile().getAverageSpeed() / 1024  
								//                        ))  
								//                        .append(" Kb/second")
								.append(uploadSuccessEvent.getFile().getPercentUploaded());

						//Handle the server response
						//                    GWT.log(uploadSuccessEvent.getServerData().toString());

						//                    recivedImg.setSize("480px", "270px");

						/* Display uploded image:
						Parse response into JSON */
						JSONValue jsonValue = JSONParser.parseStrict(uploadSuccessEvent.getServerData());
						//Convert JSONValue into JSONObject
						JSONObject responseAsObject = jsonValue.isObject();
						//Convert JSONValue into String
						//Get base64 image from JSON
						JSONString base64EncodedString = responseAsObject.get("base64EncodedString").isString();
						//Get image path from JSON
						JSONString imagePath = responseAsObject.get("pathToFile").isString();
						//Send uploaded image path to addQuestionsActivity.
						listener.setUploadedImageName(imagePath.stringValue());
						
						//Received image tag settings
						recivedImg.getElement().setId("recivedImage");
						//Delete image on double click/tap
						recivedImg.addDoubleClickHandler(new DoubleClickHandler() {
							
							@Override
							public void onDoubleClick(DoubleClickEvent event) {
								resetImg();
							}
						});
						recivedImg.setVisible(true);
						recivedImg.setUrl("data:image;base64," + base64EncodedString.stringValue());
						recivedImg.setType(ImageType.ROUNDED);
						recivedImg.setResponsive(true);
						verticalPanel.add(recivedImg);

						progressLabel.setText(sb.toString());
						return true;
					}
				})
				.setFileDialogCompleteHandler(new FileDialogCompleteHandler() {
					@Override
					public boolean onFileDialogComplete(FileDialogCompleteEvent fileDialogCompleteEvent) {
						if(fileDialogCompleteEvent.getTotalFilesInQueue() > 0 && uploader.getStats().getUploadsInProgress() <= 0) {
							progressLabel.setText("0%");
							uploader.setButtonText("<button class=\"btn btn-default\">Uploading...</button>");
							uploader.startUpload();
						}
						return true;
					}
				})
				.setFileQueueErrorHandler(new FileQueueErrorHandler() {
					@Override
					public boolean onFileQueueError(FileQueueErrorEvent fileQueueErrorEvent) {
						resetText();
						
//						Window.alert("1 Upload of file " + fileQueueErrorEvent.getFile().getName() + " failed due to [" +
//								fileQueueErrorEvent.getErrorCode().toString() + "]: " + fileQueueErrorEvent.getMessage());
						
						//Check whether error server response is 415 and display proper message to the user
						if(fileQueueErrorEvent.getMessage() == "Unsuccessful server response code of: 415") {
							modalLabel.setText("Uploaded file is NOT in one of those formats: png ,jpeg, img ,jpg.");
						} else {
							modalLabel.setText(fileQueueErrorEvent.getMessage());
						}
						modalBody.add(modalLabel);
						modal.show();
						return true;
					}
				})
				.setUploadErrorHandler(new UploadErrorHandler() {
					@Override
					public boolean onUploadError(UploadErrorEvent uploadErrorEvent) {
						resetText();
						
//						Window.alert("2 Upload of file " + uploadErrorEvent.getFile().getName() + " failed due to [" +
//								uploadErrorEvent.getErrorCode().toString() + "]: " + uploadErrorEvent.getMessage());
						
						//Check whether error server response is 415 and display proper message to the user
						if(uploadErrorEvent.getMessage() == "Unsuccessful server response code of: 415") {
							modalLabel.setText("Uploaded file is NOT in one of those formats: png ,jpeg, img ,jpg.");
						} else {
							modalLabel.setText(uploadErrorEvent.getMessage());
						}
						modalBody.add(modalLabel);
						modal.show();
						return true;
					}
				});

		verticalPanel.add(uploader);

		//Drag and drop field.
		if(Uploader.isAjaxUploadWithProgressEventsSupported()) {
			final Label dropFilesLabel = new Label("Or drop image here");
			dropFilesLabel.setStyleName("dropFilesLabel hidden-xs");
			dropFilesLabel.addDragOverHandler(new DragOverHandler() {
				@Override
				public void onDragOver(DragOverEvent event) {
					if(!uploader.getButtonDisabled()) {
						dropFilesLabel.addStyleName("dropFilesLabelHover");
					}
				}
			});
			dropFilesLabel.addDragLeaveHandler(new DragLeaveHandler() {
				@Override
				public void onDragLeave(DragLeaveEvent event) {
					dropFilesLabel.removeStyleName("dropFilesLabelHover");
				}
			});
			dropFilesLabel.addDropHandler(new DropHandler() {
				@Override
				public void onDrop(DropEvent event) {
					dropFilesLabel.removeStyleName("dropFilesLabelHover");

					if(uploader.getStats().getUploadsInProgress() <= 0) {
						//                        progressBarPanel.clear();  
						//                        progressBars.clear();  
						//                        cancelButtons.clear();  
					}

					uploader.addFilesToQueue(Uploader.getDroppedFiles(event.getNativeEvent()));
					event.preventDefault();
				}
			});
			verticalPanel.add(dropFilesLabel);
		}
		
		Tooltip tooltip = new Tooltip();
		tooltip.setTitle("Uploader accepts files up to 1Mb in one of the following formats: png ,jpeg, img ,jpg."
				+ " To delete uploaded image - double click on it.");
		Icon questionIcon = new Icon(IconType.QUESTION_CIRCLE);
		questionIcon.setSize(IconSize.LARGE);
		tooltip.add(questionIcon);
		verticalPanel.add(tooltip);
		verticalPanel.add(progressLabel);
		verticalPanel.getElement().getStyle().setWidth(100.0, Unit.PCT);

		initWidget(verticalPanel);
	}
	
	/**
	 * Resets Label with upload status.
	 */
	private void resetText() {
		progressLabel.setText("");
		uploader.setButtonText("<button class=\"btn btn-default\">Click to upload image</button>");
	}
	
	private void resetImg() {
		//Delete path to image so it won't be shown.
		recivedImg.setUrl("");
		recivedImg.setVisible(false);
		//Reset progress label.
		resetText();
		//Set uploaded image name to null since we deleted the image.
		listener.setUploadedImageName(null);
	}
}