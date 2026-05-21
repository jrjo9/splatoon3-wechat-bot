package com.mayday9.splatoonbot.business.dto.game;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Lianjiannan
 * @since 2026/5/20
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IdiomValidationResult {

    private boolean valid;

    private String feedback;

    private String nextIdiom;

    private String explanation;

}
