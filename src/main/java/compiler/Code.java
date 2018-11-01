package compiler;

import java.io.*;
import java.util.Arrays;
import java.util.concurrent.*;

public class Code {
    private String code;
    private String input = "";
    private String language;
    private boolean runCompiledProgram;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input.replace("\n", "\0");
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

        //compile program
        try {
            Runtime rt = Runtime.getRuntime();

            Process pr = rt.exec("\"" + Constants.PATH_TO_CPP_COMPILER +"\" -o \""+Constants.PATH_TO_SAVE+"\\program.exe\" \""+Constants.PATH_TO_SAVE+"\\test.cpp\"");

            BufferedReader errorStream = new BufferedReader(new InputStreamReader(pr.getErrorStream()));

            String line=null;

            while((line=errorStream.readLine()) != null) {
                response.append(line).append("\n");
                System.out.println(line);
            }

            int exitVal = pr.waitFor();
            if (exitVal == 0) {
                response.append("Compilation succeeded!\n");
            } else {
                response.append("Compilation failed!\n");
                return response.toString();
            }
            System.out.println("Program compilation ended with exit code: "+exitVal);
        } catch(Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
            response.append("Something went wrong with compilation phase..");
            return response.toString();
        }

        //run compiled code
        try {
            response.append("Runtime:\n");
            final Process p1 = Runtime.getRuntime().exec("cmd /c \""+Constants.PATH_TO_SAVE+"\\program.exe\"");

            final BufferedReader input = new BufferedReader(new InputStreamReader(p1.getInputStream()));

            Callable<String> readInput = new Callable<String>() {
                public String call() {

                    String line = null;

                    try {
                        while ((line = input.readLine()) != null) {
                            response.append(line +"\n");
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            input.close();
                        } catch (Exception e) {
                            System.out.println("Closing process: "+e.toString());
                        }
                    }
                    return "cos";
                }
            };


            final BufferedReader error = new BufferedReader(new InputStreamReader(p1.getErrorStream()));
            error.close();
//            Callable<String> readError = new Callable<String>() {
//                public String call() {
//
//                    String line = null;
//
//                    try {
////                        error.close();
//                        while ((line = input.readLine()) != null) {
//                            System.out.println(line);
//                            response.append("error: " +line +"\n");
//                        }
//
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    return "cos";
//                }
//            };
//            p1.getOutputStream().close();

            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.invokeAll(Arrays.asList(readInput), Constants.MAX_DURATION_OF_RUNNING_CODE, TimeUnit.MILLISECONDS);
            executor.shutdown();

            var writer = new OutputStreamWriter(p1.getOutputStream());
            writer.write(getInput());
            writer.close();

            if(!p1.waitFor(Constants.MAX_DURATION_OF_RUNNING_CODE,TimeUnit.MILLISECONDS)){
                final Process taskkillProcess = Runtime.getRuntime().exec("taskkill /pid "+ p1.pid()+" /t /F");

                System.out.println(new BufferedReader(new InputStreamReader(taskkillProcess.getErrorStream())).readLine());
                System.out.println("Kill val: " + taskkillProcess.waitFor());

                response.append("Process hasn't finished yet: killed him forcibly");
            } else {
                response.append("Running complete!");
            }
            System.out.println("Process ended.");

        } catch (Exception e) {
            response.append("Running error: " + e.toString());
        }
        return response.toString();
    }
}
