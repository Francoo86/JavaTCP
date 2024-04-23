package genericmenu;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

public class JFileChooserPDF {
    private static final String TYPES_DESC = "Archivos PDF";
    private static final String REQUIRED_TYPE = "pdf";

    private File selectedFile;

    public boolean show() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        FileNameExtensionFilter pdfFilter = new FileNameExtensionFilter(TYPES_DESC, REQUIRED_TYPE);
        fileChooser.setFileFilter(pdfFilter);

        int result = fileChooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            return true;
        } else {
            selectedFile = null;
            return false;
        }
    }

    public File getSelectedFile() {
        return selectedFile;
    }

    public boolean isValid() {
        return selectedFile != null;
    }
}