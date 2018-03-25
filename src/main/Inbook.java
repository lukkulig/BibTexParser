package main;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * Klasa bedaca obiektowa postacia deklaracji @Inbook zczytanej przez {@link Parser} 
 * 
 * @author Festus
 *
 */
public class Inbook extends Publication {
	/**
	 * Pole bedace kluczem deklaracji
	 */
	private String entryKey;
	/**
	 * Pola wymagane
	 */
	private String author;
	private String editor;
	private String title;
	private String chapter;
	private String pages;
	private String publisher;
	private String year;
	/**
	 * Mapa pol opcjonalnych
	 */
	private Map<String,String> optionalFields = new LinkedHashMap<>();
	
	public Inbook(String key) {
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
				+ String.format("%c%-"+(width-2)+"s%c\n",c," Inbook ("+entryKey+")",c)
				+ line;
		if(author!=null){
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

		}
		if(editor!=null){
			String[] edit = editor.split(" and ");
			matcher = pattern.matcher(edit[0]);
			if(matcher.matches())
				result += String.format(format, c,"editor",c,matcher.group(2)+" "+matcher.group(1),c);
			else
				result += String.format(format, c,"editor",c,edit[0],c);
			
			for(int i=1;i<edit.length;i++){
				matcher = pattern.matcher(edit[i]);
				if(matcher.matches())
					result += String.format(format, c,"",c,matcher.group(2)+" "+matcher.group(1),c);
				else
					result += String.format(format, c,"",c,edit[i],c);
			}
		}
	
				
		result += line
				+ String.format(format, c,"title",c,title,c)
				+ line;
		
		if(chapter!=null){
			result += String.format(format, c,"author",c,chapter,c)+ line;

		}
		if(pages!=null){
			result += String.format(format, c,"editor",c,pages,c)+line;
		}
		
		result += String.format(format, c,"publisher",c,publisher,c)
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
			if(field.getName().equals("author")||field.getName().equals("editor")||
					field.getName().equals("chapter")||field.getName().equals("pages"))
				continue;
			try {
				if(field.get(this)==null)
					throw new RuntimeException("Publikacja o kluczu: "+this.entryKey+""
							+ " - brak wszystkich pol wymaganych! Pominieto.");
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		if((author==null && editor==null) || (chapter==null && pages==null))
			throw new RuntimeException("Publikacja o kluczu: "+this.entryKey+""
					+ " - brak wszystkich pol wymaganych! Pominieto.");
	}
	/* (non-Javadoc)
	 * @see main.Publication#getAuthor()
	 */
	@Override
	public String getAuthor() {
		if(author!=null)
			return author;
		return editor;
	}
}
