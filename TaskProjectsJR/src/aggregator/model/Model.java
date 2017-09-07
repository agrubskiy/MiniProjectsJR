package aggregator.model;

import aggregator.view.View;
import aggregator.vo.Vacancy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Model {

    private View view;
    private Provider[] providers;

    public Model(View view, Provider... providers) {
        if (view == null || providers == null || providers.length == 0)
            throw new IllegalArgumentException();

        this.view = view;
        this.providers = providers;

    }

    public void selectCity(String city) throws IOException {

        List<Vacancy> vacancies = new ArrayList<>();

        for (Provider provider : providers) {

            for (Vacancy vacancy : provider.getJavaVacancies(city)) {
                vacancies.add(vacancy);
            }

        }

        view.update(vacancies);
    }
}
