package pl.johnny.gwtQuiz.client.ui.widgets;

import org.gwtbootstrap3.client.ui.constants.ImageType;
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
import com.google.gwt.event.dom.client.DragLeaveEvent;
import com.google.gwt.event.dom.client.DragLeaveHandler;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DragOverHandler;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

import pl.johnny.gwtQuiz.client.ui.AddQuestionsView.Presenter;  
  
/** 
 * Simple Text Link and Progress text label example of GWT Uploader 
 */  
public class UploadWidget extends Composite{  
  
    private Label progressLabel;  
    private Uploader uploader;  
    private VerticalPanel verticalPanel = new VerticalPanel();  
    private org.gwtbootstrap3.client.ui.Image recivedImg = new org.gwtbootstrap3.client.ui.Image();
  
	public UploadWidget(final Presenter listener) {  
        progressLabel = new Label();  
        progressLabel.setStyleName("progressLabel");  
  
        uploader = new Uploader();
        
        /*
         * Set post parameter with currently highest id (+1) from questions database table 
         * so server can create folder with this value in 
         * /GWTQuiz/src/main/webapp/quiz_resources/question_images/{id}
         */
        JSONObject params = new JSONObject();
        params.put("post_param_name_1", new JSONString("HELLO_BITCH!"));
        uploader.setPostParams(params);
        
        uploader.setUploadURL(GWT.getModuleBaseURL() + "upload")  
        	.setButtonText("<button class=\"btn btn-default\">Click to upload image</button>")
            .setButtonTextStyle(".buttonText {font-family: Arial, sans-serif; font-size: 14px; color: #BB4B44}")  
            .setFileSizeLimit("1 MB")  
            .setButtonWidth(200)  
            .setButtonHeight(42)  
            .setButtonCursor(Uploader.Cursor.HAND)  
            .setButtonAction(Uploader.ButtonAction.SELECT_FILE)  
            .setUploadProgressHandler(new UploadProgressHandler() {  
                @Override
				public boolean onUploadProgress(UploadProgressEvent uploadProgressEvent) {  
                    progressLabel.setText(NumberFormat.getPercentFormat().format(  
                        (double)uploadProgressEvent.getBytesComplete() / (double)uploadProgressEvent.getBytesTotal()  
                    ));  
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
//                    		.append(uploadSuccessEvent.getFile().getName())  
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
                    
                    //Send uploaded image name into addQuestionsActivity.
                    listener.setUploadedImageName(uploadSuccessEvent.getFile().getName());
                    
                    //Display uploded image
                    recivedImg.setUrl("data:image;base64," + uploadSuccessEvent.getServerData());
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
                    if (fileDialogCompleteEvent.getTotalFilesInQueue() > 0 && uploader.getStats().getUploadsInProgress() <= 0) {  
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
                    Window.alert("Upload of file " + fileQueueErrorEvent.getFile().getName() + " failed due to [" +  
                        fileQueueErrorEvent.getErrorCode().toString() + "]: " + fileQueueErrorEvent.getMessage()  
                    );  
                    return true;  
                }  
            })  
            .setUploadErrorHandler(new UploadErrorHandler() {  
                @Override
				public boolean onUploadError(UploadErrorEvent uploadErrorEvent) {  
                    resetText();  
                    Window.alert("Upload of file " + uploadErrorEvent.getFile().getName() + " failed due to [" +  
                        uploadErrorEvent.getErrorCode().toString() + "]: " + uploadErrorEvent.getMessage()  
                    );  
                    return true;  
                }  
            });  
        
        
       
        verticalPanel.add(uploader);  
        
        //Drag and drop field.
        if (Uploader.isAjaxUploadWithProgressEventsSupported()) {  
            final Label dropFilesLabel = new Label("Drop image here");  
            dropFilesLabel.setStyleName("dropFilesLabel");  
            dropFilesLabel.addDragOverHandler(new DragOverHandler() {  
                @Override
				public void onDragOver(DragOverEvent event) {  
                    if (!uploader.getButtonDisabled()) {  
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
  
                    if (uploader.getStats().getUploadsInProgress() <= 0) {  
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
  
        
        verticalPanel.add(progressLabel);
        verticalPanel.getElement().getStyle().setProperty("marginLeft", "auto");
        verticalPanel.getElement().getStyle().setProperty("marginRight", "auto");
//        verticalPanel.setCellHorizontalAlignment(uploader, HorizontalPanel.ALIGN_LEFT);  
//        verticalPanel.setCellHorizontalAlignment(progressLabel, HorizontalPanel.ALIGN_RIGHT);  
  
        //noinspection GwtToHtmlReferences  
//        RootPanel.get().add(verticalPanel); 
        
        initWidget(verticalPanel);
    }  
  
    private void resetText() {  
        progressLabel.setText("");  
        uploader.setButtonText("<button class=\"btn btn-default\">Click to upload image</button>");  
    }  
}  