package compiler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;

public class Code {
    private String code;
    private String language;
    private boolean runCompiledProgram;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public boolean isRunCompiledProgram() {
        return runCompiledProgram;
    }

    public void setRunCompiledProgram(boolean runCompiledProgram) {
        this.runCompiledProgram = runCompiledProgram;
    }

    public String run(){

        //save code
        try (PrintWriter out = new PrintWriter(Constants.PATH_TO_SAVE + "\\test.cpp")) {
            out.println(getCode());
            out.close();
        } catch (Exception e){
            return "Unable to save file: " + e.toString();
        }

        try {
            Runtime rt = Runtime.getRuntime();

            Process pr = rt.exec("D:\\Programy\\CodeBlocks\\MinGW\\bin\\g++ -o "+Constants.PATH_TO_SAVE+"\\program.exe "+Constants.PATH_TO_SAVE+"\\test.cpp");

            BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));

            String response = "";
            String line=null;

            while((line=input.readLine()) != null) {
                response += line + "\n";
                System.out.println(line);
            }

            int exitVal = pr.waitFor();
            System.out.println("Program compilation ended with exit code: "+exitVal);

            return response;
        } catch(Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
            return e.toString();
        }


    }
}
