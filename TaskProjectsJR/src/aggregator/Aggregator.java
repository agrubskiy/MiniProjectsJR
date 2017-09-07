package aggregator;

import aggregator.model.HHStrategy;
import aggregator.model.Model;
import aggregator.model.MoikrugStrategy;
import aggregator.model.Provider;
import aggregator.view.HtmlView;

import java.io.IOException;

public class Aggregator {
    public static void main(String... args) throws IOException {
        HtmlView view = new HtmlView();
        Provider[] provider = new Provider[]{new Provider(new HHStrategy()), new Provider(new MoikrugStrategy())};
        Model model = new Model(view, provider);
        Controller controller = new Controller(model);
        view.setController(controller);
        view.userCitySelectEmulationMethod();
    }
}
