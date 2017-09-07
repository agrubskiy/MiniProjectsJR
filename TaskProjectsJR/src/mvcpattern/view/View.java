package mvcpattern.view;

import mvcpattern.controller.Controller;
import mvcpattern.model.ModelData;

public interface View {
    void refresh (ModelData modelData);
    void setController(Controller controller);
}
