package com.mayday9.splatoonbot.business.dto.game;

import com.mayday9.splatoonbot.common.enums.IdiomGameStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Lianjiannan
 * @since 2026/5/20
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IdiomGameState implements Serializable {

    private String groupId;

    private String playerWxid;

    private String playerName;

    private String currentIdiom;

    private List<String> usedIdioms = new ArrayList<>();

    private int consecutiveCorrect;

    private LocalDateTime startTime;

    private IdiomGameStatusEnum status;

}
