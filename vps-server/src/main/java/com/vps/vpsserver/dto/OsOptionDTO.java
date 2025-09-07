package com.vps.vpsserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OsOptionDTO {
    
    private String name;
    private String displayName;
    private List<String> versions;
}
