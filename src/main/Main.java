package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.LinkedHashMap;

/**
 * Glowna klasa obslugujaca argumenty programu
 * 
 * 
 * @author Festus
 *
 */
public class Main {

	/**Metoda main programu, w zaleznosci od argumentow przekazanych przy wywolaniu programu
	 * dostarcza odpowiednych funkcjonalnosci.
	 * 
	 * Jesli nie zostana przekazane zadne parametry wyswietlona zostanie jedynie pomoc - opis opcji 
	 * Pierwszym parametrem programu jest zawsze sciezka do pliku
	 * Kolejne parametry sluzace filtrowaniu sa opcjonalne, jednak zachowuja kolejnosc: 
	 * autorzy, kolekcje, znak obramowania.
	 * Autorow oraz kolekcji mozna podac kilka, oddzielone jedynie znakami "," 
	 * (bez spacji; spacja oddziela kolejne argumenty programu)
	 * Jesli podani zostana autorzy, wszyscy razem musza byc autorami podanej publikacji,
	 * aby zostala ona wyswietlona.
	 * Jesli zostana podane kategorie, wyswietlone zostana publikacje nalezace do kazdej podanej kategorii osobno,
	 * Znak obramowania moze byc dowolnym pojedynczym znakiem ASCII
	 * 
	 * 
	 * @param args
	 * 			argumenty programu: wymagane - sciezka do pliku
	 * 								opcjonalne - autorzy, kolekcje, znak obramowania
	 */
	public static void main(String[] args){
		if(args.length==0) err();	
		
		Parser parser = new Parser();
		File file = new File(args[0]);
		BibTexFile bibfile=new BibTexFile();
		String[] authors;
		String[] categories;
		String[] parameters;
		
		switch (args.length) {
		case 1:		bibfile = parser.parse(file,'*');
					if(!bibfile.writeAllPub())
						System.out.println("Brak publikacji do wyswietlenia.");
				
			break;
		case 2: if(args[1].length()==1){
					char c = args[args.length-1].charAt(0);
						bibfile = parser.parse(file,c);
						if(!bibfile.writeAllPub())
							System.out.println("Brak publikacji do wyswietlenia.");
				}else{
					parameters=args[1].split(",");
						bibfile = parser.parse(file,'*');
						
						if(!bibfile.writeAuthorsPub(parameters))					
								if(checkCategories(parameters))
									if(!bibfile.writeCategoriesPub(parameters))
										System.out.println("Brak publikacji do wyswietlenia!");
				}
			break;
		case 3: if(args[2].length()==1){
					char c = args[2].charAt(0);
					parameters=args[1].split(",");
						bibfile = parser.parse(file,c);
						if(!bibfile.writeAuthorsPub(parameters))					
							if(checkCategories(parameters))
								if(!bibfile.writeCategoriesPub(parameters))
									System.out.println("Brak publikacji do wyswietlenia.");
				}else{
					authors=args[1].split(",");
					categories=args[2].split(",");
					if(checkCategories(categories)){
							bibfile = parser.parse(file,'*');
							if(!bibfile.writeAllPub(authors,categories))
								System.out.println("Brak publikacji do wyswietlenia.");
					}
				}
			break;
		case 4: if(args[3].length()>1)	
					System.err.println("Blad:\nBledny parametr - znak_obramowania");
				else{
					authors=args[1].split(",");
					categories=args[2].split(",");
					char c = args[3].charAt(0);
					if(checkCategories(categories)){
							bibfile = parser.parse(file,c);
							if(!bibfile.writeAllPub(authors,categories))
								System.out.println("Brak publikacji do wyswietlenia.");
					}
				}
			break;
		default:
			err();
		}


	}
	
	/**
	 * Sprawdza czy istnieja kategorie takie jak podane w argumentach programu
	 * 
	 * @param categories
	 * 			Tablica zmiennych napisowych zawierajaca kategorie podane w argumentach programu, 
	 * 			wymagajace sprawdzenia.
	 * @return
	 * 		Czy kategorie sa poprawne.
	 */
	private static boolean checkCategories(String[] categories) {
		for (String category : categories) {
				try {
					Class<?> cl = Class.forName("main."+category);
				} catch (ClassNotFoundException e) {
					System.err.println("Blad:\n"
							+ "Nieodpowiednie parametry!");
					return false;
				}

		}
		return true;
		
	}
	
	/**
	 * Wyswietla pomoc - w jakis sposob i z jakimi parametrami wywolac program
	 * 
	 */
	private static void err(){
		System.out.println("Wyswietla publikacje zawarte w pliku.\n\n"
				+ "bibtexparser sciezka_pliku [autorzy] [kategorie] [znak_obramowania]\n\n"
				+ String.format("%-20s%s\n","\tsciezka_pliku","okresla plik do przeszukania")
				+ String.format("%-20s%s\n","\tautorzy","nazwiska autorów do filtrowania -  oddzielone \",\"")
				+ String.format("%-20s%s\n","\tkategorie","nazwy kategorii do filtrowania -  oddzielone \",\"")
				+ String.format("%-20s%s\n","\tznak_obramowania","jesli nie zostanie podany - domyslnie \"*\"")
		);
		System.exit(1);
	}

}
