package projects.sample6.nodes.nodeImplementations;



import java.awt.Color;
import java.awt.Graphics;

import projects.matala12.nodes.messages.RoundColorMessage;
import projects.sample6.nodes.messages.SixVcol.ColorMessage;
import sinalgo.configuration.WrongConfigurationException;
import sinalgo.gui.transformation.PositionTransformation;
import sinalgo.nodes.Node;
import sinalgo.nodes.edges.Edge;
import sinalgo.nodes.messages.Inbox;
import sinalgo.nodes.messages.Message;
import sinalgo.tools.logging.Logging;

/**
 * An internal node (or leaf node) of the tree.
 * Note that the leaves are instances of LeafNode, a subclass of this class. 
 */
public class TreeNode extends Node {

	Logging logger = Logging.getLogger();
	
	public TreeNode parent = null; // the parent in the tree, null if this node is the root
	
	public int six_vcol_color = -1; // Possible values: 0..5. At the start, there is N colors. After the algo finishes, there are 6 colors maximum.
	private boolean flag_sixVcol_started = false; // Used only for the root to start the algorithm.
	private int round = 0;
	private boolean flag_sixVcol_finished = false; // Used locally for each node to change their draw function
	
	@Override
	public void checkRequirements() throws WrongConfigurationException {
	}

	@Override
	public void handleMessages(Inbox inbox) {
//		while(inbox.hasNext()) {
//			Message m = inbox.next();
//			if(m instanceof MarkMessage) {
//				if(parent == null || !inbox.getSender().equals(parent)) {
//					continue;// don't consider mark messages sent by children
//				}
//				this.setColor(Color.RED);
//				broadcast(m);
//			}
//		}
		six_vcol(inbox);
	}
	
	/** 
	 * This function sends a message to all the node's children.
	 * I didn't understand why 'broadcast(m);' doesn't work correctly. Apparently it sends to parent aswell (which I didn't take into account), which is not what I wanted.
	 * @param m
	 */
	private void broadcast_children(Message m) {
		for(Edge e : this.outgoingConnections) {
			// Note: .equals is comparing objects, I did '==' and spent 30 minutes on why it doesn't work! Because it compares values, not object hashes.
			TreeNode outgoing_node = (TreeNode)e.endNode;
			if (outgoing_node.parent == null) {
				// do nothing, root has no parent
			}
			else if (outgoing_node.parent.equals(this)) {
				// if 'this' is the parent of outgoing node, then outgoing node is the child of 'this'. Send
				logger.logln(this + " sends message: " + m + " to: " + e.endNode);
				send(m, e.endNode);
			}
		}
	}
	
	// Page 126, Algo 9.9 within the book (six_vcol)
	// Specifically, section 9.5.2: Six Coloring Algorithm
	private void six_vcol(Inbox inbox) {
		if (flag_sixVcol_started == false) {
			// "The algorithm starts by the root coloring itself with 0 and sending this color to its children."
			if (parent == null) {
				this.six_vcol_color = 0;
			}
			
			// All parents send their color to all their children (which gives log * n run-time, instead of height of tree, like regualr broadcast)
			flag_sixVcol_started = true;
			// Send color of parent to children
			Message m = new ColorMessage(this.six_vcol_color, round);
			broadcast_children(m);
		}
		
		while(inbox.hasNext() && (this.six_vcol_color < 0 || this.six_vcol_color > 5)) {
			Message m = inbox.next();
			// Received from parent
			if(m instanceof ColorMessage) {				
				round += 1;
				logger.logln(this + " received message from parent: " + m.toString());
				/*
				 * "In every round thereafter, each node receives a color c_p from its parent 
				 * and finds the smallest index k that its current color c_i differs from c_p."
				 */
				int cp = ((ColorMessage) m).getSixVcolColor(); 	// parent color
				int ci = this.six_vcol_color;					// current color
				
				// Convert to binary
				String cp_binary = Integer.toBinaryString(cp);
				String ci_binary = Integer.toBinaryString(ci);
				
				// Zero-pad
				int bin_len_diff = Math.abs(ci_binary.length() - cp_binary.length());
				if (cp_binary.length() < ci_binary.length()) {
					for(int i = 0; i < bin_len_diff; i++) {
						cp_binary = "0" + cp_binary;
					}
				} else {
					for(int i = 0; i < bin_len_diff; i++) {
						ci_binary = "0" + ci_binary;
					}
				}
				
				// Find smallest k-index 
				int k_index = -1;
				// We start from the start of the string (left-to-right), even though MSB (most significant bit) binary string is read from right-to-left.
				// Note: ci_binary and cp_binary lengths are the same, because of zero-pad. We could also do: i = Math.max(cp_binary.length(), ci_binary.length()
				for(int i = 0; i < ci_binary.length(); i++) {
					if (ci_binary.charAt(i) != cp_binary.charAt(i)) {
						k_index = i;
						break; // We must find the 'smallest' k_index, so we stop when we got the first result.
					}
				}
				
				assert k_index == -1; // At the beginning all nodes have different colors. Its impossible to not find any difference.
				
				/*
				 * "It then assigns a new color to itself by a bit string representing k
				 * concatenated with the value of c_i in the kth position."
				 */
				String k_binary = Integer.toBinaryString(k_index);
				String new_color_binary = k_binary + ci_binary.charAt(k_index);
				int new_color = Integer.parseInt(new_color_binary, 2);
				logger.logln(this + " changed color from " + this.six_vcol_color +  " to " + new_color + " at round " + round);
				this.six_vcol_color = new_color;
				
				/*
				 * "This new value of c_i is sent to children to be received
				 * in the next round and used for the same computation in the next round."
				 */
				ColorMessage new_color_message = new ColorMessage(this.six_vcol_color, round);
				broadcast_children(new_color_message);
				
				/*
				 * "This process continues until each node has a color in the range {0,..,5}..."
				 */
			}
		}
		if (flag_sixVcol_finished == false && this.six_vcol_color >= 0 && this.six_vcol_color < 6) {
			logger.logln(this + " finished running, vertex color is in range {0,..,5}");
			flag_sixVcol_finished = true;
		}
	}

	@Override
	public void init() {
		// "Initially, all nodes have color represented by their identifiers" (for total of n colors)
		this.six_vcol_color = this.ID;
	}

	@Override
	public void neighborhoodChange() {
	}

	@Override
	public void preStep() {

	}

	@Override
	public void postStep() {

	}

	@Override
	public void draw(Graphics g, PositionTransformation pt, boolean highlight) {
		String text = ""+six_vcol_color;
		int fontSize = 12;
		Color textColor = Color.YELLOW;
		if (flag_sixVcol_finished) {
			this.setColor(Color.GREEN);
			textColor = Color.RED;
		}
		this.drawNodeAsDiskWithText(g, pt, highlight, text, fontSize, textColor);
	}

	@Override
	public String toString() {
		return "Node(ID="+this.ID+", Color="+this.six_vcol_color+")";
	}
	
//	@NodePopupMethod(menuText = "Color children") 
//	public void colorKids() {
//		MarkMessage msg = new MarkMessage();
//		MessageTimer timer = new MessageTimer(msg);
//		timer.startRelative(1, this);
//	}

	
	
}
