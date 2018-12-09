package tests;

import files.FileManager;
import org.junit.Test;

import java.util.HashSet;

import static org.junit.Assert.*;

public class FileManagerTest {

    FileManager fileManager = new FileManager("D:\\Studia\\Sem. 7\\Studio projektowe\\Online-compiler\\src\\main\\resources\\tasks");

    @Test
    public void getLanguages() {
        HashSet<String> languages = new HashSet<>();
        languages.add("cpp");
        languages.add("tcl");
        assertEquals(languages, fileManager.getLanguages());
    }

    @Test
    public void getTasksForLanguage() {
        assertEquals(0, fileManager.getTasksForLanguage("cpp").iterator().next().getId());
        assertEquals("Hello world CPP", fileManager.getTasksForLanguage("cpp").iterator().next().getName());
        assertEquals(2, fileManager.getTasksForLanguage("cpp").size());
        assertEquals(1, fileManager.getTasksForLanguage("tcl").size());
        assertEquals(6, fileManager.getTasksForLanguage("tcl").iterator().next().getId());
    }

    @Test
    public void getTaskById() {
        assertEquals(0, fileManager.getTaskById(0).getId());
        assertEquals(1, fileManager.getTaskById(1).getId());
        assertEquals(6, fileManager.getTaskById(6).getId());
        assertNull(fileManager.getTaskById(7));
        assertNull(fileManager.getTaskById(-10));
        assertNull(fileManager.getTaskById(100));
    }
}