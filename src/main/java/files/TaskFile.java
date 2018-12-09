package files;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;

/**
 * Fields in files are separated by "#####"
 * Order:
 * -id
 * -name
 * -language
 * -contents
 * -code
 * -output
 * -lines (in one field separated by new line)
 * -hints (can be few fields)
 */
public class TaskFile {
    private int id;
    private String name;
    private String language;
    private String contents;
    private String code;
    private String output;
    private LinkedList<Integer> lines = new LinkedList<>();
    private LinkedList<String> hints = new LinkedList<>();

    public TaskFile(File file) {
        if (file.canRead()) {

            try {
                String content = new String(Files.readAllBytes(Paths.get(file.getPath())));
                String[] splittedContent = content.split("#####");
                id = Integer.parseInt(splittedContent[0].trim());
                name = splittedContent[1].trim();
                language = splittedContent[2].trim();
                contents = splittedContent[3].trim();
                code = splittedContent[4].trim();
                output = splittedContent[5].trim();
                if (splittedContent.length > 6)
                    for (String noEditableLine : splittedContent[6].trim().split("\n")) {
                        if (!noEditableLine.equals(""))
                            lines.add(Integer.parseInt(noEditableLine.trim()));
                    }

                for (int i = 7; i < splittedContent.length; i++) {
                    hints.add(splittedContent[i].trim());
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

    public String getOutput() {
        return output;
    }
}
