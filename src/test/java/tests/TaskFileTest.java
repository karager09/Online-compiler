package tests;

import files.TaskFile;
import org.junit.Test;

import java.io.File;
import java.util.LinkedList;

import static org.junit.Assert.*;

public class TaskFileTest {


    TaskFile firstFile = new  TaskFile(new File("D:\\Studia\\Sem. 7\\Studio projektowe\\Online-compiler\\src\\main\\resources\\tasks\\0"));
    TaskFile secondFile = new  TaskFile(new File("D:\\Studia\\Sem. 7\\Studio projektowe\\Online-compiler\\src\\main\\resources\\tasks\\1"));
    TaskFile thirdFile = new  TaskFile(new File("D:\\Studia\\Sem. 7\\Studio projektowe\\Online-compiler\\src\\main\\resources\\tasks\\6"));

    @Test
    public void getId() {
        assertEquals(0, firstFile.getId());
        assertEquals(1, secondFile.getId());
        assertEquals(6, thirdFile.getId());
    }

    @Test
    public void getName() {
        assertEquals("Hello world CPP", firstFile.getName());
        assertEquals("Program adding two numbers", secondFile.getName());
        assertEquals("Nazwa tcl", thirdFile.getName());
    }

    @Test
    public void getLanguage() {
        assertEquals("cpp", firstFile.getLanguage());
        assertEquals("cpp", secondFile.getLanguage());
        assertEquals("tcl", thirdFile.getLanguage());
    }

    @Test
    public void getContents() {
        assertEquals("Simple program to demonstrate how CPP looks.", firstFile.getContents());
        assertEquals("Look and Learn", secondFile.getContents());
        assertEquals("Simple program in tcl", thirdFile.getContents());
    }

    @Test
    public void getCode() {
        assertEquals("#include <iostream>\r\n" +
                "\r\n" +
                "int main()\r\n" +
                "{\r\n" +
                "    std::cout << \"Hello, World!\";\r\n" +
                "    return 0;\r\n" +
                "}", firstFile.getCode());


        assertEquals("#include <iostream>\r\n" +
                "using namespace std;\r\n" +
                "\r\n" +
                "int main()\r\n" +
                "{\r\n" +
                "    int firstNumber, secondNumber, sumOfTwoNumbers;\r\n" +
                "\r\n" +
                "    cout << \"Enter two integers: \";\r\n" +
                "    cin >> firstNumber >> secondNumber;\r\n" +
                "\r\n" +
                "    // sum of two numbers in stored in variable sumOfTwoNumbers\r\n" +
                "    sumOfTwoNumbers = firstNumber + secondNumber;\r\n" +
                "\r\n" +
                "    // Prints sum\r\n" +
                "    cout << firstNumber << \" + \" <<  secondNumber << \" = \" << sumOfTwoNumbers;\r\n" +
                "\r\n" +
                "    return 0;\r\n" +
                "}", secondFile.getCode());


        assertEquals("set lista [list raz dwa trzy]\r\n" +
                "set concated {}\r\n" +
                "foreach {i} $lista {\r\n" +
                "\tset concated \"$concated$i\"\r\n" +
                "}\r\n" +
                "puts $concated", thirdFile.getCode());
    }

    @Test
    public void getHints() {
        assertEquals(new LinkedList<>(), firstFile.getHints());
        assertEquals(new LinkedList<>(), secondFile.getHints());
        LinkedList<String> hints = new LinkedList<>();
        hints.add("\"puts\" is used to print some line.");
        hints.add("\"set\" is a function used to execute attribution");
        hints.add("brackets \"[]\" is used to execute command within");
        assertEquals(hints, thirdFile.getHints());
    }

    @Test
    public void getLines() {
        assertEquals(new LinkedList<>(), firstFile.getLines());
        assertEquals(new LinkedList<>(), secondFile.getLines());

        LinkedList<Integer> lines = new LinkedList<>();
        lines.add(0);
        lines.add(1);
        assertEquals(lines, thirdFile.getLines());
    }

    @Test
    public void getOutput() {
        assertEquals("", firstFile.getOutput());
        assertEquals("", secondFile.getOutput());
        assertEquals("razdwatrzy", thirdFile.getOutput());
    }
}