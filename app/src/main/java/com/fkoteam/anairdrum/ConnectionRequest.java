package com.fkoteam.anairdrum;

public class ConnectionRequest {
    String name;

    public ConnectionRequest() {
    }

    public ConnectionRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
