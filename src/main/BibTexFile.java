package main;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Klasa bedaca obiektowa postacia pliku .bib
 * Zawiera liste wszystkich publikacji zczytanych przez {@link Parser} 
 * 
 * @author Festus
 *
 */
public class BibTexFile {
	
	/**
	 * Znak obramowania
	 */
	private char c;
	
	/**
	 * Lista wszystkich publikcaji zczytanych przez {@link Parser} 
	 */
	private List<Publication> publications = new LinkedList<>();
	
	/**
	 * Konstruktor bezparametrowy ustawiajacy znak obramowania na domyslny "*""
	 */
	public BibTexFile() {
		this.c='*';
	}
	
	/**
	 * Konstruktor ustawiajaca znak obramowania na podany w parametrze
	 * @param c
	 * 	znak obramowania
	 */
	public BibTexFile(char c) {
		this.c=c;
	}

	/**
	 * Dodaje nowy obiekt do listy {@link BibTexFile#publications}
	 * @param pub
	 * 			obiekt do dodania
	 */
	public void addNewPub(Publication pub){
		publications.add(pub);
	}
	
	/**
	 * Wypisuje wszystkie publikacjie zawarte w {@link BibTexFile#publications}.
	 * @return
	 * 		Czy wypisano jakakolwiek publikacje.
	 */
	public boolean writeAllPub(){
		boolean result=false;
		for(Publication pub : publications){
			System.out.println(pub.toString(c));
			result=true;
		}
		return result;
	}
	
	/**
	 * Wypisuje publikacjie zawarte w {@link BibTexFile#publications}, ktore naleza 
	 * do podanych w parametrze kategorii i ktorych autorami sa ci podani w parametrze
	 * @param authors
	 * 			tablica autorow, ktorych publikacje maja byc brane pod uwage
	 * @param categories
	 * 			tablica kategorii, ktore maja byc brane pod uwage
	 * @return
	 * 		Czy wypisano jakakolwiek publikacje.
	 */
	public boolean writeAllPub(String[] authorsString, String[] categories){
		boolean result=false;
		for(Publication pub : publications){
			for (String category : categories) {
				try {
					Class<?> cl = Class.forName("main."+category);
					if(cl.isInstance(pub)){
						boolean flag=true;
						String[] authors = pub.getAuthor().split(" and ");
						for (String authorString : authorsString) {
							if(flag==true){
								flag=false;
								for (String author : authors) {
									Pattern pattern=Pattern.compile("([a-zA-Z]+)\\, ([A-Z][a-zA-Z \\.]*)");
									Matcher matcher=pattern.matcher(author);
									if(matcher.matches())
										if(matcher.group(1).equals(authorString))
											flag=true;
									pattern=Pattern.compile("([A-Z][a-zA-Z \\.]*) ([a-zA-Z]+)");
									matcher=pattern.matcher(author);
									if(matcher.matches())
										if(matcher.group(2).equals(authorString))
											flag=true;

								}
							}
						}
						
						if(flag){
							System.out.println(pub.toString(c));
							result=true;
						}
					}
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			
		}
		return result;
	}
	
	/**
	 * Wypisuje publikacjie zawarte w {@link BibTexFile#publications}, ktore naleza 
	 * do podanych w parametrze kategorii
	 * @param categories
	 * 			tablica kategorii, ktore maja byc brane pod uwage
	 * @return
	 * 		Czy wypisano jakakolwiek publikacje.
	 */
	public boolean writeCategoriesPub(String[] categories){
		boolean result=false;
		for(Publication pub : publications){
			for (String category : categories) {
				try {
					Class<?> cl = Class.forName("main."+category);
					if(cl.isInstance(pub)){
						System.out.println(pub.toString(c));
						result=true;
					}
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			
		}
		return result;
	}
			

	
	/**
	 * Wypisuje publikacjie zawarte w {@link BibTexFile#publications}, 
	 * ktorych autorami sa ci podani w parametrze
	 * @param authors
	 * 			tablica autorow, ktorych publikacje maja byc brane pod uwage
	 * @return
	 * 		Czy wypisano jakakolwiek publikacje.
	 */
	public boolean writeAuthorsPub(String[] authorsString){
			boolean result = false;
			for(Publication pub : publications){
				boolean flag=true;
				String[] authors = pub.getAuthor().split(" and ");
				for (String authorString : authorsString) {
					if(flag==true){
						flag=false;
						for (String author : authors) {
							Pattern pattern=Pattern.compile("([a-zA-Z]+)\\, ([A-Z][a-zA-Z \\.]*)");
							Matcher matcher=pattern.matcher(author);
							if(matcher.matches())
								if(matcher.group(1).equals(authorString))
									flag=true;
							pattern=Pattern.compile("([A-Z][a-zA-Z \\.]*) ([a-zA-Z]+)");
							matcher=pattern.matcher(author);
							if(matcher.matches())
								if(matcher.group(2).equals(authorString))
									flag=true;

						}
					}
				}
				
				if(flag){
					System.out.println(pub.toString(c));
					result=true;
				}
			}
			return result;
	}
}
