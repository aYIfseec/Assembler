package utils;

public enum OpEnum {
    MOV("mov"), ADD("add"), SUB("sub"),
    LOAD("load"), STO("sto"), JMP("jmp"),
    BRNEG("brneg"), BRPOS("brpos"), END("end");
    
    private String op;
    
    private OpEnum(String op) {
	this.op = op;
    }
    
    private String getOp() {
	return op;
    }
    
    public static OpEnum val(String op) {
	for (OpEnum s: values()) {
	    if (op.equals(s.getOp())) return s;
	}
	return null;
    }
}
