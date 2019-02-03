package com.discordbolt.boltbot.discord.modules.games.minesweeper;

import com.discordbolt.api.commands.BotCommand;
import com.discordbolt.api.commands.CommandContext;
import com.discordbolt.api.commands.exceptions.CommandException;
import com.discordbolt.api.commands.exceptions.CommandStateException;

import java.util.*;

public class Minesweeper {

    private static final int SAFE_ZONE = 3;

    @BotCommand(command = "minesweeper", description = "Play a game of minesweeper", usage = "Minesweeper [Easy/Medium/Hard] [Small/Medium/Large]", module = "Games", allowDM = true)
    public static void command(CommandContext cc) throws CommandException {
        Optional<GameDifficulty> difficulty = Arrays.stream(GameDifficulty.values())
                .map(GameDifficulty::name)
                .map(String::toLowerCase)
                .filter(cc.getMessageContent()::contains)
                .map(String::toUpperCase)
                .map(GameDifficulty::valueOf)
                .findAny();

        Optional<BoardSize> size = Arrays.stream(BoardSize.values())
                .map(BoardSize::name)
                .map(String::toLowerCase)
                .filter(cc.getMessageContent()::contains)
                .map(String::toUpperCase)
                .map(BoardSize::valueOf)
                .findAny();

        int boardSize = size.orElse(BoardSize.MEDIUM).getBoardLength();
        int bombCount = difficulty.orElse(GameDifficulty.MEDIUM).getBombCount();

        int[][] gameBoard = generateBoard(boardSize, bombCount, SAFE_ZONE);
        boolean[][] mask = calculateMask(gameBoard);

        StringBuilder board = new StringBuilder(String.format("Find the %d bombs.\n", bombCount));

        for (int row = 0; row < boardSize; row++) {
            for (int column = 0; column < boardSize; column++) {
                boolean hidden = mask[row][column];
                if (hidden) board.append("||");
                board.append(BoardEmoji.getByTile(gameBoard[row][column]).getEmoji());
                if (hidden) board.append("||");
            }
            board.append('\n');
        }

        if (board.length() > 2000)
            throw new CommandStateException("Generated board is too large to fit in a Discord message!");
        cc.replyWith(board.toString()).subscribe();
    }

    public enum GameDifficulty {
        EASY(10),
        MEDIUM(25),
        HARD(50);

        private int bombCount;

        GameDifficulty(int bombCount) {
            this.bombCount = bombCount;
        }

        public int getBombCount() {
            return bombCount;
        }
    }

    public enum BoardSize {
        SMALL(7),
        MEDIUM(11),
        LARGE(15);

        private int boardLength;

        BoardSize(int boardLength) {
            this.boardLength = boardLength;
        }

        public int getBoardLength() {
            return boardLength;
        }
    }

    private enum BoardEmoji {
        BOMB(-1, "\uD83D\uDCA3"),
        EMPTY(0, "\u2B1C"),
        ONE(1, "1\u20E3"),
        TWO(2, "2\u20E3"),
        THREE(3, "3\u20E3"),
        FOUR(4, "4\u20E3"),
        FIVE(5, "5\u20E3"),
        SIX(6, "6\u20E3"),
        SEVEN(7, "7\u20E3"),
        EIGHT(8, "8\u20E3");

        private static final Map<Integer, BoardEmoji> map = new HashMap<>(values().length, 1);

        static {
            for (BoardEmoji e : values()) map.put(e.tile, e);
        }

        private int tile;
        private String emoji;

        BoardEmoji(int tile, String emoji) {
            this.tile = tile;
            this.emoji = emoji;
        }

        public static BoardEmoji getByTile(int tile) {
            BoardEmoji result = map.get(tile);
            if (result == null)
                throw new IllegalArgumentException("Invalid tile ID: " + tile);
            return result;
        }

        public String getEmoji() {
            return emoji;
        }
    }

    /**
     * Generate the minesweeper board based on given inputs.
     * Returned board is an integer 2D array where -1 is a bomb, 0 is an empty square, and >0 is number of bombs touching that square
     * The center safeZone square will never have bombs
     *
     * @return
     * @throws IllegalArgumentException when input parameters are invalid
     */
    private static int[][] generateBoard(int boardLength, int bombCount, int safeZone) {
        if (safeZone < 1 || safeZone > boardLength)
            throw new IllegalArgumentException("Safezone is an invalid size.");
        if(Math.pow(boardLength,2)-Math.pow(safeZone,2) < bombCount)
            throw new IllegalArgumentException("Not enough valid locations for bombs.");
        int[][] board = new int[boardLength][boardLength];

        int safeStart = (boardLength-1)/2 - (safeZone-1)/2;
        for(int y = safeStart; y < safeStart+safeZone; y++){
            for(int x = safeStart; x < safeStart+safeZone; x++){
                board[x][y] = -2;
            }
        }

        Random gen = new Random();
        while(bombCount > 0){
            int x = gen.nextInt(boardLength);
            int y = gen.nextInt(boardLength);
            if(board[x][y] != -1 && board[x][y] != -2){
                board[x][y] = -1;
                bombCount--;
            }
        }

        for(int y = 0; y < boardLength; y++){
            for(int x = 0; x < boardLength; x++){
               if(board[x][y] != -1){
                   int mineCount = 0;
                   for(int yi = -1; yi <=1; yi++){
                       for(int xi = -1; xi <=1; xi++){
                           if (x + xi < 0 || x + xi >= boardLength || y + yi < 0 || y + yi >= boardLength) {
                               continue;
                           }
                           if (board[x + xi][y + yi] == -1) {
                               mineCount++;
                           }
                       }
                   }
                   board[x][y] = mineCount;
               }
            }
        }

        return board;
    }

    /**
     * Generate a mask that unhides certain game squares
     *
     * @param gameBoard
     * @return A 2D mask where false is shown and true is hidden
     */
    private static boolean[][] calculateMask(int[][] gameBoard) {
        boolean[][] mask = new boolean[gameBoard.length][gameBoard.length];
        for(int x = 0; x < gameBoard.length; x++){
            for(int y = 0; y < gameBoard.length; y++){
                mask[x][y] = true;
            }
        }

        revealSquare((gameBoard.length-1)/2,(gameBoard.length-1)/2, gameBoard, mask);
        return mask;
    }

    private static void revealSquare(int x, int y, int[][] board, boolean[][] mask) {
        if(x < 0 || x >= board.length || y < 0 || y >= board.length || mask[x][y] == false){
           return;
        }
        mask[x][y] = false;
        if(board[x][y] == 0){
            for(int yi = -1; yi <=1; yi++){
                for(int xi = -1; xi <=1; xi++){
                    revealSquare(x + xi, y + yi, board, mask);
                }
            }
        }
    }
}
