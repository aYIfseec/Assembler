package model;

public class Obj {

    private String opMachineCode;
    private String dataMachineCode;
    
    public Obj(){
	this("", "");
    }
    
    public Obj(String opMachineCode, String dataMachineCode) {
	this.opMachineCode = opMachineCode;
	this.dataMachineCode = dataMachineCode;
    }
    public String getOpMachineCode() {
        return opMachineCode;
    }
    public void setOpMachineCode(String opMachineCode) {
        this.opMachineCode = opMachineCode;
    }
    public String getDataMachineCode() {
	if (dataMachineCode.equals("")) return "        ";
        return dataMachineCode;
    }
    public void setDataMachineCode(String dataMachineCode) {
        this.dataMachineCode = dataMachineCode;
    }

}
