package io.allpad.pad.repository;

import io.allpad.pad.entity.Pad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PadRepository extends JpaRepository<Pad, Long> {
}
