package pl.johnny.gwtQuiz.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RootPanel;

public class GWTQuiz implements EntryPoint {
  @Override
public void onModuleLoad() {
    // Use Widget API to Create a <paper-button>
    Button button = new Button("Press me!");
    RootPanel.get().add(button);
  }
}