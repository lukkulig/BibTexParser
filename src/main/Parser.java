package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Klasa zamieniajaca postac tekstowa zczytana z pliku .bib na postac obiektowa
 * 
 * @author Festus
 */
public class Parser {
	
	/**
	 * Wzor linii poczatkowej deklaracji dowolnej publikacji
	 */
	private Pattern classPattern = Pattern.compile("@([a-zA-Z]+)\\{(.+),");
	/**
	 * Wzory pola deklaracji @String
	 */
	private Pattern stringPattern1 = Pattern.compile("@[a-zA-Z]+\\{(.+)\\s?=\\s?\"(.+)\"\\}");
	private Pattern stringPattern2 = Pattern.compile("@[a-zA-Z]+\\{(.+)\\s?=\\s?\\{(.+)\\}\\}");
	
	/**
	 * Wzor sluzacy do wylapania napisow ze Stringa zawierajacego ogranicznik pol
	 */
	private Pattern findLimiter = Pattern.compile("(.*)\\\\.*\\{(.*)\\}(.*)");
	/**
	 * Wzor linii pola wewnatrz deklaracji, w ktorej wartosc miesci sie w {}
	 */
	private Pattern fieldWithBracesPattern = Pattern.compile("\\s*(.+)\\s?=\\s?\\{(.+)\\},?");
	/**
	 * Wzor linii pola wewnatrz deklaracji, w ktorej wartosc miesci sie w ""
	 */
	private Pattern fieldWithQuotesPattern = Pattern.compile("\\s*(.+)\\s?=\\s?\"(.+)\",?");
	/**
	 * Wzor linii pola wewnatrz deklaracji, w ktorej wartosc jest liczba
	 */
	private Pattern fieldWithInt = Pattern.compile("\\s*(.+)\\s?=\\s?(\\d+),?");
	/**
	 * Wzor linii pola wewnatrz deklaracji, w ktorej wartosc nie miesci sie ani w {}, 
	 * ani w "", ani nie jest liczba
	 */
	private Pattern fieldWithString = Pattern.compile("\\s*(.+)\\s?=\\s?(.+)");
	
	private Matcher matcher;
	/**
	 * Mapa przechowujaca nazwy oraz wartosci Stringow zczytanych z deklaracji @String
	 */
	private Map<String,String> strings = new LinkedHashMap<>();
	
	/**
	 * Metoda tworzy obiekt {@link BibTexFile} i dodaje do niego kolejno znajdowane 
	 * i tworzone obiekty klas dziedziczacych z {@link Publication} wraz z ich zczytanymi polami
	 * Znajduje równiez deklaracje @string i uwzglednia je przy wypelnianiu pol obiektow.
	 * Metoda ignoruje deklaracje @comment oraz @preamble oraz fragmenty niezawierajace rekordow, ani deklaracji.
	 * 
	 * @param file
	 * 			Sciezka do pliku do zparsowania
	 * @param c
	 * 			Znak obramowania przekazywany obiektowi {@link BibTexFile}
	 * 
	 * @return obiekt {@link BibTexFile} wypelniony zczytanymi publikacjami
	 * 
	 * @throws FileNotFoundException
	 * 				Jesli podana sciezka pliku jest nieprawidlowa, nie udalo sie otworzyc pliku - program wyrzuca wyjatek i konczy dzialanie
	 *@exception RuntimeException
	 *				
	 *
	 */
	public BibTexFile parse(File file, char c){
	
		BibTexFile bibtexfile = new BibTexFile(c);
		Scanner input;
		try {
			input = new Scanner(file);
		 
			predefineStrings();
			while(input.hasNextLine()){ 
				String line=input.nextLine();
				line=searchNewEntrance(input, line);
				
				matcher = stringPattern1.matcher(line);
				if(matcher.matches()){
					addString(matcher);	
				}
				matcher = stringPattern2.matcher(line);
				if(matcher.matches()){
					addString(matcher);	
				}
				
				matcher = classPattern.matcher(line);
				if(matcher.matches()){
					String lowerClassName = matcher.group(1).toLowerCase();
					String className="main."+lowerClassName.substring(0,1).toUpperCase()+lowerClassName.substring(1);
					String key=matcher.group(2);
					Publication pub = createNew(className,key);
					if(pub!=null){
						Map<String,String> parametersMap = getParameters(input, line);
						pub.setParameters(parametersMap);
						try{
							pub.checkParameters();
							bibtexfile.addNewPub(pub);
						}catch (RuntimeException e){
							System.err.println(e.getMessage());
						}
						
					}
				}
			}
			input.close();
		} catch (FileNotFoundException e) {
			System.err.println("B³¹d:\n"+e.getMessage());
			System.exit(1);
		}  
		return bibtexfile;		
	}
	
	/**
	 * Metoda tworzy nowy obiekt odpowiedniej klasy.
	 * 
	 * @param className
	 * 			nazwa klasy, ktorej obiekt ma zostac stworzony
	 * @param key
	 * 			Wartosc klucza zczytanego z deklaracji
	 * @return
	 * 		obiekt utworzonej klasy
	 * 		lub null, jesli taka klasa nie istnieje
	 */
	private Publication createNew(String className,String key){
		Publication o=null;
		try {
			Class<?> c = Class.forName(className);
			Constructor construct = c.getConstructor(String.class);
			o = (Publication) construct.newInstance(key);
		} catch (ClassNotFoundException e) {
			//po prostu ignoruje publikacje, dla ktorych nie zostaly stworzone klasy
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return o;
}
	
	/**
	 * Tworzy mape zczytanych parametrow danej deklaracji
	 * Kluczem przechowywanym w mapie jest nazwa pola, wartoscia w mapie jest wartosc pola
	 * 
	 * @param input
	 * 			Zmienna sluzaca do zczytywania kolejnych linii w poszukiwaniu parametrow
	 * @param line
	 * 			Przechowuje kolejene zczytywane linie
	 * 
	 * @return Mapa zawierajaca wszystkie zczytane pola i ich wartosci w danej deklaracji
	 */
	private Map<String,String> getParameters(Scanner input, String line){
		Map<String,String> paramMap = new LinkedHashMap<>();
		while(input.hasNextLine() && !line.endsWith("}")){
			line=input.nextLine();
			matcher=fieldWithBracesPattern.matcher(line);
			if(matcher.matches()){
				paramMap.put(matcher.group(1).trim(), clearString(matcher.group(2)));
				continue;
			}
			matcher=fieldWithQuotesPattern.matcher(line);
			if(matcher.matches()){
				paramMap.put(matcher.group(1).trim(), clearString(matcher.group(2)));
				continue;
			}
			matcher=fieldWithInt.matcher(line);
			if(matcher.matches()){
				paramMap.put(matcher.group(1).trim(), clearString(matcher.group(2)));
				continue;
			}
			matcher=fieldWithString.matcher(line);
			if(matcher.matches()){
				String name=matcher.group(1).trim();
				String value="";
				String[] values=matcher.group(2).split(" # ");
				values[values.length-1]=values[values.length-1].replace(",","");
				
				for(String val:values){
					if(val.startsWith("\"") && val.endsWith("\"")){
						value+=clearString(val);
					}else{	
						if(strings.containsKey(val))
							value+=strings.get(val);
						else
							continue;
					}
				}

				
				paramMap.put(name, value);
			}
			
		}
		return paramMap;

	}

	
	/**
	 * Dodaje zmienna napisowa zczytana z deklaracji @String do {@link Parser#strings}
	 * 
	 * @param matcher
	 * 				dopasowanie pasujace do {@link Parser#stringPattern1} lub {@link Parser#stringPattern2}
	 */
	private void addString(Matcher matcher){
			String gr1=matcher.group(1).trim();
			String gr2=clearString(matcher.group(2));
			
			strings.put(gr1, gr2);
	}

	/**
	 * Wyszukuje kolejna linie zaczynajaca sie znakiem "@", bedaca potencjalna nowa deklaracja
	 * 
	 * @param input
	 * 			Zmienna sluzaca do zczytywania kolejnych linii w poszukiwaniu parametrow
	 * @param line
	 * 			Przechowuje kolejene zczytywane linie
	 * 
	 * @return
	 *       kolejna linie zaczynajaca sie znakiem "@" lub jesli takiej nie ma to ostatnia w pliku
	 */
	private String searchNewEntrance(Scanner input, String line){
		while(input.hasNextLine() && !line.startsWith("@")){
			line = input.nextLine();
		} 
		return line;
	}
	
	/**
	 * Czysci zmienna napisowa ze zbednych znakow
	 * 
	 * @param s
	 * 			zmienna napisowa do wyczyszczenia
	 * @return
	 * 			wyczyszcozna zmienna napisowa
	 */
	private String clearString(String s){
		matcher = findLimiter.matcher(s);
		while(matcher.matches()){
			s=matcher.group(1)+matcher.group(2)+matcher.group(3);
			matcher = findLimiter.matcher(s);
		}
		s=s.replace("{", "");
		s=s.replace("}", "");
		s=s.replace("\"", "");
		return s;
	}
	
	/**
	 * Uzupelnia {@link Parser#strings} predefiniowanymi zmiennymi napisowymi odpowiadajacymi miesiacom
	 */
	private void predefineStrings(){
		strings.put("jan", "January");
		strings.put("feb", "February");
		strings.put("mar", "March");
		strings.put("apr", "April");
		strings.put("may", "May");
		strings.put("jun", "June");
		strings.put("jul", "July");
		strings.put("aug", "August");
		strings.put("sep", "September");
		strings.put("oct", "October");
		strings.put("nov", "November");
		strings.put("dec", "December");
	}
	
}
