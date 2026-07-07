package com.ternura.model.enums;

public enum WordleIsWin {
    ONGOING,  // is_win IS NULL
    WIN,      // is_win = true
    LOSE,     // is_win = false
    FINISHED, // is_win IS NOT NULL
    ALL       // 不進行該條件的篩選
}
