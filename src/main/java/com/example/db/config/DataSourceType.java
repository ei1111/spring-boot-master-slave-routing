package com.example.db.config;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum DataSourceType {
    MASTER("생성, 수정")
    , SLAVE("읽기");
    
    private final String desc;
}
