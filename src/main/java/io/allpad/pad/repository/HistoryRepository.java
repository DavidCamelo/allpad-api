package io.allpad.pad.repository;

import io.allpad.pad.dto.TinyHistoryDTO;
import io.allpad.pad.entity.File;
import io.allpad.pad.entity.History;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface HistoryRepository extends JpaRepository<History, UUID> {
    List<TinyHistoryDTO> findAllByFile(File file);
}
