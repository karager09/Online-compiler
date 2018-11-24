package compiler;

import files.FileManager;
import org.apache.tomcat.util.bcel.Const;

import java.io.*;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Code {
    private String code;
    private String input = "";
    private String language;
    private boolean runCompiledProgram;
    private int taskId;

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

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public CodeResult run() {
        CodeResult codeResult = new CodeResult(true, -1, "", "");
        boolean needCompilation = true;
        String nameOfFile = "F" + new Random().nextInt();
        String nameOfFileWithExtension = null;
        String compileCommand = null;
        String execCommand = null;

        switch (language) {
            case "cpp":
                nameOfFileWithExtension = nameOfFile + ".cpp";
                needCompilation = true;
                compileCommand = "\"" + Constants.PATH_TO_CPP_COMPILER + "\" -o \"" + Constants.PATH_TO_SAVE + "\\" + nameOfFile + ".exe\" \"" + Constants.PATH_TO_SAVE + "\\" + nameOfFileWithExtension + "\"";
                execCommand = "cmd /c \"" + Constants.PATH_TO_SAVE + "\\" + nameOfFile + ".exe\"";
                break;
            case "tcl":
                nameOfFileWithExtension = nameOfFile + ".tcl";
                needCompilation = false;
                execCommand = "cmd /c tclsh \"" + Constants.PATH_TO_SAVE + "\\" + nameOfFileWithExtension + "\"";
                break;

        }

        //save code
        try (PrintWriter out = new PrintWriter(Constants.PATH_TO_SAVE + "\\" + nameOfFileWithExtension)) {
            out.println(getCode());
            out.close();
        } catch (Exception e) {
            return new CodeResult(false, -1, null, "Unable to save file: " + e.toString());
        }

        StringBuilder responseOfCompilation = new StringBuilder();

        //compile program
        if (needCompilation) {
            try {
                Runtime rt = Runtime.getRuntime();

                Process pr = rt.exec(compileCommand);

                BufferedReader errorStream = new BufferedReader(new InputStreamReader(pr.getErrorStream()));

                String line = null;

                while ((line = errorStream.readLine()) != null) {
                    responseOfCompilation.append(line).append("\n");
                    System.out.println(line);
                }

                int exitVal = pr.waitFor();
                if (exitVal == 0) {
//                    response.append("Compilation succeeded!\n");
                    codeResult.setCompilationSucceeded(true);
                } else {
                    codeResult.setCompilationSucceeded(false);
                    String couseOfFailure = responseOfCompilation.toString();

                    Pattern myPattern = Pattern.compile("\\d+:\\d+:");
                    Matcher matcher = myPattern.matcher(couseOfFailure);
                    if (matcher.find()) {
                        String lineWithNumbers = couseOfFailure.substring(matcher.start(), matcher.end());
                        codeResult.setLineOfError(Integer.parseInt(lineWithNumbers.substring(0, lineWithNumbers.indexOf(':'))));
                    }

                    codeResult.setError(couseOfFailure);
                    return codeResult;
//                    response.append("Compilation failed!\n");
//                    return response.toString();
                }
                System.out.println("Program compilation ended with exit code: " + exitVal);
            } catch (Exception e) {
                System.out.println(e.toString());
                e.printStackTrace();
//                response.append("Something went wrong with compilation phase..");
//                return response.toString();
                codeResult.setCompilationSucceeded(false);
                codeResult.setError(responseOfCompilation.toString());
                return codeResult;
            }
        }
        codeResult.setCompilationSucceeded(true);

        StringBuilder responseOfExecution = new StringBuilder();
        StringBuilder responseOfExecutionError = new StringBuilder();
        String errorResponse = "";
        //run compiled code
        try {
//            response.append("Runtime:\n");
            final Process p1 = Runtime.getRuntime().exec(execCommand);

            final BufferedReader input = new BufferedReader(new InputStreamReader(p1.getInputStream()));

            Callable<String> readInput = new Callable<String>() {
                public String call() {

                    String line = null;

                    try {
                        while ((line = input.readLine()) != null) {
                            responseOfExecution.append(line + "\n");
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            input.close();
                        } catch (Exception e) {
                            System.out.println("Closing process: " + e.toString());
                        }
                    }
                    return "cos";
                }
            };


//            Callable<String> readError = new Callable<String>() {
//                public String call() {
//
//                    String line = null;
//
//                    try {
////                        error.close();
//                        while ((line = error.readLine()) != null) {
////                            System.out.println(line);
//                            responseOfExecutionError.append(line +"\n");
//                        }
//
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    } finally {
//                        try {
//                            error.close();
//                        } catch (Exception e) {
//                            System.out.println("Closing error stream: " + e.toString());
//                        }
//                    }
//                    return "cos";
//                }
//            };
//            p1.getOutputStream().close();

            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.invokeAll(Arrays.asList(readInput), Constants.MAX_DURATION_OF_RUNNING_CODE, TimeUnit.MILLISECONDS);
            executor.shutdown();

            OutputStreamWriter writer = new OutputStreamWriter(p1.getOutputStream());
            writer.write(getInput());
            writer.close();

            String line;
            final BufferedReader error = new BufferedReader(new InputStreamReader(p1.getErrorStream()));
            while ((line = error.readLine()) != null) {
//                            System.out.println(line);
                responseOfExecutionError.append(line + "\n");
            }
            error.close();

            if (!p1.waitFor(Constants.MAX_DURATION_OF_RUNNING_CODE, TimeUnit.MILLISECONDS)) {
                final Process taskkillProcess = Runtime.getRuntime().exec("taskkill /pid " + p1.pid() + " /t /F");

                System.out.println(new BufferedReader(new InputStreamReader(taskkillProcess.getErrorStream())).readLine());
                System.out.println("Kill val: " + taskkillProcess.waitFor());

//                response.append("Process hasn't finished yet: killed him forcibly");
                errorResponse = "Process hasn't finished yet: killed him forcibly";
            } else {
//                response.append("Running complete!");
            }
            System.out.println("Process ended.");

            final Process removeFileProcess = Runtime.getRuntime().exec("cmd /c del /Q /F \"" + Constants.PATH_TO_SAVE + "\\" + nameOfFileWithExtension + "\"");
            if (needCompilation) {
                final Process removeExecFileProcess = Runtime.getRuntime().exec("cmd /c del /Q /F \"" + Constants.PATH_TO_SAVE + "\\" + nameOfFile + ".exe\"");
            }

            if (!errorResponse.equals("")) {
                codeResult.setError(errorResponse);
                return codeResult;
            }

        } catch (Exception e) {
            codeResult.setError(e.toString());
            return codeResult;
        }

        if (taskId >= 0) {
            String requiredOutput = FileManager.get().getTaskById(taskId).getOutput();
            if (!requiredOutput.equals("") && !requiredOutput.trim().equals(responseOfExecution.toString().trim()))
                codeResult.setOutputOk(false);
        }
        codeResult.setResponse(responseOfExecution.toString());
        codeResult.setError(responseOfExecutionError.toString());
        return codeResult;
    }
}
