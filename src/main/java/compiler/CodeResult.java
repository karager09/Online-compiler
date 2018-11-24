package compiler;

public class CodeResult {
    private boolean compilationSucceeded;
    private int lineOfError;
    private String response;
    private String error;
    private boolean isOutputOk = true;

    CodeResult(){
    }

    CodeResult(boolean compilationSucceeded, int lineOfError, String response, String error) {
        this.compilationSucceeded = compilationSucceeded;
        this.lineOfError = lineOfError;
        this.response = response;
        this.error = error;
    }

    public boolean getCompilationSucceeded() {
        return compilationSucceeded;
    }

    public void setCompilationSucceeded(boolean compilationSucceeded) {
        this.compilationSucceeded = compilationSucceeded;
    }

    public int getLineOfError() {
        return lineOfError;
    }

    public void setLineOfError(int lineOfError) {
        this.lineOfError = lineOfError;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public boolean isOutputOk() {
        return isOutputOk;
    }

    public void setOutputOk(boolean outputOk) {
        isOutputOk = outputOk;
    }
}
