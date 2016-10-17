package lab_1;

import java.util.Scanner;

public class Calculator {
	//subclasses and structures

	
	//Constants & Control panel
	final static int	maxItemCount	=	1000;
	final static int	maxVarCount		=	30;
	final static double	minDouble		=	0.0000001;
	final static Boolean isDebugging	=	false;
	
	static Boolean haveExpression	=	false;
	static int itemCount = 0;
	static int expressionArray[][];
	static double coefficientArray[]; 
	
	
	
	//Utility Functions
	public static int charIndex(char c)
	{
		return (int)(c - 'a');
	}
	
	public static char indexChar(int i)
	{
		return (char)('a'+i);
	}
	
	public static Boolean isZero(double d)
	{
		if (Math.abs(d)<minDouble)
			return true;
		else
			return false;
	}
	
	public static void outputExpression(int[][] exp, double[] co)
	{
		//combine same items, if possible
			for (int i=0;i<itemCount-1;i++)
				{
					if (isZero(co[i]))			//skip the empty term
						continue;
					for  (int j=i+1;j<itemCount;j++)
					{
						Boolean sameFlag=true;
						for (int k=0;k<maxVarCount;k++)			//check if they are similar items
							if (exp[i][k]!=exp[j][k])
							{
								sameFlag=false;
								break;
							};
						if (sameFlag)							//combine the items and clean the latter one
						{
							co[i]+=co[j];
							co[j]=0;
							for (int k=0;k<maxVarCount;k++)
								exp[j][k]=0;
						}
					}
				};
				
				
				//do something, output the expression to see the result
				
				Boolean isFirstFlag=true;
				for (int i=0;i<itemCount;i++)
					if (!isZero(co[i]))
					{
						if(!isFirstFlag)
						{
							if (co[i]>0)
								System.out.print("+");
						} else isFirstFlag=false;
						//if (coefficientArray[i]<0)
						//	System.out.print("-");
						System.out.print(co[i]);
						
						for (int j=0;j<maxVarCount;j++)
							if (exp[i][j]!=0)
							{
								if (exp[i][j] == 1)	
								{
									System.out.print(indexChar(j));
								} else 
								{
									System.out.print(indexChar(j));
									System.out.print("^");
									System.out.print(exp[i][j]);
								}
							}
					}
				System.out.println();	
	}
	
	//Syntax Check
	public static Boolean isLegitExpression(String s)
	{
		Boolean illegalExpression=false;
		//add rules for illegal expression
		for (int i=0;i<s.length();i++)		//if contains illegal character
			if ("0123456789-+*^. abcdefghijklmnopqrstuvwxyz".indexOf(s.charAt(i)) == -1) 
			{
				illegalExpression=true;
				break;
			};
		
		
		
		if (illegalExpression) return false;
		if (isDebugging) System.out.println("DBG:possible expression");
		return true;
	}
	
	public static Boolean isLegitSimplifyCommand(String s)
	{
		for (int i=0;i<s.length();i++)		//if contains illegal character
			if ("!0123456789-=. abcdefghijklmnopqrstuvwxyz".indexOf(s.charAt(i)) == -1) 
				return false;
		if (!s.startsWith("!simplify")) return false;
		
		if (isDebugging) System.out.println("DBG:possible simplify command");
		return true;
	}
	
	public static Boolean isLegitDerivationCommand(String s)
	{
		if (s.length()!=6) return false;
		if (!s.startsWith("!d/d ")) return false;
		if ("abcdefghijklmnopqrstuvwxyz".indexOf(s.charAt(5))==-1) return false;
		if (isDebugging) System.out.println("DBG:possible derivation command");
		return true;
	}
	
	//Initialization
	public static void initialize(String s)
	{
		s = s.replace(" ", "");			//delete all spaces
		s = s.replace("-","+@");		//use @ to represent minus
		s = s.replace("*","");			//remove all *
		if (s.charAt(0)=='+')
			s=s.substring(1);
		
		if (isDebugging) System.out.println("DBG:replaced useless chars:"+s);
		
		String items[] = s.split("\\+");
		itemCount = items.length;
		
		if (isDebugging)
		{
			System.out.println("DBG:splitted tokens:");
			for (int i=0;i<itemCount;i++)
				System.out.println(items[i]+"#");
		}
		
		//data structure initializing
		expressionArray 	= new int[itemCount][maxVarCount];
		coefficientArray	= new double[itemCount];
		
		
		
		//parse each token into the array
		for (int i=0;i<itemCount;i++)
		{
			if (isDebugging) System.out.println("DBG:Processing token "+i);
			
			coefficientArray[i]=1;
			StringBuffer token = new StringBuffer(items[i]);
			StringBuffer cache = new StringBuffer();
			while (token.length() != 0)
			{
				//if (isDebugging) System.out.println(" DBG:another sub-token:"+token);
				cache.setLength(0);
				if (".0123456789@".indexOf(token.charAt(0)) != -1) //parse into number
				{
					while (".0123456789@".indexOf(token.charAt(0)) != -1)
					{
						cache.append(token.charAt(0));
						token.deleteCharAt(0);
						if (token.length()==0) break;
					};
					if (cache.charAt(0) == '@')
						cache.setCharAt(0, '-');
					if (isDebugging) System.out.println(" DBG:sub-token:"+cache );
					coefficientArray[i] *= Double.parseDouble(cache.toString());
						
					
				} else	if("abcdefghijklmnopqrstuvwxyz".indexOf(token.charAt(0)) != -1)//parse into variable
				{
					if (token.length()>1) //possible power
					{
						if (token.charAt(1)=='^')
						{
							int varTemp = charIndex(token.charAt(0));
							if (isDebugging) System.out.print(" DBG:sub-token:"+token.substring(0, 2) );
							token.deleteCharAt(0);
							token.deleteCharAt(0);
							
							if (token.length()==0)
							{
								haveExpression=false;
								System.out.println("Empty after ^symbol, please check");
								return;
							}
							
							if ("0123456789".indexOf(token.charAt(0)) == -1) //deal with the case ^ followed by not a number
							{
								haveExpression=false;
								System.out.println("Illegal Character after ^symbol, please check");
								return;
							};
							
							while ("0123456789".indexOf(token.charAt(0)) != -1)
							{
								cache.append(token.charAt(0));
								token.deleteCharAt(0);
								if (token.length()==0) break;
							};
							if (isDebugging) System.out.println(cache );
							expressionArray[i][varTemp]+= Integer.parseInt(cache.toString());
						} else
						{
							expressionArray[i][charIndex(token.charAt(0))]++;
							if (isDebugging) System.out.println(" DBG:sub-token:"+ token.charAt(0) );
							token.deleteCharAt(0);
						}
						
					} else		//there's only one variable in the expression
					{
						expressionArray[i][charIndex(token.charAt(0))]++;
						if (isDebugging) System.out.println(" DBG:sub-token:"+ token.charAt(0) );
						token.deleteCharAt(0);
					}
				} else
				{
					//Illegal character detected, parse failed
					haveExpression=false;
					System.out.println("Illegal Character Detected, please retry");
					return;
				};
				
			}
		}
		//combine same items, if possible
		
		outputExpression(expressionArray,coefficientArray);
		
	}
	//commands
	public static void simplify(String s)
	{
		//copy the expression for simplification
		int[][]		tempExArray = new int[expressionArray.length][];
		double[]	tempCoArray = coefficientArray.clone();
		for (int i=0;i<expressionArray.length;i++)
			tempExArray[i]=expressionArray[i].clone();
		//parse the command and do the simplification
		s=s.substring(9);
		while (s.startsWith(" ")) s=s.substring(1);
		while (s.endsWith(" ")) s=s.substring(0, s.length()-1);
		while (s.contains("  ")) s.replace("  ", " ");
		
		//deals with empty commands here:
		if (s.length()==0)
		{
			outputExpression(tempExArray,tempCoArray);
			return;
		}
		
		String[]	detVariables	= s.split(" "); 
		String[][]	assignments		= new String[detVariables.length][];
		
		Boolean parseError = false;
		for (int i=0;i<detVariables.length;i++)
		{
			double value;
		
			assignments[i]=detVariables[i].split("=");
			if (assignments[i].length!=2) parseError=true;
			if ((assignments[i][0].length()!=1) ||( !"abcdefghijklmnopqrstuvwxyz".contains(assignments[i][0])))
			{
				parseError=true;
				System.out.println("Illegal Variable for assignment");
				return;
			};
			
			try
			{
				value=Double.parseDouble(assignments[i][1]);
			} catch (NumberFormatException e)
			{
				System.out.println("Illegal assignment: Not a number.");
				parseError=true;
				return;
			};
			
			if (parseError) break;
				
		}
		if(parseError)
		{
			System.out.println("Illegal Simplification Command.Please check.");
			return;
		}
		
		for (int i=0;i<detVariables.length-1;i++)
			for (int j=i+1;j<detVariables.length;j++)
				if (assignments[i][0]==assignments[j][0])
				{
					System.out.println("Duplicated Assignment, please check.");
					return;
				};
		
		for (int i=0;i<detVariables.length;i++)
		{
			Boolean existVariable = false;
			for (int j=0;j<itemCount;j++)
				if (!isZero(tempCoArray[j]))
					if (tempExArray[j][charIndex(assignments[i][0].charAt(0))]!=0)
					{
						existVariable = true;
						tempCoArray[j]*=Math.pow(Double.parseDouble(assignments[i][1]), tempExArray[j][charIndex(assignments[i][0].charAt(0))]);
						tempExArray[j][charIndex(assignments[i][0].charAt(0))]=0;
					}
			if (!existVariable)
			{
				System.out.println("Assignment to non exist variable, please check.");
				return;
			}
		};
				
			
		//output the result
		
		outputExpression(tempExArray,tempCoArray);
		
		
		return;
	}
	
	public static void derivate(char var)
	{
		//copy the expression for simplification
		int[][]		tempExArray = new int[expressionArray.length][];
		double[]	tempCoArray = coefficientArray.clone();
		for (int i=0;i<expressionArray.length;i++)
			tempExArray[i]=expressionArray[i].clone();		
		//do the derivation
		Boolean varExist=false;
		for (int i=0;i<itemCount;i++)
			if (!isZero(tempCoArray[i]))
				if (tempExArray[i][charIndex(var)]==0) //this var does not exist in this item, delete it
				{
					tempCoArray[i]=0;
					//TODO: may be clean the table here or not
				} else //this var exist in this item
				{
					tempCoArray[i]*=tempExArray[i][charIndex(var)];
					tempExArray[i][charIndex(var)]--;
					varExist=true;
				};
				
		if(!varExist)
		{
			System.out.println("Variable does not exist, please check.");
			return;
		}
		//output the expression processed
		outputExpression(tempExArray,tempCoArray);
		return;
	}
	
	
	public static void main(String args[])
	{
		
		String	inputString;
		
		System.out.println("Expression Calculator.");
		System.out.println("Please input an legitimate expression first.");
		
		
		Scanner inputSource	=	new Scanner(System.in);
		
		while(true)
		{
			System.out.print(">>");
			inputString = inputSource.nextLine();
			
			if (inputString == "exit")				//exit the program
				break;
			
			if (inputString.length()==0) continue;	//continue the loop if the input is empty
			
			if (!haveExpression)					//first run, without an expression
			{
				if (isLegitExpression(inputString))
				{	
					haveExpression = true;
					initialize(inputString);
				} else
				{
					System.out.println("This is not a good expression, please retry.");
					continue;
				}
				
			} else
			{
				if (inputString.contains("!"))	//possible command
				{
					if (isLegitSimplifyCommand(inputString))
						simplify(inputString);
					else if (isLegitDerivationCommand(inputString))
						derivate(inputString.charAt(5));
					else System.out.println("This command doesn't exist, please check.");
				} else if (isLegitExpression(inputString))	//inputed a new expression, update
				{
					haveExpression = true;
					initialize(inputString);
				} else
				{
					System.out.println("This is not a good expression or command, please retry.");
					continue;
				}
					
			};
		}
		
		
		
		//post run process
		inputSource.close();
	}
	//This is some added comment before commit
	
	
}
