import Interfejsy.Description;
import Interfejsy.ICallable;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Okno extends JFrame {
    JFileChooser jFileChooser;
    JButton exec;
    JPanel panel;
    JButton wybierz;
    int wyborJFIle;
    JList lista;
    DefaultListModel<String> model;
    JScrollPane pane;
    JTextArea tekstOpis;
    JTextField parametry;
    JTextArea wynik;
    Class<?> c;
    URLClassLoader cl;
    ArrayList<URL[]> listaURl = new ArrayList<URL[]>();
    URL[] urls;

    public Okno() throws HeadlessException {

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(new Dimension(700, 700));
        utworzElementy();
        dodajElementy();
        lisenery();
        setVisible(true);
    }

    private void lisenery() {
        wybierz.addActionListener(e -> {
            wyborJFIle = jFileChooser.showOpenDialog(null);
            if (wyborJFIle == JFileChooser.APPROVE_OPTION) {
                utworzLise(jFileChooser.getSelectedFile().listFiles());

                System.out.println(jFileChooser.getSelectedFile());
            }
        });
        exec.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    cl = URLClassLoader.newInstance(listaURl.get(lista.getSelectedIndex()));
                    c = cl.loadClass(lista.getSelectedValue().toString());
                } catch (ClassNotFoundException e1) {
                    e1.printStackTrace();
                }
                ICallable callable = null;
                try {
                    callable = (ICallable) c.newInstance();
                } catch (InstantiationException e1) {
                    e1.printStackTrace();
                } catch (IllegalAccessException e1) {
                    e1.printStackTrace();
                }
                if (null == callable)
                    try {
                        throw new Exception();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                String input = parametry.getText();
                String pattern = ",";            // Split on hyphens
                String[] substrings = input.split(pattern);
                wynik.setText(String.valueOf(callable.Call(Integer.parseInt(substrings[0]), Integer.parseInt(substrings[1]))));
            }
        });
        lista.addListSelectionListener((ListSelectionEvent a) -> {
                    try {
                        System.out.println(lista.getSelectedValue());
                        cl = URLClassLoader.newInstance(listaURl.get(lista.getSelectedIndex()));
                        c = cl.loadClass(lista.getSelectedValue().toString());
                        Description description = c.getAnnotation(Description.class);
                        tekstOpis.setText("Description: " + description.description());
                    } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    }

                }
        );
    }

    private void utworzLise(File[] f) {
        for (File fee : f) {

            String sciezkaFolderu = fee.getAbsolutePath();

            String jarPath = sciezkaFolderu;

            File jarIoFile = new File(jarPath);
            if (!jarIoFile.exists())
                return;

            JarFile jarFile = null;
            try {
                jarFile = new JarFile(jarPath);
                Enumeration<JarEntry> entries = jarFile.entries();

                urls = new URL[]{new URL("jar:file:" + jarPath + "!/")};

                cl = URLClassLoader.newInstance(urls);

                while (entries.hasMoreElements()) {
                    JarEntry je = entries.nextElement();
                    if (je.isDirectory() || !je.getName().endsWith(".class")) {
                        continue;
                    }

                    String className = je.getName().substring(0, je.getName().length() - 6);
                    className = className.replace('/', '.');


                    try {
                        c = cl.loadClass(className);
                        if (!c.isAnnotationPresent(Description.class))
                            continue;


                        if (!ICallable.class.isAssignableFrom(c))
                            throw new Exception("Class " + className + " does not implement the contract.");

                        ICallable callable = (ICallable) c.newInstance();
                        if (null == callable)
                            throw new Exception();
                        else {
                            listaURl.add(urls);
                            model.addElement(c.getName().toString());
                        }
//                    System.out.println(callable.Call(2,30));
                    } catch (ClassNotFoundException exp) {
                        continue;
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            } catch (IOException exp) {
            } finally {
                if (null != jarFile)

                {
                    try {
                        jarFile.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        lista.setModel(model);

    }


    private void utworzElementy() {
        panel = new JPanel();
        lista = new JList();
        model = new DefaultListModel();
        pane = new JScrollPane(lista);
        wybierz = new JButton("Wybierz");
        tekstOpis = new JTextArea();
        tekstOpis.setColumns(30);
        wynik = new JTextArea();
        parametry = new JTextField();
        exec = new JButton("Exec");
        jFileChooser = new JFileChooser();

    }

    private void wypelnijListe() {

        //tutaaaj

        lista.setModel(model);
    }

    private void dodajElementy() {
        add(panel);
        panel.setLayout(null);
        wybierz.setBounds(10, 20, 110, 30);

        panel.add(wybierz);
        jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        jFileChooser.setCurrentDirectory(new File("."));

        pane.setBounds(10, 65, 200, 200);
        panel.add(pane);
        tekstOpis.setBounds(250, 100, 300, 100);
        panel.add(tekstOpis);

        parametry.setBounds(20, 400, 100, 30);
        panel.add(parametry);

        parametry.setColumns(15);

        exec.setBounds(10, 450, 70, 30);
        panel.add(exec);
        wynik.setBounds(100, 450, 120, 30);
        panel.add(wynik);

        wynik.setColumns(15);

    }

}
