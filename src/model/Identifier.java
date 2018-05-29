package model;

public class Identifier {
    
    private String identifier;
    private String relativeAddress;
    private String value;
    
    public String getIdentifier() {
        return identifier;
    }
    
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
    
    
    public String getRelativeAddress() {
        return relativeAddress;
    }
    public void setRelativeAddress(String relativeAddress) {
	String zero = "";
	if (relativeAddress.length() % 4 != 0) {
	    int temp = 4 - relativeAddress.length()%4;
	    while (temp-- > 0) {
		zero += "0";
	    }
	}
        this.relativeAddress = zero + relativeAddress;
    }
    
    
    public String getValue() {
	Integer temp = null;
	try{
	    temp = Integer.parseInt(value);
	} catch (Exception ex) {}
	if (temp != null) {
	    value = Integer.toBinaryString(temp);
	    String zero = "";
	    // 程序没有考虑数据溢出
	    if (value.length() < 8) {// 不足8位补零
		temp = 8 - value.length();
        	while (temp-- > 0) {
        	    zero += "0";
        	}
        	value = zero + value;
	    } else {
		// 符号位+7位数据位
		value = value.charAt(0) + value.substring(25, 32);
	    }
	    
	}
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
	return "[" + identifier + ",	"
		+ relativeAddress + ",	" + value + "]";
    }
    
}
