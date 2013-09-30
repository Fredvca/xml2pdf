package ContentStructAnalyst;

import java.util.ArrayList;
public class BridgeStone {
	
	public ArrayList<MemberField> fields = null;
	
	public String className = null;
	public String parentName = null;
	public String memberName = null;
	public boolean isRoot = false;
	public BridgeStone() {
		fields = new ArrayList<MemberField>();
		
	}
}
