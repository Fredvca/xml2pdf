package ContentStructAnalyst;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

import ContentStructAnalyst.PDFTemplateCooper.Cooper;

public class Generator {
	private ArrayList<BridgeStone> bridge = null;
	public ArrayList<String> mapingClass = null;
	private boolean bFinishScan = false;
	private Cooper cp = null;

	public Generator(Cooper cp) {
		super();
		this.bridge = new ArrayList<BridgeStone>();
		this.mapingClass = new ArrayList<String>();
		this.cp = cp;
	}
	
	public void AddNew(BridgeStone bridgestone){
		bridge.add(bridgestone);
		
		Runnable r0 = new Worker(bridgestone, cp);
		Thread thread = new Thread(r0);
		thread.setName(bridgestone.className);
		thread.setPriority(Thread.MAX_PRIORITY);
		thread.start();
	}

	public boolean isbFinishScan() {
		return bFinishScan;
	}
	
	protected final BridgeStone findBridgeStoneByClassName(String className) {
		BridgeStone ret = null;
		for(BridgeStone bs: this.bridge) {
			if (bs.className.equals(className)) return bs;
		}
		return ret;
	}
	public void setbFinishScan(boolean bFinishScan) {
		this.bFinishScan = bFinishScan;
	}
	public void createMappingXml() {
		String stab = "    ";
		if (!bFinishScan) return;
		try {
			Thread.sleep(300L);
			PrintWriter pw = new PrintWriter(cp.getRootPath() + cp.getRootClassName() + ".xml");
			pw.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			pw.println("<mapping>");
			pw.println();
			for (String classstr: mapingClass) {
				BridgeStone bs = this.findBridgeStoneByClassName(classstr);
				if (bs != null) {
					pw.println(stab + "<class name=\"" + this.cp.getPackageName() + "." + bs.className + "\">");
					if(bs.isRoot) pw.println(stab + "<map-to xml=\""+ cp.getMappingFileRootTag() + "\" />");
					else pw.println(stab + stab + "<map-to xml=\"" + bs.memberName +"\" />");
					for(MemberField mf: bs.fields) {
						pw.println(stab + stab + "<field name=\"" + mf.fieldName + "\"	type=\"" + ((mf.isRecord)? (this.cp.getPackageName() + "."):"") + (mf.className.equals("String")? "string":mf.className) + "\"" + ((mf.isRecord && !bs.isRoot)? ("\n" + stab + stab + stab + "collection=\"arraylist\""):"") + ">");
						pw.println(stab + stab + stab + "<bind-xml name=\"" + mf.fieldName +"\" node=\"element\" />");
						pw.println(stab + stab + "</field>");
					}
					pw.println(stab + "</class>");
				}
			}
			pw.println("</mapping>");
			pw.close();
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
