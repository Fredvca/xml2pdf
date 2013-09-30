package ContentStructAnalyst;

public class MemberField {
	public String fieldName = null;
	public boolean isRecord = false;
	public String className = null;
	public MemberField(String fieldName, boolean isRecord, String className) {
		super();
		this.fieldName = fieldName;
		this.isRecord = isRecord;
		this.className = className;
	}
};