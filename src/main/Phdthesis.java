package main;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * Klasa bedaca obiektowa postacia deklaracji @Phdthesis zczytanej przez {@link Parser} 
 * 
 * @author Festus
 *
 */
public class Phdthesis extends Publication {
	/**
	 * Pole bedace kluczem deklaracji
	 */
	private String entryKey;
	/**
	 * Pola wymagane
	 */
	private String author;
	private String title;
	private String school;
	private String year;
	/**
	 * Mapa pol opcjonalnych
	 */
	private Map<String,String> optionalFields = new LinkedHashMap<>();
	/**
	 * Konstruktor ustawiajacy klucz obiektu
	 * 
	 * @param key
	 * 			klucz deklaracji zczytany z pliku
	 */
	public Phdthesis(String key) {
		this.entryKey = key;
	}
	
	/* (non-Javadoc)
	 * @see main.Publication#toString(char)
	 */
	@Override
	public String toString(char c) {
		Pattern pattern=Pattern.compile("([a-zA-Z .]+), ([a-zA-Z .]+)");
		Matcher matcher;
		
		String line = String.format("%0"+(width)+"d\n", 0).replace('0', c);
		String format = "%c %-"+(width/4-4)+"s%c %-"+width*3/4+"s%c\n";
		String result =line
				+ String.format("%c%-"+(width-2)+"s%c\n",c," Phdthesis "+entryKey,c)
				+ line;
		String[] auth = author.split(" and ");
		matcher = pattern.matcher(auth[0]);
		if(matcher.matches())
			result += String.format(format, c,"author",c,matcher.group(2)+" "+matcher.group(1),c);
		else
			result += String.format(format, c,"author",c,auth[0],c);
		
		for(int i=1;i<auth.length;i++){
			matcher = pattern.matcher(auth[i]);
			if(matcher.matches())
				result += String.format(format, c,"",c,matcher.group(2)+" "+matcher.group(1),c);
			else
				result += String.format(format, c,"",c,auth[i],c);
		}
		result += line
				+ String.format(format, c,"title",c,title,c)
				+ line
				+ String.format(format, c,"school",c,school,c)
				+ line
				+ String.format(format, c,"year",c,year,c)
				+ line;
		
		for(Map.Entry<String,String> entry : optionalFields.entrySet()) {
			result +=String.format(format, c, entry.getKey(),c,entry.getValue(),c)
					+ line;			
		}
			

		return 	result;
	}

	/* (non-Javadoc)
	 * @see main.Publication#setParameters(java.util.Map)
	 */
	@Override
	public void setParameters(Map<String, String> parametersMap) {
		for(Map.Entry<String, String> entry:parametersMap.entrySet()){
			String key=entry.getKey();
			String value=entry.getValue();
			Field field;
			try {
				field=this.getClass().getDeclaredField(key);
				field.set(this, value);
			} catch (NoSuchFieldException | SecurityException e) {
				optionalFields.put(key, value);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
		
	}

	/* (non-Javadoc)
	 * @see main.Publication#checkParameters()
	 */
	@Override
	public void checkParameters() {
		for(Field field:getClass().getDeclaredFields()){
			field.setAccessible(true);
			try {
				if(field.get(this)==null)
					throw new RuntimeException("Publikacja o kluczu: "+this.entryKey+""
							+ " - brak wszystkich pol wymaganych! Pominieto.");
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	/* (non-Javadoc)
	 * @see main.Publication#getAuthor()
	 */
	@Override
	public String getAuthor() {
		return author;
	}
}
