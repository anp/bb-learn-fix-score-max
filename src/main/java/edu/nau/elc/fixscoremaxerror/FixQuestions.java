package edu.nau.elc.fixscoremaxerror;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.util.ArrayList;

public class FixQuestions {

    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException |
                IllegalAccessException | UnsupportedLookAndFeelException e) {
            //don't worry if these are thrown, crossplatform look and feel will be used
        }

        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "ZIP Archives", "zip");
        chooser.setFileFilter(filter);

        int returnVal = chooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {

            File in = chooser.getSelectedFile();
            String filename = in.getAbsolutePath();
            String extension = filename.substring(filename.lastIndexOf('.'), filename.length());
            if (!extension.equals(".zip")) {
                System.out.println("Not a zip file, exiting.");
            }

            String path = in.getAbsolutePath().replace(in.getName(), "");
            String outputFolderName = path + "temp_" + in.getName().replace(".zip", "");
            File outFolder = new File(outputFolderName);
            if (!outFolder.exists()) {
                outFolder.mkdir();
            }

            extractAllFiles(in.getAbsolutePath(), outFolder.getAbsolutePath());

            File[] outFolderFiles = outFolder.listFiles();
            ArrayList<File> dats = getDotDats(outFolderFiles);
            for (File f : dats) {
                replaceScoreMax(f);
            }

            File outZip = new File(path + "Fixed__" + in.getName());
            folderToZip(outZip, outFolder);

            deleteFolder(outFolder);
        } else {
            System.exit(1);
        }
    }

    private static void replace(String[] oldstrings, String newstring, File in, File out) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(in));
        PrintWriter writer = new PrintWriter(new FileWriter(out));
        String line;
        while ((line = reader.readLine()) != null) {
            for (String s : oldstrings) {
                line = line.replaceAll(s, newstring);
            }
            writer.println(line);
        }
        reader.close();
        writer.close();
    }

    private static void replaceScoreMax(File in) {
        try {
            String newString = "100";
            if (!in.exists()) {
                System.out.println("The input file " + in + " does not exist.");
                System.exit(1);
            }
            File out = new File(in.getAbsolutePath() + "_temp");
            if (out.exists()) {
                System.out.println("The output file " + out + " already exists.");
                System.exit(1);
            }

            String[] toReplace = {"Score.max", "SCORE.max"};

            replace(toReplace, newString, in, out);

            in.delete();
            out.renameTo(in);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    private static void folderToZip(File output, File folder) {
        try {
            ZipFile zipFile = new ZipFile(output.getAbsolutePath());
            ZipParameters parameters = new ZipParameters();
            parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
            parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
            parameters.setIncludeRootFolder(false);

            zipFile.addFolder(folder, parameters);

        } catch (ZipException e) {
            e.printStackTrace();
        }
    }

    private static void extractAllFiles(String file, String outputDir) {
        try {
            ZipFile zipFile = new ZipFile(file);
            zipFile.extractAll(outputDir);

        } catch (ZipException e) {
            e.printStackTrace();
            System.out.println("Error extracting zip file.");
        }
    }

    private static ArrayList<File> getDotDats(File[] files) {
        ArrayList<File> dats = new ArrayList<>();
        for (File f : files) {
            String fn = f.getName();
            if (f.isDirectory()) {
                dats.addAll(getDotDats(f.listFiles()));
            } else if (isDatFilename(fn)) {
                dats.add(f);
            }
        }
        return dats;
    }

    private static boolean isDatFilename(String fn) {
        int extStart = fn.lastIndexOf('.');
        if (extStart < 1) {
            return false;
        }
        String extension = fn.substring(extStart, fn.length());
        return extension.equals(".dat");
    }

    private static void deleteFolder(File folder) {

        File[] files = folder.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteFolder(f);
                } else {
                    f.delete();
                }
            }
            folder.delete();
        }
    }
}
