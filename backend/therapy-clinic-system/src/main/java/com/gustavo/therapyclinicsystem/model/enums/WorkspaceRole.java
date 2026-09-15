package com.gustavo.therapyclinicsystem.model.enums;

public enum WorkspaceRole {
    OWNER,
    ADMIN,
    THERAPIST;

    public boolean canCreatePatients(){
        return this == OWNER || this == ADMIN;
    }
}