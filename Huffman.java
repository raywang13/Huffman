public class Huffman {

	private static int[] freq = new int[256];
	private Node[] nodes = new Node[256];
	private static String[] codes = new String[256];

	public static Node root;
	public static int totalCount = 0, UCFileSize = 0;
	static boolean pause;

	public Huffman() {

	}

	public Node[] setUpNodes() {
		for (int i = 0; i <= freq.length-1; i++) {
			if (freq[i] != 0) {
				nodes[i] = new Node(freq[i],(char) i, null, null);
			}
		}
		return nodes;
	}

	public void createTree() {
		while (!isEmpty()) {
			int index = Integer.MAX_VALUE;
			int lowest = Integer.MAX_VALUE;
			for (int i = 0; i <= nodes.length-1; i++) {
				if (nodes[i] != null) {
					if (nodes[i].count < lowest) {
						lowest = nodes[i].count;
						index = i;
					}
				}
			}
			int index2 = Integer.MAX_VALUE;
			int lowest2 = Integer.MAX_VALUE;
			for (int i = 0; i <= nodes.length-1; i++) {
				if (nodes[i] != null && i != index) {
					if (nodes[i].count < lowest2) {
						lowest2 = nodes[i].count;
						index2 = i;
					}
				}
			}
			if (index == Integer.MAX_VALUE || index2 == Integer.MAX_VALUE) {
				break;
			}
			Node newTree = new Node(lowest2 + lowest, null, nodes[index],
					nodes[index2]);
			nodes[index] = newTree;
			nodes[index2] = null;
			root = newTree;
		}
	}

	public void getBinary(int count, String binary, Node tree) {
		if (tree.leftval != null & tree.rightval != null) {
			getBinary(count, (binary + "1"), tree.rightval);
			getBinary(count, (binary + "0"), tree.leftval);

		} else {
			count = (tree.count * binary.length());
			totalCount = totalCount + count;
			codes[tree.data.charValue()] = binary;
		}
	}

	public static void Verbose() {
		System.out.println("Char: Value and Count");
		for (int i = 0; i <= freq.length-1; i++) {
			if (freq[i] != 0) {
				System.out.println("Char: " + i + " " + freq[i]);
			}
		}
		System.out.println("\nChar: Value and Huffcode");
		for (int j = 0; j <= codes.length-1; j++) {
			if (codes[j] != null) {
				System.out.println("Char: " + j + " " + codes[j]);
			}
		}
		System.out.println("\nHuffman Tree");
		PrintHuff(root, 5);
	}

	public void Uncompress(String input, String output) {
		TextFile writer = new TextFile(output, 'W');
		BinaryFile read = new BinaryFile(input, 'R');

		if (read.readChar() == 'H' && read.readChar() == 'F') {
			root = build(read);
			Node temp = root;
			while (read.EndOfFile() == false) {
				if (!(read.readBit() == false)) {
					temp = temp.rightval;
					if (temp.data != null) {
						writer.writeChar(temp.data);
						temp = root;
					}
				} else {
					temp = temp.leftval;
					if (temp.data != null) {
						writer.writeChar(temp.data);
						temp = root;
					}
				}
			}
			read.close();
			writer.close();
			pause = true;
			return;
		} else {
			read.close();
			writer.close();
			System.out.println("Your file cannot be compressed");
			pause = false;
			return;
		}
	}

	public static void PrintHuff(Node tree, int indent) {
		if (tree != null) {
			for (int i = 0; i <= indent-1; i++) {
				System.out.print("\t");
			}
			System.out.println(tree.count);
			for (int i = 0; i <= indent-1; i++) {
				System.out.print("\t");
			}
			System.out.println(tree.data);
			PrintHuff(tree.rightval, indent + 1);
			PrintHuff(tree.leftval, indent - 1);

		}
	}

	public static boolean containsVflag(String[] stringHolder) {
		for (String string : stringHolder) {
			if (string.equals("-v") || string.equals("-V")) {
				return true;
			}
		}
		return false;
	}

	public static boolean containsFflag(String[] stringHolder) {
		for (String string : stringHolder) {
			if (string.equals("-f") || string.equals("-F")) {
				return true;
			}
		}
		return false;
	}

	private boolean isEmpty() {
		for (int i = 0; i <= nodes.length-1; i++) {
			if (nodes[i] != null) {
				return false;
			}
		}
		return true;
	}

	private static int calculateHuffmanOut() {
		int currentCount = numberofNodes(root) + totalCount + 48;
		if (currentCount % 8 != 0) {
			int ceiling = (8 - currentCount % 8);
			return currentCount + ceiling;
		} else {
			return currentCount;
		}
	}

	private static int numberofNodes(Node tree) {
		if (tree.leftval == null && tree.rightval == null) {
			return 9;
		}
		return 1 + numberofNodes(tree.leftval) + numberofNodes(tree.rightval);
	}

	private void preOrderTraversal(Node root, BinaryFile writer) {
		if (root == null) {
			return;
		}
		if (root.data == null) {
			writer.writeBit(true);
		}
		if (root.data != null) {
			writer.writeBit(false);
			writer.writeChar(root.data);
		}
		preOrderTraversal(root.leftval, writer);
		preOrderTraversal(root.rightval, writer);
	}

	private static Node build(BinaryFile reader) {
		boolean bit = reader.readBit();
		if (bit == true) {
			return new Node(0, null, build(reader), build(reader));
		} else {
			return new Node(0, reader.readChar(), null, null);
		}
	}

	public void write(TextFile read, String output) {
		BinaryFile writer = new BinaryFile(output, 'W');
		if (calculateHuffmanOut() < UCFileSize) {
			read.rewind();
			writer.writeChar('H');
			writer.writeChar('F');
			preOrderTraversal(root, writer);
			while (read.EndOfFile() == false) {
				char current = read.readChar();
				for (char c : codes[current].toCharArray()) {
					if (c == '0') {
						writer.writeBit(false);
					}
					if (c == '1') {
						writer.writeBit(true);
					}
				}
			}
			try {
				writer.close();
				read.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			System.out
			.println("Compressed file is bigger than the original file");
			writer.close();
			read.close();
		}
	}

	public void flaggedWrite(TextFile read, String output) {
		BinaryFile writer = new BinaryFile(output, 'W');
		read.rewind();
		writer.writeChar('H');
		writer.writeChar('F');
		preOrderTraversal(root, writer);
		while (read.EndOfFile() == false) {
			char current = read.readChar();
			for (char c : codes[current].toCharArray()) {
				if (c == '0') {
					writer.writeBit(false);
				}
				if (c == '1') {
					writer.writeBit(true);
				}
			}
		}
		try {
			writer.close();
			read.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public TextFile read(String input) {
		TextFile read = new TextFile(input, 'R');
		while (read.EndOfFile() == false) {
			UCFileSize++;
			char curr = read.readChar();
			freq[curr]++;
		}
		UCFileSize = UCFileSize * 8;
		return read;
	}

	public static void main(String[] args) {

		Huffman temp = new Huffman();
		String input = args[args.length - 2];
		String output = args[args.length - 1];

		if (args[0].equals("-u") || args[0].equals("-U")) {
			temp.Uncompress(input, output);
			if (Huffman.pause == true & Huffman.containsVflag(args)) {
				Huffman.PrintHuff(Huffman.root, 5);
			}
		}

		if (args[0].equals("-c") || args[0].equals("-C")) {
			TextFile reader = temp.read(input);
			temp.setUpNodes();
			temp.createTree();
			temp.getBinary(0, "", Huffman.root);
			if (Huffman.containsFflag(args)) {
				temp.flaggedWrite(reader, output);
			} else {
				temp.write(reader, output);
			}
			if (Huffman.containsVflag(args)) {
				Huffman.Verbose();
			}
		}
	}

}
