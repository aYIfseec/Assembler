package main;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import utils.OpEnum;
import utils.OpcodeUtil;

import model.*;


public class Assembler {
    
    private static String filePath;
    
    private List<Identifier> identifierTable;
    private List<Lst> lst;
    
    private int byteCount;
    
    public Assembler() {
	init();
    }
    
    private void init() {
	identifierTable = new ArrayList<Identifier>();
	lst = new ArrayList<Lst>();
	byteCount = 0;
    }


    public static void main(String[] args) {
	
	filePath = System.getProperty("user.dir") + "/";
	
	if (args.length < 2) {
	    System.out.println("Usage:java Assembler input_filename output_filename");
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
	    
	    if (!res.equals("success")) {
		System.out.println("Error on " + source.getLine_num() + ": " + res);
		return false;
	    }
	}
	return true;
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
	Lst lst = new Lst();
	if (opEnum == null) {
	    // 循环符号表，查lab
	    
	    message = "not support commands" + arr[0]; // 符号表中无arr[0] op
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
                    Identifier identifier = new Identifier();
                    identifier.setIdentifier(arr[2]);
                    identifierTable.add(identifier); // 加入符号表等待数据解析
                    lst.setDataMachineCode(arr[2]); // 与""区别
                    byteCount ++; // 地址+1字节
                }
                
                if ("R1".equals(arr[1])) {
                    opCode += OpcodeUtil.R1;
                } else if ("R2".equals(arr[1])) {
                    opCode += OpcodeUtil.R2;
                } else {
                    message = "mov first parameter must be R1 or R2";
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
                    Identifier identifier = new Identifier();
                    identifier.setIdentifier(arr[2]);
                    identifierTable.add(identifier); // 加入符号表等待数据解析
                    lst.setDataMachineCode(arr[2]); // 与""区别
                    byteCount ++; // 地址+1字节
                }
                
                if ("R1".equals(arr[1])) {
                    opCode += OpcodeUtil.R1;
                } else if ("R2".equals(arr[1])) {
                    opCode += OpcodeUtil.R2;
                } else {
                    message = "add first parameter must be R1 or R2";
                    break;
                }
                
                break;
            case SUB:
                break;
            case LOAD:
                break;
            case STO:
                break;
            case JMP:
                break;
            case BRNEG:
                break;
            case BRPOS:
                break;
            case END:
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
	System.out.println(lst.toString());
	
	return message;
    }

    /**
     * 解析数据块
     * @param arr
     * @return
     */
    private String analysisData(String[] arr) {
	System.out.println("Analysis Data:" + Arrays.toString(arr));
	
	return "success";
    }
    
    
}
