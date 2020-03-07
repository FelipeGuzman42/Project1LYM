package uniandes.lym.robot.control;

import java.util.ArrayList;

import uniandes.lym.robot.kernel.RobotWorld;
import uniandes.lym.robot.kernel.RobotWorldDec;

public class InstructionReader {
	private RobotWorldDec world;
	private RobotWorld rob;
	private String[] variables;
	private int[] valor;

	public void reader(ArrayList<String> input, boolean s) {
		int i = 2, n;
		if(s) {
			s = checkCondition(input.get(0),input.get(1));
		} else {
			s = !checkCondition(input.get(0),input.get(1));
		}
		boolean ok = false, begin = s;
		n = input.size();
		try {
			ArrayList<String> str = input;
			String variable = "", a, sr;
			while (begin) {
				a = str.get(i);
				switch (a) {
				case "ROBOT_R":
					ok = true;
					break;
				case "VARS":
					sr = str.get(++i);
					while (!sr.equals("BEGIN")) {
						variable += str;
						sr = str.get(++i);
					}
					i--;
					break;
				case "BEGIN":
					begin = false;
					break;
				default:
					ok = false;
					begin = false;
					break;
				}
				i++;
			}

			variables = variable.split(",");
			valor = new int[variables.length];
			while (ok && i < n) {
				int r = 0, k;
				a = str.get(i);
				String var;
				switch (a) {
				case "ASSIGN":
					int m = Integer.parseInt(str.get(i));
					if (str.get(i).equals("TO")) {
						var = str.get(++i);
						for (int j = 0; j < variables.length; j++) {
							String v = variables[j];
							if (v.equals(var)) {
								valor[j] = m;
								break;
							}
						}
					} else {
						ok = false;
					}
					break;
				case "MOVE":
					var = str.get(++i);
					k = buscarV(var);
					if (k == 0)
						k = Integer.parseInt(var);
					var = str.get(++i);
					switch (var) {
					case "TOTHE":
						var = str.get(++i);
						switch (var) {
						case "LEFT":
							r++;
						case "BACK":
							r++;
						case "RIGHT":
							r++;
							break;
						}
						rotar(r);
						mover(k);
						rotar(4 - r);
						break;
					case "INDIR":
						var = str.get(++i);
						orientar(var);
						mover(k);
						break;
					default:
						i--;
						mover(k);
						break;
					}
					break;
				case "TURN":
					var = str.get(++i);
					switch (var) {
					case "LEFT":
						r++;
					case "AROUND":
						r++;
					case "RIGHT":
						r++;
						break;
					}
					rotar(r);
					break;
				case "FACE":
					var = str.get(++i);
					orientar(var);
					break;
				case "PUT":
					var = str.get(++i);
					k = buscarV(var);
					if (k == 0)
						k = Integer.parseInt(var);
					if (str.get(++i).equals("OF")) {
						var = str.get(++i);
						switch (var) {
						case "BALLOONS":
							world.putBalloons(k);
							break;
						case "CHIPS":
							world.putChips(k);
							break;
						}
					}
					break;
				case "PICK":
					var = str.get(++i);
					k = buscarV(var);
					if (k == 0)
						k = Integer.parseInt(var);
					if (str.get(++i).equals("OF")) {
						var = str.get(++i);
						switch (var) {
						case "BALLOONS":
							world.grabBalloons(k);
							break;
						case "CHIPS":
							world.pickChips(k);
							break;
						}
					}
					break;
				case "END":
					ok = false;
					break;
				default:
					if (a.equals(";") || a.equals(",")) {
						break;
					} else {
						ok = false;
					}
					break;
				}
				i++;
			}

		} catch (Error e) {
		}
	}

	private Integer buscarV(String var) {
		int x = 0;
		for (int j = 0; j < variables.length; j++) {
			String v = variables[j];
			if (v.equals(var)) {
				x = valor[j];
				break;
			}
		}
		return x;
	}

	private void mover(int k) {
		world.moveForward(k);
	}

	private void rotar(int dir) {
		switch (dir) {
		case 3:
			world.turnRight();
		case 2:
			world.turnRight();
		case 1:
			world.turnRight();
			break;
		}
	}

	private void orientar(String or) {
		int dir = rob.getOrientation(), r;
		if (or.equals("NORTH")) {
			r = 0;
		} else if (or.equals("SOUTH")) {
			r = 1;
		} else if (or.equals("EAST")) {
			r = 2;
		} else
			r = 3;
		while (dir != r) {
			world.turnRight();
			dir = rob.getOrientation();
		}
	}
	
	private boolean checkCondition(String s1, String s2) {
		boolean r = false;
		switch(s1) {
		case "facing":
			switch(s2) {
			case "north":
				r = rob.facingNorth();
				break;
			case "south":
				r = rob.facingSouth();
				break;
			case "east":
				r = rob.facingEast();
				break;
			case "west":
				r = rob.facingWest();
				break;
			}
			break;
		case "canPick":
			switch(s2) {
			case "chips":
				r=rob.anyChips();
				break;
			case "baloons":
				r=rob.anyBalloons();
				break;
			}
			break;
		case "canMove":
			switch(s2) {
			case "north":
				r = !rob.atTop();
				break;
			case "south":
				r = !rob.atBottom();
				break;
			case "east":
				r = !rob.atRight();
				break;
			case "west":
				r = !rob.atLeft();
				break;
			}
			break;
		case "canPut":
			r = true;
			break;
		}
		return r;
	}
}
