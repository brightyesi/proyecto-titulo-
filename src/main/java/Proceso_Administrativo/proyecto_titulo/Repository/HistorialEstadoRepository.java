package Proceso_Administrativo.proyecto_titulo.Repository;

import Proceso_Administrativo.proyecto_titulo.Modelo.HistorialEstado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistorialEstadoRepository extends JpaRepository<HistorialEstado, Long> {
}