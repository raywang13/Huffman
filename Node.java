public class Node {

	public Node leftval;
	public Node rightval;
	public Character data;
	public int count;
	public String code;

	Node(int count, Character newdata, Node left, Node right) {
		this.leftval = left;
		this.rightval = right;
		this.count = count;
		this.data = newdata;
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(String v) {
		this.code = v;
	}
}