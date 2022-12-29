package projects.sample6.nodes.messages.SixVcol;

import sinalgo.nodes.messages.Message;

public class ColorMessage extends Message {
	private int six_vcol_color = 0;
	private int round = 0;
	
	public ColorMessage(int six_vcol_color, int round) {
		this.six_vcol_color = six_vcol_color;
		this.round = round;
	}
	
	public int getSixVcolColor() {
		return this.six_vcol_color;
	}

	@Override
	public Message clone() {
		return this; // read-only policy 
	}

	@Override
	public String toString() {
		return "Color(" + this.six_vcol_color + ", " + round + ")";
	}
	
	

}
