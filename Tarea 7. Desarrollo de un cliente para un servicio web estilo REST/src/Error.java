/*
  Error.java
  Permite regresar al cliente REST un mensaje de error
*/

public class Error
{
	String message;

	Error(String message)
	{
		this.message = message;
	}
}
