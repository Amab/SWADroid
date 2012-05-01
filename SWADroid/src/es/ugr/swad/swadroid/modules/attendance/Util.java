package es.ugr.swad.swadroid.modules.attendance;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {
	public static boolean isValidEmail(String email) {
		String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
		CharSequence inputStr = email;

		Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(inputStr);

		return matcher.matches();
	}

	public static boolean isValidDate(String date) {
		String monthExpression = "(0?[1-9]|1[0-2])";
		String dayExpression = "(0?[1-9]|[12][0-9]|3[01])";
		String expression = "^" + dayExpression + "/" + monthExpression + "/(18|19|20|21)\\d{2}";
		CharSequence inputStr = date;

		Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(inputStr);

		return matcher.matches();
	}

	public static boolean isValidTime(String hora) {
		String TIME24HOURS_PATTERN = "([01]?[0-9]|2[0-3]):[0-5][0-9]";

		Pattern pattern = Pattern.compile(TIME24HOURS_PATTERN);
		Matcher matcher = pattern.matcher(hora);

		return matcher.matches();
	}

	public static String pad(int c) {
		if (c >= 10)
			return String.valueOf(c);
		else
			return "0" + String.valueOf(c);
	}

	public static boolean isValidDni(String dni) {
		String patronDni = "^[A-Z]?\\d{1,16}[A-Z]?$";	// (0 or 1 letter) + (1 to 16 digits) + (0 or 1 letter)

		Pattern pattern = Pattern.compile(patronDni, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(dni);

		return matcher.matches();
		/*if (matcher.matches())
			return validarLetra(dni);
		return false;*/
	}

	@SuppressWarnings("unused")
	private static boolean validarLetra(String n) {		 
		String numero = n.substring(0, n.length()-1);
		String letra = n.substring(n.length()-1, n.length());

		int codigo = (Integer.valueOf(numero).intValue()) % 23;
		String[] abc = {"T","R","W","A","G","M","Y","F","P","D","X","B","N","J","Z","S","Q","V","H","L","C","K","E","T"};

		if(abc[codigo].compareToIgnoreCase(letra) == 0)
			return true;
		return false;
	}

	public static boolean isValidNickname(String nickname) {
		String patronNickname = "[a-zA-Z_0-9]{1,30}";	// 1 to 30 letters, underscored or digits

		Pattern pattern = Pattern.compile(patronNickname, Pattern.CASE_INSENSITIVE);

		Matcher matcher = pattern.matcher(nickname);

		return matcher.matches();
	}
}
