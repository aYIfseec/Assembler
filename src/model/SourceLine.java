package model;

public class SourceLine {
    
    private int line_num;
    private String source;
    
    public SourceLine(int count, String line) {
	line_num = count;
	source = line;
    }
    public SourceLine(){}
    
    public int getLine_num() {
        return line_num;
    }
    public void setLine_num(int line_num) {
        this.line_num = line_num;
    }
    
    
    public String getSource() {
        return source;
    }
    public void setSource(String source) {
        this.source = source;
    }
}
