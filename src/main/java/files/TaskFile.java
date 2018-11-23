package files;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;

public class TaskFile {
    private int id;
    private String name;
    private String language;
    private String contents;
    private String code;
    private LinkedList<String> hints = new LinkedList<>();
    private LinkedList<Integer> lines = new LinkedList<>();

    TaskFile(File file) {
        if (file.canRead()) {

            try {
                String content = new String(Files.readAllBytes(Paths.get(file.getPath())));
                String[] splittedContent = content.split("#####");
                id = Integer.parseInt(splittedContent[0].trim());
                name = splittedContent[1].trim();
                language = splittedContent[2].trim();
                contents = splittedContent[3].trim();
                code = splittedContent[4].trim();
                for (int i = 5; i < splittedContent.length - 1; i++) {
                    hints.add(splittedContent[i].trim());
                }

                for (String noEditableLine : splittedContent[splittedContent.length - 1].trim().split("\n")) {
                    lines.add(Integer.parseInt(noEditableLine.trim()));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            System.out.println("Cannot read file " + file.getPath());
        }

    }


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLanguage() {
        return language;
    }

    public String getContents() {
        return contents;
    }

    public String getCode() {
        return code;
    }

    public LinkedList<String> getHints() {
        return hints;
    }

    public LinkedList<Integer> getLines() {
        return lines;
    }
}
