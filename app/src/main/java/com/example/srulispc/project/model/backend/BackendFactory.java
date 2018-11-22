package com.example.srulispc.project.model.backend;

import com.example.srulispc.project.model.datasource.FireBase;

public class BackendFactory {
    private static final Ibackend Instance = new FireBase();

    public static Ibackend getInstance() {
        return Instance;
    }

    private BackendFactory() {
    }
}
