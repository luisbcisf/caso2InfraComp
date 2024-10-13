package interfaz;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import estructura.Imagen;
import logica.Referencia;

public class consola {

	
	public static void main(String[] args) {
		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(isr);
		while (true) {
			try {
				System.out.println("============ Caso 2 ============");
				System.out.println("Escoja una opcion porfavor ");
				System.out.println("1. Generación de las referencias");
				System.out.println("2. Calcular los datos resultantes");
				String opcion = br.readLine();
				if (opcion.equals("1")) {
					System.out.println("Ingrese el nombre de la imágen");
					System.out.println("Nota: Debe estar dentro de la carpeta archivos del proyecto");
					String ruta = br.readLine();
					Imagen imagen = new Imagen("archivos/"+ ruta);
					System.out.println("Ingrese el tamaño de pagina");
					int tamanioPagina = Integer.parseInt(br.readLine());

					Referencia archivoReferencia = new Referencia(imagen,tamanioPagina);
					archivoReferencia.generarArchivo();
					
				}
				else if (opcion.equals("2")) {
					
				}
				else {
					System.out.println("Porfavor digite una opcion válida");
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}
