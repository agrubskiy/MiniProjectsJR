package mvcpattern.view;

import mvcpattern.bean.User;
import mvcpattern.controller.Controller;
import mvcpattern.model.ModelData;

public class EditUserView implements View{
    private Controller controller;
    @Override
    public void refresh(ModelData modelData) {
        System.out.println("User to be edited:");
            System.out.print('\t');
            System.out.println(modelData.getActiveUser());
        System.out.println("===================================================");
    }

    public void fireEventUserDeleted(long id) { controller.onUserDelete(id); }

    public void fireEventUserChanged(String name, long id, int level) { controller.onUserChange(name, id, level); }

    @Override
    public void setController(Controller controller) {
        this.controller = controller;
    }
}
