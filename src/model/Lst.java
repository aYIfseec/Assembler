package model;


public class Lst extends Obj{

    private String line_num;
    private String opCodeHex;
    private String dataCodeHex;
    
    private String lab;
    
    private String op;
    private String[] args;
    
    public Lst() {
	this("", "", "", "", "", null);
    }
    
    public Lst(String line_num, String opCodeHex, String dataCodeHex,
	    String lab, String op, String[] args) {
	super();
	this.line_num = line_num;
	this.opCodeHex = opCodeHex;
	this.dataCodeHex = dataCodeHex;
	this.lab = lab;
	this.op = op;
	this.args = args;
    }

    public String getLine_num() {
        return line_num;
    }

    public void setLine_num(String line_num) {
        this.line_num = line_num;
    }

    public String getOpCodeHex() {
        return opCodeHex;
    }

    public void setOpCodeHex(String opCodeHex) {
        this.opCodeHex = opCodeHex;
    }

    public String getDataCodeHex() {
        return dataCodeHex;
    }

    public void setDataCodeHex(String dataCodeHex) {
        this.dataCodeHex = dataCodeHex;
    }

    public String getLab() {
        return lab;
    }

    public void setLab(String lab) {
        this.lab = lab;
    }

    public String getOp() {
        return op;
    }
    public void setOp(String op) {
        this.op = op;
    }
    
    
    public String[] getArgs() {
        return args;
    }
    public void setArgs(String[] args) {
        this.args = args;
    }




    @Override
    public String toString() {
	return line_num + "	" + super.getOpMachineCode() + "	"
		+ super.getDataMachineCode() + "	"
		+ opCodeHex + "	" + dataCodeHex
		+ "	" + lab + "	"
		+ op + "	" + printArgs() + "";
    }
    
    private String printArgs() {
	String res = "";
	if (args == null) return res;
	for (String str: args) {
	    res += str + ", ";
	}
	if (res.length() != 0) res = res.substring(0, res.length()-2);
	return res;
    }
}
