package com.ShortNews.ShortNews.entity;

import lombok.Data;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@Embeddable
public class PreferenceKey implements Serializable {
    private String cate_id;
    private String id;
}
