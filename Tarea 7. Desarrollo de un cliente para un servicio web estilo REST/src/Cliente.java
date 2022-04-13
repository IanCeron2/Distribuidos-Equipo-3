
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 *
 * @author Iancr
 */
public class Cliente {

    public static void enviar_parametros(String ip_servicio, String funcionalidad) throws MalformedURLException, IOException{
        String url_sevicio = "http://" + ip_servicio + ":8080/Servicio/rest/ws/" + funcionalidad;
        URL url = new URL(url_sevicio);
        HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
        //true si se va a enviar el "body", en este caso el "body" son los parametros
        conexion.setDoOutput(true);
        //en este caso utilizamos el metodo POST de HTTP
        conexion.setRequestMethod("POST");
        //indica que la peticion estara codificada como URL
        conexion.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        String parametros = "";
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        
        switch(funcionalidad){
            case "alta_usuario":                
                //Obtenemos los datos del usuario que se va a registrar
                Usuario usuario = new Usuario();
                System.out.print("Email: ");
                usuario.email = br.readLine();
                System.out.print("Nombre: ");
                usuario.nombre = br.readLine();
                System.out.print("Apellido Paterno: ");
                usuario.apellido_paterno = br.readLine();
                System.out.print("Apellido Materno: ");
                usuario.apellido_materno = br.readLine();                    
                System.out.print("Fecha de nacimiento: ");
                usuario.fecha_nacimiento = br.readLine();
                System.out.print("Telefono: ");
                usuario.telefono = br.readLine();
                System.out.print("Genero (M/F): ");
                usuario.genero = br.readLine();
                usuario.foto = null;
                
                //serializamos la instancia de tipo Usuario
                GsonBuilder builder = new GsonBuilder();
                builder.serializeNulls();
                Gson gson = builder.create();
                String body = gson.toJson(usuario);
                parametros = "usuario=" + URLEncoder.encode(body, "UTF-8");
                break;
                
            case "consulta_usuario":
                System.out.print("Ingresa el correo de usuario que quieres consultar: ");
                String email_consulta = br.readLine();
                parametros = "email=" + URLEncoder.encode(email_consulta, "UTF-8");
                break;
                
            case "borra_usuario":
                System.out.print("Ingresa el correo de usuario que quieres borrar: ");
                String email_borra = br.readLine();
                parametros = "email=" + URLEncoder.encode(email_borra, "UTF-8");
                break;                
        }
        OutputStream os = conexion.getOutputStream();
        os.write(parametros.getBytes());
        os.flush();
        
        //se debe verificar si hubo error
        if(conexion.getResponseCode() == 200){
            //no hubo error
            switch(funcionalidad){
                case "alta_usuario":
                    System.out.println("OK");
                    break;
                    
                case "consulta_usuario":
                    br = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
                    //el metodo web regresa una string en formato JSON
                    String respuesta = br.readLine();
                    Gson j = new GsonBuilder().create();
                    Usuario user = (Usuario) j.fromJson(respuesta, Usuario.class);
                    System.out.println("Email: " + user.email);
                    System.out.println("Nombre: " + user.nombre);
                    System.out.println("Apellido Paterno: " + user.apellido_paterno);
                    System.out.println("Apellido Materno: " + user.apellido_materno);
                    System.out.println("Fecha: " + user.fecha_nacimiento);
                    System.out.println("Telefono: " + user.telefono);
                    System.out.println("Genero: " + user.genero);
                    
                    System.out.print("¿Deseas modificar el usuario (s/n)? : ");
                    br = new BufferedReader(new InputStreamReader(System.in));
                    if(br.readLine().equals("s")){                        
                        String str_url_modifica_usuario = "http://" + ip_servicio + ":8080/Servicio/rest/ws/modifica_usuario";
                        URL url_modifica_usuario = new URL(str_url_modifica_usuario);
                        HttpURLConnection conexion_modifica = (HttpURLConnection) url_modifica_usuario.openConnection();
                        conexion_modifica.setDoOutput(true);                        
                        conexion_modifica.setRequestMethod("POST");                        
                        conexion_modifica.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                        
                        System.out.print("Nombre: ");
                        String nombre = br.readLine();
                        System.out.print("Apellido Paterno: ");
                        String apellido_paterno = br.readLine();
                        System.out.print("Apellido Materno: ");
                        String apellido_materno = br.readLine();                    
                        System.out.print("Fecha de nacimiento: ");
                        String fecha_nacimiento = br.readLine();
                        System.out.print("Telefono: ");
                        String telefono = br.readLine();
                        System.out.print("Genero (M/F): ");
                        String genero = br.readLine();
                        
                        //si los campos no son un entrer, los actualizamos
                        if(!nombre.equals("")) user.nombre = nombre;
                        if(!apellido_paterno.equals("")) user.apellido_paterno = apellido_paterno;
                        if(!apellido_materno.equals("")) user.apellido_materno = apellido_materno;
                        if(!fecha_nacimiento.equals("")) user.fecha_nacimiento = fecha_nacimiento;
                        if(!telefono.equals("")) user.telefono = telefono;
                        if(!genero.equals("")) user.genero = genero;
                        
                        //serializamos la instancia de tipo Usuario
                        GsonBuilder builder = new GsonBuilder();
                        builder.serializeNulls();
                        Gson gson = builder.create();
                        String body = gson.toJson(user);
                        parametros = "usuario=" + URLEncoder.encode(body, "UTF-8");
                        os = conexion_modifica.getOutputStream();
                        os.write(parametros.getBytes());
                        os.flush();
                        
                        //se debe verificar si hubo error
                        if(conexion_modifica.getResponseCode() == 200) System.out.println("OK");
                     
                        else{
                            //hubo error
                            BufferedReader br_error = new BufferedReader(new InputStreamReader(conexion_modifica.getErrorStream()));
                            respuesta = br_error.readLine();
                            Error x = (Error) j.fromJson(respuesta, Error.class);
                            System.out.println(x.message);
                        }
                        conexion_modifica.disconnect();
                    }
                    break;
                    
                case "borra_usuario":
                    System.out.println("OK");
                    break;
            }           
        }
        else{
            //hubo error
            BufferedReader br_error = new BufferedReader(new InputStreamReader(conexion.getErrorStream()));
            String respuesta = br_error.readLine();
            Gson j = new GsonBuilder().create();
            Error x = (Error) j.fromJson(respuesta, Error.class);
            System.out.println(x.message);
        }
        conexion.disconnect();        
    }
    
    
    public static void main(String[] args) throws MalformedURLException, IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Cual es la ip del servicio web: ");
        String ip_servicio = br.readLine();
        
        while (true) {
            System.out.println("MENU");
            System.out.println("a. Alta usuario");
            System.out.println("b. Consulta usuario");
            System.out.println("c. Borrar usuario");
            System.out.println("d. Salir");
            System.out.print("Opcion: ");
            char opc = br.readLine().charAt(0);

            switch (opc) {
                case 'a':
                    System.out.println("Alta usuario");
                    enviar_parametros(ip_servicio, "alta_usuario");
                    break;
                case 'b':
                    System.out.println("Consulta usuario");
                    enviar_parametros(ip_servicio, "consulta_usuario");
                    break;
                case 'c':
                    System.out.println("Borrar usuario");
                    enviar_parametros(ip_servicio, "borra_usuario");
                    break;
                case 'd':
                    br.close();
                    System.exit(0);
                    break;
                default:
                    System.out.println("Opcion no valida");
                    break;
            }
            System.out.println("");
        }               
    }    
}
