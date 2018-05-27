package utils;

public class OpcodeUtil {
    // Register
    public static final String R1 = "0001";
    public static final String R2 = "0010";
    
    // MOV
    public static final String MOV_FROM_REGISTER = "0000";
    public static final String MOV_DATA = "0001";
    
    // ADD
    public static final String ADD_REGISTER_DATA = "1000";
    public static final String ADD_DATA = "1001";
    
    // sub
    public static final String SUB_REGISTER_DATA = "1100";
    public static final String SUB_DATA = "1101";
    
    // load
    public static final String LOAD_DATA = "0010";
    
    // sto
    public static final String STO = "0011";
    
    // jmp
    public static final String JMP = "0100";
    
    // brneg 否定分支
    public static final String BRNEG = "0101";
    
    // brpos true分支
    public static final String BRPOS = "0111";
    
    // end
    public static final String END = "1111";
}
