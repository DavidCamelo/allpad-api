package io.allpad.pad.repository;

import io.allpad.pad.dto.TinyFileDTO;
import io.allpad.pad.entity.File;
import io.allpad.pad.entity.Pad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FileRepository extends JpaRepository<File, UUID> {
    List<TinyFileDTO> findAllByPad(Pad pad);
}
