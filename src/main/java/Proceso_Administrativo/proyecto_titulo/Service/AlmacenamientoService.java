package Proceso_Administrativo.proyecto_titulo.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class AlmacenamientoService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    public String guardarArchivo(MultipartFile archivo, Long facturaId) {
        //valida que el archivo no este vacio
        if (archivo == null || archivo.isEmpty()) {
            throw new RuntimeException("El archivo esta vacio");
        }
        // confirma si es pdf o imagen
        String contentType = archivo.getContentType();
        if (contentType == null ||
                !(contentType.equals("application/pdf") || contentType.startsWith("image/"))) {
            throw new RuntimeException("Solo se permiten archivos PDF o imagenes");
        }

        try {
            //crea la carpeta si no existe
            Path carpeta = Paths.get(uploadDir);
            Files.createDirectories(carpeta);

            //genera un nombre unico
            String extension = obtenerExtension(archivo.getOriginalFilename());
            String nombreArchivo = "factura_" + facturaId + "_" + System.currentTimeMillis() + extension;
            //ve la ruta
            Path destino = carpeta.resolve(nombreArchivo);
            Files.copy(archivo.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);
            //
            return destino.toString();
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar el archivo: " +e.getMessage());
        }
    }

    private String obtenerExtension(String nombreOriginal) {
        if (nombreOriginal != null && nombreOriginal.contains(".")) {
            return nombreOriginal.substring(nombreOriginal.lastIndexOf("."));
        }
        return "";
    }
}
