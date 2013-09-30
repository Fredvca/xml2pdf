package ContentStructAnalyst;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

import ContentStructAnalyst.PDFTemplateCooper.Cooper;

public class Worker implements Runnable {
	private BridgeStone  bs = null;
	private Cooper cp = null;
	public Worker(BridgeStone bs, Cooper cp) {
		this.bs = bs;
		this.cp = cp;
	}

	@Override
	public void run() {
		try {
			PrintWriter pw = new PrintWriter(cp.getRootPath().toString() + bs.className + ".java");
			//specify package
			pw.println("package " +cp.getPackageName() + ";");
			pw.println();
			pw.println("public class " + bs.className  + "{");
			//variables
			for (MemberField mf: bs.fields) {
				mf.className = bs.parentName + cp.capitalFirstChar(mf.fieldName);
				if (bs.isRoot) pw.println("    private " + mf.className + " " + mf.fieldName + " = null;");
				else if (mf.isRecord) pw.println("    private ArrayList<" + mf.className + "> " + mf.fieldName + " = null;");
				else { 
					pw.println("    String " + mf.fieldName + " = null;");
					mf.className = "String";//for mapping process
				}
				
			}
			//constractors
			//1.default
			pw.println("    public " + bs.className +"() {}");
			//2.with parameters
			pw.print("    public " + bs.className +"(");
			//specifying parameters
			StringBuilder parameters = new StringBuilder();
			StringBuilder assignParameters = new StringBuilder();
			for (MemberField mf: bs.fields) {
				if (!bs.isRoot && mf.isRecord) parameters.append("ArrayList<" + mf.className + "> " + mf.fieldName + ", ");
				else { 
					parameters.append(mf.className + " " + mf.fieldName + ", ");
				}
				assignParameters.append("        this." + mf.fieldName + " = " + mf.fieldName + ";\n");
			}
			pw.print(parameters.substring(0, parameters.length()-2));//cut the tail,', '
			pw.println(") {");
			//assignment statements
			pw.print(assignParameters);
			pw.println();
			pw.println("    }");
			//gets and sets function
			for (MemberField mf: bs.fields) {
				String fieldType = mf.className;
				if (!bs.isRoot && mf.isRecord) {
					fieldType = "ArrayList<" + mf.className + "> ";
				}
				//get function
				pw.println("    public " + fieldType + " " + "get" + cp.capitalFirstChar(mf.fieldName) + "() {");
				pw.println("        return " + mf.fieldName + ";");
				pw.println("    }");
				//set function
				pw.println("    public " + "set" + cp.capitalFirstChar(mf.fieldName) + "(" + fieldType + " " + mf.fieldName + ") {");
				pw.println("        this." + mf.fieldName + " = " + mf.fieldName + ";");
				pw.println("    }");
			}
			//closing class
			pw.println("}");
			pw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
