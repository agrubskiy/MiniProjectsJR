package mvcpattern.view;

import mvcpattern.bean.User;
import mvcpattern.controller.Controller;
import mvcpattern.model.ModelData;

public class UsersView implements View{
    private Controller controller;

    public void fireEventShowAllUsers() {
        controller.onShowAllUsers();
    }

    public void fireEventShowDeletedUsers() { controller.onShowAllDeletedUsers();}

    public void fireEventOpenUserEditForm(long id) { controller.onOpenUserEditForm(id); }


    public void refresh(ModelData modelData) {
        if (!modelData.isDisplayDeletedUserList())
        System.out.println("All users:");
        else System.out.println("All deleted users:");
        for (User user: modelData.getUsers()) {
            System.out.print('\t');
            System.out.println(user);
        }
        System.out.println("===================================================");
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }
}
