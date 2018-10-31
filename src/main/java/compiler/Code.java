package compiler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

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

        StringBuilder response = new StringBuilder();

        try {
            Runtime rt = Runtime.getRuntime();

            Process pr = rt.exec("\"" + Constants.PATH_TO_CPP_COMPILER +"\" -o \""+Constants.PATH_TO_SAVE+"\\program.exe\" \""+Constants.PATH_TO_SAVE+"\\test.cpp\"");

            BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));

            String line=null;

            while((line=input.readLine()) != null) {
                response.append(line).append("\n");
                System.out.println(line);
            }

            int exitVal = pr.waitFor();
            if (exitVal == 0) {
                response.append("Compilation succeeded!\n");
            } else {
                response.append("Compilation failed!\n");
            }
            System.out.println("Program compilation ended with exit code: "+exitVal);
        } catch(Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
            response.append("Something went wrong with compilation phase..");
            return response.toString();
        }

        try {
            response.append("\nRuntime: ");
            final Process p1 = Runtime.getRuntime().exec("cmd /c \""+Constants.PATH_TO_SAVE+"\\program.exe\"");
            //final Process p = Runtime.getRuntime().exec("cmd /c .\\program.exe");

//            new Thread(new Runnable() {
//                public void run() {
                    BufferedReader input = new BufferedReader(new InputStreamReader(p1.getInputStream()));

                    String line = null;
                    try {
                        while ((line = input.readLine()) != null) {
                            System.out.println(line);
                            response.append(line +"\n");
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
//                }
//            }).start();

            System.out.println("Running exit:" + p1.waitFor());
            response.append("Running complete!");
        } catch (Exception e) {
            response.append("Running error: " + e.toString());
        }
        return response.toString();
    }
}
