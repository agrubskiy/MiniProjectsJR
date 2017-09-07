package aggregator.view;

import aggregator.Controller;
import aggregator.vo.Vacancy;

import java.io.IOException;
import java.util.List;

public interface View {
    void update(List<Vacancy> vacancies) throws IOException;
    void setController(Controller controller);
}
