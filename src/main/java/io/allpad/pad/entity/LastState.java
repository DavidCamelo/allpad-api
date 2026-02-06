package io.allpad.pad.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class LastState {
    @ElementCollection
    @CollectionTable(name = "pad_active_files", joinColumns = @JoinColumn(name = "pad_id"))
    @MapKeyColumn(name = "pane")
    @Column(name = "file_id")
    private Map<Short, Long> activeFiles = new HashMap<>();
    private Short activePane;
    private String layout;
}
