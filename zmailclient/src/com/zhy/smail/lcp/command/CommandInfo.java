package com.zhy.smail.lcp.command;

/**
 * Created by wenliz on 2017/1/18.
 */
public class CommandInfo {
    private int commandNo;
    private int boardNo;

    public int getCommandNo() {
        return commandNo;
    }

    public void setCommandNo(int commandNo) {
        this.commandNo = commandNo;
    }

    public int getBoardNo() {
        return boardNo;
    }

    public void setBoardNo(int boardNo) {
        this.boardNo = boardNo;
    }

    public int[] getData() {
        return data;
    }

    public void setData(int[] data) {
        this.data = data;
    }

    private int[] data;
}
