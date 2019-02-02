package com.discordbolt.boltbot.discord.modules.games.minesweeper;

import com.discordbolt.api.commands.BotCommand;
import com.discordbolt.api.commands.CommandContext;
import com.discordbolt.boltbot.discord.api.BotModule;
import discord4j.core.DiscordClient;

import java.util.Arrays;
import java.util.Optional;

public class Minesweeper implements BotModule {

    @Override
    public void initialize(DiscordClient client) {

    }

    @BotCommand(command = "minesweeper", description = "Play a game of minesweeper", usage = "Minesweeper [Easy/Medium/Hard] [Small/Medium/Large]", module = "Games", allowDM = true)
    public static void command(CommandContext cc) {
        Optional<GameDifficulty> difficulty = Arrays.stream(GameDifficulty.values())
                .map(GameDifficulty::name)
                .map(String::toLowerCase)
                .filter(cc.getMessageContent()::contains)
                .map(GameDifficulty::valueOf)
                .findAny();

        Optional<BoardSize> size = Arrays.stream(BoardSize.values())
                .map(BoardSize::name)
                .map(String::toLowerCase)
                .filter(cc.getMessageContent()::contains)
                .map(BoardSize::valueOf)
                .findAny();
    }

    public enum GameDifficulty {
        EASY(10),
        MEDIUM(25),
        HARD(50);

        private int bombCount;

        GameDifficulty(int bombCount) {
            this.bombCount = bombCount;
        }

        public int getBomeCount() {
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
        BOMB("\uD83D\uDCA3"),
        EMPTY("\u2B1C"),
        ZERO("0\u20E3"),
        ONE("1\u20E3"),
        TWO("2\u20E3"),
        THREE("3\u20E3"),
        FOUR("4\u20E3"),
        FIVE("5\u20E3"),
        SIX("6\u20E3"),
        SEVEN("7\u20E3"),
        EIGHT("8\u20E3");

        private String emoji;

        BoardEmoji(String emoji) {
            this.emoji = emoji;
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
    private int[][] generateBoard(int boardLength, int bombCount, int safeZone) {
        if (safeZone < 1 || safeZone > boardLength)
            throw new IllegalArgumentException("Safezone is an invalid size.");
        return null;
    }

    /**
     * Generate a mask that unhides certain game squares
     *
     * @param gameBoard
     * @return A 2D mask where true is shown and false is hidden
     */
    private boolean[][] calculateMask(int[][] gameBoard) {
        return null;
    }
}
