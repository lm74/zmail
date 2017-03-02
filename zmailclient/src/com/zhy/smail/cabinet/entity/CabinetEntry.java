package com.zhy.smail.cabinet.entity;

import com.zhy.smail.lcp.BoxEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wenliz on 2017/2/24.
 */
public class CabinetEntry {
    private Integer value;
    private List<BoardEntry> boards;
    private Map<Integer, BoardEntry> boardMap;

    public List<BoardEntry> getBoards() {
        return boards;
    }

    public void setBoards(List<BoardEntry> boards) {
        this.boards = boards;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public BoxEntry getBoxEntry(int boardId, int boxId){
        BoardEntry boardEntry = boardMap.get(boardId);
        if(boardEntry == null) return null;

        return boardEntry.getBoxEntry(boxId);

    }

    public CabinetEntry(){
        value = 0;

        boards = new ArrayList<>();
        boardMap = new HashMap<>();
    }

    public void addBox(BoxInfo box){
        BoardEntry board = boardMap.get(box.getControlCardId());
        if(board == null){
            board = new BoardEntry();
            board.setBoardNo(box.getControlCardId());
            boards.add(board);
            boardMap.put(board.getBoardNo(), board);
        }

        board.addBox(box);


    }


}
