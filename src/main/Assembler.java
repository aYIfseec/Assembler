package main;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Assembler {
    
    private static String filePath;
    
    public static void main(String[] args) {
	
	filePath = System.getProperty("user.dir") + "/";
	
	if (args.length < 2) {
	    System.out.println("Usage:java Assembler input_filename output_filename");
	}
	
	FileInputStream fis = null;
	List<String> sourceLines = null; // 存放文件中获取的汇编代码
	try {
	    fis = new FileInputStream(filePath + args[0]);
	    sourceLines = new ArrayList<String>();
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
	assembler.translate(sourceLines, outputFileName);
	
    }
    
    /**
     * 将文件中的有效汇编代码读入
     * @param fis
     * @param sourceLines
     */
    private static void readSourceFile(FileInputStream fis, List<String> sourceLines) {
	Scanner scanner = new Scanner(fis);
	while (scanner.hasNextLine()) {
	    String line = scanner.nextLine();
	    
	    int index = line.indexOf(';'); // 判断是否存在注释
	    // 去掉注释部分
	    if (index > 0) line = line.substring(0, index);
	    else if (index == 0) continue; // 整行都是注释
	    
	    line = line.trim(); // 去除行首、行尾多余空白符
	    if (line.equals(""))  continue; // 空行都跳过
	    System.out.println(line);
	    sourceLines.add(line); // 将有效汇编代码行add
	}
    }

    /**
     * 解析汇编代码
     * @param sourceList
     * @param outputFileName
     * @return
     */
    private boolean translate(List<String> sourceList, String outputFileName) {
	
	return false;
    }

}
