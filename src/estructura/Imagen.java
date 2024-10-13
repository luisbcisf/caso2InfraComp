package estructura;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Imagen {
	
	byte[] header = new byte[54];
	byte[][][]imagen;
	int alto, ancho; // en pixeles
	int padding;
	String nombre;
	
	
	/***
	* Método para crear una matriz imagen a partir de un archivo. 
	* @param input: nombre del archivo. El formato debe ser BMP de 24 bits de bit depth
	* @pos la matriz imagen tiene los valores correspondientes a la imagen
	* almacenada en el archivo.
	* */
	
	public Imagen (String input) {
			nombre = new String(input);
			try {
				FileInputStream fis = new FileInputStream(nombre);
				fis.read(header);
 
				ancho = ((header[21] & 0xFF) << 24) | ((header[20] & 0xFF) << 16) |
						((header[19] & 0xFF) << 8) | (header[18] & 0xFF);
				alto = ((header[25] & 0xFF) << 24) | ((header[24] & 0xFF) << 16) |
						((header[23] & 0xFF) << 8) | (header[22] & 0xFF);
				
				imagen = new byte[alto][ancho][3];
				
				int rowSizeSinPadding = ancho * 3; 
				padding = (4 - (rowSizeSinPadding%4))%4;
	 
				byte[] pixel = new byte[3];
				for (int i = 0; i < alto; i++) {
					for (int j = 0; j < ancho; j++) {
						fis.read(pixel);
						imagen[i][j][0] = pixel[0]; 
						imagen[i][j][1] = pixel[1]; 
						imagen[i][j][2] = pixel[2]; 
					}
					fis.skip(padding);
				} 
				fis.close(); 
			} catch (IOException e) { e.printStackTrace(); }
			
	}
	
	
	
	/**
	* Método para esconder un valor en una matriz imagen.
	* @param contador: contador de bytes escritos en la matriz 
	* 
	* @param valor: valor que se quiere esconder 
	* @param numbits: longitud (en bits) del valor
	* @pre la matriz imagen debe haber sido inicializada con una imagen
	* @pos los bits recibidos como parámetro (en valor) están escondido en la imagen. 
	* 
	*/
	
	private void escribirBits(int contador, int valor, int numbits) {
		
		int bytesPorFila = ancho*3;
		int mascara;
		for (int i=0; i < numbits ; i++) {
			int fila = (8*contador + i) / bytesPorFila; 
			int col = ((8*contador + i) % bytesPorFila) / 3; 
			int color = ((8*contador + i) % bytesPorFila) % 3;
			mascara = valor >> i;
			mascara = mascara & 1;
			imagen[fila][col][color] = (byte)((imagen[fila][col][color] & 0xFE) | mascara );
		}
	 }
	
	
	/**
	* Método para esconder un mensaje en una matriz imagen.
	* @param mensaje: Mensaje a esconder
	* @param longitud: longitud del mensaje
	* @pre la matriz imagen debe haber sido inicializada con una imagen
	* @pre la longitud del mensaje en bits debe ser menor que el numero de pixels de la imagen * 3
	* @pos la longitud del mensaje y el mensaje completo están escondidos en la imagen
	*/
	
	public void esconder( char [] mensaje, int longitud) {
		
		int contador=0;
		byte elByte;
		escribirBits(contador,longitud,16) ;
		contador = 2; 
	 
		for (int i=0; i < longitud; i ++) {
			elByte =(byte) mensaje[i];
			escribirBits(contador, elByte, 8);
			contador++;
		}
	}
	
	/**
	* Método para escribir una imagen a un archivo en formato BMP
	* @param output: nombre del archivo donde se almacenará la imagen.
	* Se espera que se invoque para almacenar la imagen modificada.
	* @pre la matriz imagen debe haber sido inicializada con una imagen
	* @pos se creó el archivo en formato bmp con la información de la matriz imagen 
	*/
	
	public void escribirImagen(String output) {
		byte pad = 0;
		try {
			FileOutputStream fos = new FileOutputStream(output);
			fos.write(header);
			byte[] pixel = new byte[3];
			
			for (int i = 0; i < alto; i++) {
				for (int j = 0; j < ancho; j++) {
	
					pixel[0] = imagen[i][j][0];
					pixel[1] = imagen[i][j][1];
					pixel[2] = imagen[i][j][2];
					fos.write(pixel);
				}
				for (int k=0; k<padding; k++) fos.write(pad);
			} 
			fos.close(); 
		} catch (IOException e) { e.printStackTrace(); }
	}
	
	 
	/**
	* Método para recuperar la longitud del mensaje escondido en una imagen. 
	* 
	* @pre la matriz imagen debe haber sido inicializada con una imagen. 
	* la imagen debe esconder la longitud de un mensaje escondido, 
	* y el mensaje. 
	* @return La longitud del mensaje que se esconde en la imagen. 
	*/
	
	public int leerLongitud() {
		int longitud=0;
		for (int i=0; i < 16 ; i++) {
			int col = (i % (ancho*3)) / 3; 
			longitud = longitud | (imagen[0][col][((i % (ancho*3)) % 3)] & 1) << i ; 
		} 
		return longitud; 
	}
	
	 
	/**
	* Método para recuperar un mensaje escondido en una imagen
	* @param cadena: vector de char, con espacio ya asignado 
	* @param longitud: tamaño del mensaje escondido
	* y tamaño del vector (espacio disponible para almacenar información)
	* @pre la matriz imagen debe haber sido inicializada con una imagen. 
	* la imagen debe esconder la longitud de un mensaje escondido, 
	* y el mensaje. 
	* @pos cadena contiene el mensaje escondido en la imagen
	*/
	
	public void recuperar(char[] cadena, int longitud ) {
		int bytesFila=ancho*3;
		
		for (int posCaracter = 0; posCaracter < longitud; posCaracter++) {
			cadena[posCaracter]=0; 
			for (int i=0; i < 8 ; i++) {
				int numBytes = 16 + (posCaracter*8) + i;
				int fila = numBytes/bytesFila;
				int col = numBytes % (bytesFila) / 3;
				cadena[posCaracter] = 
		   (char)( cadena[posCaracter] | (imagen[fila][col][( (numBytes % bytesFila) % 3 )] & 1) << i) ;
			} 
		}
	} 
	
	public int getAlto() {
		return alto;
	}
	
	public int getAncho() {
		return ancho;
	}
	
} 
