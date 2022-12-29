package projects.sample6.nodes.edges;

import java.awt.Graphics;

import projects.sample6.nodes.nodeImplementations.TreeNode;
import sinalgo.gui.transformation.PositionTransformation;
import sinalgo.nodes.Position;
import sinalgo.nodes.edges.BidirectionalEdge;
import sinalgo.nodes.edges.Edge;
import sinalgo.nodes.messages.Message;
import sinalgo.tools.logging.Logging;

public class MyEdge extends BidirectionalEdge {

	Logging logger = Logging.getLogger();
	
	public String message_on_edge = "";
	
	@Override
	public void draw(Graphics g, PositionTransformation pt) {
		super.draw(g, pt);
		
		if (this.message_on_edge != "") {
			Position start = this.startNode.getPosition();
			Position end = this.endNode.getPosition();
			
			pt.translateToGUIPosition(start);
			int fromX = pt.guiX, fromY = pt.guiY;
			pt.translateToGUIPosition(end);
			int toX = pt.guiX, toY = pt.guiY;
			
			int x = (int) ((toX + fromX)/2);
			int y = (int) ((toY + fromY)/2);
			
			//logger.logln("Drawing edge string: " + this.message_on_edge + " on location: (" + x + ", " + y + ")");
			g.drawString(message_on_edge, x, y);
		}
	}

	@Override
	public void addMessageForThisEdge(Message msg) {
		super.addMessageForThisEdge(msg);
		//logger.logln("addMessageForThisEdge message: " + msg);
		message_on_edge = msg.toString();
	}
}
