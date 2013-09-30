package ContentStructAnalyst;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import com.itextpdf.text.pdf.PdfReader;
//import com.itextpdf.text.pdf.PdfStamper;

//import curam.util.exception.AppException;
//import curam.util.exception.InformationalException;


public class PDFTemplateCooper {
	private class _SimXMLReader {
		private int tagBeginPost, tagEndPost;
		private StringBuilder m_sbTarget = null;
		public _SimXMLReader(StringBuilder sb) {
			this.m_sbTarget = sb;
			tagEndPost = tagBeginPost = 0;
		}
		protected Boolean FindTag(String str) {
			Boolean bFound = false;
			if (m_sbTarget == null) return bFound;
			//while (true) {
			int startAt = m_sbTarget.indexOf("<"+str);
			if (startAt == -1) return bFound;
			int endAt = m_sbTarget.indexOf(">", startAt);
			if (endAt == -1) return bFound;
			bFound = true;
			
			tagBeginPost = startAt;
			tagEndPost = endAt + 1;
			//}
			return bFound;
		}
		
		protected String getCurrentTag() {
			return m_sbTarget.substring(tagBeginPost, tagEndPost);
		}
		
		protected String peekNextTag() {
			if (m_sbTarget == null) return null;
			int startAt = m_sbTarget.indexOf("<", tagBeginPost+1);
			if (startAt == -1) return null;
			int endAt = m_sbTarget.indexOf(">", startAt);
			if (endAt == -1) return null;
			return m_sbTarget.substring(startAt, endAt+1); 
		}
		
		protected String getNextTag() {
			if (m_sbTarget == null) return null;
			int startAt = m_sbTarget.indexOf("<", tagBeginPost+1);
			if (startAt == -1) return null;
			int endAt = m_sbTarget.indexOf(">", startAt);
			if (endAt == -1) return null;
			
			tagBeginPost = startAt;
			tagEndPost = endAt + 1;
			
			return m_sbTarget.substring(tagBeginPost, tagEndPost);
		}
	}
	
	public class TagIndentifier {
		public boolean isStartTag(String st) {
			if (st == null || st.equals("")) return false;
			if (st.charAt(1) != '/' && st.charAt(st.length()-2) != '/') return true;
			return false;
		}
		
		public boolean isEndTag(String st) {
			if (st == null || st.equals("")) return false;
			if (st.charAt(1) == '/'  && st.charAt(st.length()-2) != '/') return true;
			return false;
		}
		
		public boolean isSelfClosedTag(String st) {
			if (st == null || st.equals("")) return false;
			if (st.charAt(1) != '/' && st.charAt(st.length()-2) == '/') return true;
			return false;
		}
		public String getTagName(String st) {
			int startAt = 1;
			if (isEndTag(st)) startAt = 2;
			int endAt = st.indexOf(' ', startAt);
			if (endAt == -1) endAt = st.indexOf('/', startAt);
			if (endAt == -1) endAt = st.indexOf('>', startAt);
			
			if (endAt == -1) return "";
			return st.substring(startAt, endAt);
		}
	}
	
	public class Cooper {
		private StringBuilder rootPath = null;
		private StringBuilder sourcePDFFileName = null;
//		private StringBuilder destinationPDFFileName = null;
//		private StringBuilder dataHolderPath = null; //Contains classes for pdf file's entire struct.
//		private StringBuilder interfacePath = null; //Contains interface classes for generating final pdf file.
		private StringBuilder sourcePDFContent = null; //Contains pdf file content that is additional fields are trimmed
		private StringBuilder casename = null; //
	
		//package naming
		final private static String prefixPackageName = "curam.mcys.common.pdfform.";
		final private static String suffixPackageName = "sl.impl";
		//class naming
		final private static String prefixClassName = "MCYS";
//		final private static String suffixClassName_Section = "Section";
//		final private static String suffixClassName_Record = "Record";
		//import statement
//		final private static String importingArrayList = "import java.util.ArrayList;";
		//cutting field name in original xml-data
		final private static String fieldMustBeCut = "dd:dataDescription";
		private String MapStartAtField = "bks:form";
		private String MappingFileRootTag = "bks";
		
		Generator generater = null;
		
		private FileInputStream pdfInputStream = null;
		
		public StringBuilder getRootPath() {
			return rootPath;
		}
		public void setRootPath(StringBuilder rootPath) {
			this.rootPath = rootPath;
		}
		public Cooper(){
			//initTestData();
			generater = new Generator(this);
		}
		public String getMappingFileRootTag(){
			return MappingFileRootTag;
		}
		public String getRootClassName() {
			return prefixClassName+casename+"Data";
		}
		public String getPackageName() {
			return prefixPackageName + casename.toString().toLowerCase() + "." + suffixPackageName;
		}
		/**
		 * Replace all '\' with '/'; 
		 * @param strbuilder
		 */
		private void parsePath(StringBuilder strbuilder) {
			int index = 0;
			while(index >= 0) {
				index = strbuilder.indexOf("\\");
				if( index != -1) strbuilder.replace(index, index+1, "/");
			}
		}
		
		private void trimFieldByName(String fieldName, StringBuilder sb) {
			int startFrom = -1, endTo = -1;
			if(sb != null && fieldName != null && fieldName != "") {
				startFrom = sb.indexOf("<" + fieldName);
				endTo = sb.indexOf("</" + fieldName);
				if(startFrom>=0 && startFrom < endTo)
					sb.replace(startFrom, endTo+fieldName.length()+3, "");
			}
			
		}
		
		private  void initTestData(String path, String filename, String caseName) {
			rootPath = new StringBuilder(path);
			parsePath(rootPath);
			sourcePDFFileName = new StringBuilder(filename);
			casename = new StringBuilder(caseName);//case sensitive !!!!!
			//MapStartAtField = "form";
			//MappingFileRootTag = "form";
		}
		
		protected boolean generateOriginXML() throws IOException {
			if ( rootPath == null || sourcePDFFileName == null || sourcePDFContent == null) return false;
			StringBuilder xmlFileName = new StringBuilder(sourcePDFFileName.replace(sourcePDFFileName.indexOf(".pdf"), sourcePDFFileName.length(), ".xml"));
			PrintWriter fu = new PrintWriter(rootPath.toString()+xmlFileName.toString());
			
			fu.print(sourcePDFContent.toString());
			fu.close();
			return true;
		}
		
		protected  StringBuilder readXML() {
			//Agreement
			initTestData("C:/WorkArea/CPIN/PDF Forms Uploading/Agreement/", "Agreement-20130916.pdf", "Agreement");
			//KinshipSummary
			//initTestData("C:/WorkArea/CPIN/PDF Forms Uploading/KinshipSummary/", "KinshipSummary-20130822.pdf", "KinshipSummary");
			return readXML(rootPath.toString() + sourcePDFFileName.toString());
		}
		
		protected StringBuilder readXML(String filename) {
			try {
				File file = new File(filename);
	            byte[] bytes = new byte[(int) file.length()];
	            pdfInputStream = new FileInputStream(file);
	            pdfInputStream.read(bytes);
	            PdfReader pdfReader = new PdfReader(bytes);
	
	            DOMSource domSource = new DOMSource(pdfReader.getAcroFields().getXfa().getDatasetsNode());
	            StringWriter writer = new StringWriter();
	            StreamResult result = new StreamResult(writer);
	            TransformerFactory tf = TransformerFactory.newInstance();
	            Transformer transformer = tf.newTransformer();
	            transformer.transform(domSource, result);
	            sourcePDFContent = new StringBuilder(writer.toString());
			} catch (Exception e) {
	            e.printStackTrace();
	        }
			return sourcePDFContent;
		}
	
		/*protected Boolean setXML(String srcFile, String dataFile, String destFile) {
			Boolean bResult = false;
		    try {
	
		        File file = new File(srcFile);
	
		        byte[] bytes = new byte[(int) file.length()];
		        new FileInputStream(file).read(bytes);
		        PdfReader pdfReader = new PdfReader(bytes);
	
		        FileOutputStream opStream = new FileOutputStream(destFile);
		        PdfStamper stamper = new PdfStamper(pdfReader, opStream);
	
		        stamper.getAcroFields().getXfa().fillXfaForm(new File(dataFile));
		        stamper.close();
	
		        DOMSource domSource = new DOMSource(pdfReader.getAcroFields().getXfa().getDatasetsNode());
		        StringWriter writer = new StringWriter();
		        StreamResult result = new StreamResult(writer);
		        TransformerFactory tf = TransformerFactory.newInstance();
		        Transformer transformer = tf.newTransformer();
		        transformer.transform(domSource, result);
		        System.out.println(writer.toString());
		        bResult = true;
	
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
	
		    return bResult;
		}
		*/
		public void parseXML() {
			if (sourcePDFContent == null) return;
			_SimXMLReader simXMLReader = new _SimXMLReader(sourcePDFContent);
			TagIndentifier tagIndentifier = new TagIndentifier();
			if (simXMLReader.FindTag(MapStartAtField)){
				if (tagIndentifier.isStartTag(simXMLReader.getCurrentTag())) readRootNode(simXMLReader);
			}
		}
		
		public void readRootNode(_SimXMLReader node) {
			TagIndentifier ti= new TagIndentifier();
			int deep = 0;
			//displaying the current struct name;
			int ci = deep;
			while (ci>0) {System.out.print("    "); ci--;};
			System.out.println(node.getCurrentTag());
			//---------------end of displaying the current struct name;
			
			BridgeStone brgStone = new BridgeStone();
			brgStone.isRoot = true;
			brgStone.className = this.getRootClassName();
			brgStone.parentName = prefixClassName+casename.toString();
			brgStone.memberName = ti.getTagName(node.getCurrentTag());
			generater.mapingClass.add(brgStone.className);
			//Storing current tag name
			//String curTagName = node.getCurrentTag();
			
			while (!ti.isEndTag(node.peekNextTag())) {
				MemberField mf = new MemberField(ti.getTagName(node.peekNextTag()), false, null);
				if (ti.isStartTag(node.peekNextTag())) {
					mf.isRecord = true;
					node.getNextTag();
					readNode(node, deep, brgStone.parentName);
				}
				else {
					ci = deep+1;
					while (ci>0) {System.out.print("    "); ci--;};
					System.out.println(node.getNextTag());
				}
				brgStone.fields.add(mf);
			}
			
			generater.AddNew(brgStone);
			//on the end of current tag
			ci = deep;
			while (ci>0) {System.out.print("    "); ci--;};
			System.out.println(node.getNextTag());
			
			generater.setbFinishScan(true);
			generater.createMappingXml();
		}
		
		public void readNode(_SimXMLReader node, int deep, String parentName) {
			TagIndentifier ti= new TagIndentifier();
			deep ++;
			//displaying the current struct name;
			int ci = deep;
			while (ci>0) {System.out.print("    "); ci--;};
			System.out.println(node.getCurrentTag());
			//---------------end of displaying the current struct name;
			
			//Storing current tag name
			String curTagName = node.getCurrentTag();
			String capitalCurTagName = capitalFirstChar(ti.getTagName(curTagName));

			BridgeStone brgStone = new BridgeStone();
			brgStone.isRoot = false;
			brgStone.parentName = brgStone.className = parentName+(capitalCurTagName);
			brgStone.memberName = ti.getTagName(node.getCurrentTag());
			generater.mapingClass.add(brgStone.className);
			
			while (!ti.isEndTag(node.peekNextTag())) {
				MemberField mf = new MemberField(ti.getTagName(node.peekNextTag()), false, null);
				if (ti.isStartTag(node.peekNextTag())) {
					node.getNextTag();
					readNode(node,deep,parentName+capitalCurTagName);
					mf.isRecord = true;
				}
				else {
					ci = deep+1;
					while (ci>0) {System.out.print("    "); ci--;};
					System.out.println(node.getNextTag());
				}
				brgStone.fields.add(mf);
			}
			generater.AddNew(brgStone);
			//on the end of current tag
			ci = deep;
			while (ci>0) {System.out.print("    "); ci--;};
			System.out.println(node.getNextTag());
		}
		
		public String capitalFirstChar(String st) {
			return st.substring(0,1).toUpperCase() + st.substring(1);
		}
		public String getSourceFullName(){
			return rootPath.toString()+ sourcePDFFileName.toString();
		}		
		public StringBuilder getSourcePDFContent(){
			return sourcePDFContent;
		}
		
		public String getFieldNameMustBeCut(){
			return fieldMustBeCut;
		}
	};
	
	public PDFTemplateCooper() {
		op = new Cooper();
	}
	
	public static Cooper op;
	public static void main(String[] args) throws IOException {
		PDFTemplateCooper pt = new PDFTemplateCooper();
		pt.op.trimFieldByName(pt.op.getFieldNameMustBeCut(),pt.op.readXML());
		pt.op.generateOriginXML();
		
		pt.op.parseXML();
		//System.out.println(pt.op.getSourcePDFContent());
    }
}