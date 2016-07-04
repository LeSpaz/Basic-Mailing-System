import java.util.Scanner;
import java.io.*;

public class A2
{
	
//----------------------------------------------------------------------------------------CLASSES-------------------------------------------------------------------------------------------
	//Letter class implementation
	static class Letter
	{
		Letter (String starting, String recipient, String destination, String owner, int transits)
		{
			start = starting;
			recip = recipient;
			dest = destination;
			own = owner;
			days = transits;
		}
		String start;
		String recip;
		String dest;
		String own;
		int days;
	}
	
	//Package class implementation
	static class Package
	{
		Package(String starting, String recipient, String destination, int moneys, int size, int transits)
		{
			start = starting;
			recip = recipient;
			dest = destination;
			postage = moneys;
			length = size;
			days = transits;
		}
		String start;
		String recip;
		String dest;
		int postage;
		int length;
		int days;
	}
	
	//Wrapper class for letters on shelves
	static class lPost
	{
		lPost(Letter mail, boolean vanyes, boolean pickme, int date)
		{
			letter = mail;
			for_van = vanyes;
			waiting = pickme;
			due = date;
		}
		Letter letter;
		boolean for_van;
		boolean waiting;
		int due;
	}
	
	//Wrapper class for packages on shelves
	static class pPost
	{
		pPost(Package mail, boolean vanyes, boolean pickme, int date)
		{
			parcel = mail;
			for_van = vanyes;
			waiting = pickme;
			due = date;
		}
		Package parcel;
		boolean for_van;
		boolean waiting;
		int due;
	}
	
	//Implementation for shelf, contains both letters and packages
	static class Shelf
	{
		Shelf(lPost mail, pPost box, boolean alive, boolean notpackage)
		{
			letter = mail;
			parcel = box;
			exists = alive;
			isletter = notpackage;
		}
		lPost letter;
		pPost parcel;
		boolean exists;
		boolean isletter;
	}
	
	//Implementation for Office
	static class Office
	{
		Office ( String nom, int travel, int postage, int storage, int persuasion, int maxsize, Shelf[] rack)
		{
			name = nom;
			transit = travel;
			cost = postage;
			cap = storage;
			pers = persuasion;
			maxlen = maxsize;
			shelf = rack;
		}
		String name;
		int transit;
		int cost;
		int cap;
		int pers;
		int maxlen;
		Shelf[] shelf;
	}

//-----------------------------------------------------------------------------------------------METHODS---------------------------------------------------------------------------------	

	//Gets command number
	public static int commnum() throws FileNotFoundException, IOException
	{
		File iscommandhere = new File("commands.txt");
		boolean exists = iscommandhere.exists();
		if (!exists)
		{
			System.out.println("No commands file");//REMOVE THIS
		}	
		BufferedReader br = new BufferedReader ( new InputStreamReader ( new FileInputStream(iscommandhere)));
		String line = null;
		line = br.readLine();
		int numOfComm = Integer.parseInt(line);
		return numOfComm;
	}
	//Method for Criminal Check
	public static String[] crimCheck() throws FileNotFoundException, IOException
	{	
		File iscrimhere = new File("wanted.txt");
		boolean exists = iscrimhere.exists();
		if (!exists)
		{
			System.out.println("No wanted file");//REMOVE THIS
		}	
		FileReader crim = new FileReader("wanted.txt");
		BufferedReader reader = new BufferedReader (crim);
		String nber = reader.readLine();
		int number = Integer.parseInt(nber);
		String[] list = new String[number];
		nber = reader.readLine();

		for (int i = 0; i < number; i++)
		{
			list[i] = nber;
			nber = reader.readLine();		
		}
		return list;	
		
	}
	
	// Checks if a name is a criminal's
	public static boolean iscrim(String name, String[] crimarray) throws FileNotFoundException, IOException
	{
		for (int i =0; i< crimarray.length; i++)
		{
			if (name.equals(crimarray[i]))
			{
				return true;
			}
		}
		return false;
	}
	
	//Puts offices from txt into array
	public static Office[] officemaker(int numOfComm) throws FileNotFoundException, IOException
	{
		File isofficehere = new File("offices.txt");
		boolean exists = isofficehere.exists();
		if (!exists)
		{
			System.out.println("No office file");//REMOVE THIS
		}	
		Shelf[] shelf = null;
		BufferedReader br = new BufferedReader ( new InputStreamReader ( new FileInputStream(isofficehere)));
		String line = null;
		line = br.readLine();
		int number = Integer.parseInt(line);
		Office[] offices = new Office[number];
		for (int i = 0; i < number; i++)
		{
			line = br.readLine();
			String[] tokens = line.split("\\s+");
			int travel = Integer.parseInt(tokens[1]);
			int postage = Integer.parseInt(tokens[2]);
			int storage = Integer.parseInt(tokens[3]);
			int persuasion = Integer.parseInt(tokens[4]);
			int maxsize = Integer.parseInt(tokens[5]);
			shelf = new Shelf[numOfComm];
			offices[i] = new Office(tokens[0],travel, postage, storage, persuasion, maxsize, shelf);			
		}
		return offices;
	}
	
	//Checks and creates letters and then converts them to lPosts
	public static lPost letterMaker(String line, String[] crimarray, Office[]officearray, String[] maslog, String[] offices, int day) throws FileNotFoundException, IOException
	{
		int home = 0;
		int away = 0;
		boolean legit1 = false;
		boolean legit2 = false;		
		String[] tokens = line.split(" ");
		Letter mail = new Letter(tokens[1], tokens[2], tokens[3], tokens[4], 0);
		for(int i =0; i< officearray.length; i++)
		{
			if(tokens[1].equals(officearray[i].name))
			{
				legit1 = true;
				home =i;				
			}
			if(tokens[3].equals(officearray[i].name))
			{
				legit2 = true;
				away =i;
			}
		}
		offices[home] += "- New LETTER -\nSource: "+tokens[1]+"\nDestination: "+tokens[3]+"\n";
		if (!legit1 || !legit2 || iscrim(tokens[2], crimarray) || officearray[home].cap == 0)
		{	
			offices[home] += "- Rejected LETTER -\nSource: "+officearray[home].name+"\n";
			maslog[0] += "- Rejected LETTER -\nSource: "+officearray[home].name+"\n";
			return null;
		}
		officearray[home].cap --;
		int dueday = day + 1 + officearray[home].transit;
		lPost post = new lPost(mail, true, false, dueday);
		offices[home] += "- Accepted LETTER -\nDestination: "+officearray[away].name+"\n";
		return post;
	}

	//Checks and creates letters and then converts them to pPosts
	public static pPost packageMaker (String line, String[] crimarray, Office[]officearray, String[] maslog, String[] offices, int day) throws FileNotFoundException, IOException
	{
		int home = 0;
		int away = 0;
		boolean legit1 = false;
		boolean legit2 = false;	
		String[] tokens = line.split(" ");
		Package mail = new Package(tokens[1], tokens[2], tokens[3], Integer.parseInt(tokens[4]),Integer.parseInt(tokens[5]),0 );
		for(int i =0; i< officearray.length; i++)
		{
			if(tokens[1].equals(officearray[i].name))
			{
				legit1 = true;
				home =i;				
			}
			if(tokens[3].equals(officearray[i].name))
			{
				legit2 = true;
				away =i;
			}
		}
		offices[home] += "- New PACKAGE -\nSource: "+tokens[1]+"\nDestination: "+tokens[3]+"\n";
		if ((legit1 && legit2) && !iscrim(tokens[2], crimarray) && officearray[home].cap > 0)
		{
			if (officearray[home].cost <= mail.postage)
			{
				if((mail.length <= officearray[home].maxlen)&&(mail.length <= officearray[away].maxlen))
				{
					int dueday = day + 1 + officearray[home].transit;
					pPost post = new pPost(mail, true, false, dueday);
					offices[home] += "- Accepted PACKAGE -\nDestination: "+officearray[away].name+"\n";
					officearray[home].cap --;
					return post;
				}
				else if(mail.postage >= (officearray[home].cost + officearray[home].pers))
				{
					int dueday = day + 1 + officearray[home].transit;
					pPost post = new pPost(mail, true, false, dueday);
					offices[home] += "- Accepted PACKAGE -\nDestination: "+officearray[away].name+"\n";
					maslog[0] += "- Something funny going on... -\nWhere did that extra money at "+officearray[home].name+" come from?\n";
					officearray[home].cap --;
					return post;
				}
			}
		}
		
		offices[home] += "- Rejected PACKAGE-\nSource: "+officearray[home].name+"\n";
		maslog[0] += "- Rejected PACKAGE -\nSource: "+officearray[home].name+"\n";
		return null;
	}
	//Loads a letter to the respective shelf of the office
	public static void loadShelf (lPost mail, pPost parcel, Office[] officearray) throws FileNotFoundException, IOException
	{
		Shelf rack = null;
		String start = "";
		int home = 0;
		int i = 0;
		if (parcel == null)
		{
			rack = new Shelf(mail, null, true,true);
			start = mail.letter.start;
		}
		else
		{
			rack = new Shelf(null, parcel, true,false);
			start = parcel.parcel.start;
		}
		if(!start.equals(officearray[i].name))
		{
			home++;
			i++;
		}
		for (i = 0; i<officearray[home].shelf.length;i++)
		{
			if (officearray[home].shelf[i]== null)
			{
				officearray[home].shelf[i] = rack;
				return;
			}
		}
	}
	
	//Selects all the items to get in the Van
	public static void emptyShelves(Shelf[] Van,Office[] officearray, String[] offices) throws FileNotFoundException, IOException
	{
		for (int i =0; i < officearray.length;i++)
		{
			for(int j = 0; j < officearray[i].shelf.length;j++)
			{
				if(officearray[i].shelf[j]!=null)	
				{
					if(officearray[i].shelf[j].exists)
					{
						if(officearray[i].shelf[j].isletter)
						{
							if(officearray[i].shelf[j].letter.for_van)
							{
									officearray[i].shelf[j].letter.for_van = false;
									officearray[i].shelf[j].letter.waiting = true;
									officearray[i].cap++;
									loadVan(Van,officearray[i].shelf[j].letter,officearray[i].shelf[j].parcel, officearray[i].shelf[j].exists, officearray[i].shelf[j].isletter);
									officearray[i].shelf[j]=null;
							}
						}
						else if(officearray[i].shelf[j].parcel.for_van)
						{
							officearray[i].shelf[j].parcel.for_van = false;
							officearray[i].shelf[j].parcel.waiting = true;
							officearray[i].cap++;
							loadVan(Van,officearray[i].shelf[j].letter,officearray[i].shelf[j].parcel, officearray[i].shelf[j].exists, officearray[i].shelf[j].isletter);
							officearray[i].shelf[j]=null;	
						}				
						offices[i] += "- Standard transit departure -\n";
					}
				}
			}
		}
		
	}
	
	//Dumps ready objects into the vans
	public static void loadVan(Shelf[] Van, lPost a, pPost b, boolean c, boolean d) throws FileNotFoundException, IOException
	{
		Shelf item = new Shelf(a,b,c,d);
		for (int i =0; i<Van.length;i++)
		{
			if( Van[i]==null)
			{
				Van[i] = item;
				return;
			}
		}
	}
	
	//Goes through all the Shelves/ Van and adds a day to all the existing 
	public static void updateDays(Office[] officearray, Shelf[] Van)
	{
		for (int i =0; i < officearray.length;i++)
		{
			for(int j = 0; j < officearray[i].shelf.length;j++)
			{
				if(officearray[i].shelf[j]!=null)	
				{
					if(officearray[i].shelf[j].isletter)
					{
						officearray[i].shelf[j].letter.letter.days++;
					}
					else
					{
						officearray[i].shelf[j].parcel.parcel.days++;
					}
				}
			}
		}
		for(int j = 0; j < Van.length;j++)
		{
			if(Van[j]!=null)	
			{
				if(Van[j].isletter)
				{
					Van[j].letter.letter.days++;
				}
				else
				{
					Van[j].parcel.parcel.days++;
				}
			}
		}
	}
	
	//Takes any parcels from the van and delivers them to their respective offices
	public static void vanDump(Office[] officearray,Shelf[] Van,int day, String[] offices, String[] maslog)
	{
		for(int i =0; i< Van.length;i++)
		{
			if(Van[i]!=null)
			{
				if(Van[i].isletter)
				{	
					if(Van[i].letter.due==day)
					{
						deliveryLetter(officearray, Van[i], offices, maslog);
						Van[i].exists = false;
						Van[i] = null;
					}
				}
				else
				{
					if(Van[i].parcel.due==day)
					{
						deliveryPackage(officearray, Van[i], offices, maslog);
						Van[i].exists = false;
						Van[i] = null;
					}
				}
			}
		}
	}
	
	//Takes letters from van in the morning and puts them in the office shelf
	public static void deliveryLetter(Office[] officearray, Shelf item, String[] offices, String[] maslog)
	{
		for(int i = 0; i<officearray.length;i++)
		{
			if(item.letter.letter.dest.equals(officearray[i].name))
			{	
				offices[i] += "- Standard Transit arrival -\n";
				
				if(officearray[i].cap > 0)
				{	
					for(int j = 0; j <officearray[i].shelf.length;j++)
					{
						if(officearray[i].shelf[j]==null)
						{
							officearray[i].shelf[j] = item;
							officearray[i].cap--;
							return;
						}
					}
				}
				else
				{
					offices[i] += "- Incinerated LETTER -\nDestroyed at: "+officearray[i].name+"\n";
					maslog[0] += "- Incinerated LETTER -\nDestroyed at: "+officearray[i].name+"\n";
					return;
				}
				
			}
		}
	}
	
	//Takes packages from van in the morning and puts them in the office shelf
	public static void deliveryPackage(Office[] officearray, Shelf item, String[] offices, String[] maslog)
	{
		for(int i = 0; i<officearray.length;i++)
		{
			if(item.parcel.parcel.dest.equals(officearray[i].name))
			{
				offices[i] += "- Standard transit arrival -\n";
				if(item.parcel.parcel.length < officearray[i].maxlen)
				{
					if(officearray[i].cap > 0)
					{
						for(int j = 0; j <officearray[i].shelf.length;j++)
						{
							if(officearray[i].shelf[j]==null)
							{
								officearray[i].shelf[j] = item;
								officearray[i].cap--;
								return;
							}
						}
					}
					else
					{
						offices[i] += "- Incinerated PACKAGE -\nDestroyed at: "+officearray[i].name+"\n";
						maslog[0] += "- Incinerated PACKAGE -\nDestroyed at: "+officearray[i].name+"\n";
						return;
					}
				}
				else
				{
					offices[i] += "- Incinerated PACKAGE -\nDestroyed at: "+officearray[i].name+"\n";
					maslog[0] += "- Incinerated PACKAGE -\nDestroyed at: "+officearray[i].name+"\n";
					return;
				}
			}
		}
	}
	
	//Checks who picks it up and moves the package
	public static void pickUp(String line,String[] crimarray,Office[] officearray,String[] offices,String[] maslog, int day) throws FileNotFoundException, IOException
	{
		int home = 0;
		int days = 0;
		String[] tokens = line.split(" ");
		for (int i = 0; i < officearray.length; i++)
		{
			if (tokens[1].equals(officearray[i].name))
			{
				home = i;
			}
		}
		if(iscrim(tokens[2], crimarray))
		{
			maslog[1]+= "Good News!!!!!1! The famous masked criminal "+ tokens[2] + " was finally caught and \narrested at a local post office. He/She was arrested on the charges of mass genocide,\nseveral incidents of vandalism and being part of the illuminati and\nthe reptilian brotherhood\n";
			return;
		}
		for (int i=0;i<officearray[home].shelf.length;i++)
		{
			if(officearray[home].shelf[i] != null)
			{
				if (officearray[home].shelf[i].isletter)
				{
					if(officearray[home].shelf[i].letter.waiting)
					{
						if(officearray[home].shelf[i].letter.letter.recip.equals(tokens[2]))
						{	
							days = officearray[home].shelf[i].letter.letter.days+1;
							offices[home] += "- Delivery Process complete -\nDelivery took "+ days +" days.\n";
							officearray[home].shelf[i] = null;
						}
					}
				}
				else if(officearray[home].shelf[i].parcel.waiting)
				{
					if(officearray[home].shelf[i].parcel.parcel.recip.equals(tokens[2]))
					{	
						days = officearray[home].shelf[i].parcel.parcel.days+1;
						officearray[home].shelf[i] = null;
						offices[home] += "- Delivery Process complete -\nDelivery took "+ days +" days.\n";
					}
				}
			}
		}
	}
	
	//Takes any old mail and either returns it or destroys it
	public static void garbageMan(Office[] officearray, String[] offices, String[] maslog, String[] crimarray, int day) throws FileNotFoundException, IOException
	{
		String ret = "";
		lPost re = null;
		for (int i = 0; i < officearray.length; i++)
		{
			for (int j =0; j < officearray[i].shelf.length; j++)
			{
				if(officearray[i].shelf[j] != null)
				{
					if (officearray[i].shelf[j].isletter)
					{
						if(officearray[i].shelf[j].letter.letter.days-1> 13)
						{
							if (!(officearray[i].shelf[j].letter.letter.own.equals("NONE")))
							{
								re = new lPost(officearray[i].shelf[j].letter.letter,true, false, day+officearray[i].transit);
								officearray[i].shelf[j] = null;
								re.due = day + officearray[i].transit+1;
								ret = re.letter.dest;
								re.letter.dest = re.letter.start;
								re.letter.start= ret;
								re.letter.recip = re.letter.own;
								re.letter.own = "NONE";
								re.letter.days = 0;
								offices[i] += "- New LETTER -\nSource: "+re.letter.start+"\nDestination: "+re.letter.dest+"\n- Accepted LETTER -\nDestination: "+re.letter.dest+"\n";
								loadShelf(re,null, officearray);
							}
							else
							{
								officearray[i].shelf[j] = null;
								offices[i] += "- Incinerated LETTER -\nDestroyed at: "+officearray[i].name+"\n";
								maslog[0] += "- Incinerated LETTER -\nDestroyed at: "+officearray[i].name+"\n";
							}
						}
					}
					else if (officearray[i].shelf[j].parcel.parcel.days-1> 13)
					{
						officearray[i].shelf[j] = null;
						offices[i] += "- Incinerated PACKAGE -\nDestroyed at: "+officearray[i].name+"\n";
						maslog[0] += "- Incinerated PACKAGE -\nDestroyed at: "+officearray[i].name+"\n";
					}
				}
			}
		}
	}
	
	//Reads each line and calls all functions
	public static void readCommands(String[] crimarray, Office[] officearray) throws FileNotFoundException, IOException
	{ 
		int day = 1;
		File iscommandhere = new File("commands.txt");
		boolean exists = iscommandhere.exists();
		if (!exists)
		{
			System.out.println("No commands file");//REMOVE THIS
		}	
		String []maslog = new String[2]; //Put the two string files into an array cuz they don't pass to other functions too well
		for (int j =0;j < 2;j++)
		{
			maslog[j] = "";
		}
		String[] offices = new String[officearray.length];
		for (int j =0;j < officearray.length;j++)
		{
			offices[j] = "";
		}
		BufferedReader br = new BufferedReader ( new InputStreamReader ( new FileInputStream(iscommandhere)));
		lPost lttr = null;
		pPost pckg = null;
		String line = null;
		line = br.readLine();
		int numOfComm = Integer.parseInt(line);
		Shelf[] Van = new Shelf[numOfComm];
		for(int i=0;i<numOfComm;i++)
		{
			line = br.readLine();
			if (line==null)
				break;
			String[] tokens = line.split(" ");
			if(tokens[0].equals("LETTER"))
			{
				lttr = letterMaker(line, crimarray, officearray, maslog, offices, day);
				if(lttr!=null)
					loadShelf(lttr,null, officearray);
			}	
			else if (tokens[0].equals("PACKAGE"))
			{
				pckg = packageMaker(line, crimarray, officearray, maslog, offices, day);
				if(pckg!=null)
				loadShelf(null,pckg, officearray);
			}
			else if (tokens[0].equals("PICKUP"))
			{
				pickUp(line, crimarray, officearray, offices, maslog, day);
			}
			else if (tokens[0].equals("DAY"))
			{
				emptyShelves(Van,officearray, offices);
				maslog[0] += "- - DAY "+day+" OVER - -\n";	
				for(int j=0; j < officearray.length; j++)
				{
					offices[j] +="- - DAY "+day+" OVER - -\n";
				}
				updateDays(officearray, Van);
				day++;
				garbageMan(officearray, offices, maslog, crimarray,day);
				vanDump(officearray, Van, day, offices, maslog);
			}
		}
		// This part outputs all the strings to their appropriate files
		File masta = new File("output/log_master.txt");
		FileWriter fw = new FileWriter(masta.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(maslog[0]);
		bw.close();
		File frontier = new File("output/log_front.txt");
		fw = new FileWriter(frontier.getAbsoluteFile());
		bw = new BufferedWriter(fw);
		bw.write(maslog[1]);
		bw.close();
		File ofic = new File("output/log_"+ officearray[0].name +".txt");
		fw = new FileWriter(ofic.getAbsoluteFile());
		bw = new BufferedWriter(fw);
		bw.write(offices[0]);
		bw.close();
		for(int i=1;i<officearray.length;i++)
		{
			ofic = new File("output/log_"+ officearray[i].name +".txt");
			fw = new FileWriter(ofic.getAbsoluteFile());
			bw = new BufferedWriter(fw);
			bw.write(offices[i]);
			bw.close();	
		}
		System.out.println("-------Master Log-------\n"+maslog[0]+"-------Front Log-------\n"+maslog[1]+"-------Burnaby Office-------\n"+offices[0]+"-------Vancouver Office-------\n"+offices[1]);
		
	}
//---------------------------------------------------------------------------------------------------------MAIN---------------------------------------------------------------------------------	
	public static void main(String[] args) throws FileNotFoundException, IOException
	{
		//Start Up: Creating arrays and files
		int numOfComm = A2.commnum();
		String[] crimarray = A2.crimCheck();
		Office[] officearray = A2.officemaker(numOfComm);
		File dir = new File("output");
		dir.mkdir();
		File front = new File("output", "log_front.txt");
		front.createNewFile();
		File master = new File("output", "log_master.txt");
		master.createNewFile();
		for(int i=0; i < officearray.length; i++)
		{
			File file = new File("output", "log_"+ officearray[i].name +".txt");
			file.createNewFile();
		}
		//Reading Commands.txt
		A2.readCommands(crimarray, officearray);
	}
}	