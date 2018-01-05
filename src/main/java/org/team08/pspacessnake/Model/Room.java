package org.team08.pspacessnake.Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Room {
    private String ID;
    private String name;
    private List<Token> tokens = new ArrayList<>();

    public Room(String ID, String name) {
        this.ID = ID;
        this.name = name;
    }

    public Room(String ID, String name, Token token) {
        this.ID = ID;
        this.name = name;
        this.tokens.add(token);
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Token> getTokens() {
        return tokens;
    }

    public void setTokens(List<Token> tokens) {
        this.tokens = tokens;
    }

    public void addToken(Token token) {
        if (this.tokens == null) {
            this.tokens = new ArrayList<>();
        }
        this.tokens.add(token);
    }
}
