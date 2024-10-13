package logica;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import estructura.Imagen;

public class Referencia {
	
	Imagen imagen;
	int tamanioPagina;
	
	public Referencia(Imagen imagen, int tamanioPagina) {
		this.imagen = imagen;
		this.tamanioPagina = tamanioPagina;
	}
	
	public void generarArchivo() {
		int tamanioMensaje = imagen.leerLongitud();
		System.out.println("TAMANIO: " + tamanioMensaje);
		int numReferencias = tamanioMensaje * 17 + 16;
		int numPaginas = Math.ceilDiv((imagen.getAlto() * imagen.getAncho() * 3) + tamanioMensaje, tamanioPagina);
		
		String filePath = "archivos/referencia.txt";
		
		try {
            FileWriter fileWriter = new FileWriter(filePath);
            BufferedWriter escritor = new BufferedWriter(fileWriter);
            
            escritor.write("P="+tamanioPagina);
            escritor.newLine();
            escritor.write("NF="+imagen.getAlto());
            escritor.newLine();
            escritor.write("NC="+imagen.getAncho());
            escritor.newLine();
            escritor.write("NR="+numReferencias);
            escritor.newLine();
            escritor.write("NP="+numPaginas);
            escritor.newLine();
            
            int paginaVirtual = 0;
            int desplazamiento = 0;
            int fila = 0;
            int col = 0;
            String color = "R";
            int modulo = 0;
            int byteMensaje = 0;
            
            for(int veces = 0; veces < 16; veces++) {
            	if (modulo == 0) {
            		color = "R";
            	}
            	else if(modulo == 1){
            		color = "G";
            	}
            	else {
            		color = "B";
            	}
            	
            	
            	escritor.write("Imagen["+fila+"]["+col+"]"+"."+color+","+paginaVirtual+","+desplazamiento+",R");
            	escritor.newLine();
            	
            	modulo += 1;
            	desplazamiento+=1;
            	
            	if (modulo == 3) {
            		modulo = 0;
            		col+=1;
            	}
            	if (col == imagen.getAncho()) {
            		col = 0;
            		fila +=1;
            	}
            	if (desplazamiento == tamanioPagina) {
            		desplazamiento = 0;
            		paginaVirtual += 1;
            	}
            }
            
            int modByte;
            int pagVirtMensaje = Math.floorDiv(imagen.getAlto() * imagen.getAncho() * 3, tamanioPagina);
            int despMensaje = imagen.getAlto() * imagen.getAncho() * 3 % tamanioPagina;
            int counterMensaje = 0;
            boolean inicializar = true;
            
            for (int i = 0; i < numReferencias-tamanioMensaje-15; i++) {
            	modByte = i %2;
            	
            	if (modByte == 0) {            		
            		escritor.write("Mensaje["+byteMensaje+"]"+","+pagVirtMensaje+","+despMensaje+",W");
            		escritor.newLine();
            		
            		counterMensaje += 1;
            		
            		if (counterMensaje == 9) {
            			counterMensaje = 1;
            			despMensaje += 1;
            			byteMensaje += 1;
         
	            		if (despMensaje == tamanioPagina) {
	            			despMensaje = 0;
	            			pagVirtMensaje += 1;
	            		}
	            		
	            		if (i != numReferencias-tamanioMensaje-16) {
		            		escritor.write("Mensaje["+byteMensaje+"]"+","+pagVirtMensaje+","+despMensaje+",W");
		            		escritor.newLine(); 	
	            		}
	           
            		}            		
            		
            	}
            	
            	else if (modByte == 1) {
            		if (modulo == 0) {
                		color = "R";
                	}
                	else if(modulo == 1){
                		color = "G";
                	}
                	else {
                		color = "B";
                	}
            		
                	escritor.write("Imagen["+fila+"]["+col+"]"+"."+color+","+paginaVirtual+","+desplazamiento+",R");
                	escritor.newLine();
                	
                	modulo += 1;
                	desplazamiento+=1;
                	
                	if (modulo == 3) {
                		modulo = 0;
                    	col+=1;
                	}
                	if (col == imagen.getAncho()) {
                		col = 0;
                		fila +=1;
                	}
                	if (desplazamiento == tamanioPagina) {
                		desplazamiento = 0;
                		paginaVirtual += 1;
                	}
             
            	}
            	
            }
            
            escritor.close();
            
            System.out.println("Archivo creado y datos escritos correctamente.");
        } catch (IOException e) {
            e.printStackTrace();
        }
		
		
	}
	
	

}
