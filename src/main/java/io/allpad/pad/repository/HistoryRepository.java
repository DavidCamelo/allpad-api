package io.allpad.pad.repository;

import io.allpad.pad.entity.History;
import io.allpad.pad.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoryRepository extends JpaRepository<History, Long> {
    List<History> findAllByFile(File file);
}
