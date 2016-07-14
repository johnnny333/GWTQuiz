package pl.johnny.gwtQuiz.client.ui;

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
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;  
  
/** 
 * Simple Text Link and Progress text label example of GWT Uploader 
 */  
public class TextButtonAndProgressText extends Composite{  
  
    private Label progressLabel;  
    private Uploader uploader;  
    private VerticalPanel verticalPanel = new VerticalPanel();  
    private Image recivedImg = new Image();
  
	public TextButtonAndProgressText() {  
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
            .setButtonText("<span class=\"buttonText\">Click to Upload</span>")  
            .setButtonTextStyle(".buttonText {font-family: Arial, sans-serif; font-size: 14px; color: #BB4B44}")  
            .setFileSizeLimit("50 MB")  
            .setButtonWidth(150)  
            .setButtonHeight(22)  
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
                    sb.append("File ").append(uploadSuccessEvent.getFile().getName())  
                        .append(" (")  
                        .append(NumberFormat.getDecimalFormat().format(uploadSuccessEvent.getFile().getSize() / 1024))  
                        .append(" KB)")  
                        .append(" uploaded successfully at ")  
                        .append(NumberFormat.getDecimalFormat().format(  
                            uploadSuccessEvent.getFile().getAverageSpeed() / 1024  
                        ))  
                        .append(" Kb/second")
                        .append(uploadSuccessEvent.getFile().getPercentUploaded()); 
                    
                    //Handle the server response
                    GWT.log(uploadSuccessEvent.getServerData().toString());
                    
                    recivedImg.setUrl("data:image;base64," + uploadSuccessEvent.getServerData());
                    GWT.log("base64? " + recivedImg);
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
                        uploader.setButtonText("<span class=\"buttonText\">Uploading...</span>");  
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
            final Label dropFilesLabel = new Label("Drop Files Here");  
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
        verticalPanel.setCellHorizontalAlignment(uploader, HorizontalPanel.ALIGN_LEFT);  
        verticalPanel.setCellHorizontalAlignment(progressLabel, HorizontalPanel.ALIGN_LEFT);  
  
        //noinspection GwtToHtmlReferences  
//        RootPanel.get().add(verticalPanel); 
        
        initWidget(verticalPanel);
    }  
  
    private void resetText() {  
        progressLabel.setText("");  
        uploader.setButtonText("<span class=\"buttonText\">Click to Upload</span>");  
    }  
}  