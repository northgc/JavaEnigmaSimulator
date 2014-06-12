import java.util.*;

/*
 * enigma.java is a command line tool for simulating the German Enigma Machine.
 * This program encrypts or decrypts a user defined string using a series of electrical
 * 'rotors' that change their values in reaction to the input. More info on this cryptosystem
 * can be found here: http://en.wikipedia.org/wiki/Enigma_machine
 * 
 * @author Grayson North
 * @date May 2014
 */

public class enigma{
	static ArrayList<Switcher> rlist = new ArrayList<Switcher>();
	static String plaintext;
	static String ciphertext;

	public static void main(String args[]) {
		System.out.println("Enigma Machine Simulator by Grayson North v1.0");
		System.out.println("Would you like to set the machine up yourself or use the default settings?");
		System.out.print("Type (custom) or (default) or (info) to see information about this simulator");
		
		Scanner scan = new Scanner(System.in);
		String in = scan.nextLine();
		
		if(in.equals("custom") || in.equals("c")) {
			System.out.println("If you would like to connect two letters on the plugboard type them in sequence (example: abxy would change a to b, b to a, x to y, etc)");
			System.out.println("Otherwise just hit enter/return");
			rlist.add(new PlugBoard(scan.nextLine().toUpperCase()));
			System.out.println("Now type the rotors you wish to use in order (for example 123 for rotors I II and III)");
			String rstring = scan.nextLine();
			System.out.println("Using rotors: ");
			for(int i=0; i<rstring.length(); i++) {
				if(i == rstring.length()-1) {
					System.out.print("and "+rstring.charAt(i)+"\n");
				}
				else {
				System.out.print(rstring.charAt(i)+" ");
				}
			}
			
			char w;
			char n;
			for(int i=0; i<rstring.length(); i++) { //this loop querys the user for the individual rotor settings
				System.out.println("What window setting would you like to use for rotor number "+(i+1)+" (type a character)");
				w = scan.nextLine().toUpperCase().charAt(0);
				System.out.println("Where should the notch/turnover be located? (type another character)");
				n = scan.nextLine().toUpperCase().charAt(0);
				
				System.out.println("Placing rotor "+rstring.charAt(i)+" at position "+(i+1)+" with window setting "+w+" and notch setting "+n);
				//Character c = rstring.charAt(i);
				rlist.add(new Rotor(Character.getNumericValue(rstring.charAt(i)),w,n));
			}
			
			System.out.println("Which reflector would you like to use? (type 'B' or 'C')");
			rlist.add(new Reflector(scan.nextLine().toUpperCase().charAt(0)));
		}
		else if (in.equals("default") || in.equals("d")) {
			//default settings: 5 rotors all set with their 'first' letter in the window
			//no connections made on plug board and using reflector B
			System.out.println("Using default rotor settings");
			rlist.add(new PlugBoard(""));
			rlist.add(new Rotor(1,'A','C'));
			rlist.add(new Rotor(1,'A','C'));
			rlist.add(new Rotor(1,'A','C'));
			//rlist.add(new Rotor(4,'E'));
			//rlist.add(new Rotor(5,'V'));
			rlist.add(new Reflector('B'));
		}
		
		else if(in.equals("info") || in.equals("i")) {
			showInfo();
			main(null);
		}
		
		System.out.println("Here are your settings");
		printRotors();
		
		System.out.println("Please input your text to be encoded/decoded");
		plaintext = scan.nextLine().toUpperCase();
		StringBuilder ss = new StringBuilder(plaintext.length());
		for(int i = 0; i<plaintext.length(); i++) {
			ss.append(translate(plaintext.charAt(i))); //"type in" letters one at a time
		}
		ciphertext = ss.toString(); //make a string out of the outputs
		System.out.println("\nOutput: "+ciphertext);
		//System.out.println("\nFinal rotor state: ");
		//printRotors();
		System.out.println("\n\nTranslate another message? (y/n)");
		if(scan.nextLine().equals("y")) {
			main(null);
		}
		else
			System.exit(0);
	}
	
	static public void showInfo() {
		System.out.println("This is a simulator for the enigma machine, an electro-mechanical rotor cipher machine.");
		System.out.println("It uses a series of rotors and wires to translate messages character by character.");
		System.out.println("This implementation translates whole strings at a time and uses the following rotor values");
		System.out.println("Rotor I: EKMFLGDQVZNTOWYHXUSPAIBRCJ");
		System.out.println("Rotor II: AJDKSIRUXBLHWTMCQGZNPYFVOE");
		System.out.println("Rotor III: BDFHJLCPRTXVZNYEIWGAKMUSQO");
		System.out.println("Rotor IV: ESOVPZJAYQUIRHXLNFTGKDCMWB");
		System.out.println("Rotor V: VZBRGITYUPSDNHLXAWMJQOFECK");
		System.out.println("And the following reflectors: ");
		System.out.println("Reflector 'B': YRUHQSLDPXNGOKMIEBFZCWVJAT");
		System.out.println("Reflector 'C': FVPJIAOYEDRZXWGCTKUQSBNMHL");
		System.out.println(" 'Default'   : YRUHQSLDPXNGOKMIEBFZCWVJAT  (this is a simple reflector for testing purpsoes)");
		System.out.println("These specs have ben collected from several places online, many versions of the enigma exist");
	}
	
	//translate takes an input character and runs it through the list of rotors and reflectors
	//it outputs the translated character and handles rotor rotation
	public static char translate(char in) {
		char ret = in;
		for(int i=0; i < rlist.size(); i++) {
			Switcher s = rlist.get(i);
			//System.out.println("Switcher "+i+"Read "+ret);
			ret = s.read(ret);
			//System.out.print("Out "+ret+"\n");
		}
		
		for(int i=rlist.size()-2; i >= 0; i--) {
			Switcher z = rlist.get(i);
			//System.out.println("Switcher "+i+"Read "+ret);
			ret = z.read(ret);
			//System.out.print("Out "+ret+"\n");
		}
		//System.out.println("Rotating... OLD");
		//printRotors();
		rlist.get(1).rotate(); //rotate the first rotor
		int i = 1;
		while(rlist.get(i).isNotch()) { //see if we have to rotate any more
			rlist.get(i+1).rotate();
			i++;
		}
		//System.out.println("........... NEW");
		//printRotors();
		return ret;
	}
	
	public static void printRotors() {
		for(Switcher s : rlist) {
			s.printRotor();
		}
	}
}

//the Switcher interface lets me handle the steps of the enigma encoding generically
//the plugboard, reflector, and rotors all have 3 unified methods
interface Switcher {
	public char read(char in); //read takes an input char and returns the corresponding output
	public boolean isNotch(); //isNotch returns true if the 'notch' char is in the window of a rotor
	public void rotate();     //rotate simulates the rotation of a rotor
	public void printRotor();
}

class PlugBoard implements Switcher {
	String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"; //default plugboard, does not change letters
	
	//the plugboard constructor takes as an input an even length string containing letters
	//to be connect in a digraphic pair
	public PlugBoard(String swaps) { //ex: new PlugBoard("AB") swaps A and B in translation
		char[] c;
		//this loop swaps the characters in the letters string according to user input
		for(int i=0; i<swaps.length(); i++) {
			c = letters.toCharArray();
			int first = letters.indexOf(swaps.charAt(i));
			int second = letters.indexOf(swaps.charAt(i+1));
			char temp = c[first];
			c[first] = c[second];
			c[second] = temp;
			letters = new String(c);
			i++;
		}
	}
	
	//read returns the corresponding char output given an input
	public char read(char in) {
		return letters.charAt("ABCDEFGHIJKLMNOPQRSTUVWXYZ".indexOf(in));
	}
	
	public boolean isNotch() {
		return false; //this is not a rotor so it never has a notch
	}
	
	public void rotate() {
		//this is not a rotor so it cannot rotate
	}
	
	public void printRotor() {
		System.out.print("Plugboard: ");
		System.out.println(letters);
	}
}

class Reflector implements Switcher {
	String letters;
	
	public Reflector(char type) {
		switch(type) {
			case 'B': letters = "YRUHQSLDPXNGOKMIEBFZCWVJAT";
				break;
			case 'C': letters = "FVPJIAOYEDRZXWGCTKUQSBNMHL";
				break;
			default: letters = "ZYXWVUTSRQPONMLKJIHGFEDCBA";//default reflector reverses input
		}
	}
	
	public char read(char in) {
		return letters.charAt("ABCDEFGHIJKLMNOPQRSTUVWXYZ".indexOf(in));
	}
	
	public boolean isNotch() {
		return false; //not a rotor, never a notch
	}
	
	public void rotate() {
		//I do nothing
	}
	
	public void printRotor() {
		System.out.print("Reflector: ");
		System.out.println(this.letters);
	}
}

class Rotor implements Switcher {
	String letters;
	char notch;
	static char currentPos;
	
	public Rotor(int rnum, char pos, char not) {
		switch(rnum) { //determine which rotor number we are referring to
			case 1: notch = not;
				letters = "EKMFLGDQVZNTOWYHXUSPAIBRCJ";
				break;
			case 2: notch = not;
				letters = "AJDKSIRUXBLHWTMCQGZNPYFVOE";
				break;
			case 3: notch = not;
				letters = "BDFHJLCPRTXVZNYEIWGAKMUSQO";
				break;
			case 4: notch = not;
				letters = "ESOVPZJAYQUIRHXLNFTGKDCMWB";
				break;
			case 5: notch = not;
				letters = "VZBRGITYUPSDNHLXAWMJQOFECK";
				break;
		}
		letters = setPos(pos);
	}
	
	public char read(char in) {
		char ret = letters.charAt(in-'A'); //hacky hack hack
		//System.out.println("Printing index "+(in-'A'));
		//rotate(); //be sure to rotate the current rotor

		return ret;
	}
	
	private String setPos(char pos) {
		int windowpos = -1;
		for(int i = 0; i<letters.length(); i++) {
			if (letters.charAt(i)==pos) {
				windowpos = i;
			}
		}
		
		String newlet;
		StringBuilder sb = new StringBuilder(26);
		
		for(int i = 0; i < 26; i++) {
			sb.append(letters.charAt(windowpos%26));
			windowpos++;
		}
		newlet = sb.toString();
		currentPos = pos;
		return newlet;
	}
	
	public char getPos() {
		return letters.charAt(currentPos);
	}
	
	public void rotate() {
		//setPos(letters.charAt((char)(currentPos+1)%26));
		StringBuilder sb = new StringBuilder(26);
		sb.append(letters.charAt(letters.length()-1));
		for(int i=0; i < letters.length()-1 ; i++) {
			sb.append(letters.charAt(i));
		}
		letters = sb.toString();
	}

	public boolean isNotch() {
			return (letters.charAt(0) == notch);
	}
	
	public void printRotor() {
		System.out.println(letters);
	}
}
