package tests;

import compiler.Code;
import compiler.CodeResult;
import org.junit.Test;

import static org.junit.Assert.*;

public class CodeTest {

    @Test
    public void runTcl() {

        Code code = new Code();
        code.setInput("");
        code.setLanguage("tcl");
        code.setRunCompiledProgram(true);
        code.setTaskId(6);
        code.setCode("set lista [list raz dwa trzy]\n" +
                "set concated {}\n" +
                "foreach {i} $lista {\n" +
                "\tset concated \"$concated$i\"\n" +
                "}\n" +
                "puts $concated");

        CodeResult result = code.run();

        assertTrue(result.getCompilationSucceeded());
        assertEquals("", result.getError());
        assertEquals("razdwatrzy\n", result.getResponse());
        assertEquals(-1,  result.getLineOfError());
        assertTrue(result.isOutputOk());
    }

    @Test
    public void runCpp() {

        Code code = new Code();
        code.setInput("");
        code.setLanguage("cpp");
        code.setRunCompiledProgram(true);
        code.setTaskId(0);
        code.setCode("#include <iostream>\n" +
                "\n" +
                "int main()\n" +
                "{\n" +
                "    std::cout << \"Hello, World!\";\n" +
                "    return 0;\n" +
                "}");

        CodeResult result = code.run();
        assertTrue(result.getCompilationSucceeded());
        assertEquals("", result.getError());
        assertEquals(-1,  result.getLineOfError());
        assertTrue(result.isOutputOk());
        assertEquals("Hello, World!\n", result.getResponse());
    }

    @Test
    public void runCpp2() {

        Code code = new Code();
        code.setInput("4 7");
        code.setLanguage("cpp");
        code.setRunCompiledProgram(true);
        code.setTaskId(1);
        code.setCode("#include <iostream>\n" +
                "using namespace std;\n" +
                "\n" +
                "int main()\n" +
                "{\n" +
                "    int firstNumber, secondNumber, sumOfTwoNumbers;\n" +
                "\n" +
                "    cout << \"Enter two integers: \";\n" +
                "    cin >> firstNumber >> secondNumber;\n" +
                "\n" +
                "    // sum of two numbers in stored in variable sumOfTwoNumbers\n" +
                "    sumOfTwoNumbers = firstNumber + secondNumber;\n" +
                "\n" +
                "    // Prints sum\n" +
                "    cout << firstNumber << \" + \" <<  secondNumber << \" = \" << sumOfTwoNumbers;\n" +
                "\n" +
                "    return 0;\n" +
                "}");

        CodeResult result = code.run();
        assertTrue(result.getCompilationSucceeded());
        assertEquals("", result.getError());
        assertEquals(-1,  result.getLineOfError());
        assertTrue(result.isOutputOk());
        assertEquals("Enter two integers: 4 + 7 = 11\n", result.getResponse());
    }


    @Test
    public void runCppFaulty() {

        Code code = new Code();
        code.setInput("");
        code.setLanguage("cpp");
        code.setRunCompiledProgram(true);
        code.setTaskId(2);
        code.setCode("#include <iostream>\n" +
                "using namespace std;\n" +
                "\n" +
                "int main()\n" +
                "{\n" +
                "    int firstNumber...\n" +
                "    return 0;\n" +
                "}");

        CodeResult result = code.run();
        assertFalse(result.getCompilationSucceeded());
        assertNotEquals("", result.getError());
        assertEquals(6,  result.getLineOfError());
        assertEquals("", result.getResponse());
    }

}