package vova.group.id.SensorAPI.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vova.group.id.SensorAPI.models.Sensor;

import java.util.Optional;

@Repository
public interface SensorsRepository extends JpaRepository<Sensor, Integer> {
    boolean existsByName(String name);
    Optional<Sensor> findByName(String name);
}
