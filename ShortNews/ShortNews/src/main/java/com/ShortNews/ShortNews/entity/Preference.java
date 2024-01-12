package com.ShortNews.ShortNews.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Preference {
    // 복합키
    @EmbeddedId
    private PreferenceKey preferenceKey;
}