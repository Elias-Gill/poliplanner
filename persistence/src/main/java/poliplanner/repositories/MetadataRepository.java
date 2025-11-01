package poliplanner.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import poliplanner.models.metadata.SubjectsMetadata;

import java.util.List;

@Repository
public interface MetadataRepository extends JpaRepository<SubjectsMetadata, Long> {

    /** Consulta optimizada que carga solo los campos necesarios */
    @Query("SELECT m.name, m.semester FROM SubjectsMetadata m WHERE m.career.code = :careerCode")
    List<Object[]> findNameAndSemesterByCareerCode(@Param("careerCode") String careerCode);

    /** Método alternativo usando proyección con interface */
    @Query("SELECT m FROM SubjectsMetadata m WHERE m.career.code = :careerCode")
    List<MetadataProjection> findMetadataProjectionByCareerCode(
            @Param("careerCode") String careerCode);

    interface MetadataProjection {
        String getName();

        Integer getSemester();
    }
}
