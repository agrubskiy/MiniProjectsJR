package aggregator.model;

import aggregator.vo.Vacancy;

import java.io.IOException;
import java.util.List;

public class Provider {
    private Strategy strategy;

    public Provider(Strategy strategy) {
        this.strategy = strategy;
    }

    public Strategy getStrategy() {
        return strategy;
    }

    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }

    public List<Vacancy> getJavaVacancies(String searchString) throws IOException {
        return strategy.getVacancies(searchString);
    }
}
