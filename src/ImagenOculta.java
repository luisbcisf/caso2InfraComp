import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImagenOculta {
    byte[] cabecera = new byte[54];
    byte[][][] datosPixeles;
    int altura, anchura;
    int relleno;
    String archivoNombre;

    public ImagenOculta(String archivoEntrada) {
        archivoNombre = archivoEntrada;
        try {
            FileInputStream archivoLectura = new FileInputStream(archivoNombre);
            archivoLectura.read(cabecera);

            anchura = ((cabecera[21] & 0xFF) << 24) | ((cabecera[20] & 0xFF) << 16) | 
                      ((cabecera[19] & 0xFF) << 8) | (cabecera[18] & 0xFF);
            altura = ((cabecera[25] & 0xFF) << 24) | ((cabecera[24] & 0xFF) << 16) | 
                     ((cabecera[23] & 0xFF) << 8) | (cabecera[22] & 0xFF);

            System.out.println("Anchura: " + anchura + " px, Altura: " + altura + " px");
            datosPixeles = new byte[altura][anchura][3];

            int tamañoFila = anchura * 3;
            relleno = (4 - (tamañoFila % 4)) % 4;

            byte[] pixel = new byte[3];
            for (int i = 0; i < altura; i++) {
                for (int j = 0; j < anchura; j++) {
                    archivoLectura.read(pixel);
                    datosPixeles[i][j][0] = pixel[0];
                    datosPixeles[i][j][1] = pixel[1];
                    datosPixeles[i][j][2] = pixel[2];
                }
                archivoLectura.skip(relleno);
            }
            archivoLectura.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void codificarBits(int posicion, int valor, int numBits) {
        int filaBytes = anchura * 3;
        int mascara;
        for (int i = 0; i < numBits; i++) {
            int fila = (8 * posicion + i) / filaBytes;
            int columna = ((8 * posicion + i) % filaBytes) / 3;
            int canal = ((8 * posicion + i) % filaBytes) % 3;
            mascara = valor >> i;
            mascara = mascara & 1;
            datosPixeles[fila][columna][canal] = (byte)((datosPixeles[fila][columna][canal] & 0xFE) | mascara);
        }
    }

    public void ocultarMensaje(char[] mensaje, int tamaño) {
        int posicion = 0;
        byte caracter;
        codificarBits(posicion, tamaño, 16);
        posicion = 2;
        for (int i = 0; i < tamaño; i++) {
            caracter = (byte) mensaje[i];
            codificarBits(posicion, caracter, 8);
            posicion++;
            if (i % 1000 == 0)
                System.out.println("Procesados " + i + " caracteres de " + tamaño);
        }
    }

    public void guardarImagen(String archivoSalida) {
        byte padByte = 0;
        try {
            FileOutputStream archivoEscritura = new FileOutputStream(archivoSalida);
            archivoEscritura.write(cabecera);
            byte[] pixel = new byte[3];
            for (int i = 0; i < altura; i++) {
                for (int j = 0; j < anchura; j++) {
                    pixel[0] = datosPixeles[i][j][0];
                    pixel[1] = datosPixeles[i][j][1];
                    pixel[2] = datosPixeles[i][j][2];
                    archivoEscritura.write(pixel);
                }
                for (int k = 0; k < relleno; k++)
                    archivoEscritura.write(padByte);
            }
            archivoEscritura.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public int obtenerTamañoMensaje() {
        int tamaño = 0;
        for (int i = 0; i < 16; i++) {
            int columna = (i % (anchura * 3)) / 3;
            tamaño = tamaño | (datosPixeles[0][columna][((i % (anchura * 3)) % 3)] & 1) << i;
        }
        return tamaño;
    }

    public void extraerMensaje(char[] mensajeExtraido, int tamaño) {
        int filaBytes = anchura * 3;
        for (int i = 0; i < tamaño; i++) {
            mensajeExtraido[i] = 0;
            for (int j = 0; j < 8; j++) {
                int bytePos = 16 + (i * 8) + j;
                int fila = bytePos / filaBytes;
                int columna = (bytePos % filaBytes) / 3;
                mensajeExtraido[i] = (char) (mensajeExtraido[i] | (datosPixeles[fila][columna][(bytePos % filaBytes) % 3] & 1) << j);
            }
        }
    }
}