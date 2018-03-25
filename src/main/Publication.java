package main;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class Publication {
	
	/**
	 * Szerokosc wypisywanego {@link Publication#toString()}
	 */
	protected int width=99;
	/**
	 * Zwraca wszystkie pole oraz ich wartosc, zarowno wymagane, 
	 * jak i opcjonalne, jako zmienna napisowa w formie tabeli z obramowaniem postaci znaku obramowania.
	 * 
	 * @param c
	 * 		znak obramowania
	 * @return
	 * 		zmienna napisowa zawieracjaca tabele pol i ich wartosci
	 */
	public abstract String toString(char c);

	/**
	 * Ustawia pola wymagane jak i opcjonalne danego obiektu.
	 * 
	 * @param parametersMap
	 * 			Mapa parametrow.
	 */
	public abstract void setParameters(Map<String, String> parametersMap);
	
	/**
	 * Sprawdza czy dany obiekt zawiera wszystkie pola wymagane
	 * 
	 * @throws RuntimeException jesli nie zawiera ktoregos z wymaganych pol
	 */
	public abstract void checkParameters(); 
	
	/**
	 * @return
	 * 		wartosc pola autor
	 */
	public abstract String getAuthor();
}
