package genericmenu;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.JTextComponent;

public class JFileChooserPDF extends JFrame implements ActionListener {
    //data of the descriptors.
    private static final String FILES_DESC = "Archivos PDF";
    private static final String FILETYPE = "pdf";

    //height and width
    private final int WIDTH = 400;
    private final int HEIGHT = 150;

    private JTextField txt;
    private JButton btn;
    private JLabel helpText;
    private JButton acceptBtn;

    private File selectedFile;

    public JFileChooserPDF() {
        super("Selección de archivo");
        setLayout(new FlowLayout());

        helpText = new JLabel("Seleccione un documento PDF:");
        add(helpText);

        txt = new JTextField(30);
        //why the user should edit this?
        txt.setEditable(false);
        add(txt);

        btn = new JButton("Buscar.");
        btn.addActionListener(this);
        add(btn);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        FileNameExtensionFilter pdfFilter = new FileNameExtensionFilter(FILES_DESC, FILETYPE);
        fileChooser.setFileFilter(pdfFilter);

        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            //return true;
        } else {
            selectedFile = null;
            //return false;
        }
    }

    public File getFile() {
        return selectedFile;
    }

    public boolean isValidFile() {
        return selectedFile != null;
    }

    public void tryToShow() {
        try {
            SwingUtilities.invokeAndWait(() -> showWindow());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public void showWindow(){
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setVisible(true);
    }
}
