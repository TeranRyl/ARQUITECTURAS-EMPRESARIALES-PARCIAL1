package org.example;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.*;
import java.io.*;

public class HttpServer {
    public static void main(String[] args) throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ServerSocket serverSocket = null;
        StringBuilder sMetodos = new StringBuilder();
        String clase = "";
        Method metodos[];
        Field campos[];
        StringBuilder sCampos = new StringBuilder();
        try {
            serverSocket = new ServerSocket(36000);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 35000.");
            System.exit(1);
        }

        boolean running = true;
        while (running){

            Socket clientSocket = null;
            try {
                System.out.println("Listo para recibir ...");
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }
            PrintWriter out = new PrintWriter(
                    clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
            String inputLine, outputLine;
            outputLine = "HTTP/1.1 200 OK\r\n"
                    + "Content-Type: text/html\r\n"
                    + "\r\n" +
                    "<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "    <head>\n" +
                    "        <title>Form Example</title>\n" +
                    "        <meta charset=\"UTF-8\">\n" +
                    "        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                    "    </head>\n" +
                    "    <body>\n" +
                    "        <h1>Form with GET</h1>\n" +
                    "        <form action=\"/consulta\">\n" +
                    "            <label for=\"comando\">Name:</label><br>\n" +
                    "            <input type=\"text\" id=\"comando\" name=\"comando\" value=\"John\"><br><br>\n" +
                    "            <input type=\"button\" value=\"Submit\" onclick=\"loadGetMsg()\">\n" +
                    "        </form> \n" +
                    "        <div id=\"getrespmsg\"></div>\n" +
                    "\n" +
                    "        <script>\n" +
                    "            function loadGetMsg() {\n" +
                    "                let nameVar = document.getElementById(\"comando\").value;\n" +
                    "                const xhttp = new XMLHttpRequest();\n" +
                    "                xhttp.onload = function() {\n" +
                    "                    document.getElementById(\"getrespmsg\").innerHTML =\n" +
                    "                    this.responseText;\n" +
                    "                }\n" +
                    "                xhttp.open(\"GET\", \"/consulta?comando=\"+nameVar);\n" +
                    "                xhttp.send();\n" +
                    "            }\n" +
                    "        </script>\n" +
                    "\n" +
                    "    </body>\n" +
                    "</html>";
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Recibí: " + inputLine);
                if (inputLine.startsWith("GET") && inputLine.contains("consulta?comando=")){
                    if (inputLine.split("comando=")[1].startsWith("Class")) {
                        clase = inputLine.split("Class")[1].replace("(", "").replace(")", "").split(" ")[0];
                        System.out.println("HOLA" + clase);
                        Class<?> c = Class.forName(clase);
                        metodos = c.getDeclaredMethods();
                        campos = c.getFields();
                        for (Field i : campos) {
                            sCampos.append(" CAMPO: " + i.getName());
                        }
                        for (Method j : metodos) {

                            sMetodos.append(" METODO: " + j.getName());
                        }
                        outputLine = "HTTP/1.1 200 OK\r\n"
                                + "Content-Type: text/html\r\n"
                                + "\r\n"
                                + String.valueOf(sCampos)
                                + String.valueOf(sMetodos);
                    }

                    else if (inputLine.split("comando=")[1].startsWith("invoke")) {
                        clase = inputLine.split("invoke")[1].replace("(", "").replace(")", "").split(" ")[0].split(",")[0];
                        String metodo = inputLine.split("invoke")[1].split(",")[1].replace(")", "").split(" ")[0];
                        //System.out.println("HOLA" + clase);
                        Class<?> c = Class.forName(clase);
                        Method Meth= c.getDeclaredMethod(metodo);
                        //Meth.invoke(null);
                        outputLine = "HTTP/1.1 200 OK\r\n"
                                + "Content-Type: text/html\r\n"
                                + "\r\n"
                                + Meth.invoke(null);

                    }

                }


                if (!in.ready()) {break; }
            }
            out.println(outputLine);
            out.close();
            in.close();
            clientSocket.close();
        }
        serverSocket.close();
        }
}