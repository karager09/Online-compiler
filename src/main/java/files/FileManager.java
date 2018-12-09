package files;

import compiler.Constants;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class FileManager {
    private static FileManager fileManager;

    public static FileManager get() {
        if (fileManager == null) {
            fileManager = new FileManager(Constants.PATH_WITH_TASKS);
        }
        return fileManager;
    }

    private HashMap<String, HashMap<Integer, TaskFile>> taskFiles = new HashMap<>();

    public FileManager(String pathWithTasks) {
        for (File file : new File(pathWithTasks).listFiles()) {

            TaskFile task = new TaskFile(file);
            if(!taskFiles.containsKey(task.getLanguage()))
                taskFiles.put(task.getLanguage(), new HashMap<>());
            taskFiles.get(task.getLanguage()).put(task.getId(), task);
        }

    }

//    public TaskFile getTaskByLanguageAndId(String language, int id) {
//        return taskFiles.getOrDefault(language,new HashMap<>()).getOrDefault(id, null);
//    }

    public Set<String> getLanguages(){
        return taskFiles.keySet();
    }

    public Collection<TaskFile> getTasksForLanguage(String language){
        return taskFiles.get(language).values();
    }

    public TaskFile getTaskById(int id) {
        for(HashMap<Integer, TaskFile> forLanguage: taskFiles.values()) {
            if(forLanguage.containsKey(id))
                return forLanguage.get(id);
        }
        return null;
    }
}
