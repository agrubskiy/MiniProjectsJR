package mvcpattern;

import mvcpattern.controller.Controller;
import mvcpattern.model.FakeModel;
import mvcpattern.model.MainModel;
import mvcpattern.model.Model;
import mvcpattern.view.EditUserView;
import mvcpattern.view.UsersView;

public class Solution {
    public static void main(String[] args) {
        Model model = new MainModel();
        UsersView usersView = new UsersView();
        Controller controller = new Controller();
        EditUserView editUserView = new EditUserView();

        usersView.setController(controller);
        controller.setModel(model);
        controller.setUsersView(usersView);
        editUserView.setController(controller);
        controller.setEditUserView(editUserView);

        usersView.fireEventShowAllUsers();
        usersView.fireEventOpenUserEditForm(126L);
        editUserView.fireEventUserChanged("C", 126L, 1);
        editUserView.fireEventUserDeleted(124L);

        usersView.fireEventShowDeletedUsers();

    }
}