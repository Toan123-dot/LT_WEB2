package com.truongquoctoan.example01.service;

import com.truongquoctoan.example01.dto.SettingsDTO;
import com.truongquoctoan.example01.entity.Settings;

public interface SettingsService {
    Settings getSettings();

    Settings updateSettings(SettingsDTO dto);
}