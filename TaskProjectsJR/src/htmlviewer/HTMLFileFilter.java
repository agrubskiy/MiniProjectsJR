package htmlviewer;

import javax.swing.filechooser.FileFilter;
import java.io.File;

public class HTMLFileFilter extends FileFilter {
    @Override
    public boolean accept(File f) {
        if (f.isDirectory()) return true;
        if (f.getName().toLowerCase().contains(".html") || f.getName().toLowerCase().contains(".htm")) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String getDescription() {
        return "HTML и HTM файлы";
    }
}
