package net.lacnic.epp;

import java.util.ArrayList;
import java.util.List;

import net.lacnic.epp.exceptions.EppSplitterException;

public class EppSplitter {

	public static String split(String xml) throws EppSplitterException {
		// final String text =
		// "Hola como <tag attr=\"$hola\"></tag> estas <tag>$hola2</tag>?";
		// System.out.println(text);
		// System.out.println(removeSurroundingTags(text));

		final String removeComments = removeComments(xml);
		final String removeSurroundingTags = removeSurroundingTags(removeComments);
		return removeAttrOnly(removeSurroundingTags);
	}

	public static String removeAttrOnly(String xml) throws EppSplitterException {

		final String PESOS = "=\"$";
		final String abrir = " ";
		final String cerrar = "\"";

		return removeSurroundingCharacters(xml, PESOS, abrir, cerrar);
	}

	/*
	 * Casos: 1. "<tag>$hola</tag>" --> "" 2.
	 * "<tag attr="$hola">contenido</tag>" --> "<tag>contenido</tag>"
	 */
	/*
	 * No contempla casos anidados
	 */
	public static String removeSurroundingTags(String xml) throws EppSplitterException {
		final String PESOS = ">$";
		final String abrir = "<";
		final String cerrar = ">";

		return removeSurroundingCharacters(xml, PESOS, abrir, cerrar);
	}

	public static String removeComments(String xml) throws EppSplitterException {

		final String COMMENT = "!--";
		final String abrir = "<";
		final String cerrar = "-->";

		return removeSurroundingCharacters(xml, COMMENT, abrir, cerrar);
	}

	public static String removeSurroundingCharacters(String xml, final String PESOS, final String abrir, final String cerrar) throws EppSplitterException {

		final String VACIO = "";
		String procesado = xml;

		List<Integer> ocurrencias = obtenerTodasLasOcurrenciasDe(procesado, PESOS);
		int i = 0;
		for (int position : ocurrencias) {

			final int izq = primeraOcurrenciaHaciaLaIzquierda(procesado, position, abrir);
			final int der = primeraOcurrenciaHaciaLaDerecha(procesado, position, cerrar);

			if (izq < 0)
				throw new EppSplitterException(String.format("Error searching for first occurence of %c.", abrir));

			if (der < 0)
				throw new EppSplitterException(String.format("Error searching for first occurence of %c.", cerrar));

			final String substring = procesado.substring(izq, der + cerrar.length());
			procesado = procesado.replace(substring, VACIO);

			// actualizar las ocurrencias que vienen...
			i++;
			int j = i;
			final List<Integer> subList = ocurrencias.subList(i, ocurrencias.size());
			for (int ocurrencia : subList) {
				final int dif = substring.length();
				ocurrencias.set(j++, ocurrencia - dif);
			}
		}

		return procesado;
	}

	private static List<Integer> obtenerTodasLasOcurrenciasDe(String text, String character) {

		List<Integer> list = new ArrayList<Integer>();
		int index = text.indexOf(character);

		while (index > 0) {
			int offset = character.length() - 1;
			list.add(index + offset);// almacenar la posición del último
										// caracter de 'charcter'
			index = text.indexOf(character, index + offset);
		}

		return list;
	}

	/**
	 * 
	 * @param text
	 * @param position
	 * @param character
	 * @return
	 * 
	 *         <p>
	 *         Ejemplo
	 *         </p>
	 *         <p>
	 *         String text = "Hola como estas?"; int position = 10; // 'e' int
	 *         character = ' '; primeraOcurrenciaHaciaLaIzquierda(text,
	 *         position, character); // retorna 9
	 *         </p>
	 */
	private static int primeraOcurrenciaHaciaLaIzquierda(String text, int position, String character) {

		final int N = text.length() - 1;
		String reversed = new StringBuilder(text).reverse().toString();
		final int X = N - position;
		final int Y = reversed.indexOf(character, X);
		final int y = N - Y;

		return y;
	}

	private static int primeraOcurrenciaHaciaLaDerecha(String text, int position, String character) {

		return text.indexOf(character, position);

	}
}
