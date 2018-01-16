package org.team08.pspacessnake.Model;

import java.util.ArrayList;
import java.util.List;

public class Room {
    private String URL;
    private String name;
    private List<Token> tokens = new ArrayList<>();
    private long heartbet;

    public Room(String URL, String name) {
        this.URL = URL;
        this.name = name;
        this.heartbet = System.currentTimeMillis();
    }

    public Room(String URL, String name, Token token) {
        this.URL = URL;
        this.name = name;
        this.tokens.add(token);
        this.heartbet = System.currentTimeMillis();
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
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

    public long getHeartbet() {
        return heartbet;
    }

    public void setHeartbet(long heartbet) {
        this.heartbet = heartbet;
    }
}
