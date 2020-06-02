package com.fkoteam.anairdrum;



public class EntityUpdate {
    int id;


    public EntityUpdate() {
    }

    public EntityUpdate(int i) {
        id=i;

    }

    @Override
    public String toString() {
        return "Holiiii EntityUpdate{" +
                "id=" + id +
                '}';
    }

    public int getId() {
        return id;
    }
}

