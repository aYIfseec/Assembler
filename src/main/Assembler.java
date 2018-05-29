package main;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import utils.OpEnum;
import utils.OpcodeUtil;

import model.*;


public class Assembler {
    
    private static String filePath;
    
    private Map<String, Identifier> identifierTable;
    private List<Lst> lstTable;
    
    private int byteCount;
    
    public Assembler() {
	init();
    }
    
    private void init() {
	identifierTable = new HashMap<String, Identifier>();
	lstTable = new ArrayList<Lst>();
	byteCount = 0;
    }


    public static void main(String[] args) {
	
	filePath = System.getProperty("user.dir") + "/";
	
	if (args.length < 2) {
	    System.out.println("Usage:java Assembler inputFileName.xxx outputFileName");
	}
	
	FileInputStream fis = null;
	List<SourceLine> sourceLines = null; // 存放文件中获取的汇编代码
	try {
	    fis = new FileInputStream(filePath + args[0]);
	    sourceLines = new ArrayList<SourceLine>();
	    readSourceFile(fis, sourceLines);
	} catch (FileNotFoundException e) {
	    System.out.println("File " + args[0] + " Not Found");
	    // e.printStackTrace();
	    return;
	} finally {
	    try {
		if (fis != null) fis.close();
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}
	String outputFileName = filePath + args[1];
	
	Assembler assembler = new Assembler();
	boolean isSuccess = assembler.translate(sourceLines, outputFileName);
	
	if (isSuccess) {
	    System.out.println("success");
	} else {
	    System.out.println("fail");
	}
	
    }
    
    /**
     * 将文件中的有效汇编代码读入
     * @param fis
     * @param sourceLines
     */
    private static void readSourceFile(FileInputStream fis, List<SourceLine> sourceLines) {
	Scanner scanner = new Scanner(fis);
	int count = 0;
	while (scanner.hasNextLine()) {
	    count++;
	    String line = scanner.nextLine();
	    
	    int index = line.indexOf(';'); // 判断是否存在注释
	    // 去掉注释部分
	    if (index > 0) line = line.substring(0, index);
	    else if (index == 0) continue; // 整行都是注释
	    
	    line = line.trim(); // 去除行首、行尾多余空白符
	    if (line.equals(""))  continue; // 空行都跳过
	    // System.out.println(line);
	    sourceLines.add(new SourceLine(count, line)); // 将有效汇编代码行add
	}
    }

    /**
     * 解析汇编代码
     * @param sourceLines
     * @param outputFileName
     * @return
     */
    private boolean translate(List<SourceLine> sourceLines, String outputFileName) {
	String curr = "";
	String res = null;
	int count = -1;
	for (SourceLine source: sourceLines) {
	    String line = source.getSource();
	    // split传入正则表达式，匹配多个空格，tab，逗号，换行符
	    // 得到助记符及参数组成的array
	    String[] arr = line.split("\\s+|	|,|\n");
	    
	    if (arr[0].equals(".code")) {
		curr = "code";
		
		continue;
	    } else if (arr[0].equals(".data")) {
		curr = "data";
		continue;
	    }
	    
	    if (curr.equals("code")) {
		count ++;
		res = analysisCode(arr, count);
	    } else if (curr.equals("data")){
		res = analysisData(arr);
	    }
	    byteCount ++; // 助记码 或 analysisData中的数据占一字节空间
	    
	    if (!res.equals("success")) {
		System.out.println("Error on " + source.getLine_num() + " lines: " + res);
		return false;
	    }
	}
	
	
	// 测试输出第一遍的lst
	for (Lst lst: lstTable) {  
	    System.out.println(lst.toString());  
	}
	// 测试输出符号表
	for (Map.Entry<String, Identifier> entry : identifierTable.entrySet()) {  
	    System.out.println(entry.getValue().toString());  
	}
	
	if (assemblerCompile()) {
	    // 测试输出第二遍的lst
	    for (Lst lst: lstTable) {
		lst.setOpCodeHex(lst.getOpMachineCode());
		lst.setDataCodeHex(lst.getDataMachineCode());
		System.out.println(lst.toString());
	    }
	    
	    BufferedWriter bf = null;
	    // 生成.lst文件
	    try {
		bf = new BufferedWriter(new FileWriter(new File(outputFileName + ".lst")));
		for (Lst lst: lstTable)  bf.write(lst.toString()+ "\n");
	    } catch (IOException e) {
		e.printStackTrace();
		return false;
	    } finally {
		try {
		    if (bf != null) bf.close();
		} catch (IOException e) {
		    e.printStackTrace();
		}
	    }
	    
	    // 生成.obj文件
	    try {
		bf = new BufferedWriter(new FileWriter(new File(outputFileName + ".obj")));
		for (Lst lst: lstTable)  bf.write(lst.getOpMachineCode() 
			+ " " + lst.getDataMachineCode() + "\n");
	    } catch (IOException e) {
		e.printStackTrace();
		return false;
	    } finally {
		try {
		    if (bf != null) bf.close();
		} catch (IOException e) {
		    e.printStackTrace();
		}
	    }
	    return true;
	}
	return false;
    }

    /**
     * 解析代码块
     * @param arr
     * @param i 
     * @return
     */
    private String analysisCode(String[] arr, Integer i) {
	
	String message = "success";
	String opCode = "";
	OpEnum opEnum = OpEnum.val(arr[0]);
	Identifier identifier = new Identifier();
	Lst lst = new Lst();
	if (opEnum == null) {
	    if (identifierTable.containsKey(arr[0])) {
		Identifier temp = identifierTable.get(arr[0]);
		temp.setRelativeAddress(Integer.toBinaryString(byteCount));
		
		// 解析label后的代码
		String[] tempArr = new String[arr.length-1];
		for (int k = 1; k < arr.length; k++)  tempArr[k-1] = arr[k];
		message = analysisCode(tempArr, i);
		Lst tempLst = lstTable.get(lstTable.size()-1);
		tempLst.setLab(arr[0]); // 设置label名
		return message;
	    } else {
		System.out.println("Warning: Unnecessary Label '" + arr[0] + "'"); // 符号表中无arr[0] lab
		return message;
	    }
	} else {
            switch (opEnum) {
            case MOV:
                if (arr.length < 3) {
                    message = "missing parameter";
                    break;
                }
                
                if ("R1".equals(arr[2])) {
                    opCode = OpcodeUtil.MOV_FROM_REGISTER + OpcodeUtil.R1;
                } else if ("R2".equals(arr[2])) {
                    opCode = OpcodeUtil.MOV_FROM_REGISTER + OpcodeUtil.R2;
                } else { // 参数2为变量
                    opCode = OpcodeUtil.MOV_DATA;
                    identifier.setIdentifier(arr[2]);
                    identifierTable.put(arr[2], identifier); // 加入符号表等待数据解析
                    lst.setUntranslatedDataMachineCode(arr[2]);
                    byteCount ++; // 地址+1字节    数据占一字节
                }
                
                if ("R1".equals(arr[1])) {
                    opCode += OpcodeUtil.R1;
                } else if ("R2".equals(arr[1])) {
                    opCode += OpcodeUtil.R2;
                } else {
                    message = "mov first parameter must be register R1 or R2";
                    break;
                }
                
                break;
            case ADD:
        	if (arr.length < 3) {
                    message = "missing parameter";
                    break;
                }
                
                if ("R1".equals(arr[2])) {
                    opCode = OpcodeUtil.ADD_REGISTER_DATA + OpcodeUtil.R1;
                } else if ("R2".equals(arr[2])) {
                    opCode = OpcodeUtil.ADD_DATA + OpcodeUtil.R2;
                } else { // 参数2为变量
                    opCode = OpcodeUtil.ADD_DATA;
                    identifier.setIdentifier(arr[2]);
                    identifierTable.put(arr[2], identifier); // 加入符号表等待数据解析
                    lst.setUntranslatedDataMachineCode(arr[2]);
                    byteCount ++; // 地址+1字节
                }
                
                if ("R1".equals(arr[1])) {
                    opCode += OpcodeUtil.R1;
                } else if ("R2".equals(arr[1])) {
                    opCode += OpcodeUtil.R2;
                } else {
                    message = "add first parameter must be register R1 or R2";
                    break;
                }
                
                break;
            case SUB:
        	if (arr.length < 3) {
                    message = "missing parameter";
                    break;
                }
                
                if ("R1".equals(arr[2])) {
                    opCode = OpcodeUtil.SUB_REGISTER_DATA + OpcodeUtil.R1;
                } else if ("R2".equals(arr[2])) {
                    opCode = OpcodeUtil.SUB_REGISTER_DATA + OpcodeUtil.R2;
                } else { // 参数2为变量
                    opCode = OpcodeUtil.SUB_DATA;
                    identifier.setIdentifier(arr[2]);
                    identifierTable.put(arr[2], identifier); // 加入符号表等待数据解析
                    lst.setUntranslatedDataMachineCode(arr[2]);
                    byteCount ++; // 地址+1字节
                }
                
                if ("R1".equals(arr[1])) {
                    opCode += OpcodeUtil.R1;
                } else if ("R2".equals(arr[1])) {
                    opCode += OpcodeUtil.R2;
                } else {
                    message = "sub first parameter must be register R1 or R2";
                    break;
                }
                
                break;
            case LOAD:
        	if (arr.length < 3) {
                    message = "missing parameter";
                    break;
                }
        	
        	opCode = OpcodeUtil.LOAD_DATA;
                identifier.setIdentifier(arr[2]);
                identifierTable.put(arr[2], identifier); // 加入符号表等待数据解析
                lst.setUntranslatedDataMachineCode(arr[2]);
                byteCount ++; // 地址+1字节
        	
        	if ("R1".equals(arr[1])) {
                    opCode += OpcodeUtil.R1;
                } else if ("R2".equals(arr[1])) {
                    opCode += OpcodeUtil.R2;
                } else {
                    message = "sub first parameter must be register R1 or R2";
                    break;
                }
        	
                break;
            case STO:
        	if (arr.length < 2) {
                    message = "missing parameter";
                    break;
                }
        	opCode = OpcodeUtil.STO;
        	identifier.setIdentifier(arr[1]);
                identifierTable.put(arr[1], identifier);
        	lst.setUntranslatedOpMachineCode(arr[1]);
                break;
            case JMP:
        	if (arr.length < 2) {
                    message = "missing parameter";
                    break;
                }
        	opCode = OpcodeUtil.JMP;
        	identifier.setIdentifier(arr[1]);
                identifierTable.put(arr[1], identifier);
        	lst.setUntranslatedOpMachineCode(arr[1]);
                break;
            case BRNEG:
        	if (arr.length < 2) {
                    message = "missing parameter";
                    break;
                }
        	opCode = OpcodeUtil.BRNEG;
        	identifier.setIdentifier(arr[1]);
                identifierTable.put(arr[1], identifier);
        	lst.setUntranslatedOpMachineCode(arr[1]);
                break;
            case BRPOS:
        	if (arr.length < 2) {
                    message = "missing parameter";
                    break;
                }
        	opCode = OpcodeUtil.BRPOS;
        	identifier.setIdentifier(arr[1]);
                identifierTable.put(arr[1], identifier);
        	lst.setUntranslatedOpMachineCode(arr[1]);
                break;
            case END:
        	opCode = OpcodeUtil.END;
                break;
            }
	}
	if (message.equals("success")) {
	    lst.setOp(arr[0]);
	    lst.setLine_num(Integer.toBinaryString(i));
	    lst.setOpMachineCode(opCode);
	    String[] args = new String[arr.length-1];
	    for (int j = 1; j < arr.length; j++) {
		args[j-1] = arr[j];
	    }
	    lst.setArgs(args);
	}
	
	// System.out.println("Analysis Code:" + Arrays.toString(arr));
	// System.out.println(lst.toString());
	lstTable.add(lst);
	return message;
    }

    /**
     * 解析数据块
     * @param arr
     * @return
     */
    private String analysisData(String[] arr) {
	// System.out.println("Analysis Data:" + Arrays.toString(arr));
	if (! ".eq".equals(arr[1])) return "Syntax error";
	if (identifierTable.containsKey(arr[0])) {
	    Identifier identifier = identifierTable.get(arr[0]);
	    Integer temp = null;
	    try {
		temp = Integer.parseInt(arr[2]);
	    } catch (Exception ex) {}
	    // 数据是否为占位符 ?? 或 数字
	    if ("??".equals(arr[2]) || temp != null) {
		identifier.setValue(arr[2]);
	    } else return "Syntax error";
	    identifier.setRelativeAddress(Integer.toBinaryString(byteCount));
	    // System.out.println(identifierTable.get(arr[0]).toString());
	} else {
	    System.out.println("Warning: Unnecessary data '" + arr[0] + "'");
	}
	return "success";
    }
    
    /**
     * 用符号表将lstTable补充完整
     */
    private boolean assemblerCompile() {
	for (Lst lst: lstTable) {
	    String identifier = null;
	    if (lst.getUntranslatedDataMachineCode() != null) {
		identifier = lst.getUntranslatedDataMachineCode();
		if (identifierTable.containsKey(identifier)) {
		    Identifier temp = identifierTable.get(identifier);
		    lst.setDataMachineCode(temp.getValue());
		} else {
		    System.out.println("Error on " + lst.getLine_num() + " lines: Unknown variable");
		    return false;
		}
	    } else if (lst.getUntranslatedOpMachineCode() != null) {
		identifier = lst.getUntranslatedOpMachineCode();
		if (identifierTable.containsKey(identifier)) {
		    Identifier temp = identifierTable.get(identifier);
		    lst.setOpMachineCode(lst.getOpMachineCode() + temp.getRelativeAddress());
		} else {
		    System.out.println("Error on " + lst.getLine_num() + " lines: Unknown label");
		    return false;
		}
	    }
	}
	return true;
    }
}
