package aggregator.model;

import aggregator.vo.Vacancy;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HHStrategy implements Strategy {
    private static final String URL_FORMAT = "http://hh.ru/search/vacancy?text=java+%s&page=%d";

    @Override
    public List<Vacancy> getVacancies(String searchString) throws IOException {
        List<Vacancy> vacancies = new ArrayList<>();

        try {
            int pageNumber = 0;
            Document document;
            while (true) {

                document = getDocument(searchString, pageNumber++);
                if (document == null) break;

                Elements elements = document.select("[data-qa=vacancy-serp__vacancy]");
                if (elements.size() == 0) break;

                for (Element element : elements) {

                    Element titleElement = element.select("[data-qa=vacancy-serp__vacancy-title]").first();
                    String title = titleElement.text();

                    Element salaryElement = element.select("[data-qa=vacancy-serp__vacancy-compensation]").first();
                    String salary  = (salaryElement != null ? salaryElement.text() : "");

                    String city = element.select("[data-qa=vacancy-serp__vacancy-address]").first().text();

                    String companyName = element.select("[data-qa=vacancy-serp__vacancy-employer]").first().text();

                    String siteName = "http://hh.ru/";

                    String url = titleElement.attr("href");

                    Vacancy vacancy = new Vacancy();
                    vacancy.setTitle(title);
                    vacancy.setCity(city);
                    vacancy.setCompanyName(companyName);
                    vacancy.setSalary(salary);
                    vacancy.setSiteName(siteName);
                    vacancy.setUrl(url);
                    vacancies.add(vacancy);

                }
            }
        }catch (Exception e) {

        }

        return vacancies;

    }

    protected Document getDocument(String searchString, int page) throws IOException {

        String url = String.format(URL_FORMAT, searchString, page);

        Document doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36")
                .referrer("http://google.ru")
                .get();

        return doc;
    }
}
