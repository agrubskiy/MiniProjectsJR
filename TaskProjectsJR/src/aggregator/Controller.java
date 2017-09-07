package aggregator;

import aggregator.model.Model;
import aggregator.model.Provider;
import aggregator.vo.Vacancy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Controller {
    private Model model;

    public Controller(Model model) {
        this.model = model;
        if (model == null)
            throw new IllegalArgumentException();
    }

    public void onCitySelect(String cityName) throws IOException {
        model.selectCity(cityName);
    }
}
