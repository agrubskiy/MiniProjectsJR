package urlshortener;

public class ExceptionHandler {
    public static void log(Exception e) {
        Helper.printMessage(e.getMessage());
    }
}
