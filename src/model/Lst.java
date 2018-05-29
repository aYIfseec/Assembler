package model;


public class Lst extends Obj{

    private String line_num;
    private String opCodeHex;
    private String dataCodeHex;
    
    private String lab;
    
    private String op;
    private String[] args;
    
    private String untranslatedOpMachineCode;
    private String untranslatedDataMachineCode;

    
    public String getUntranslatedDataMachineCode() {
        return untranslatedDataMachineCode;
    }

    public void setUntranslatedDataMachineCode(String untranslatedDataMachineCode) {
        this.untranslatedDataMachineCode = untranslatedDataMachineCode;
    }
    
    public String getUntranslatedOpMachineCode() {
        return untranslatedOpMachineCode;
    }

    public void setUntranslatedOpMachineCode(String untranslatedMachineCode) {
        this.untranslatedOpMachineCode = untranslatedMachineCode;
    }

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
	String zero = "";
	if (line_num.length() % 4 != 0) {
	    int temp = 4 - line_num.length()%4;
	    while (temp-- > 0) {
		zero += "0";
	    }
	}
        this.line_num = zero + line_num;
    }

    public String getOpCodeHex() {
        return opCodeHex;
    }

    public void setOpCodeHex(String opCodeHex) {
        this.opCodeHex = binaryStrToHexStr(opCodeHex);
    }

    public String getDataCodeHex() {
        return dataCodeHex;
    }

    public void setDataCodeHex(String dataCodeHex) {
        this.dataCodeHex = binaryStrToHexStr(dataCodeHex);
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
    
    
    
    public static String binaryStrToHexStr(String binaryStr) {  
	binaryStr = binaryStr.trim();
        if (binaryStr == null || binaryStr.equals("") || binaryStr.length() % 4 != 0) {  
            return "  ";
        }  
  
        StringBuffer sbs = new StringBuffer();  
        // 二进制字符串是4的倍数，所以四位二进制转换成一位十六进制  
        for (int i = 0; i < binaryStr.length() / 4; i++) {  
            String subStr = binaryStr.substring(i * 4, i * 4 + 4);  
            String hexStr = Integer.toHexString(Integer.parseInt(subStr, 2));  
            sbs.append(hexStr);  
        }
        return sbs.toString().toUpperCase();
    }
}
