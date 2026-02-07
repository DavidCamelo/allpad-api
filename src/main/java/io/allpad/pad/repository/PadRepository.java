package io.allpad.pad.repository;

import io.allpad.auth.entity.User;
import io.allpad.pad.entity.Pad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PadRepository extends JpaRepository<Pad, UUID> {
    List<Pad> findAllByUser(User user);
}
