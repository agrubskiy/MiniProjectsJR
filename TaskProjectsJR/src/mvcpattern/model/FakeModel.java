package mvcpattern.model;

import mvcpattern.bean.User;

import java.util.ArrayList;
import java.util.List;

public class FakeModel implements Model {
    private ModelData modelData = new ModelData();

    @Override
    public void loadUserById(long userId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void loadDeletedUsers() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteUserById(long id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void changeUserData(String name, long id, int level) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ModelData getModelData() {
        return modelData;
    }

    @Override
    public void loadUsers() {
        List<User> users = new ArrayList<>();
        users.add(new User("Anton", 1, 1));
        users.add(new User("Barton", 2, 1));
        modelData.setUsers(users);
    }
}
