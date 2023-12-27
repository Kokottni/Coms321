import java.io.*;
import java.util.HashMap;

public class decompiler{
	//Hash map to store the conditions
	public static HashMap<Integer, String> conditions = new HashMap<>();

	//Hash map to store the opcodes
	public static HashMap<Integer, String> opcodes = new HashMap<>();

	public static int instCount = 1;

	public static void main(String[] args){
		//Assign everything in the beginning just to have easy conversion later
		conditions.put(0x0, "EQ");
		conditions.put(0x1, "NE");
		conditions.put(0x2, "HS");
		conditions.put(0x3, "LO");
		conditions.put(0x4, "MI");
		conditions.put(0x5, "PL");
		conditions.put(0x6, "VS");
		conditions.put(0x8, "VC");
		conditions.put(0x9, "LS");
		conditions.put(0xa, "GE");
		conditions.put(0xb, "LT");
		conditions.put(0xc, "GT");
		conditions.put(0xd, "LE");

		//Give the byte operations ahead of time to allow for easy conversion
		opcodes.put(0b10001011000, "ADD");
        opcodes.put(0b1001000100, "ADDI");
        opcodes.put(0b10001010000, "AND");
        opcodes.put(0b1001001000, "ANDI");
        opcodes.put(0b000101, "B");
        opcodes.put(0b01010100, "B.");
        opcodes.put(0b100101, "BL");
        opcodes.put(0b11010110000, "BR");
        opcodes.put(0b10110101, "CBNZ");
        opcodes.put(0b10110100, "CBZ");
        opcodes.put(0b11001010000, "EOR");
        opcodes.put(0b1101001000, "EORI");
        opcodes.put(0b11111000010, "LDUR");
        opcodes.put(0b11010011011, "LSL");
        opcodes.put(0b11010011010, "LSR");
        opcodes.put(0b10101010000, "ORR");
        opcodes.put(0b1011001000, "ORRI");
        opcodes.put(0b11111000000, "STUR");
        opcodes.put(0b11001011000, "SUB");
        opcodes.put(0b1101000100, "SUBI");
        opcodes.put(0b1111000100, "SUBIS");
        opcodes.put(0b11101011000, "SUBS");
        opcodes.put(0b10011011000, "MUL");
        opcodes.put(0b11111111101, "PRNT");
        opcodes.put(0b11111111100, "PRNL");
        opcodes.put(0b11111111110, "DUMP");
        opcodes.put(0b11111111111, "HALT");

		try{
			//read everything in
			File insFile = new File(args[0]);
			DataInputStream inputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(insFile)));

			while(inputStream.available() >= 4){
				//All operations here use bit manipulation
				byte first = inputStream.readByte();
				int firstByte = (first & 0xFF) << 24;
				byte second = inputStream.readByte();
				int secondByte = (second & 0xFF) << 16;
				byte third = inputStream.readByte();
				int thirdByte = (third & 0xFF) << 8;
				byte fourth = inputStream.readByte();
				int fourthByte = (fourth & 0xFF);

				int currInstruction = firstByte + secondByte + thirdByte + fourthByte;

				//while theres an instruction available disassemble it and print it out
				dissassemble(currInstruction);
			}
		}catch(IOException error){
			//print an error if it doesnt read properly
			System.out.println(error);
		}
	}

	public static void dissassemble(int instruction){
		String instString = "";

		//Make all opcodes from the same instruction and compare, this will always work as a specific opcode will be drawn out
		int rdOpcode = (instruction >> 21) & 0x7FF;
		int iOpcode = (instruction >> 22) & 0x3FF;
		int cbOpcode = (instruction >> 24) & 0xFF;
		int bOpcode = (instruction >> 26) & 0x3F;

		//Start with the possible rd opcodes 
		if(opcodes.containsKey(rdOpcode)){
			instString += opcodes.get(rdOpcode);
			//ADD, AND, EOR, ORR, SUB, SUBS, and finally NULL
            if((rdOpcode == 0b10001011000) || (rdOpcode == 0b10001010000) || (rdOpcode == 0b11001010000) || (rdOpcode == 0b10101010000) || (rdOpcode == 0b11001011000) || (rdOpcode == 0b11101011000) || (rdOpcode == 0b10011011000)){
				//Find the rd in the instruction and find matching value (Same sort of idea happens in each nested if statement)
				int rd = instruction & 0x1F;
				String rdString = "X" + rd;
				if(rd == 28){
					rdString = "SP";
				}else if (rd == 29){
					rdString = "FP";
				}else if(rd == 30){
					rdString = "LR";
				}else if(rd == 31){
					rdString = "XZR";
				}

				int rn = instruction >> 5 & 0x1F;
				String rnString = "X" + rn;
				if(rn == 28){
					rnString = "SP";
				}else if (rn == 29){
					rnString = "FP";
				}else if(rn == 30){
					rnString = "LR";
				}else if(rn == 31){
					rnString = "XZR";
				}

				int rm = instruction >> 16 & 0x1F;
				String rmString = "X" + rm;
				if(rm == 28){
					rmString = "SP";
				}else if (rm == 29){
					rmString = "FP";
				}else if(rm == 30){
					rmString = "LR";
				}else if(rm == 31){
					rmString = "XZR";
				}

				instString += " " + rdString + ", " + rnString + ", " + rmString;

			//BR
			}else if(rdOpcode == 0b11010110000){
				int rn = instruction >> 5 & 0x1F;
				String rnString = "X" + rn;
				if(rn == 28){
					rnString = "SP";
				}else if(rn == 29){
					rnString = "FP";
				}else if(rn == 30){
					rnString = "LR";
				}else if(rn == 31){
					rnString = "XZR";
				}

				instString += " " + rnString;
			
			//LSL or LSR
			}else if((rdOpcode == 0b11010011011) || (rdOpcode == 0b11010011010)) {
				int rd = instruction & 0x1F;
				String rdString = "X" + rd;
				if(rd == 28){
					rdString = "SP";
				}else if(rd == 29){
					rdString = "FP";
				}else if(rd == 30){
					rdString = "LR";
				}else if(rd == 31){
					rdString = "XZR";
				}

				int rn = instruction >> 5 & 0x1F;
				String rnString = "X" + rn;
				if(rn == 28){
					rnString = "SP";
				}else if(rn == 29){
					rnString = "FP";
				}else if(rn == 30){
					rnString = "LR";
				}else if(rn == 31){
					rnString = "XZR";
				}

				int shamt = instruction >> 10 & 0x3F;
				if(shamt >= 32){
					shamt -= 64;
				}

				instString += " " + rdString + ", " + rnString + ", #" + shamt;

			//PRNT
			}else if(rdOpcode == 0b11111111101){
				int rd = instruction & 0x1F;
				String rdString = "X" + rd;
				if(rd == 28){
					rdString = "SP";
				}else if(rd == 29){
					rdString = "FP";
				}else if(rd == 30){
					rdString = "LR";
				}else if(rd == 31){
					rdString = "XZR";
				}

				instString += " " + rdString;

			//LDUR or STUR
			}else if((rdOpcode == 0b11111000010) || (rdOpcode == 0b11111000000)){
				
				int rt = instruction & 0x1F;
				String rtString = "X" + rt;
				if(rt == 28){
					rtString = "SP";
				}else if(rt == 29){
					rtString = "FP";
				}else if(rt == 30){
					rtString = "LR";
				}else if(rt == 31){
					rtString = "XZR";
				}

				int rn = instruction >> 5 & 0x1F;
				String rnString = "X" + rn;
				if(rn == 28){
					rnString = "SP";
				}else if(rn == 29){
					rnString = "FP";
				}else if(rn == 30){
					rnString = "LR";
				}else if(rn == 31){
					rnString = "XZR";
				}

				int dtaddr = instruction >> 32 & 0x1FF;

				if(dtaddr >= 256){
					dtaddr -= 512;
				}

				instString += " " + rtString + ", [" + rnString + ", #" + dtaddr + "]";
			}
		
		//Now I type opcodes
		}else if(opcodes.containsKey(iOpcode)){
			instString += opcodes.get(iOpcode);

			int rd = instruction & 0x1F;
			String rdString = "X" + rd;
			if(rd == 28){
				rdString = "SP";
			}else if(rd == 29){
				rdString = "FP";
			}else if(rd == 30){
				rdString = "LR";
			}else if(rd == 31){
				rdString = "XZR";
			}

			int rn = instruction >> 5 & 0x1F;
			String rnString = "X" + rn;
			if(rn == 28){
				rnString = "SP";
			}else if(rn == 29){
				rnString = "FP";
			}else if(rn == 30){
				rnString = "LR";
			}else if(rn == 31){
				rnString = "XZR";
			}

			int aluimmediate = instruction >> 10 & 0xFFF;
			if(aluimmediate >= 2048){
				aluimmediate -= 4096;
			}
			instString += " " + rdString + ", " + rnString + ", #" + aluimmediate;
		
		//This is for cb type opcodes
		}else if(opcodes.containsKey(cbOpcode)){
			instString += opcodes.get(cbOpcode);

			if(cbOpcode == 0b01010100){
				int condition = instruction & 0x1F;
				String conditionString = conditions.get(condition);
				instString += conditionString;
			}

			int baddr = instruction >> 5 & 0x7FFFF;

			//finds the location of the line that needs to be jumped to
			if(baddr >= 262144){
				baddr -= 524288;
			}

			instString += " L" + (instCount + baddr);

		//Finally b type opcodes
		}else if(opcodes.containsKey(bOpcode)){
			int baddr = instruction & 0x3FFFFFF;
			if(baddr >= 33554432){
				baddr -= 67108864;
			}
			instString += opcodes.get(bOpcode) + " L" + (instCount + baddr);
		}else{
			System.out.println("Error opcode not found");
		}
		//Gives the line currently being r
		System.out.println(instString);
		++instCount;
	}
}
